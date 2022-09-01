package io.github.elongdeo.mybatis.core;

import java.util.List;

/**
 * Mapper接口
 *
 * @author dingyinlong
 * @date 2019年12月05日19:34:08
 */
public interface IMapper<P ,DO extends BaseDO<P>, Example extends BaseExample> {

    /**
     * 通过条件查询数量
     *
     * @param example 条件
     * @return 满足条件的数量
     */
    long countByExample(Example example);

    /**
     * 通过主键删除
     *
     * @param record 要删除的记录信息（包含主键）
     * @return 影响数据库行数
     */
    int deleteByPrimaryKey(DO record);

    /**
     * 插入记录信息
     *
     * @param record 要插入的记录信息
     * @return 影响数据库行数
     */
    int insertSelective(DO record);

    /**
     * 通过条件分页查询列表
     *
     * @param example 条件
     * @return 满足条件的分页列表
     */
    List<DO> selectByExample(Example example);

    /**
     * 通过条件更新
     *
     * @param record  要改成的记录信息
     * @param example 条件
     * @return 影响数据库行数
     */
    int updateByExampleSelective(DO record, Example example);

    /**
     * 通过主键更新
     *
     * @param record 要改成的记录信息（包含主键）
     * @return 影响数据库行数
     */
    int updateByPrimaryKeySelective(DO record);

    /**
     * 批量插入
     * <p>多库多表请调用处防止跨库事务</p>
     * <p>请注意list大小</p>
     *
     * @param list 待插入列表
     * @return 影响数据库行数
     */
    int insertList(List<? extends DO> list);
}
