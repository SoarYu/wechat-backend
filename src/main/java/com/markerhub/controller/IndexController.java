package com.markerhub.controller;

import com.markerhub.base.annotation.Login;
import com.markerhub.base.dto.CollectDto;
import com.markerhub.base.dto.DatelineDto;
import com.markerhub.base.lang.Result;
import com.markerhub.service.CollectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

        req.setAttribute("userId", getCurrentUserId());

        return "index";
    }

    // 收藏广场
    @GetMapping("/collect-square")
    public String collectSquare () {
        return "collect-square";
    }

    @ResponseBody
    @GetMapping("/api/collects/square")
    public Result allCollectsSquare() {
        Page<CollectDto> page = collectService.findSquareCollects(getPage());

        return Result.success(page);
    }
}
