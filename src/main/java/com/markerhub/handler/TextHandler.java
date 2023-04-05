package com.markerhub.handler;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class TextHandler implements WxMpMessageHandler {
    private final String UNKNOWN =  "未识别字符串！";

    @Autowired
    LoginHandler loginHandler;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService, WxSessionManager wxSessionManager) throws WxErrorException {
        String openid = wxMessage.getFromUser();
        String content = wxMessage.getContent();

        String result = UNKNOWN;

        if (StrUtil.isNotBlank(content)) {
            content = content.toUpperCase().trim();

            // 处理登录字符串
            if (content.indexOf("DY") == 0) {
                result = loginHandler.handle(openid, content, wxMpService);
            }
        }

        return WxMpXmlOutMessage.TEXT()
                .content(result)
                .fromUser(wxMessage.getToUser())
                .toUser(wxMessage.getFromUser())
                .build();
    }
}
