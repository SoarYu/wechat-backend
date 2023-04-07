package com.markerhub.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


import cn.hutool.core.date.DateUtil;
import com.markerhub.base.dto.*;
import com.markerhub.base.lang.Consts;
import com.markerhub.base.lang.Result;
import com.markerhub.entity.*;
import com.markerhub.mapstruct.CollectMapper;
import com.markerhub.repository.CollectRepository;
import com.markerhub.service.CollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpSession;


@Service
public class CollecetServiceImpl implements CollectService {

    @Autowired
    CollectRepository collectRepository;

    @Autowired
    CollectMapper collectMapper;

    @Autowired
    HttpSession httpSession;

    @Override
    public List<DatelineDto> getDatelineByUserId(long currentUserId) {

        List<Date> datelines = collectRepository.getDatelineByUserId(currentUserId);

        List<DatelineDto> datelineDtos = new ArrayList<>();

        for (Date date : datelines) {
            String parent = DateUtil.format(date, "yyyy年MM月");
            String title = DateUtil.format(date, "yyyy年MM月dd日");

            datelineDtos = handleDateline(datelineDtos, parent, title);
        }
        return datelineDtos;
    }

    @Override
    public Page<CollectDto> findUserCollects(long userId, String dateline, Pageable pageable) {

        /*
        * select * from collect left join user on c.id = u.id where id = userid;
        * */
        // 查询用户的所有收藏
        Page<Collect> page = collectRepository.findAll((root, query, builder) -> {
            // 数据库连接
            Predicate predicate = builder.conjunction();

            // 关联查询 收藏表与用户表左连接
            Join<Collect, User> join = root.join("user", JoinType.LEFT);
            predicate.getExpressions().add(builder.equal(join.get("id"), userId));

            // 查询特定日期，all表示查询全部
            if (!dateline.equals("all")) {
                // 转日期格式
                LocalDate localDate = LocalDate.parse(dateline, DateTimeFormatter.ofPattern("yyyy年MM月dd日"));
                predicate.getExpressions().add(
                        builder.equal(root.<Date>get("collected"), localDate));
            }

            UserDto userDto = (UserDto)httpSession.getAttribute(Consts.CURRENT_USER);
            boolean isOwn = (userDto != null && userId == userDto.getId().longValue());

            // 非本人，只能查看公开的
            if (!isOwn) {
                predicate.getExpressions().add(
                        builder.equal(root.get("personal"), 0));
            }

            return predicate;
        }, pageable);

        // 实体转Dto
        return page.map(collectMapper::toDto);

    }


    @Override
    public Collect findById(long id) {
        Optional<Collect> optional = collectRepository.findById(id);
        return optional.isPresent() ? optional.get() : null;
    }

    @Override
    public void deleteById(long id) {
        collectRepository.deleteById(id);
    }


    @Override
    public void save(Collect collect) {
        if (collect.getId() == null) {
            collect.setCreated(LocalDateTime.now());
            collect.setCollected(LocalDate.now());

            collectRepository.save(collect);
        } else {

            Collect temp = collectRepository.getById(collect.getId());

            temp.setTitle(collect.getTitle());
            temp.setUrl(collect.getUrl());
            temp.setNote(collect    .getNote());
            temp.setUser(collect.getUser());
            temp.setPersonal(collect.getPersonal());

            temp.setCollected(LocalDate.now());
            collectRepository.save(temp);
        }
    }

    @Override
    public Page<CollectDto> findSquareCollects(Pageable pageable) {

        Page<Collect> page = collectRepository.findAll((root, query, builder) -> {
            Predicate predicate = builder.conjunction();

            // 只查公开分享的
            predicate.getExpressions().add(
                    builder.equal(root.get("personal"), 0));

            return predicate;
        }, pageable);
        return page.map(collectMapper::toDto);
    }

    /**
     * 构建日期侧边栏的上下级关系
     */
    private List<DatelineDto> handleDateline(List<DatelineDto> datelineDtos, String parent, String title) {

        // 需要被添加到上级中的子级
        DatelineDto datelineDto = new DatelineDto();
        datelineDto.setTitle(title);

        Optional<DatelineDto> optional = datelineDtos.stream().filter(vo -> vo.getTitle().equals(parent)).findFirst();

        if (optional.isPresent()) {
            // 如果上级存在，则直接添加到该上级中
            optional.get().getChildren().add(datelineDto);
        } else {
            DatelineDto parentDto = new DatelineDto();
            parentDto.setTitle(parent);
            parentDto.getChildren().add(datelineDto);


            datelineDtos.add(parentDto);
        }
        return datelineDtos;
    }


}
