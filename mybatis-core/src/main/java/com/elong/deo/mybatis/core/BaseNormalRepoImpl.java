package com.elong.deo.mybatis.core;

import com.elong.deo.common.dal.DalPage;
import com.elong.deo.common.result.Page;
import com.elong.deo.mybatis.util.GenericsUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 正常表的基础Repo基础实现
 *
 * @author dingyinlong
 * @date 2019/12/09
 */
public abstract class BaseNormalRepoImpl<DO extends BaseDO, Example extends BaseExample> implements INormalRepo<DO, Example> {
    @Autowired
    INormalMapper<DO, Example> mapper;

    @Override
    public int insert(DO record, String modifier) {
        if (record == null) {
            return 0;
        }
        if (!isUseGeneratedKeys()) {
            setSeqId(record);
        }
        if (modifier != null) {
            record.setCreator(modifier);
            record.setModifier(modifier);
        }
        return mapper.insertSelective(record);
    }

    @Override
    public int insert(DO record) {
        return insert(record, null);
    }

    @Override
    public int insertList(List<? extends DO> list, String modifier) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        if (!isInsertListEnable()) {
            return list.stream().mapToInt(record -> insert(record, modifier)).sum();
        }
        if (!isUseGeneratedKeys()) {
            list.forEach(this::setSeqId);
        }
        if (modifier != null) {
            list.forEach(record -> {
                record.setCreator(modifier);
                record.setModifier(modifier);
            });
        }
        return ListUtils.partition(list, getInsertListBatchSize()).stream()
                .mapToInt(item -> mapper.insertList(item)).sum();
    }

    @Override
    public int insertList(List<? extends DO> list) {
        return insertList(list, null);
    }

    @Override
    public int deleteById(Long id, String modifier) {
        if (id == null) {
            return 0;
        }
        DO record = getDeleteDo(modifier);
        record.setId(id);
        return mapper.deleteByPrimaryKey(record);
    }

    @Override
    public int deleteById(Long id) {
        return deleteById(id, null);
    }

    @Override
    public int deleteByExample(Example example, String modifier) {
        DO record = getDeleteDo(modifier);
        return mapper.updateByExampleSelective(record, example);
    }

    @Override
    public int deleteByExample(Example example) {
        return deleteByExample(example, null);
    }

    @Override
    public int updateById(DO record, String modifier) {
        if (record == null) {
            return 0;
        }
        if (modifier != null) {
            record.setModifier(modifier);
        }
        return mapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateById(DO record) {
        return updateById(record, null);
    }

    @Override
    public int updateByExample(DO record, Example example, String modifier) {
        if (record == null) {
            return 0;
        }
        if (modifier != null) {
            record.setModifier(modifier);
        }
        return mapper.updateByExampleSelective(record, example);
    }

    @Override
    public int updateByExample(DO record, Example example) {
        return updateByExample(record, example, null);
    }

    @Override
    public DO getById(Long id) {
        if (id == null) {
            return null;
        }
        return mapper.selectByPrimaryKey(id);
    }

    @Override
    public <T> T getById(Long id, Function<DO, T> function) {
        DO record = getById(id);
        return Optional.ofNullable(record).map(function).orElse(null);
    }

    @Override
    public int countByExample(Example example) {
        return (int) mapper.countByExample(example);
    }

    @Override
    public List<DO> listByExample(Example example) {
        return mapper.selectByExample(example);
    }

    @Override
    public <T> List<T> listByExample(Example example, Function<DO, T> function) {
        List<DO> dos = listByExample(example);
        return dos.stream().map(function).collect(Collectors.toList());
    }

    @Override
    public Page<DO> pageByExample(Example example) {
        Page<DO> page = new Page<>();
        int count = countByExample(example);
        page.setTotalCount(count);
        if (count > 0) {
            page.setItems(listByExample(example));
        } else {
            page.setItems(Collections.emptyList());
        }
        return page;
    }

    @Override
    public <T> Page<T> pageByExample(Example example, Function<DO, T> function) {
        Page<T> page = new Page<>();
        int count = countByExample(example);
        page.setTotalCount(count);
        if (count > 0) {
            page.setItems(listByExample(example, function));
        } else {
            page.setItems(Collections.emptyList());
        }
        return page;
    }

    @Override
    public DO getByExample(Example example) {
        example.setPage(new DalPage(0, 1));
        return listByExample(example).stream().findFirst().orElse(null);
    }

    @Override
    public <T> T getByExample(Example example, Function<DO, T> function) {
        return Optional.ofNullable(getByExample(example)).map(function).orElse(null);
    }

    /**
     * 设置seq id的值
     * 原本有值就用原来的，没有值则获取新id
     *
     * @param record 待插入对象
     */
    private void setSeqId(DO record) {
        if (record != null && record.getId() == null) {
            //如果没有给定ID值，则获取新id
            record.setId(getNewId());
        }
    }

    /**
     * 获取待删除DO实例
     *
     * @param modifier 修改人（使用用户信息透传组件请传null）
     * @return 待删除DO实例
     */
    private DO getDeleteDo(String modifier) {
        DO record = null;
        try {
            record = getDoClass().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        record.setDeleted(true);
        if (modifier != null) {
            record.setModifier(modifier);
        }
        return record;
    }

    /**
     * 获取DO类型
     *
     * @return DO类型
     */
    protected Class<DO> getDoClass() {
        return GenericsUtils.getSuperClassGenericType(this.getClass());
    }

    /**
     * 是否使用数据库自增
     *
     * @return 是否使用数据库自增
     */
    protected abstract boolean isUseGeneratedKeys();

    /**
     * 是否使用批量插入
     *
     * @return 是否使用批量插入
     */
    protected abstract boolean isInsertListEnable();

    /**
     * 获取新ID
     *
     * @return 新id
     */
    protected Long getNewId() {
        return null;
    }
    /**
     * 获取批量插入大小
     *
     * @return 新id
     */
    protected int getInsertListBatchSize() {
        return Constants.INSERT_LIST_BATCH_SIZE;
    }
}