package io.github.elongdeo.mybatis.core;

import io.github.elongdeo.mybatis.common.result.Page;

import java.util.List;
import java.util.function.Function;

/**
 * 分片表的基础Repo基础接口
 * @author dingyinlong
 * @date 2019/12/09
 */
public interface IShardedRepo<P, DO extends BaseShardedDO<P, S>, Example extends BaseExample, S> {

    /**
     * 保存一个实体，
     * null的属性不会保存，会使用数据库默认值
     *
     * @param record   实体对象
     * @param shardingKey 片键值
     * @param modifier 修改人（使用用户信息透传组件请传null）
     * @return 影响的行数
     */
    int insert(DO record, S shardingKey, String modifier);

    /**
     * 保存一个实体，
     * null的属性不会保存，会使用数据库默认值
     *
     * @param record   实体对象
     * @param shardingKey 片键值
     * @return 影响的行数
     */
    int insert(DO record, S shardingKey);

    /**
     * 批量保存实体，已分批保存
     * null的属性不会保存，会使用数据库默认值
     *
     * @param list     实体列表
     * @param shardingKey 片键值
     * @param modifier 修改人（使用用户信息透传组件请传null）
     * @return 影响的行数
     */
    int insertList(List<? extends DO> list, S shardingKey, String modifier);

    /**
     * 批量保存实体，已分批保存
     * null的属性不会保存，会使用数据库默认值
     *
     * @param list     实体列表
     * @param shardingKey 片键值
     * @return 影响的行数
     */
    int insertList(List<? extends DO> list, S shardingKey);

    /**
     * 根据主键字段进行软删除
     *
     * @param id       主键
     * @param shardingKey 片键值
     * @param modifier 修改人（使用用户信息透传组件请传null）
     * @return 影响数据库行数
     */
    int deleteById(P id, S shardingKey, String modifier);

    /**
     * 根据主键字段进行软删除
     *
     * @param id       主键
     * @param shardingKey 片键值
     * @return 影响数据库行数
     */
    int deleteById(P id, S shardingKey);

    /**
     * 根据Example条件删除数据
     *
     * @param example  条件
     * @param shardingKey 片键值
     * @param modifier 修改人（使用用户信息透传组件请传null）
     * @return 影响数据库行数
     */
    int deleteByExample(Example example, S shardingKey, String modifier);

    /**
     * 根据Example条件删除数据
     *
     * @param example  条件
     * @param shardingKey 片键值
     * @return 影响数据库行数
     */
    int deleteByExample(Example example, S shardingKey);

    /**
     * 根据主键更新属性不为null的值或指定允许null值的字段
     *
     * @param record   待更新字段信息
     * @param shardingKey 片键值
     * @param modifier 修改人（使用用户信息透传组件请传null）
     * @return 影响行数
     */
    int updateById(DO record, S shardingKey, String modifier);

    /**
     * 根据主键更新属性不为null的值或指定允许null值的字段
     *
     * @param record   待更新字段信息
     * @param shardingKey 片键值
     * @return 影响行数
     */
    int updateById(DO record, S shardingKey);

    /**
     * 根据Example条件更新属性不为null的值或指定允许null值的字段
     *
     * @param record   待更新字段信息
     * @param example  Example条件
     * @param shardingKey 片键值
     * @param modifier 修改人（使用用户信息透传组件请传null）
     * @return 影响行数
     */
    int updateByExample(DO record, Example example, S shardingKey, String modifier);

    /**
     * 根据Example条件更新属性不为null的值或指定允许null值的字段
     *
     * @param record   待更新字段信息
     * @param example  Example条件
     * @param shardingKey 片键值
     * @return 影响行数
     */
    int updateByExample(DO record, Example example, S shardingKey);

    /**
     * 根据主键字段进行查询
     *
     * @param id 主键
     * @param shardingKey 片键值
     * @return 返回结果
     */
    DO getById(P id, S shardingKey);

    /**
     * 根据主键字段进行查询
     *
     * @param id       主键
     * @param shardingKey 片键值
     * @param function 转换方法
     * @return 返回结果
     */
    <T> T getById(P id, S shardingKey, Function<DO, T> function);

    /**
     * 根据条件查询数量
     *
     * @param example 条件
     * @param shardingKey 片键值
     * @return 满足条件的数量
     */
    int countByExample(Example example, S shardingKey);

    /**
     * 根据条件查询列表
     *
     * @param example 条件
     * @param shardingKey 片键值
     * @return 满足条件的列表
     */
    List<DO> listByExample(Example example, S shardingKey);

    /**
     * 根据条件查询列表
     *
     * @param example  条件
     * @param shardingKey 片键值
     * @param function 转换方法
     * @return 满足条件的列表
     */
    <T> List<T> listByExample(Example example, S shardingKey, Function<DO, T> function);

    /**
     * 根据条件分页查询
     *
     * @param example 条件
     * @param shardingKey 片键值
     * @return 分页结果
     */
    Page<DO> pageByExample(Example example, S shardingKey);

    /**
     * 根据条件分页查询
     *
     * @param example  条件
     * @param shardingKey 片键值
     * @param function 转换方法
     * @return 分页结果
     */
    <T> Page<T> pageByExample(Example example, S shardingKey, Function<DO, T> function);

    /**
     * 根据条件查询第一个
     *
     * @param example 条件
     * @param shardingKey 片键值
     * @return 满足条件的第一个
     */
    DO getByExample(Example example, S shardingKey);

    /**
     * 根据条件查询第一个
     *
     * @param example  条件
     * @param shardingKey 片键值
     * @param function 转换方法
     * @return 满足条件的第一个
     */
    <T> T getByExample(Example example, S shardingKey, Function<DO, T> function);
}
