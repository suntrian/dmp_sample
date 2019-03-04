package com.quantchi.web.metadata.service.impl;

import com.quantchi.web.metadata.mapper.TableMapper;
import com.quantchi.web.metadata.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TableServiceImpl implements TableService {

    @Autowired
    private TableMapper tableMapper;
}
