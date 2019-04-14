package com.quantchi.web.metadata.service.impl;

import com.quantchi.web.metadata.dao.MdCatalogMapper;
import com.quantchi.web.metadata.entity.MdCatalog;
import com.quantchi.web.metadata.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogServiceImpl implements CatalogService {

    @Autowired
    private MdCatalogMapper catalogMapper;

    @Override
    public Integer insert(MdCatalog catalog) {
        return catalogMapper.insert(catalog);
    }

    @Override
    public Integer update(MdCatalog catalog) {
        return catalogMapper.update(catalog);
    }

    @Override
    public Integer delete(String id) {
        return catalogMapper.delete(id);
    }

    @Override
    public List<MdCatalog> list() {
        return catalogMapper.list();
    }
}
