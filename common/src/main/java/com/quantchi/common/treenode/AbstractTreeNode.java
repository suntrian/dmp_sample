package com.quantchi.common.treenode;

import lombok.Data;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
@Data
public abstract class AbstractTreeNode<T extends AbstractTreeNode, PK extends Serializable> implements Serializable {

    private static final long serialVersionUID = -995902205512136646L;
    private static transient String pathSplit = "/";

    protected PK id;
    protected PK parentId;
    protected String path;
    protected transient T parentNode;
    protected List<T> children;

    public String getPath() {
        T visitor = (T)this;
        StringBuilder pathBuilder = new StringBuilder();
        do {
            pathBuilder.insert(0, visitor.getId());
            if (visitor.getParentNode()!=null){
                pathBuilder.insert(0, pathSplit);
            }
        }while ((visitor = (T)visitor.getParentNode())!=null);
        return pathBuilder.toString();
    }

    public void addChild(T child){
        child.setParentNode(this);
        if (this.children == null){
            this.children = new LinkedList<>();
            this.children.add(child);
            return;
        }
        this.children.add(child);
    }

    public void addChild(Integer pos,T child){
        child.setParentNode(this);
        if (this.children == null) {
            this.children = new LinkedList<>();
        }
        if (pos<0 || pos >= this.children.size()) {
            throw new RuntimeException("insert position overflow");
        }
        this.children.add(pos, child);
    }

    public static<T extends AbstractTreeNode, PK extends Serializable> List<T> buildTree(List<T> treeNodes){
        List<T> roots = new ArrayList<>();
        Map<PK, T> visited = new HashMap<>();
        Set<PK> nodeIdSet = treeNodes.stream().map(i->(PK)i.id).collect(Collectors.toSet());
        Set<PK> filteredIdSet = new HashSet<>();
        while (true) {
            Iterator<T> iterator = treeNodes.iterator();
            while (iterator.hasNext()){
                T node = iterator.next();
                if (node.isFiltered() || filteredIdSet.contains(node.parentId) ){
                    filteredIdSet.add((PK) node.id);
                    nodeIdSet.remove((PK) node.id);
                    iterator.remove();
                    continue;
                }
                if (node.isRoot() || !nodeIdSet.contains((PK) node.parentId)){
                    //根节点
                    node.setParentId(null);
                    roots.add(postition(roots, node), node);
                    visited.put((PK)node.id, node);
                    iterator.remove();
                } else if (visited.keySet().contains(node.parentId)){
                    int pos = postition(visited.get(node.getParentId()).getChildren(), node);
                    visited.get(node.parentId).addChild(pos,node);
                    visited.put((PK)node.id, node);
                    iterator.remove();
                }
            }
            if (treeNodes.isEmpty()){
                break;
            }
        }
        return roots;
    }

    protected boolean isFiltered(){
        return true;
    }

    public boolean isRoot(){
        return this.parentId == null;
    }

    public boolean isLeaf(){
        return this.children == null || this.children.size() == 0;
    }

    private static <T extends AbstractTreeNode> int postition(List<T> nodes, T node){
        if ( nodes == null || nodes.size() == 0){
            return -1;
        }
        int i = nodes.size();
        while (i>=1 && node.compareTo(nodes.get(i-1))<0){
            i--;
        }
        return i;
    }

    protected abstract int compareTo(T other);
}
