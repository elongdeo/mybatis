package io.github.elongdeo.mybatis.core;

import java.io.Serializable;
 
/**
 * 序列化的Function
 * @author dingyinlong
 * @date 2019年11月22日14:50:11
 **/
@FunctionalInterface
public interface SerializableFunction<T> extends Serializable {
    Object accept(T source);
}