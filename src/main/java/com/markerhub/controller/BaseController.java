package com.markerhub.controller;

import com.markerhub.base.dto.UserDto;
import com.markerhub.base.lang.Consts;
import com.markerhub.service.CollectService;
import com.markerhub.service.UserService;
import com.markerhub.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

public class BaseController {
    @Resource
    HttpServletRequest req;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    CollectService collectService;

    @Autowired
    UserService userService;

    UserDto getCurrentUser() {
        UserDto userDto = (UserDto) req.getSession().getAttribute(Consts.CURRENT_USER);
        if (userDto == null) {
            userDto = new UserDto();
            userDto.setId(-1L);
        }
        return userDto;
    }

    long getCurrentUserId() {
        return getCurrentUser().getId();
    }

}