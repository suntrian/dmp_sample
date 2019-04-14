package com.quantchi.web.metadata.service;

import com.quantchi.web.metadata.entity.MdCatalog;

import java.util.List;

public interface CatalogService {

    Integer insert(MdCatalog catalog);

    Integer update(MdCatalog catalog);

    Integer delete(String id);

    List<MdCatalog> list();
}
