package com.quantchi.common.basicmodel;

import com.quantchi.common.CRC64;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suntr
 * @since dmp1.6.0
 * @param <T> 当前节点的数据类型
 *
 */
public class ModelElement<T extends Serializable> implements Serializable {
  private static final long serialVersionUID = -4682118787413913155L;
  protected static final int MURMUR_HASH_SEED = 0xCDCD5571;

  private Integer level;

  //由children version算出来的hash值，用于上层数据快速比对子节点是否变更
  //叶子节点的version由关键属性计算得出
  private String version;

  //每层的数据
  private T element;

  private ModelElement<Serializable> parent;              //父节点
  private List<ModelElement<Serializable>> children;      //子孙节点

  public ModelElement(Integer level) {
    this.level = level;
  }

  public ModelElement(T element) {
    this.element = element;
  }

  public ModelElement(Integer level, T element) {
    this.level = level;
    this.element = element;
  }

  public Integer getLevel() {
    return level;
  }

  protected void setLevel(Integer level) {
    this.level = level;
  }

  public String getVersion() {
    if (this.version!=null){
      return version;
    }
    if (this.children!=null && this.children.size()>0){
      String subVersionJoin = children.stream().map(ModelElement::getVersion).sorted().collect(Collectors.joining(ModelDecorator.split));
      this.version = String.valueOf(CRC64.fromBytes(subVersionJoin.getBytes()).getValue());
      return version;
    }
    this.version = "";
    return this.version;
  }

  protected void setVersion(String version) {
    this.version = version;
  }

  public T getElement() {
    return element;
  }

  protected void setElement(T element) {
    this.element = element;
  }

  public ModelElement<Serializable> getParent() {
    return parent;
  }

  protected void setParent(ModelElement<Serializable> parent) {
    this.parent = parent;
  }

  public List<ModelElement<Serializable>> getChildren() {
    return children;
  }

  protected void setChildren(List<ModelElement<Serializable>> children) {
    this.children = children;
  }

  protected void addChild(ModelElement<Serializable> child){
    if (this.children == null){
      this.children = new LinkedList<>();
    }
    this.children.add(child);
  }

  protected void addChildren(List<ModelElement<Serializable>> children){
    if (this.children == null || this.children.size() == 0){
      this.children = children;
    } else {
      this.children.addAll(children);
    }
  }

}