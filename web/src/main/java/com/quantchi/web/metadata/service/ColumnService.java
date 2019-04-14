package com.quantchi.web.metadata.service;

import com.quantchi.web.metadata.entity.MdColumn;

import java.util.Collection;
import java.util.List;

public interface ColumnService {

    Integer delete(String columnId);

    Integer delete(Collection<String> ids);

    Integer insert(MdColumn column);

    Integer insert(Collection<MdColumn> columns);

    Integer update(MdColumn column);

    List<MdColumn> list(MdColumn column);

}
