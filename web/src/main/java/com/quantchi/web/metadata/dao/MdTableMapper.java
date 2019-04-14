package com.quantchi.web.metadata.dao;

import com.quantchi.web.metadata.entity.MdTable;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface MdTableMapper {

    Integer insert(MdTable table);

    Integer insertBatch(Collection<MdTable> tables);

    Integer delete(String id);

    Integer deleteBatch(Collection<String> ids);

    Integer update(MdTable table);

    List<MdTable> list(MdTable table);

}
