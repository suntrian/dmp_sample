package com.quantchi.web.metadata.service;

import com.quantchi.web.metadata.entity.MdDatabase;

import java.util.Collection;
import java.util.List;

public interface DatabaseService {

    Integer delete(String id);

    Integer delete(Collection<String> ids);

    Integer update(MdDatabase database);

    Integer insert(MdDatabase database);

    Integer insert(Collection<MdDatabase> databases);

    List<MdDatabase> list(MdDatabase database);

}
