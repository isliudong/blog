package com.ld.blog.core.config;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.net.multipart.UploadFile;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.ld.blog.api.controller.v1.dto.FileDTO;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.stereotype.Component;

/**
 * @author liudong
 */
@Component
@AllArgsConstructor
public class FileSever implements ApplicationRunner {
    private BlogConfig blogConfig;
    private RsaVerifier verifier;

    @Override
    public void run(ApplicationArguments args) {
        HttpUtil.createServer(blogConfig.getPort())
                .addAction("/file", (request, response) -> {
                            String token = request.getHeader("Authorization").substring(7);
                            Jwt jwt = JwtHelper.decodeAndVerify(token, verifier);
                            String claims = jwt.getClaims();
                            User user = JSON.parseObject(claims, User.class);


                            final UploadFile file = request.getMultipart().getFile("file");
                            // 传入目录，默认读取HTTP头中的文件名然后创建文件
                            response.setContentType("application/json;charset=UTF-8");
                            FileDTO fileDTO = new FileDTO();
                            fileDTO.setFileKey(IdUtil.randomUUID() + "." + FileUtil.extName(file.getFileName()));
                            fileDTO.setFileName(file.getFileName());
                            file.write(blogConfig.getFileSeverPath() + "/" + fileDTO.getFileKey());
                            response.write(JSON.toJSONString(fileDTO), ContentType.JSON.toString());
                        }
                )
                // 设置默认根目录
                .setRoot(blogConfig.getFileSeverPath())
                .start();
    }
}
