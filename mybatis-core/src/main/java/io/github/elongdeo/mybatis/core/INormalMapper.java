package io.github.elongdeo.mybatis.core;

/**
 * 正常Mapper接口
 *
 * @author dingyinlong
 * @date 2019年12月05日19:34:08
 */
public interface INormalMapper<P, DO extends BaseDO<P>, Example extends BaseExample> extends IMapper<P, DO, Example> {

    /**
     * 通过主键查询
     *
     * @param id 数据库主键
     * @return 数据记录
     */
    DO selectByPrimaryKey(P id);
}
