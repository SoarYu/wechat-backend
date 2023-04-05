package com.markerhub.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "m_collect")
public class Collect implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String url;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 笔记想法
    private String note;

    // 是否公开，0公开，1私有，默认公开
    private Integer personal = 0;

    // 收藏日期，不存时间部分
    private LocalDate collected;

    private LocalDateTime created;
}
