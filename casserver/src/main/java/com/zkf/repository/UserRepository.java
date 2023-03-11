package com.zkf.repository;

import com.zkf.vo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUsernameAndPassword(String username, String password);

    @Query(value = "select * from user where id = ?1", nativeQuery = true)
    User findUserById(Integer id);
}
