package com.quantchi.common;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Diff<T> implements Serializable {
  private static final long serialVersionUID = -8216244641262752799L;

  public static class StringDiff extends Diff<String> {
    private static final long serialVersionUID = 4533789952845108092L;

    public<C extends Collection> StringDiff diff(final C former, final C later){
      return diff(former, later, Comparator.naturalOrder());
    }
  }

  public static class IntDiff extends Diff<Integer> {
    private static final long serialVersionUID = -8962817947530621627L;

    public<C extends Collection> IntDiff diff(final C former, final C later){
      return diff(former, later, Comparator.naturalOrder());
    }
  }

  protected List<T> created = new LinkedList<>();
  protected List<T> deleted = new LinkedList<>();
  protected List<T[]> updated = new LinkedList<>();
  protected List<T> remain = new LinkedList<>();
  private boolean keepRemain = false;

  public <C extends Collection<T>, D extends Diff<T>> D diff(C formerCollection, C laterCollection, Comparator<T> comparator){
    return diff(formerCollection, laterCollection, comparator, comparator);
  }

  @SuppressWarnings("unchecked")
  public<C extends Collection<T>, D extends Diff<T>> D diff(C formerCollection, C laterCollection, Comparator<T> keyComparator, BiFunction<T, T, Boolean> isEqual){
    if (formerCollection == null || laterCollection == null){throw new IllegalArgumentException("Null Collection");}
    if (formerCollection.size() == 0){this.created.addAll(laterCollection); return (D) this; }
    if (laterCollection.size() == 0){this.deleted.addAll(formerCollection); return (D) this;}
    Iterator<T> formerIterator =  formerCollection.stream().parallel().sorted(keyComparator).collect(Collectors.toList()).iterator();
    Iterator<T> laterIterator =  laterCollection.stream().parallel().sorted(keyComparator).collect(Collectors.toList()).iterator();
    T former, later;
    FORMER: while (formerIterator.hasNext()){
      former = formerIterator.next();
      LATER:  while (laterIterator.hasNext()){
        later = laterIterator.next();
        int cmp = keyComparator.compare(former, later);
        if (cmp == 0){
          if (isEqual.apply(former, later)){
            if (keepRemain){
              this.remain.add(former);
            }
          } else {
            this.updated.add(arrayIt(former, later));
          }
          continue FORMER;
        } else if (cmp>0){
          this.created.add(later);
        } else {
          this.deleted.add(former);
          while (formerIterator.hasNext()){
            former = formerIterator.next();
            if ((cmp = keyComparator.compare(former, later))<0 ){
              this.deleted.add(former);
            } else if (cmp == 0){
              if (!isEqual.apply(former, later)){
                this.updated.add(arrayIt(former, later));
              } else {
                if (keepRemain){
                  this.remain.add(former);
                }
              }
              continue FORMER;
            } else {
              this.created.add(later);
              continue LATER;
            }
          }
          this.created.add(later);
          continue FORMER;
        }
      }
      this.deleted.add(former);
      while (formerIterator.hasNext()){
        this.deleted.add(formerIterator.next());
      }
    }
    while (laterIterator.hasNext()){
      this.created.add(laterIterator.next());
    }
    return (D) this;
  }


  @SuppressWarnings("unchecked")
  public<C extends Collection<T>, D extends Diff<T>> D diff(C formerCollection, C laterCollection, Comparator<T> keyComparator, Comparator<T> propertyComparator){
    return diff(formerCollection, laterCollection, keyComparator, (T a,T b)->propertyComparator.compare(a,b) == 0);
  }

  /**
   *
   * @param formerCollection
   * @param laterCollection
   * @param identity 用于唯一标识对象
   * @param keyProperty 用于判断对象是否变更， 当keyProperty等同于identity时，只能判断新增和删除
   * @param <C>
   * @param <M>
   * @param <D>
   * @return
   */
  @SuppressWarnings("unchecked")
  public<C extends Collection<T>, M extends Comparable<M>, D extends Diff<T>> D diff(final C formerCollection, final C laterCollection, final Function<T, M> identity, final Function<T, ? extends Comparable> keyProperty){
    return (D) diff(formerCollection, laterCollection, Comparator.comparing(identity), Comparator.comparing(keyProperty));
  }

  @SuppressWarnings("unchecked")
  public<C extends Collection<T>, M extends Comparable<M>, D extends Diff<T>> D diff(final C formerCollection, final C laterCollection, final Function<T, M> identity ){
    return (D) diff(formerCollection, laterCollection, Comparator.comparing(identity), Comparator.comparing(identity));
  }

  public List<T> getCreated() {
    return created;
  }

  public List<T> getDeleted() {
    return deleted;
  }

  public List<T[]> getUpdated() {
    return updated;
  }

  public List<T> getRemain() {
    return remain;
  }

  @SuppressWarnings("unchecked")
  private T[] arrayIt(T former, T later){
    T[] u = (T[]) Array.newInstance(former.getClass(), 2);
    u[0] = former;
    u[1] = later;
    return u;
  }
}
