package com.markerhub.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.URLUtil;
import com.markerhub.base.annotation.Login;
import com.markerhub.base.dto.CollectDto;
import com.markerhub.base.lang.Result;
import com.markerhub.entity.Collect;
import com.markerhub.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;

@Slf4j
@Controller
public class CollectController extends BaseController {

    @Value("${server.domain}")
    String domain;

    @Login
    @ResponseBody
    @GetMapping("/api/collects/{userId}/{dateline}")
    public Result userCollects (@PathVariable(name = "userId") Long userId,
                                @PathVariable(name = "dateline") String dateline) {
        Page<CollectDto> page = collectService.findUserCollects(userId, dateline, getPage());
        log.info(page.toString());
        return Result.success(page);
    }


    @Login
    @ResponseBody
    @PostMapping("/api/collect/delete")
    public Result delCollect (long id) {
        Collect collect = collectService.findById(id);

        Assert.notNull(collect, "不存在该收藏");
        Assert.isTrue(getCurrentUserId() == collect.getUser().getId(), "无权限删除！");

        collectService.deleteById(id);
        return Result.success();
    }


    @Login
    @GetMapping("/api/collect/edit")
    public String editCollect(Collect collect) throws UnsupportedEncodingException {

        // 这段js是为了放在浏览器书签中方便后面直接收藏某页面。
        // 编码这段js：
        String js = "(function(){" +
                "var site='" + domain +
                "/api/collect/edit?chatset='" +
                "+document.charset+'&title='+encodeURIComponent(document.title)" +
                "+'&url='+encodeURIComponent(document.URL);" +
                "var win = window.open(site, '_blank');" +
                "win.focus();})();";

        // javascript后面的这个冒号不能编码
        js = "" + URLUtil.encode(js);

        if (collect.getId() != null) {
            Collect temp = collectService.findById(collect.getId());
            // 只能编辑自己的收藏
            Assert.notNull(temp, "未找到对应收藏！");
            Assert.isTrue(getCurrentUserId() == temp.getUser().getId(), "无权限操作！");
            BeanUtil.copyProperties(temp, collect);
        }

        req.setAttribute("js", js);
        req.setAttribute("collect", collect);
        return "collect-edit";
    }

    @Login
    @ResponseBody
    @PostMapping("/api/collect/save")
    public Result saveCollect(Collect collect) {

        Assert.hasLength(collect.getTitle(), "标题不能为空");
        Assert.hasLength(collect.getUrl(), "URL不能为空");

        if (collect.getId() != null) {
            Collect temp = collectService.findById(collect.getId());
            // 只能编辑自己的收藏
            Assert.notNull(temp, "未找到对应收藏！");
            Assert.isTrue(getCurrentUserId() == temp.getUser().getId(), "无权限操作！");
        }

        User user = new User();
        user.setId(getCurrentUser().getId());
        collect.setUser(user);

        collectService.save(collect);

        return Result.success();
    }
}
