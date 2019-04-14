package com.quantchi.web.metadata.service;

import com.quantchi.web.metadata.entity.MdTable;

import java.util.Collection;
import java.util.List;

public interface TableService {

    Integer insert(MdTable table);

    Integer insert(Collection<MdTable> tables);

    Integer update(MdTable table);

    Integer delete(String id);

    Integer delete(Collection<String> ids);

    List<MdTable> list(MdTable table);
}
