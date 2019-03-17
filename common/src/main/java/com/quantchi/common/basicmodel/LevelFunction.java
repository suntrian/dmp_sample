package com.quantchi.common.basicmodel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.function.Function;


@NoArgsConstructor
public class LevelFunction<T, PK extends Comparable> implements Serializable {
  public interface SerializableFunction<T, R> extends Function<T, R>, Serializable{}
  private static final long serialVersionUID = -2565599590654233367L;
  @Getter
  @Setter
  SerializableFunction<T, PK> keyGetter;
  @Getter
  @Setter
  SerializableFunction<T, PK> parentGetter;
  @Getter
  SerializableFunction<T, ? extends Serializable>[] propertyGetter;

  @SafeVarargs
  public final <S extends Serializable> void setPropertyGetter(SerializableFunction<T, S>... propertyGetter) {
    this.propertyGetter = propertyGetter;
  }

  public LevelFunction(SerializableFunction<T, PK> keyGetter, SerializableFunction<T, PK> parentGetter, SerializableFunction<T, ? extends Serializable>... propertyGetter) {
    this.keyGetter = keyGetter;
    this.parentGetter = parentGetter;
    this.propertyGetter = propertyGetter;
  }
}

