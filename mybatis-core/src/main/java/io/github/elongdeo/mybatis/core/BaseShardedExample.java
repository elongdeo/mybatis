package io.github.elongdeo.mybatis.core;

/**
 * Example基类
 *
 * @author dingyinlong
 * @date 2019年10月05日19:34:08
 */
public class BaseShardedExample<S> extends BaseExample{
    private S shardingKey;

    public S getShardingKey() {
        return shardingKey;
    }

    public void setShardingKey(S shardingKey) {
        this.shardingKey = shardingKey;
    }
}
