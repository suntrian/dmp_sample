package com.quantchi.web.metadata.dao;

import com.quantchi.web.metadata.entity.MdDatabase;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface MdDatabaseMapper {

    Integer insert(MdDatabase database);

    Integer insertBatch(Collection<MdDatabase> databases);

    Integer update(MdDatabase database);

    Integer delete(String id);

    Integer deleteBatch(Collection<String> ids);

    List<MdDatabase> list(MdDatabase database);

}
