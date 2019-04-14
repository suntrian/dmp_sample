package com.quantchi.web.metadata.service.impl;

import com.quantchi.web.metadata.dao.MdDatabaseMapper;
import com.quantchi.web.metadata.entity.MdDatabase;
import com.quantchi.web.metadata.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class DatabaseServiceImpl implements DatabaseService {

    @Autowired
    private MdDatabaseMapper databaseMapper;

    @Override
    public Integer delete(String id) {
        return databaseMapper.delete(id);
    }

    @Override
    public Integer delete(Collection<String> ids) {
        return databaseMapper.deleteBatch(ids);
    }

    @Override
    public Integer update(MdDatabase database) {
        return databaseMapper.update(database);
    }

    @Override
    public Integer insert(MdDatabase database) {
        return databaseMapper.insert(database);
    }

    @Override
    public Integer insert(Collection<MdDatabase> databases) {
        return databaseMapper.insertBatch(databases);
    }

    @Override
    public List<MdDatabase> list(MdDatabase database) {
        return databaseMapper.list(database);
    }
}
