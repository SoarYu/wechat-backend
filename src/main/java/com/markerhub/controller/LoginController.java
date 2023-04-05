package com.markerhub.controller;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.markerhub.base.dto.UserDto;
import com.markerhub.base.lang.Consts;
import com.markerhub.base.lang.Result;
import com.markerhub.config.WeChatMpConfig;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Controller
public class LoginController extends BaseController {

    @Autowired
    WxMpService wxService;

    @Autowired
    WxMpMessageRouter wxMpMessageRouter;

    @Autowired
    WeChatMpConfig wxMpConfigStorage;

    /**
     * 1、获取验证码
     */
    @GetMapping(value = "/login")
    public String login(HttpServletRequest req) {
        // 生成code
        String code = "DY" + RandomUtil.randomNumbers(4);
        while (redisUtil.hasKey(code)) {
            code = "DY" + RandomUtil.randomNumbers(4);
        }
        // 生成ticket
        String ticket = RandomUtil.randomString(32);
        // 5 min key/value/expiredtime
        // ticket-{code} : ticket
        redisUtil.set("ticket-" + code, ticket, 5 * 60);

        req.setAttribute("code", code);
        req.setAttribute("ticket", ticket);
        log.info(code + "---" + ticket);
        return "login";
    }

    @ResponseBody
    @GetMapping("/login-check")
    public Result loginCheck(String code, String ticket) {

        // 校验逻辑后面写
        if (!redisUtil.hasKey("Info-" + code)) {
            return Result.failure("用户未登录");
        }

        // 验证用户传进来的code和ticket是否匹配
        String ticketBak = redisUtil.get("ticket-" + code).toString();
        if (!ticketBak.equals(ticket)) {
            return Result.failure("登录失败");
        }

        String userJson = redisUtil.get("Info-" + code).toString();
        UserDto userDto = JSONUtil.toBean(userJson, UserDto.class);
        req.getSession().setAttribute(Consts.CURRENT_USER, userDto);

        return Result.success();
    }

    /**
     * 服务号的回调
     */
    @ResponseBody
    @RequestMapping(value = "/wx/back")
    public String wxCallback(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String signature = req.getParameter("signature");
        String timestamp = req.getParameter("timestamp");
        String nonce = req.getParameter("nonce");
        String echoStr = req.getParameter("echostr");//用于验证服务器配置

        if (StrUtil.isNotBlank(echoStr)) {
            log.info("---------------->验证服务器配置");
            return echoStr;
        }
//        if (!wxService.checkSignature(timestamp, nonce, signature)) {
//            // 消息不合法
//            log.error("------------------> 消息不合法");
//            return null;
//        }

        WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(req.getInputStream());
        // 路由到各个handler
        WxMpXmlOutMessage outMessage = wxMpMessageRouter.route(inMessage);

        log.info("返回结果 ----------------> " + outMessage);
        String result =  outMessage == null ? "" : outMessage.toXml();
        return result;
    }

    /**
     * 手机端登录
     */
    @GetMapping("/autologin")
    public String autologin(String token) {
        log.info("-------------->" + token);
        String userJson = String.valueOf(redisUtil.get("autologin-" + token));

        if (StringUtils.isNotBlank(userJson)) {
            UserDto user = JSONUtil.toBean(userJson, UserDto.class);
            req.getSession().setAttribute(Consts.CURRENT_USER, user);
            return "redirect:/";
        }
        return "redirect:/login";
    }

    // 注销
    @GetMapping("/logout")
    public String logout() {
        req.getSession().removeAttribute(Consts.CURRENT_USER);
        return "redirect:/index";
    }

}
