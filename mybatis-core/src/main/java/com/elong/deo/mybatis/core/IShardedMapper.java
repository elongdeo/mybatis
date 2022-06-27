package com.elong.deo.mybatis.core;

/**
 * 片键Mapper接口
 *
 * @author dingyinlong
 * @date 2019年12月05日19:34:08
 */
public interface IShardedMapper<DO extends BaseShardedDO<T>, Example extends BaseExample, T> extends IMapper<DO, Example> {

    /**
     * 通过主键查询
     *
     * @param id          数据库主键
     * @param shardingKey 片键字段
     * @return 数据记录
     */
    DO selectByPrimaryKey(Long id, T shardingKey);
}
