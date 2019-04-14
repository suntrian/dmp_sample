package com.quantchi.web.account.entity;

import com.quantchi.common.treenode.AbstractTreeNode;
import lombok.Data;

import java.io.Serializable;

@Data
public class Department extends AbstractTreeNode<Department, Integer> implements Serializable {

    private String name;
    private Integer type;
    private Integer chiefLeader;
    private Integer viceLeader;
    private String description;
    private String guid;

    @Override
    protected int compareTo(Department other) {
        return 0;
    }
}
