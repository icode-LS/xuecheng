package com.xuecheng.ucenter.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.AuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("wx_authService")
public class WxAuthService implements AuthService {

    @Autowired
    private XcUserMapper userMapper;

    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        // 获取用户名
        String username = authParamsDto.getUsername();

        // 获取用户
        XcUser xcUser = userMapper.selectOne(new LambdaQueryWrapper<XcUser>()
                .eq(XcUser::getUsername, username));

        if(xcUser == null){
            throw new RuntimeException("微信登录错误！");
        }
        XcUserExt ext = new XcUserExt();
        BeanUtils.copyProperties(xcUser, ext);
        return ext;
    }
}
