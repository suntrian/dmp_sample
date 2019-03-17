package com.quantchi.common.basicmodel;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author suntr
 * @version dmp1.6.0
 * 用于保存边的变更信息
 */
@Data
@NoArgsConstructor
public class ModelDiffEdge {

  private int level;

  //边的变更状态无修改，只有增删
  private List<ModelEdge> created;
  private List<ModelEdge> deleted;

  public ModelDiffEdge(int level, List<ModelEdge> created, List<ModelEdge> deleted) {
    this.level = level;
    this.created = created;
    this.deleted = deleted;
  }
}
