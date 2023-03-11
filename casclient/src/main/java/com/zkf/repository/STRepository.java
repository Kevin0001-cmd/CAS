package com.zkf.repository;

import com.zkf.vo.ST;
import org.springframework.data.jpa.repository.JpaRepository;

public interface STRepository extends JpaRepository<ST, Integer> {

    ST findByStAndValidate(String st, int validate);
}
