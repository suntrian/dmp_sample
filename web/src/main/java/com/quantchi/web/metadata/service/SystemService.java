package com.quantchi.web.metadata.service;

import com.quantchi.web.metadata.entity.MdSystem;

import java.util.List;

public interface SystemService {

    Integer insert(MdSystem system);

    Integer update(MdSystem system);

    Integer delete(String id);

    List<MdSystem> list(MdSystem system);
}
