package com.zkf.repository;

import com.zkf.vo.TGT;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TGTRepository extends JpaRepository<TGT, Integer> {

    List findByIdAndValidate(Integer id,Integer validate);

    TGT findById(String id);
}
