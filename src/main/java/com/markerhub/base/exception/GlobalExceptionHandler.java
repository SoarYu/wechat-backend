package com.markerhub.base.exception;

import cn.hutool.json.JSONUtil;
import com.markerhub.base.lang.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class GlobalExceptionHandler implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 处理请求的异常参数、异常状态
        if (ex instanceof IllegalArgumentException || ex instanceof IllegalStateException) {
            log.error(ex.getMessage());
        } else {
            log.error(ex.getMessage(), ex);
        }
        // 是否是ajax请求
        String requestType = request.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equals(requestType)) {
            try {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().print(JSONUtil.toJsonStr(Result.failure(ex.getMessage())));
            } catch (IOException e) {
                // do something
            }
            return new ModelAndView();
        }
        request.setAttribute("message", "系统异常，请稍后再试！");
        return new ModelAndView("error");
    }
}
