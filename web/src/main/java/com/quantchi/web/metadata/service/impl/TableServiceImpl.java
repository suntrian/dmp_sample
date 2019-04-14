package com.quantchi.web.metadata.service.impl;

import com.quantchi.web.metadata.dao.MdTableMapper;
import com.quantchi.web.metadata.entity.MdTable;
import com.quantchi.web.metadata.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class TableServiceImpl implements TableService {

    @Autowired
    private MdTableMapper tableMapper;

    @Override
    public Integer insert(MdTable table) {
        return tableMapper.insert(table);
    }

    @Override
    public Integer insert(Collection<MdTable> tables) {
        return tableMapper.insertBatch(tables);
    }

    @Override
    public Integer update(MdTable table) {
        return tableMapper.update(table);
    }

    @Override
    public Integer delete(String id) {
        return tableMapper.delete(id);
    }

    @Override
    public Integer delete(Collection<String> ids) {
        return tableMapper.deleteBatch(ids);
    }

    @Override
    public List<MdTable> list(MdTable table) {
        return tableMapper.list(table);
    }
}
