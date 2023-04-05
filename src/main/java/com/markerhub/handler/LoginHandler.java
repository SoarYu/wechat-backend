package com.markerhub.handler;

import cn.hutool.json.JSONUtil;
import com.markerhub.base.dto.UserDto;
import com.markerhub.service.UserService;
import com.markerhub.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class LoginHandler {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    UserService userService;

    @Value("${server.domain}")
    String domain;

    public String handle(String openid, String content, WxMpService wxMpService) {

        // 1. 校验字符合法性
        if (content.length() != 6 || !redisUtil.hasKey("ticket-" + content)) {
            return "登录验证码过期或不正确";
        }

        // 2. 用户注册处理
        UserDto userDto = userService.register(openid);

        // 3.信息保存到redis中，用于用户登录成功
        redisUtil.set("Info-" + content, JSONUtil.toJsonStr(userDto), 5 * 60);
        String token = UUID.randomUUID().toString();
        String url = domain + "/autologin?token=" + token;

        redisUtil.set("autologin-" + token, JSONUtil.toJsonStr(userDto), 48*60*60);
        return "欢迎你！\n\n" + "<a href='" + url + "'>点击这里完成登录</a>";
    }
}
