package com.xuecheng.ucenter.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.auth.controller.CheckCodeClient;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.AuthService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("password_authService")
public class PasswordAuthService implements AuthService {

    @Autowired
    private XcUserMapper xcUserMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CheckCodeClient checkCodeClient;

    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        XcUserExt ext = new XcUserExt();
        // 获取账号
        String username = authParamsDto.getUsername();
        // 获取验证码
        String checkCode = authParamsDto.getCheckcode();
        if(StringUtils.isEmpty(checkCode)){
            throw new RuntimeException("验证码为空");
        }
        Boolean verify = checkCodeClient.verify(authParamsDto.getCheckcodekey(), checkCode);
        if(!verify){
            throw new RuntimeException("验证码错误！");
        }
        // 根据用户名获取用户对象
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>()
                .eq(XcUser::getUsername, username));
        // 不存在直接返回null
        if(xcUser == null){
            return null;
        }
        // 判断密码
        boolean isSuccess = passwordEncoder.matches(authParamsDto.getPassword(), xcUser.getPassword());
        if(!isSuccess){
            throw new RuntimeException("密码错误");
        }
        BeanUtils.copyProperties(xcUser, ext);
        return ext;
    }
}
