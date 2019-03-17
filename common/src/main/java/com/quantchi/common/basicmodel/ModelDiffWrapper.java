package com.quantchi.common.basicmodel;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author suntr
 * @version dmp1.6.0
 * 整个模型的变更信息包装类
 */
@Data
public class ModelDiffWrapper implements Serializable {

  private static final long serialVersionUID = -3169215895599226744L;

  public enum State{
    CREATED, UPDATED, DELETED, REMAIN
  }

  private Map<Integer, LevelFunction<Serializable, Comparable>> functionMap;

  private List<ModelDiffElement<Serializable>> elementDiff;

  private List<ModelDiffEdge> edgeDiff;

}
