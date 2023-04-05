package com.markerhub.service.impl;

import cn.hutool.core.date.DateUtil;
import com.markerhub.base.dto.DatelineDto;
import com.markerhub.repository.CollectRepository;
import com.markerhub.service.CollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CollecetServiceImpl implements CollectService {

    @Autowired
    CollectRepository collectRepository;

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
