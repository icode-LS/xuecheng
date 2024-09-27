package com.xuecheng.ucenter.service.Impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserDetailsService {

    @Autowired
    XcUserMapper xcUserMapper;

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 封装AuthParamsDto实体
        AuthParamsDto dto = JSON.parseObject(username, AuthParamsDto.class);
        // 根据beanName获取authService
        String beanName = dto.getAuthType()+"_authService";
        AuthService bean = applicationContext.getBean(beanName, AuthService.class);

        XcUserExt execute = bean.execute(dto);

        return getUserDetails(execute);
    }

    private UserDetails getUserDetails(XcUserExt execute){
        // 封装UserDetails对象
        String password = execute.getPassword();
        // 设置权限
        String[] auths = (String[]) execute.getPermissions().stream().toArray();
        execute.setPassword(null);
        String userString = JSON.toJSONString(execute);
        // 拓展信息
        UserDetails build = User.withUsername(userString).password(password).authorities(auths).build();

        return build;
    }
}
