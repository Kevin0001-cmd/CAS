package com.zkf.repository;

import com.zkf.vo.Info;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InfoRepository extends JpaRepository<Info, Integer> {

    Info findByUsername(String username);
}
