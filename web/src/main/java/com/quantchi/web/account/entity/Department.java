package com.quantchi.web.account.entity;

import com.quantchi.common.treenode.AbstractTreeNode;
import lombok.Data;

import java.io.Serializable;

@Data
public class Department extends AbstractTreeNode<Department, Integer> implements Serializable {

    private String name;

    @Override
    protected int compareTo(Department other) {
        return 0;
    }
}
