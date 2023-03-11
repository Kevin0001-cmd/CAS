package com.zkf.repository;

import com.zkf.vo.ST;
import org.springframework.data.jpa.repository.JpaRepository;

public interface STRepository extends JpaRepository<ST, Integer> {

    ST findByStAndServiceIdAndUsedAndValidate(String st, String service_id, int used, int validate);

    ST findByTgtIdAndValidate(int tgt_id,int validate);

    ST findByStAndValidate(String ticket, int i);
}
