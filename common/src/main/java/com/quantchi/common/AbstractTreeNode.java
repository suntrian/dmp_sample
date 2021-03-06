package com.quantchi.common;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"all","cast","unchecked","rawtypes"})
public abstract class AbstractTreeNode<T extends AbstractTreeNode, ID extends Serializable> implements Serializable {

    private static final long serialVersionUID = -980028682514888277L;

    private static transient String split = "/";

    protected ID parentId;          // parentId id
    protected ID id;              // self id
    protected String path;
    protected Integer depth;        //树层级

    private transient T parentNode;

    protected List<T> children;

    public AbstractTreeNode() {
    }

    public AbstractTreeNode(ID id) {
        this.id = id;
    }

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    public ID getParentId() {
        return (this.parentId ==null&&this.parentNode==null)?null:(this.parentId !=null?this.parentId :(ID) this.parentNode.getId());
    }

    public void setParentId(ID parentId) {
        this.parentId = parentId;
    }

    public T getParentNode() {
        return parentNode;
    }

    public void setParentNode(T parentNode) {
        this.parentNode = parentNode;
    }

    public List<T> getChildren() {
        return children;
    }

    public void setChildren(List<T> children) {
        children.forEach(this::addChild);
    }

    protected String getSplit() {
        return split;
    }

    public static void setSplit(String split) {
        AbstractTreeNode.split = split;
    }

    public boolean isRoot(){
        return this.parentId == null;
    }

    public boolean isLeaf(){
        return this.children == null || this.children.size() == 0;
    }

    public void addChild(T child){
        if (child==null)return;
        child.setParentNode(this);
        if (this.children == null){
            this.children = new LinkedList<>();
            this.children.add(child);
            return;
        }
        this.children.add(insertPos(this.children, child), child);
    }

    public void addChild(T child, Integer pos){
        child.setParentNode(this);
        if (this.children == null) {
            this.children = new LinkedList<>();
        }
        if (pos<0 || pos >= this.children.size()) {
            throw new RuntimeException("insert position overflow");
        }
        this.children.add(pos, child);
    }

    public void addChildren(List<T> children) {
        children.forEach(this::addChild);
    }

    public String getPath() {
        T visitor = (T)this;
        StringBuilder pathBuilder = new StringBuilder();
        do {
            pathBuilder.insert(0, visitor.getId());
            if (visitor.getParentNode()!=null){
                pathBuilder.insert(0, visitor.getSplit());
            }
        }while ((visitor = (T)visitor.getParentNode())!=null);
        return pathBuilder.toString();
    }

    public Integer getDepth() {
      if (this.depth!=null){
        return this.depth;
      }
      if (this.isRoot()){
        return 1;
      }
      if (this.getParentNode()==null){
          return 1;
      }
      this.depth = this.getParentNode().getDepth()+1;
      return depth;
    }

    public static<T extends AbstractTreeNode, ID extends Serializable> List<T> buildTree(List<T> treeNodes){
        List<T> roots = new ArrayList<>();
        Map<ID, T> visited = new HashMap<>();
        Set<ID> nodeIdSet = treeNodes.stream().map(i->(ID)i.id).collect(Collectors.toSet());
        Set<ID> filteredIdSet = new HashSet<>();
        while (true) {
            Iterator<T> iterator = treeNodes.iterator();
            while (iterator.hasNext()){
                T node = iterator.next();
                if (!node.filter() || filteredIdSet.contains(node.parentId) ){
                    filteredIdSet.add((ID) node.id);
                    nodeIdSet.remove(node.id);
                    iterator.remove();
                    continue;
                }
                if (node.isRoot() || !nodeIdSet.contains((ID)node.parentId)){
                    //根节点
                    node.setParentId(null);
                    roots.add(insertPos(roots, node), node);
                    visited.put((ID)node.id, node);
                    iterator.remove();
                } else if (visited.keySet().contains((ID)node.parentId)){
                    visited.get((ID)node.parentId).addChild(node);
                    visited.put((ID)node.id, node);
                    iterator.remove();
                }
            }
            if (treeNodes.isEmpty()){
                break;
            }
        }
        return roots;
    }

    private static<T extends AbstractTreeNode> int insertPos(List<T> nodes, T node){
        if ( nodes == null || nodes.size() == 0){
            return 0;
        }
        int i = nodes.size();
        while (i>=1 && node.compare(nodes.get(i-1))<0){
            i--;
        }
        return i;
    }

    /**
     * return true if node included;
     * @return
     */
    protected boolean filter(){
        return true;
    }

    protected int compare(T node){
        return 0;
    }

}
