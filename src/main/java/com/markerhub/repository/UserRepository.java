package com.markerhub.repository;


import com.markerhub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

// 使User获取一些基本JPA里的CRUD接口操作
public interface UserRepository extends JpaRepository<User, Long> {
    User findByOpenId(String openid);
}
