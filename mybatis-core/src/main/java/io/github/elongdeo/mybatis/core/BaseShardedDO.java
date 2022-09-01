package io.github.elongdeo.mybatis.core;

/**
 * 带片键DO基类
 *
 * @author dingyinlong
 * @date 2019年10月05日19:34:08
 */
public abstract class BaseShardedDO<P, S> extends BaseDO<P> {
    /**
     * 设置片键值
     *
     * @param shardingKey 片键值
     */
    public abstract void setShardingKey(S shardingKey);
}
