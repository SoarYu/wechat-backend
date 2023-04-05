package com.markerhub.base.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DatelineDto {
    private String title;
    private List<DatelineDto> children = new ArrayList<>();
}
