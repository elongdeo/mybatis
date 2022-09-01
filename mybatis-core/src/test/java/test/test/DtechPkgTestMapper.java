package test.test;

import io.github.elongdeo.mybatis.core.INormalMapper;

import java.util.List;

public interface DtechPkgTestMapper extends INormalMapper<Long, DtechPkgTestDO, DtechPkgTestExample> {
    @Override
    long countByExample(DtechPkgTestExample example);

    @Override
    int deleteByPrimaryKey(DtechPkgTestDO record);

    @Override
    int insertSelective(DtechPkgTestDO record);

    @Override
    List<DtechPkgTestDO> selectByExample(DtechPkgTestExample example);

    @Override
    DtechPkgTestDO selectByPrimaryKey(Long id);

    @Override
    int updateByExampleSelective(DtechPkgTestDO record, DtechPkgTestExample example);

    @Override
    int updateByPrimaryKeySelective(DtechPkgTestDO record);
}