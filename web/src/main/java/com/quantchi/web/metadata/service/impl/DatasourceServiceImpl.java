package com.quantchi.web.metadata.service.impl;

import com.quantchi.web.metadata.mapper.DatasourceMapper;
import com.quantchi.web.metadata.service.DatasourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatasourceServiceImpl implements DatasourceService {

    @Autowired
    private DatasourceMapper datasourceMapper;
}
