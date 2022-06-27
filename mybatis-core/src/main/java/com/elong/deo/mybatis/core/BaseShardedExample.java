package com.elong.deo.mybatis.core;

/**
 * Example基类
 *
 * @author dingyinlong
 * @date 2019年10月05日19:34:08
 */
public class BaseShardedExample<T> extends BaseExample{
    private T shardingKey;

    public T getShardingKey() {
        return shardingKey;
    }

    public void setShardingKey(T shardingKey) {
        this.shardingKey = shardingKey;
    }
}
