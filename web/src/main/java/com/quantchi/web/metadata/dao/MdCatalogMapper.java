package com.quantchi.web.metadata.dao;

import com.quantchi.web.metadata.entity.MdCatalog;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface MdCatalogMapper {

    Integer insert(MdCatalog catalog);

    Integer update(MdCatalog catalog);

    Integer delete(String id);

    List<MdCatalog> list();

    List<MdCatalog> listByParentId(String parentId);

    List<MdCatalog> listByParentIds(Collection<String> parentIds);

}
