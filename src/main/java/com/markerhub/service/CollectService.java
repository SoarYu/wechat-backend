package com.markerhub.service;


import com.markerhub.base.dto.CollectDto;
import com.markerhub.base.dto.DatelineDto;
import com.markerhub.entity.Collect;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CollectService {
    List<DatelineDto> getDatelineByUserId(long currentUserId);
}
