package io.github.elongdeo.mybatis.core;

/**
 * 片键Mapper接口
 *
 * @author dingyinlong
 * @date 2019年12月05日19:34:08
 */
public interface IShardedMapper<P, DO extends BaseShardedDO<P, S>, Example extends BaseExample, S> extends IMapper<P, DO, Example> {

    /**
     * 通过主键查询
     *
     * @param id          数据库主键
     * @param shardingKey 片键字段
     * @return 数据记录
     */
    DO selectByPrimaryKey(P id, S shardingKey);
}
