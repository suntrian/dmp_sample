package com.quantchi.web.metadata.service.impl;

import com.quantchi.web.metadata.dao.MdSystemMapper;
import com.quantchi.web.metadata.entity.MdSystem;
import com.quantchi.web.metadata.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemServiceImpl implements SystemService {

    @Autowired
    private MdSystemMapper systemMapper;

    @Override
    public Integer insert(MdSystem system) {
        return systemMapper.insert(system);
    }

    @Override
    public Integer update(MdSystem system) {
        return systemMapper.update(system);
    }

    @Override
    public Integer delete(String id) {
        return systemMapper.delete(id);
    }

    @Override
    public List<MdSystem> list(MdSystem system) {
        return systemMapper.list(system);
    }
}
