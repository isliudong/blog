package com.ld.blog.auth.service;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.MultiValueMap;
import top.dcenter.ums.security.core.oauth.entity.ConnectionData;
import top.dcenter.ums.security.core.oauth.entity.ConnectionDto;
import top.dcenter.ums.security.core.oauth.exception.RegisterUserFailureException;
import top.dcenter.ums.security.core.oauth.repository.exception.UpdateConnectionException;
import top.dcenter.ums.security.core.oauth.signup.ConnectionService;

/**
 * @author liudong
 */
//@Service
@Slf4j
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public UserDetails signUp(AuthUser authUser, String s, String s1) throws RegisterUserFailureException {
        return new User(authUser.getUsername(), passwordEncoder.encode("123"), null);

    }

    @Override
    public void updateUserConnectionAndAuthToken(AuthUser authUser, ConnectionData connectionData) throws UpdateConnectionException {

    }

    @Override
    public void binding(UserDetails userDetails, AuthUser authUser, String s) {

    }

    @Override
    public void unbinding(String s, String s1, String s2) {

    }

    @Override
    public List<ConnectionData> findConnectionByProviderIdAndProviderUserId(String s, String s1) {
        return null;
    }

    @Override
    public MultiValueMap<String, ConnectionDto> listAllConnections(String s) {
        return null;
    }
}
