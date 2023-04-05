package com.markerhub.interceptor;

import com.markerhub.base.annotation.Login;
import com.markerhub.base.dto.UserDto;
import com.markerhub.base.lang.Consts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class AuthInterceptor implements HandlerInterceptor {
    // 加在Handler上，判断登录状态
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserDto userDto = (UserDto)request.getSession().getAttribute(Consts.CURRENT_USER);
        if (userDto == null) {
            userDto = new UserDto();
            userDto.setId(-1L);
        }
        request.setAttribute("current", userDto);

        Login annotation;
        if(handler instanceof HandlerMethod) {
            annotation = ((HandlerMethod) handler).getMethodAnnotation(Login.class);
        }else{
            return true;
        }

        if(annotation == null){
            // 没有@Login注解，说明是公开接口，直接放行
            return true;
        }

        if (userDto.getId() == null || userDto.getId() == -1L) {
            response.sendRedirect("/login");
            return false;
        }
        log.info("欢迎您：{}", userDto.getUsername());
        return true;
    }
}