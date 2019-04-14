package com.quantchi.web.metadata.dao;

import com.quantchi.web.metadata.entity.MdDatasource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MdDatasourceMapper {

    Integer insert(MdDatasource datasource);

    Integer delete(String id);

    Integer update(MdDatasource datasource);

    List<MdDatasource> list(MdDatasource datasource);

}
