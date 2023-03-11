package com.zkf.repository;

import com.zkf.vo.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Integer> {
    Service findByUrl(String url);
}
