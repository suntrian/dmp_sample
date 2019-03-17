package com.quantchi.common.basicmodel;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author suntr
 * @since dmp1.6.0
 * @date 20190128
 *
 * 实现一序列连接变更的合并，以及基于某一状态的模型根据变更数据进行回滚与演进
 */
public class ModelDiffTracer {

  /**
   * 合并一系列diff,
   * @param diffWrappers 从小到大按时间先后排序
   * @return 合并后的从list[0]到list[length-1]的变化
   */
  public static ModelDiffWrapper accumulate(List<ModelDiffWrapper> diffWrappers){
    assert diffWrappers.size()>0;
    if (diffWrappers.size() == 1){
      return diffWrappers.get(0);
    }
    List<Map.Entry<Integer, LevelFunction<Serializable, Comparable>>> levelFunctionEntry = diffWrappers.get(0).getFunctionMap().entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).collect(Collectors.toList());
    List<ModelDiffElement<Serializable>> diffElements = mergeDiffElement(diffWrappers.stream().map(ModelDiffWrapper::getElementDiff).collect(Collectors.toList()), 0, levelFunctionEntry);
    List<ModelDiffEdge> diffEdges = mergeDiffEdge(diffWrappers.stream().map(ModelDiffWrapper::getEdgeDiff).collect(Collectors.toList()));
    ModelDiffWrapper wrapper = new ModelDiffWrapper();
    wrapper.setFunctionMap(diffWrappers.get(0).getFunctionMap());
    wrapper.setElementDiff(diffElements);
    wrapper.setEdgeDiff(diffEdges);
    return wrapper;
  }

  /**
   * 实现模型的回滚，
   *
   * @param current  回滚基于的版本， 会直接在current上回滚到之前的版本
   * @param diffWrappers 版本之前的变更
   * @return 变更前的模型
   */
  public static ModelDecorator rollback(ModelDecorator current, List<ModelDiffWrapper> diffWrappers){
    ModelDiffWrapper diffWrapper = accumulate(diffWrappers);
    List<Map.Entry<Integer, LevelFunction<Serializable, Comparable>>> levelFunctionEntry = current.getFunctionMap().entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).collect(Collectors.toList());
    List<ModelElement<Serializable>> elements = traceElements(true, current.getElements(), diffWrapper.getElementDiff(), 0, levelFunctionEntry);
    current.setElements(elements);
    //todo current.setEdges();
    return current;
  }

  /**
   * 实现模型的演进
   * @param current 演进基于的版本，会改变current模型到新的版本
   * @param diffWrappers 版本后续的变更
   * @return 变更后的模型
   */
  public static ModelDecorator evolution(ModelDecorator current, List<ModelDiffWrapper> diffWrappers){
    ModelDiffWrapper diffWrapper = accumulate(diffWrappers);
    List<Map.Entry<Integer, LevelFunction<Serializable, Comparable>>> levelFunctionEntry = current.getFunctionMap().entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey)).collect(Collectors.toList());
    List<ModelElement<Serializable>> elements = traceElements(false, current.getElements(), diffWrapper.getElementDiff(), 0, levelFunctionEntry);
    current.setElements(elements);
    //todo current.setEdges();
    return current;
  }

  private static<C extends Comparable> List<ModelElement<Serializable>> traceElements(final boolean isBackward , List<ModelElement<Serializable>> originalElementList, List<ModelDiffElement<Serializable>> diffList, final int level, final List<Map.Entry<Integer, LevelFunction<Serializable, C>>> functionEntry){
    Function<Serializable, C> keyGetter = functionEntry.get(level).getValue().getKeyGetter();
    Map<ModelDiffWrapper.State, List<ModelDiffElement<Serializable>>> elementMap = diffList.stream().collect(Collectors.groupingBy(ModelDiffElement::getState));
    List<ModelDiffElement<Serializable>> deletedElements = elementMap.getOrDefault(ModelDiffWrapper.State.DELETED, Collections.emptyList());
    List<ModelDiffElement<Serializable>> createdElements = elementMap.getOrDefault(ModelDiffWrapper.State.CREATED, Collections.emptyList());
    Set<C> deletedElementKeySet = findKeySet(deletedElements, keyGetter);
    Set<C> createdElementKeySet = findKeySet(createdElements, keyGetter);
    if (isBackward){
      originalElementList.addAll(parseDiffElement2ModelElement(deletedElements, level, isBackward));
      originalElementList.removeIf(element -> createdElementKeySet.contains(keyGetter.apply(element.getElement())));
    } else {
      originalElementList.addAll(parseDiffElement2ModelElement(createdElements, level, isBackward));
      originalElementList.removeIf(element -> deletedElementKeySet.contains(keyGetter.apply(element.getElement())));
    }

    List<ModelDiffElement<Serializable>> updatedElements = elementMap.getOrDefault(ModelDiffWrapper.State.UPDATED, Collections.emptyList());
    List<ModelDiffElement<Serializable>> remainElements = elementMap.getOrDefault(ModelDiffWrapper.State.REMAIN, Collections.emptyList());
    List<ModelDiffElement<Serializable>> modifiedElements = new ArrayList<>(updatedElements.size());
    modifiedElements.addAll(remainElements);
    Set<C> modifiedKeySet = findKeySet(modifiedElements, keyGetter);

    for (ModelElement<Serializable> element: originalElementList){
      C key = keyGetter.apply(element.getElement());
      if (modifiedKeySet.contains(key)){
        ModelDiffElement<Serializable> diffElement = modifiedElements.stream().filter(i->keyGetter.apply(isBackward?i.getOriginal():i.getCurrent()).compareTo(key)==0).findFirst().orElse(new ModelDiffElement<>());
        element.setElement(isBackward?diffElement.getOriginal():diffElement.getCurrent());
        if (element.getChildren()!=null && diffElement.getCurrent()!=null){
          List<ModelElement<Serializable>> childrenElements = traceElements(isBackward, element.getChildren(), diffElement.getChildren(), level+1, functionEntry);
          element.setChildren(childrenElements);
        }
      }

    }
    return originalElementList;
  }

  /**
   * 决定使用哪个值
   * @param diffElement  变更的节点
   * @param level 设置对象的层级
   * @param isBackward 回退还是前溯
   * @param <T> 对象
   * @return
   */
  private static <T extends Serializable>  ModelElement<T>  parseDiffElement2ModelElement(ModelDiffElement<T> diffElement, final int level, final boolean isBackward){
    ModelElement<T> element = new ModelElement<>(level);
    switch (diffElement.getState()){
      case CREATED:
        element.setElement(diffElement.getCurrent());
        break;
      case DELETED:
        element.setElement(diffElement.getOriginal());
        break;
      case UPDATED:
      case REMAIN:
        element.setElement(isBackward?diffElement.getOriginal():diffElement.getCurrent());
        break;
      default:
    }
    if (diffElement.getChildren()!=null && diffElement.getChildren().size()>0){
      for (ModelDiffElement<Serializable> childDiff: diffElement.getChildren()){
        ModelElement<Serializable> childElement = parseDiffElement2ModelElement(childDiff, level+1, isBackward);
        element.addChild(childElement);
      }
    }
    return element;
  }

  private static <T extends Serializable> List<ModelElement<T>> parseDiffElement2ModelElement(List<ModelDiffElement<T>> diffElements, final int level, final boolean isBackward){
    if (diffElements == null || diffElements.size() == 0){return  Collections.emptyList();}
    return diffElements.stream().map(i->parseDiffElement2ModelElement(i, level, isBackward)).collect(Collectors.toList());
  }

  private static <C extends Comparable> List<ModelDiffElement<Serializable>> mergeDiffElement(List<List<ModelDiffElement<Serializable>>> diffElements, int level, List<Map.Entry<Integer, LevelFunction<Serializable, C>>> functionEntry){
    if (level>=functionEntry.size()){return Collections.emptyList();}
    final Function<Serializable, C> keyGetter = functionEntry.get(level).getValue().getKeyGetter();
    List<ModelDiffElement<Serializable>> firstVersion = new ArrayList<>(diffElements.get(0));
    Set<C> firstVersionKeySet = findKeySet(firstVersion, keyGetter);
    for (int i = 1; i < diffElements.size(); i++){
      Set<C> thisVersionKeySet = findKeySet(diffElements.get(i), keyGetter);
      thisVersionKeySet.retainAll(firstVersionKeySet);
      if (thisVersionKeySet.size()==0){
        //不存在KEY相同元素，直接合并差异
        firstVersion.addAll(diffElements.get(i));
      } else {
        for (ModelDiffElement<Serializable> item: diffElements.get(i)){
          C key = getKey(item, keyGetter);
          if (!thisVersionKeySet.contains(key)){
            firstVersion.add(item);
            firstVersionKeySet.add(key);
          } else {
            //合并差异部分
            ModelDiffElement<Serializable> firstElement = firstVersion.stream().filter(f-> Objects.requireNonNull(getKey(f, keyGetter)).compareTo(key) == 0).findFirst().orElse(null);
            if (firstElement==null){ continue; }
            switch (firstElement.getState()){
              case REMAIN:
                //原来不变的情况，则全都以新状态覆盖
                firstElement.setState(item.getState());
                switch (item.getState()){
                  case DELETED:
                    firstElement.setCurrent(null);
                    firstElement.setChildren(item.getChildren());
                    break;
                  case UPDATED:
                  case REMAIN:
                    firstElement.setCurrent(item.getCurrent());
                    firstElement.setChildren(mergeDiffElement(Arrays.asList(firstElement.getChildren(), item.getChildren()), level+1, functionEntry));
                    break;
                  case CREATED:
                    default:
                    //不应该出现此情况
                    break;
                }
                break;
              case DELETED:
                switch (item.getState()){
                  case CREATED:
                    //先删后再增，作为更新处理
                    firstElement.setState(ModelDiffWrapper.State.UPDATED);
                    firstElement.setCurrent(item.getCurrent());
                    firstElement.setChildren(mergeDiffElement(Arrays.asList(firstElement.getChildren(), item.getChildren()), level+1, functionEntry));
                    break;
                  case DELETED:
                  case UPDATED:
                  case REMAIN:
                    //连接的版本变化不应该出来这些情况，暂不考虑
                    default:
                      break;
                }
                break;
              case CREATED:
                switch (item.getState()){
                  case DELETED:
                    //先增后删的情况，直接删除此元素
                    firstVersionKeySet.remove(getKey(firstElement, keyGetter));
                    firstVersion.remove(firstElement);
                    break;
                  case UPDATED:
                  case REMAIN:
                    firstElement.setState(ModelDiffWrapper.State.CREATED);
                    firstElement.setCurrent(item.getCurrent());
                    firstElement.setChildren(mergeDiffElement(Arrays.asList(firstElement.getChildren(), item.getChildren()), level+1, functionEntry));
                    break;
                  case CREATED:
                  default:
                    //不应该出现此情况
                    break;
                }
                break;
              case UPDATED:
                switch (item.getState()){
                  case DELETED:
                    //先更新，再删除的情况，作为删除
                    firstElement.setState(ModelDiffWrapper.State.DELETED);
                    firstElement.setCurrent(null);
                    firstElement.setChildren(item.getChildren());
                    break;
                  case UPDATED:
                  case REMAIN:
                    firstElement.setCurrent(item.getCurrent());
                    firstElement.setChildren(mergeDiffElement(Arrays.asList(firstElement.getChildren(), item.getChildren()), level+1, functionEntry));
                    break;
                  case CREATED:
                  default:
                    //不应出现此情况
                    break;
                }
            }
          }
        }
      }
    }
    return firstVersion;
  }

  private static  <T extends Serializable, C extends Comparable> Set<C> findKeySet(List<ModelDiffElement<T>> list, Function<T, C> keyGetter){
    if (list==null || list.size() == 0) {return Collections.emptySet();}
    return list.stream().map(i-> getKey(i, keyGetter)).collect(Collectors.toSet());
  }

  private static <T extends Serializable, C extends Comparable> C getKey(ModelDiffElement<T> item, Function<T, C> keyGetter){
    switch (item.getState()){
      case CREATED:
      case UPDATED:
        return keyGetter.apply(item.getCurrent());
      case DELETED:
      case REMAIN:
        return keyGetter.apply(item.getOriginal());
      default:
        return null;
    }
  }

  private static Function<ModelEdge, String> edgeHash = (edge)->edge.getSource().toString() + ModelDecorator.split + edge.getTarget().toString() + ModelDecorator.split + edge.getExtra().toString();

  private static List<ModelDiffEdge> mergeDiffEdge(List<List<ModelDiffEdge>> diffEdgesList){
    if (diffEdgesList==null || diffEdgesList.size()==0) {return Collections.emptyList();}
    if (diffEdgesList.size()==1){ return diffEdgesList.get(0); }
    List<ModelDiffEdge> firstDiffEdgeList = diffEdgesList.get(0);
    List<Integer> firstLevels = firstDiffEdgeList.stream().map(ModelDiffEdge::getLevel).collect(Collectors.toList());
    for (int i = 1; i < diffEdgesList.size(); i++){
      List<Integer> levels = diffEdgesList.get(i).stream().map(ModelDiffEdge::getLevel).collect(Collectors.toList());
      levels.retainAll(firstLevels);
      for (ModelDiffEdge diffEdge: diffEdgesList.get(i)){
        if (!levels.contains(diffEdge.getLevel())){
          //不存在此层级的边关系xx
          firstDiffEdgeList.add(diffEdge);
        } else {
          //存在此此层边关系交集
          for (Integer level: levels){
            ModelDiffEdge firstDiffEdge = firstDiffEdgeList.stream().filter(f->f.getLevel()==level).findFirst().get();
            List<String> createdDiffEdgeHash = diffEdge.getCreated().stream().map(edgeHash).collect(Collectors.toList());
            List<String> deletedDiffEdgeHash = diffEdge.getDeleted().stream().map(edgeHash).collect(Collectors.toList());

            firstDiffEdge.getCreated().removeIf(c->createdDiffEdgeHash.contains(edgeHash.apply(c)));
            diffEdge.getCreated().removeIf(c->firstDiffEdge.getDeleted().stream().map(edgeHash).anyMatch(e->e.equals(edgeHash.apply(c))));
            firstDiffEdge.getCreated().addAll(diffEdge.getCreated());

            firstDiffEdge.getDeleted().removeIf(c->deletedDiffEdgeHash.contains(edgeHash.apply(c)));
            diffEdge.getDeleted().removeIf(c->firstDiffEdge.getCreated().stream().map(edgeHash).anyMatch(e->e.equals(edgeHash.apply(c))));
            firstDiffEdge.getDeleted().addAll(diffEdge.getDeleted());
          }
        }
      }
    }
    return firstDiffEdgeList;
  }

}
