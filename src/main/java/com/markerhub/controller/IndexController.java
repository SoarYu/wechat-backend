package com.markerhub.controller;

import com.markerhub.base.annotation.Login;
import com.markerhub.base.dto.DatelineDto;
import com.markerhub.service.CollectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@Controller
public class IndexController extends BaseController{

    @Login
    @GetMapping(value = {"", "/"})
    public String index() {
        // 时间线
        List<DatelineDto> datelineDtos = collectService.getDatelineByUserId(getCurrentUserId());

        req.setAttribute("datelines", datelineDtos);

        return "index";
    }

}
