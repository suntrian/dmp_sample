package com.quantchi.common.basicmodel;


import lombok.Data;

import java.io.Serializable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.quantchi.common.basicmodel.ModelDiffWrapper.State.CREATED;
import static com.quantchi.common.basicmodel.ModelDiffWrapper.State.DELETED;

/**
 * @author suntr
 * @version dmp1.6.0
 *
 * 用于遍历{@link ModelDiffWrapper}对象
 */
@SuppressWarnings("unchecked")
public class ModelDiffVisitHelper {
  public enum Order{
    LRD("后序"),
    DLR("前序"),
    //非二叉树，无所谓中序
    //LDR("中序"),
    LEVEL("层次");

    private String desc;

    Order(String desc) {
      this.desc = desc;
    }
  }

  /**
   *  暂时没做边的reduce
   */
  @Data
  public static class ReduceResult {
    private int level;

    private Object created;
    private Object updated;
    private Object deleted;
    private Object remain;

    public ReduceResult(int level, Object created, Object updated, Object deleted, Object remain) {
      this.level = level;
      this.created = created;
      this.updated = updated;
      this.deleted = deleted;
      this.remain = remain;
    }
  }

  private int initialSize = 4;

  private Order order = Order.DLR;

  private Map<Integer, Function<Serializable, Object>> createdElementFunction = new HashMap<>(initialSize);
  private Map<Integer, Function<Serializable, Object>> deletedElementFunction = new HashMap<>(initialSize);
  private Map<Integer, BiFunction<Serializable, Serializable, Object>> updatedElementFunction = new HashMap<>(initialSize);
  private Map<Integer, BiFunction<Serializable, Serializable, Object>> remainElementFunction = new HashMap<>(initialSize);

  private Map<Integer, Function<Collection<Serializable>, Object>> createdElementBatchFunction = new HashMap<>(initialSize);
  private Map<Integer, Function<Collection<Serializable>, Object>> deletedElementBatchFunction = new HashMap<>(initialSize);
  private Map<Integer, BiFunction<Collection<Serializable>, Collection<Serializable>, Object>> updatedElementBatchFunction = new HashMap<>(initialSize);
  private Map<Integer, BiFunction<Collection<Serializable>, Collection<Serializable>, Object>> remainElementBatchFunction = new HashMap<>(initialSize);

  private Map<Integer, TriFunction<Serializable, Serializable, Serializable, Object>> createdEdgeFunction = new HashMap<>(initialSize);
  private Map<Integer, TriFunction<Serializable, Serializable, Serializable, Object>> deletedEdgeFunction = new HashMap<>(initialSize);

  private Map<Integer, BiConsumer<ModelDiffElement<Serializable>, ModelDiffElement<Serializable>>> createdParentConsumer = new HashMap<>(initialSize);
  private Map<Integer, BiConsumer<ModelDiffElement<Serializable>, ModelDiffElement<Serializable>>> deletedParentConsumer = new HashMap<>(initialSize);
  private Map<Integer, BiConsumer<ModelDiffElement<Serializable>, ModelDiffElement<Serializable>>> updatedParentConsumer = new HashMap<>(initialSize);
  private Map<Integer, BiConsumer<ModelDiffElement<Serializable>, ModelDiffElement<Serializable>>> remainParentConsumer = new HashMap<>(initialSize);

  private Map<Integer, BiFunction<Object, Object, Object>> createdReduce = new HashMap<>(initialSize);
  private Map<Integer, BiFunction<Object, Object, Object>> deletedReduce = new HashMap<>(initialSize);
  private Map<Integer, BiFunction<Object, Object, Object>> updatedReduce = new HashMap<>(initialSize);
  private Map<Integer, BiFunction<Object, Object, Object>> remainReduce =  new HashMap<>(initialSize);
  private Map<Integer, Object> createdDefaultReduceValue = new HashMap<>(initialSize);
  private Map<Integer, Object> updatedDefaultReduceValue = new HashMap<>(initialSize);
  private Map<Integer, Object> deletedDefaultReduceValue = new HashMap<>(initialSize);
  private Map<Integer, Object> remainDefaultReduceValue = new HashMap<>(initialSize);

  private Map<Integer, BiFunction<Object, Object, Object>> createdEdgeReduce = new HashMap<>(initialSize);
  private Map<Integer, BiFunction<Object, Object, Object>> deletedEdgeReduce = new HashMap<>(initialSize);

  private Map<Integer, ReduceResult> reduceResult = new HashMap<>(initialSize);

  private List<Integer> elementLevels0Based;
  private List<Integer> edgeLevels0Based;

  public Map<Integer, ReduceResult> visit(ModelDiffWrapper wrapper){
    if (checkElementHandler()){
      elementLevels0Based = wrapper.getFunctionMap().keySet().stream().sorted().collect(Collectors.toList());
      ReduceResult rootResult = visitElements(wrapper.getElementDiff(), 0);
      if (rootResult!=null){
        reduceResult.put(elementLevels0Based.get(0), rootResult);
      }
    }
    if (checkEdgeHandler()){
      edgeLevels0Based = checkEdgeLevel();
      visitEdges(wrapper.getEdgeDiff());
    }
    if (reduceResult==null){
      reduceResult = new HashMap<>(elementLevels0Based.size());
    }
    List<Integer> levels = Stream.of(createdDefaultReduceValue.keySet(), updatedDefaultReduceValue.keySet(), deletedDefaultReduceValue.keySet(), remainDefaultReduceValue.keySet())
            .flatMap(Collection::stream).distinct().sorted().collect(Collectors.toList());
    for (Integer lv: levels){
      reduceResult.putIfAbsent(lv, new ReduceResult(lv,
              createdDefaultReduceValue.getOrDefault(lv, null),
              updatedDefaultReduceValue.getOrDefault(lv,null),
              deletedDefaultReduceValue.getOrDefault(lv,null),
              remainDefaultReduceValue.getOrDefault(lv,null) ));
    }
    return reduceResult;
  }

  //前序遍历树
  private ReduceResult visitElements(List<ModelDiffElement<Serializable>> diffElements, int level){
    if (diffElements == null || diffElements.size() == 0){
      return null;
    }
    List<Serializable> createdList = new LinkedList<>(),
            deletedList = new LinkedList<>(),
            updatedOriginalList = new LinkedList<>(),
            updatedCurrentList = new LinkedList<>(),
            remainOriginalList = new LinkedList<>(),
            remainCurrentList = new LinkedList<>();

    List<Object> createdResult = new LinkedList<>(),
            updatedResult = new LinkedList<>(),
            deletedResult = new LinkedList<>(),
            remainResult = new LinkedList<>();

    List<ReduceResult> childrenReduceResult = new LinkedList<>();

    for (ModelDiffElement<Serializable> e: diffElements){
      if (Order.LRD.equals(order)){
        childrenReduceResult.add(visitElements(e.getChildren(), level+1));
      }
      switch (e.getState()){
        case CREATED:
          if (elementLevels0Based.size()>0 && this.createdParentConsumer.get(elementLevels0Based.get(level))!=null){
            this.createdParentConsumer.get(elementLevels0Based.get(level)).accept(e.getParent(), e);
          }
          if (elementLevels0Based.size()>0 && this.createdElementFunction.get(elementLevels0Based.get(level))!=null){
            createdResult.add(this.createdElementFunction.get(elementLevels0Based.get(level)).apply(e.getCurrent()));
          }
          createdList.add(e.getCurrent());
          break;
        case DELETED:
          if (elementLevels0Based.size()>level && this.deletedParentConsumer.get(elementLevels0Based.get(level))!=null){
            this.deletedParentConsumer.get(elementLevels0Based.get(level)).accept(e.getParent(), e);
          }
          if (elementLevels0Based.size()>level && this.deletedElementFunction.get(elementLevels0Based.get(level))!=null){
            deletedResult.add(this.deletedElementFunction.get(elementLevels0Based.get(level)).apply(e.getOriginal()));
          }
          deletedList.add(e.getOriginal());
          break;
        case REMAIN:
          if (elementLevels0Based.size()>level && this.remainParentConsumer.get(elementLevels0Based.get(level))!=null){
            this.remainParentConsumer.get(elementLevels0Based.get(level)).accept(e.getParent(), e);
          }
          if (elementLevels0Based.size()>level && this.remainElementFunction.get(elementLevels0Based.get(level))!=null){
            remainResult.add(this.remainElementFunction.get(elementLevels0Based.get(level)).apply(e.getOriginal(), e.getCurrent()));
          }
          remainOriginalList.add(e.getOriginal());
          remainCurrentList.add(e.getCurrent());
          break;
        case UPDATED:
          if (elementLevels0Based.size()>level && this.updatedParentConsumer.get(elementLevels0Based.get(level))!=null){
            this.updatedParentConsumer.get(elementLevels0Based.get(level)).accept(e.getParent(), e);
          }
          if (elementLevels0Based.size()>level && this.updatedElementFunction.get(elementLevels0Based.get(level))!=null){
            updatedResult.add(this.updatedElementFunction.get(elementLevels0Based.get(level)).apply(e.getOriginal(), e.getCurrent()));
          }
          updatedOriginalList.add(e.getOriginal());
          updatedCurrentList.add(e.getCurrent());
          break;
      }
    };
    if (elementLevels0Based.size()>level && this.createdElementBatchFunction.get(elementLevels0Based.get(level))!=null && createdList.size()>0){
      createdResult.add(this.createdElementBatchFunction.get(elementLevels0Based.get(level)).apply(createdList));
    }
    if (elementLevels0Based.size()>level && this.deletedElementBatchFunction.get(elementLevels0Based.get(level))!=null && deletedList.size()>0){
      deletedResult.add(this.deletedElementBatchFunction.get(elementLevels0Based.get(level)).apply(deletedList));
    }
    if (elementLevels0Based.size()>level && this.updatedElementBatchFunction.get(elementLevels0Based.get(level))!=null && updatedCurrentList.size()>0){
      updatedResult.add(this.updatedElementBatchFunction.get(elementLevels0Based.get(level)).apply(updatedOriginalList, updatedCurrentList));
    }
    if (elementLevels0Based.size()>level && this.remainElementBatchFunction.get(elementLevels0Based.get(level))!=null && remainCurrentList.size()>0){
      remainResult.add(this.remainElementBatchFunction.get(elementLevels0Based.get(level)).apply(remainOriginalList, remainCurrentList));
    }
    if (Order.DLR.equals(order)){
      for (ModelDiffElement<Serializable> i : diffElements) {
        childrenReduceResult.add(visitElements(i.getChildren(), level +1 ));
      }
    } else if (Order.LEVEL.equals(order)){
      childrenReduceResult.add(
          visitElements(diffElements.stream()
              .map(ModelDiffElement::getChildren)
              .filter(i->i!=null&&i.size()>0)
              .flatMap(Collection::stream)
              .collect(Collectors.toList()),
                  level + 1)
      );
    }
    childrenReduceResult.stream().filter(Objects::nonNull).reduce((ReduceResult a, ReduceResult b) -> mergeReduceResult(a, b, level)).ifPresent(childResult -> {
      if(!this.reduceResult.containsKey(elementLevels0Based.get(level+1))){
        this.reduceResult.put(elementLevels0Based.get(level + 1), childResult);
      } else {
        ReduceResult formerResult = reduceResult.get(elementLevels0Based.get(level+1));
        mergeReduceResult(formerResult, childResult, level);
      }
    });
    return new ReduceResult(elementLevels0Based.get(level),
            createdReduce.containsKey(elementLevels0Based.get(level))?createdResult.stream().reduce((a, b)->createdReduce.get(elementLevels0Based.get(level)).apply(a,b)).orElse(createdDefaultReduceValue.get(elementLevels0Based.get(level))):null,
            updatedReduce.containsKey(elementLevels0Based.get(level))?updatedResult.stream().reduce((a, b)->updatedReduce.get(elementLevels0Based.get(level)).apply(a,b)).orElse(updatedDefaultReduceValue.get(elementLevels0Based.get(level))):null,
            deletedReduce.containsKey(elementLevels0Based.get(level))?deletedResult.stream().reduce((a, b)->deletedReduce.get(elementLevels0Based.get(level)).apply(a,b)).orElse(deletedDefaultReduceValue.get(elementLevels0Based.get(level))):null,
            remainReduce.containsKey(elementLevels0Based.get(level))?remainResult.stream().reduce((a, b)->remainReduce.get(elementLevels0Based.get(level)).apply(a,b)).orElse(remainDefaultReduceValue.get(elementLevels0Based.get(level))):null
            );
  }

  private void visitEdges(List<ModelDiffEdge> diffEdges){
    for (ModelDiffEdge diffEdge: diffEdges) {
      if (this.createdEdgeFunction.get(edgeLevels0Based.get(diffEdge.getLevel())) != null) {
        for (ModelEdge edge : diffEdge.getCreated()) {
          this.createdEdgeFunction.get(edgeLevels0Based.get(diffEdge.getLevel())).apply(edge.getSource(), edge.getTarget(), edge.getExtra());
        }
      }
      if (this.deletedEdgeFunction.get(edgeLevels0Based.get(diffEdge.getLevel())) != null){
        for (ModelEdge edge : diffEdge.getDeleted()) {
          this.createdEdgeFunction.get(edgeLevels0Based.get(diffEdge.getLevel())).apply(edge.getSource(), edge.getTarget(), edge.getExtra());
        }
      }
    }
  }

  private ReduceResult mergeReduceResult(ReduceResult a, ReduceResult b, int level){
    a.setCreated(createdReduce.containsKey(elementLevels0Based.get(level + 1)) ? createdReduce.get(elementLevels0Based.get(level+1)).apply(a.getCreated(), b.getCreated()) : null );
    a.setUpdated(updatedReduce.containsKey(elementLevels0Based.get(level + 1)) ? updatedReduce.get(elementLevels0Based.get(level+1)).apply(a.getUpdated(), b.getUpdated()) : null );
    a.setDeleted(deletedReduce.containsKey(elementLevels0Based.get(level + 1)) ? deletedReduce.get(elementLevels0Based.get(level+1)).apply(a.getDeleted(), b.getDeleted()) : null );
    a.setRemain(remainReduce.containsKey(elementLevels0Based.get(level + 1)) ?    remainReduce.get(elementLevels0Based.get(level+1)).apply(a.getRemain(), b.getRemain()) : null );
    return a;
  }

  private boolean checkElementHandler(){
    return this.createdElementFunction.size()
            +this.deletedElementFunction.size()
            +this.updatedElementFunction.size()
            +this.remainElementFunction.size()
            +this.createdElementBatchFunction.size()
            +this.deletedElementBatchFunction.size()
            +this.updatedElementBatchFunction.size()
            +this.remainElementBatchFunction.size()
            > 0;
  }

  private boolean checkEdgeHandler(){
    return this.createdEdgeFunction.size() + this.deletedEdgeFunction.size() > 0;
  }

  private List<Integer> checkEdgeLevel(){
    Set<Integer> list = new HashSet<>(this.createdEdgeFunction.size() + this.deletedEdgeFunction.size());
    list.addAll(this.createdEdgeFunction.keySet());
    list.addAll(this.deletedEdgeFunction.keySet());
    return list.stream().sorted().collect(Collectors.toList());
  }

  public ModelDiffVisitHelper setVisitOrder(Order order){
    this.order = order;
    return this;
  }

  public<T extends Serializable, R> ModelDiffVisitHelper setCreatedHandler(final int level, Function<T, R> handler){
    setElementHandler(level, CREATED, handler);
    return this;
  }

  public<T extends Serializable, R> ModelDiffVisitHelper setDeletedHandler(final int level, Function<T, R> handler){
    setElementHandler(level, ModelDiffWrapper.State.DELETED, handler);
    return this;
  }

  public<T extends Serializable, R> ModelDiffVisitHelper setUpdatedHandler(final int level, BiFunction<T, T, R> handler){
    setElementHandler(level, ModelDiffWrapper.State.UPDATED, handler);
    return this;
  }

  public<T extends Serializable, R> ModelDiffVisitHelper setUpdatedHandler(final int level, Function<T, R> handler){
    setElementHandler(level, ModelDiffWrapper.State.UPDATED, handler);
    return this;
  }

  public<T extends Serializable, R> ModelDiffVisitHelper setRemainedHandler(final int level, BiFunction<T, T, R> handler){
    setElementHandler(level, ModelDiffWrapper.State.REMAIN, handler);
    return this;
  }

  public<T extends Serializable, R> ModelDiffVisitHelper setRemainedHandler(final int level, Function<T, R> handler){
    setElementHandler(level, ModelDiffWrapper.State.REMAIN, handler);
    return this;
  }

  public<U extends Serializable, V extends Serializable> ModelDiffVisitHelper setCreatedParentConsumer(final int level, BiConsumer<U, V> consumer, boolean parentUseOriginalOrCurrent){
    this.createdParentConsumer.put(level, (ModelDiffElement<Serializable> parent, ModelDiffElement<Serializable> child)
            -> consumer.accept((U)(CREATED.equals(parent.getState())?parent.getCurrent():parentUseOriginalOrCurrent?parent.getOriginal():parent.getCurrent()), ((V)(child.getCurrent()))));
    return this;
  }
  public<U extends Serializable, V extends Serializable> ModelDiffVisitHelper setDeletedParentConsumer(final int level, BiConsumer<U, V> consumer, boolean parentUseOriginalOrCurrent){
    this.deletedParentConsumer.put(level, (ModelDiffElement<Serializable> parent, ModelDiffElement<Serializable> child)
            -> consumer.accept((U)(DELETED.equals(parent.getState())?parent.getOriginal():parentUseOriginalOrCurrent?parent.getOriginal():parent.getCurrent()), ((V)(child.getOriginal()))));
    return this;
  }
  public<U extends Serializable, V extends Serializable> ModelDiffVisitHelper setUpdatedParentConsumer(final int level, BiConsumer<U, V> consumer, boolean parentUseOriginalOrCurrent, boolean childUseOriginalOrCurrent){
    this.updatedParentConsumer.put(level, (ModelDiffElement<Serializable> parent, ModelDiffElement<Serializable> child)
            -> consumer.accept((U)(parentUseOriginalOrCurrent?parent.getOriginal():parent.getCurrent()), ((V)(childUseOriginalOrCurrent?child.getOriginal():child.getCurrent()))));
    return this;
  }
  public<U extends Serializable, V extends Serializable> ModelDiffVisitHelper setRemainParentConsumer(final int level, BiConsumer<U, V> consumer, boolean parentUseOriginalOrCurrent, boolean childUseOriginalOrCurrent){
    this.remainParentConsumer.put(level, (ModelDiffElement<Serializable> parent, ModelDiffElement<Serializable> child)
            -> consumer.accept((U)(parentUseOriginalOrCurrent?parent.getOriginal():parent.getCurrent()), ((V)(childUseOriginalOrCurrent?child.getOriginal():child.getCurrent()))));
    return this;
  }

  public<T extends Serializable, C extends Collection<T>, R> ModelDiffVisitHelper setBatchCreatedHandler(final int level, Function<C, R> handler){
    this.createdElementBatchFunction.put(level, (Function<Collection<Serializable>, Object>) handler);
    return this;
  }

  public<T extends Serializable, C extends Collection<T>, R>  ModelDiffVisitHelper setBatchDeletedHandler(final int level, Function<C, R> handler){
    this.deletedElementBatchFunction.put(level, (Function<Collection<Serializable>, Object>) handler);
    return this;
  }

  public<T extends Serializable, C extends Collection<T>, R>  ModelDiffVisitHelper setBatchUpdatedHandler(final int level, BiFunction<C, C, R> handler){
    this.updatedElementBatchFunction.put(level, (BiFunction<Collection<Serializable>, Collection<Serializable>, Object>) handler);
    return this;
  }

  public<T extends Serializable, C extends Collection<T>, R>  ModelDiffVisitHelper setBatchRemainHandler(final int level, BiFunction<C, C, R> handler){
    this.remainElementBatchFunction.put(level, (BiFunction<Collection<Serializable>, Collection<Serializable>, Object>) handler);
    return this;
  }

  public<T> ModelDiffVisitHelper setCreatedReduce(final  int level, BiFunction<T, T, T> reduce, T defaultValue){
    setReduce(level, CREATED, reduce);
    this.createdDefaultReduceValue.put(level, defaultValue);
    return this;
  }

  public<T> ModelDiffVisitHelper setUpdatedReduce(final  int level, BiFunction<T, T, T> reduce, T defaultValue){
    setReduce(level, ModelDiffWrapper.State.UPDATED, reduce);
    this.updatedDefaultReduceValue.put(level, defaultValue);
    return this;
  }

  public<T> ModelDiffVisitHelper setDeletedReduce(final  int level, BiFunction<T, T, T> reduce, T defaultValue){
    setReduce(level, ModelDiffWrapper.State.DELETED, reduce);
    this.deletedDefaultReduceValue.put(level, defaultValue);
    return this;
  }

  public<T> ModelDiffVisitHelper setRemainReduce(final  int level, BiFunction<T, T, T> reduce, T defaultValue){
    setReduce(level, ModelDiffWrapper.State.REMAIN, reduce);
    this.remainDefaultReduceValue.put(level, defaultValue);
    return this;
  }


  private<T extends Serializable, R> void setElementHandler(final int level, ModelDiffWrapper.State state, Function<T, R> handler){
    switch (state){
      case CREATED:
        this.createdElementFunction.put(level, (Function<Serializable, Object>) handler);
        break;
      case DELETED:
        this.deletedElementFunction.put(level, (Function<Serializable, Object>) handler);
        break;
      case REMAIN:
        //只处理新的数据，即第二个数据
        this.remainElementFunction.put(level, (Serializable a, Serializable b)-> handler.apply((T) b) );
        break;
      case UPDATED:
        //只处理新的数据，即第二个数据
        this.updatedElementFunction.put(level, (Serializable a, Serializable b)-> handler.apply((T) b));
        break;
        default:
    }
  }

  private<T extends Serializable, R> void setElementHandler(final int level, ModelDiffWrapper.State state, BiFunction<T, T, R> handler){
    switch (state) {
      case CREATED:
        this.createdElementFunction.put(level, (Serializable s)->handler.apply(null, (T) s));
        break;
      case DELETED:
        this.deletedElementFunction.put(level, (Serializable s)->handler.apply((T) s, null));
        break;
      case UPDATED:
        this.updatedElementFunction.put(level, (BiFunction<Serializable, Serializable, Object>) handler);
        break;
      case REMAIN:
        this.remainElementFunction.put(level, (BiFunction<Serializable, Serializable, Object>) handler);
        break;
    }
  }

  private void setEdgeHandler(final int level, ModelDiffWrapper.State state, TriFunction<Serializable, Serializable, Serializable, Object> handler){
    switch (state){
      case CREATED:
        this.createdEdgeFunction.put(level, handler);
        break;
      case DELETED:
        this.deletedEdgeFunction.put(level, handler);
        break;
      case UPDATED:
      case REMAIN:
        default:
          //do nothing;
    }
  }

  private<T> void setReduce(final int level, ModelDiffWrapper.State state, BiFunction<T, T, T> reduce){
    switch (state){
      case CREATED:
        this.createdReduce.put(level, (BiFunction<Object, Object, Object>) reduce);
        break;
      case UPDATED:
        this.updatedReduce.put(level, (BiFunction<Object, Object, Object>) reduce);
        break;
      case REMAIN:
        this.remainReduce.put(level, (BiFunction<Object, Object, Object>) reduce);
        break;
      case DELETED:
        this.deletedReduce.put(level, (BiFunction<Object, Object, Object>) reduce);
        break;
        default:
    }
  }

}
