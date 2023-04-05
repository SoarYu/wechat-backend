package com.markerhub.config;

import com.markerhub.handler.TextHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "wechat")
public class WeChatMpConfig {

    private String mpAppId;
    private String mpAppSecret;
    private String token;

    @Autowired
    TextHandler textHandler;

    @Bean
    public WxMpService wxMpService() {
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(wxMpConfigStorage());
        return wxMpService;
    }
    /**
     * 配置公众号密钥信息
     * @return
     */
    @Bean
    public WxMpConfigStorage wxMpConfigStorage() {
        WxMpInMemoryConfigStorage wxMpConfigStorage = new WxMpInMemoryConfigStorage();
        wxMpConfigStorage.setAppId(mpAppId);
        wxMpConfigStorage.setSecret(mpAppSecret);
        wxMpConfigStorage.setToken(token);
        return wxMpConfigStorage;
    }
    /**
     * 配置消息路由
     * @param wxMpService
     * @return
     */
    @Bean
    public WxMpMessageRouter router(WxMpService wxMpService) {
        WxMpMessageRouter router = new WxMpMessageRouter(wxMpService);
        // TODO 消息路由
        router
                .rule()
                .async(false)
                .msgType(WxConsts.XmlMsgType.TEXT)
                .handler(textHandler)
                .end();
        return router;
    }
}