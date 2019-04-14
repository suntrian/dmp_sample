package com.quantchi.web.metadata.service;

import com.quantchi.web.metadata.entity.MdDatasource;

import java.util.List;

public interface DatasourceService {

    Integer insert(MdDatasource datasource);

    Integer update(MdDatasource datasource);

    Integer delete(String id);

    List<MdDatasource> list(MdDatasource datasource);
}
