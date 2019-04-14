package com.quantchi.web.metadata.service.impl;

import com.quantchi.web.metadata.dao.MdColumnMapper;
import com.quantchi.web.metadata.entity.MdColumn;
import com.quantchi.web.metadata.service.ColumnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class ColumnServiceImpl implements ColumnService {

    @Autowired
    private MdColumnMapper columnMapper;


    @Override
    public Integer delete(String columnId) {
        return columnMapper.delete(columnId);
    }

    @Override
    public Integer delete(Collection<String> ids) {
        return columnMapper.deleteBatch(ids);
    }

    @Override
    public Integer insert(MdColumn column) {
        return columnMapper.insert(column);
    }

    @Override
    public Integer insert(Collection<MdColumn> columns) {
        return columnMapper.insertBatch(columns);
    }

    @Override
    public Integer update(MdColumn column) {
        return columnMapper.update(column);
    }

    @Override
    public List<MdColumn> list(MdColumn column) {
        return columnMapper.list(column);
    }
}
