package com.quantchi.web.metadata.entity;

import com.quantchi.common.treenode.AbstractTreeNode;
import lombok.Data;

import java.io.Serializable;
import java.util.stream.Stream;

@Data
public class MdCatalog extends AbstractTreeNode<MdCatalog, String> implements Serializable {

    private String name;
    private String context;
    private Integer rank;
    private Stream remark;

    @Override
    protected int compareTo(MdCatalog other) {
        return 0;
    }
}
