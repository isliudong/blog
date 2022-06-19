package com.ld.blog.core.security.permission;


import static com.ld.blog.core.security.core.Authority.publicPer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ld.blog.core.Reflections;
import com.ld.blog.core.security.core.Authority;
import com.ld.blog.domain.entity.SysPermission;
import com.ld.blog.infra.mapper.PermissionMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * 权限解析实现
 *
 * @author liudong
 */
@Component
public class ApplicationPermissionParserImpl implements ApplicationPermissionParser, ApplicationContextAware {
    /**
     * 日志打印对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationPermissionParserImpl.class);

    private static final String SCOPED_TARGET_NAME_PREFIX = "scopedTarget.";

    /**
     * 上下文对象
     */
    private ApplicationContext applicationContext;


    /**
     * 应用权限仓库对象
     */
    private PermissionMapper permissionMapper;

    @Autowired
    private Authority authority;

    /**
     * 连接两个路径
     *
     * @param controllerPath 控制器路径
     * @param methodPath     方法路径
     * @return 连接的结果路径
     */
    private static String concatPath(String controllerPath, String methodPath) {
        String path = "";
        if (StringUtils.isNotBlank(controllerPath)) {
            path += controllerPath;
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        if (StringUtils.isNotBlank(methodPath)) {
            if (path.endsWith("/") && methodPath.startsWith("/")) {
                path += methodPath.substring(1);
            } else if (path.endsWith("/") || methodPath.startsWith("/")) {
                path += methodPath;
            } else {
                path += "/" + methodPath;
            }
        }

        return path;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Autowired
    public void setPermissionMapper(PermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    @Override
    public void parse() {
        // 查询和解析controller
        this.findAndParseController();
    }

    /**
     * 查询和解析controller
     */
    private void findAndParseController() {
        List<SysPermission> permissionDataList = new ArrayList<>();

        String[] beanNames = this.applicationContext.getBeanNamesForType(Object.class);
        if (ArrayUtils.isNotEmpty(beanNames)) {
            Class<?> beanType = null;
            for (String beanName : beanNames) {
                if (!beanName.startsWith(SCOPED_TARGET_NAME_PREFIX)) {
                    try {
                        // 获取bean的类型
                        beanType = this.applicationContext.getType(beanName);
                        if (Reflections.isCglibProxyClass(beanType)) {
                            beanType = beanType.getSuperclass();
                        }
                    } catch (Throwable ex) {
                        // An unresolvable bean type, probably from a lazy bean - let's ignore it.
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Could not resolve target class for bean with name '" + beanName + "'", ex);
                        }
                    }
                    if (Objects.nonNull(beanType) && this.isHandler(beanType)) {
                        // 解析controller
                        permissionDataList.addAll(this.parseController("blog", beanName, beanType));
                    }
                }
            }

            // 更新权限数据
            this.batchUpdate(permissionDataList);


        }
    }

    private void batchUpdate(List<SysPermission> permissionDataList) {
        ArrayList<SysPermission> needUpdatePermissions = new ArrayList<>();
        Iterator<SysPermission> iterator = permissionDataList.iterator();
        while (iterator.hasNext()) {
            SysPermission permission = iterator.next();
            QueryWrapper<SysPermission> queryWrapper = new QueryWrapper<>();
            SysPermission entity = new SysPermission();
            entity.setUrl(permission.getUrl());
            queryWrapper.setEntity(entity);
            SysPermission sysPermission = permissionMapper.selectOne(queryWrapper);
            if (sysPermission != null) {
                permission.setId(sysPermission.getId());
                needUpdatePermissions.add(permission);
                iterator.remove();
            }
        }
        if (CollectionUtils.isNotEmpty(needUpdatePermissions)) {
            permissionMapper.batchUpdate(needUpdatePermissions);
        }

        //todo 批量插入
        for (SysPermission sysPermission : permissionDataList) {
            permissionMapper.insert(sysPermission);

        }

        System.out.println("数据权限");
        System.out.println(permissionDataList);
        System.out.println(needUpdatePermissions);
        System.out.println("公开权限");
        System.out.println(publicPer);
    }

    /**
     * 判断是否为需要处理的对象
     *
     * @param beanType bean类型
     * @return 判断结果，true 是，false 不是
     */
    private boolean isHandler(Class<?> beanType) {
        return (AnnotatedElementUtils.hasAnnotation(beanType, Controller.class) ||
                AnnotatedElementUtils.hasAnnotation(beanType, RequestMapping.class));
    }

    /**
     * 处理controller
     *
     * @param serviceName    服务名
     * @param controllerName controller的名称
     * @param clazz          controller的类
     * @return 解析的权限数据
     */
    private List<SysPermission> parseController(String serviceName, String controllerName, Class<?> clazz) {
        LOGGER.debug("Start Parse Service [{}] : Controller [{}] Permission", serviceName, controllerName);
        List<SysPermission> permissionDataList = new ArrayList<>();


        // 处理Controller类的 RequestMapping注解
        RequestMapping controllerMapping = AnnotatedElementUtils.findMergedAnnotation(clazz, RequestMapping.class);
        String[] controllerPaths = null;
        if (Objects.nonNull(controllerMapping)) {
            controllerPaths = controllerMapping.value();
        }
        if (ArrayUtils.isEmpty(controllerPaths)) {
            // 如果没有指定controller的请求路径，则默认为空
            controllerPaths = new String[]{""};
        }

        // 处理Controller内部的方法
        for (Method method : clazz.getMethods()) {
            permissionDataList.addAll(this.parseMethod(controllerName, method, controllerPaths));
        }

        LOGGER.debug("Finished Parse Service [{}] : Controller [{}] Permission", serviceName, controllerName);

        return permissionDataList;
    }

    /**
     * 处理controller类内部的方法
     *
     * @param controllerName  服务名
     * @param method          方法对象
     * @param controllerPaths controller上的路径
     * @return 解析的权限数据
     */
    private List<SysPermission> parseMethod(String controllerName, Method method, String[] controllerPaths) {
        List<SysPermission> permissionDataList = new ArrayList<>();

        RequestMapping methodMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
        if (Objects.nonNull(methodMapping)) {
            Permission permission = AnnotationUtils.findAnnotation(method, Permission.class);


            String[] methodPaths = methodMapping.value();
            if (ArrayUtils.isEmpty(methodPaths)) {
                // 如果没有指定请求路径，则默认为空
                methodPaths = new String[]{""};
            }

            RequestMethod[] requestMethods = methodMapping.method();
            if (ArrayUtils.isEmpty(requestMethods)) {
                // 如果没有指定请求方法，则默认为全部方法
                requestMethods = RequestMethod.values();
            }


            // 方法名
            String methodName = method.getName();
            // 请求方式
            String requestMethodString;
            for (RequestMethod requestMethod : requestMethods) {
                requestMethodString = requestMethod.name().toLowerCase();
                // 不同的请求方式下按照数量进行更新索引，主要用于区分相同请求方式的不同路径
                for (String controllerPath : controllerPaths) {
                    for (String methodPath : methodPaths) {

                        SysPermission sysPermission = new SysPermission();
                        sysPermission.setName(controllerName + ":" + methodName);
                        sysPermission.setType("permission");
                        String url = requestMethodString + concatPath(controllerPath, methodPath);
                        sysPermission.setUrl(url);
                        if (Objects.isNull(permission)) {
                            LOGGER.warn("Method [{}.{}] Without @{} Annotation, Use Default Parameters To Define Permission Data",
                                    method.getDeclaringClass().getCanonicalName(), method.getName(), Permission.class.getCanonicalName());
                            publicPer.add(url);
                            return Collections.emptyList();
                        }
                        if (permission.permissionPublic()) {
                            publicPer.add(url);
                        }
                        sysPermission.setLoginAccess(permission.permissionLogin());
                        sysPermission.setPublicAccess(permission.permissionPublic());
                        permissionDataList.add(sysPermission);

                    }
                }
            }
        }

        return permissionDataList;
    }

}
