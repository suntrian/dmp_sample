package com.quantchi.web.metadata.dao;

import com.quantchi.web.metadata.entity.MdColumn;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface MdColumnMapper {

  Integer insert(MdColumn column);

  Integer insertBatch(Collection<MdColumn> columns);

  Integer update(MdColumn column);

  Integer delete(String id);

  Integer deleteBatch(Collection<String> ids);

  List<MdColumn> list(MdColumn column);
}
