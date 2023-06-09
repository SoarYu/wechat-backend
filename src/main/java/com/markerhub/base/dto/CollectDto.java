package com.markerhub.base.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CollectDto implements Serializable {

    private Long id;
    private String title;
    private String url;
    private String note;

    // 是否公开，0公开，1私有，默认公开
    private Integer personal = 0;

    // 收藏日期
    private LocalDate collected;
    private LocalDateTime created;

    private UserDto user;
}