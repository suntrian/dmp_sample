package com.quantchi.web.metadata.service.impl;

import com.quantchi.web.metadata.dao.MdDatasourceMapper;
import com.quantchi.web.metadata.entity.MdDatasource;
import com.quantchi.web.metadata.service.DatasourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatasourceServiceImpl implements DatasourceService {

    @Autowired
    private MdDatasourceMapper datasourceMapper;

    @Override
    public Integer insert(MdDatasource datasource) {
        return datasourceMapper.insert(datasource);
    }

    @Override
    public Integer update(MdDatasource datasource) {
        return datasourceMapper.update(datasource);
    }

    @Override
    public Integer delete(String id) {
        return datasourceMapper.delete(id);
    }

    @Override
    public List<MdDatasource> list(MdDatasource datasource) {
        return datasourceMapper.list(datasource);
    }
}
