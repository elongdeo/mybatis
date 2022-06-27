package com.elong.deo.mybatis.core;

/**
 * 正常Mapper接口
 *
 * @author dingyinlong
 * @date 2019年12月05日19:34:08
 */
public interface INormalMapper<DO extends BaseDO, Example extends BaseExample> extends IMapper<DO,Example> {

    /**
     * 通过主键查询
     *
     * @param id 数据库主键
     * @return 数据记录
     */
    DO selectByPrimaryKey(Long id);
}
