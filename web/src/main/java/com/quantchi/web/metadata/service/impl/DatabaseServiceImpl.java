package com.quantchi.web.metadata.service.impl;

import com.quantchi.web.metadata.mapper.DatabaseMapper;
import com.quantchi.web.metadata.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseServiceImpl implements DatabaseService {

    @Autowired
    private DatabaseMapper databaseMapper;
}
