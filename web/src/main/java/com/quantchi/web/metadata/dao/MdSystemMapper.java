package com.quantchi.web.metadata.dao;

import com.quantchi.web.metadata.entity.MdSystem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MdSystemMapper {

    Integer insert(MdSystem system);

    Integer update(MdSystem system);

    Integer delete(String id);

    List<MdSystem> list(MdSystem system);

}
