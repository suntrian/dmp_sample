package com.quantchi.web.metadata.service.impl;

import com.quantchi.web.metadata.mapper.ColumnMapper;
import com.quantchi.web.metadata.service.ColumnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ColumnServiceImpl implements ColumnService {

    @Autowired
    private ColumnMapper columnMapper;


}
