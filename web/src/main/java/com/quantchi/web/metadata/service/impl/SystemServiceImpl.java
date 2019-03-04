package com.quantchi.web.metadata.service.impl;

import com.quantchi.web.metadata.mapper.SystemMapper;
import com.quantchi.web.metadata.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemServiceImpl implements SystemService {

    @Autowired
    private SystemMapper systemMapper;
}
