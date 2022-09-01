package io.github.elongdeo.mybatis.core;

import io.github.elongdeo.mybatis.common.dal.DalPage;
import io.github.elongdeo.mybatis.common.result.Page;
import io.github.elongdeo.mybatis.util.GenericsUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分片表的基础Repo基础实现
 *
 * @author dingyinlong
 * @date 2019/12/09
 */
public abstract class BaseShardedRepoImpl<P, DO extends BaseShardedDO<P, S>, Example extends BaseShardedExample<S>, S> implements IShardedRepo<P, DO, Example, S> {

    @Autowired
    IShardedMapper<P, DO, Example, S> mapper;

    @Override
    public int insert(DO record, S shardingKey, String modifier) {
        if (record == null) {
            return 0;
        }
        if (!isUseGeneratedKeys()) {
            setSeqId(record);
        }
        if (shardingKey != null) {
            record.setShardingKey(shardingKey);
        }
        if (modifier != null) {
            record.setCreator(modifier);
            record.setModifier(modifier);
        }
        return mapper.insertSelective(record);
    }

    @Override
    public int insert(DO record, S shardingKey) {
        return insert(record, shardingKey, null);
    }

    @Override
    public int insertList(List<? extends DO> list, S shardingKey, String modifier) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        if (!isInsertListEnable()) {
            return list.stream().mapToInt(record -> insert(record, shardingKey, modifier)).sum();
        }
        if (!isUseGeneratedKeys()) {
            list.forEach(this::setSeqId);
        }
        list.forEach(record -> {
            if (shardingKey != null) {
                record.setShardingKey(shardingKey);
            }
            if (modifier != null) {
                record.setCreator(modifier);
                record.setModifier(modifier);
            }
        });
        final AtomicInteger counter = new AtomicInteger(0);
        return list.stream().collect(Collectors.groupingBy(it -> counter.getAndIncrement() / getInsertListBatchSize())).values()
                .stream().mapToInt(item -> mapper.insertList(item)).sum();
    }

    @Override
    public int insertList(List<? extends DO> list, S shardingKey) {
        return insertList(list, shardingKey, null);
    }

    @Override
    public int deleteById(P id, S shardingKey, String modifier) {
        if (id == null) {
            return 0;
        }
        DO record = getDeleteDo(modifier);
        record.setId(id);
        if (shardingKey != null) {
            record.setShardingKey(shardingKey);
        }
        return mapper.deleteByPrimaryKey(record);
    }

    @Override
    public int deleteById(P id, S shardingKey) {
        return deleteById(id, shardingKey, null);
    }

    @Override
    public int deleteByExample(Example example, S shardingKey, String modifier) {
        DO record = getDeleteDo(modifier);
        if (shardingKey != null) {
            example.setShardingKey(shardingKey);
        }
        return mapper.updateByExampleSelective(record, example);
    }

    @Override
    public int deleteByExample(Example example, S shardingKey) {
        return deleteByExample(example, shardingKey, null);
    }

    @Override
    public int updateById(DO record, S shardingKey, String modifier) {
        if (record == null) {
            return 0;
        }
        if (shardingKey != null) {
            record.setShardingKey(shardingKey);
        }
        if (modifier != null) {
            record.setModifier(modifier);
        }
        return mapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateById(DO record, S shardingKey) {
        return updateById(record, shardingKey, null);
    }

    @Override
    public int updateByExample(DO record, Example example, S shardingKey, String modifier) {
        if (record == null) {
            return 0;
        }
        if (shardingKey != null) {
            example.setShardingKey(shardingKey);
        }
        if (modifier != null) {
            record.setModifier(modifier);
        }
        return mapper.updateByExampleSelective(record, example);
    }

    @Override
    public int updateByExample(DO record, Example example, S shardingKey) {
        return updateByExample(record, example, shardingKey, null);
    }

    @Override
    public DO getById(P id, S shardingKey) {
        if (id == null) {
            return null;
        }
        return mapper.selectByPrimaryKey(id, shardingKey);
    }

    @Override
    public <T> T getById(P id, S shardingKey, Function<DO, T> function) {
        DO record = getById(id, shardingKey);
        return Optional.ofNullable(record).map(function).orElse(null);
    }

    @Override
    public int countByExample(Example example, S shardingKey) {
        if (shardingKey != null) {
            example.setShardingKey(shardingKey);
        }
        return (int) mapper.countByExample(example);
    }

    @Override
    public List<DO> listByExample(Example example, S shardingKey) {
        if (shardingKey != null) {
            example.setShardingKey(shardingKey);
        }
        return mapper.selectByExample(example);
    }

    @Override
    public <T> List<T> listByExample(Example example, S shardingKey, Function<DO, T> function) {
        List<DO> dos = listByExample(example, shardingKey);
        return dos.stream().map(function).collect(Collectors.toList());
    }

    @Override
    public Page<DO> pageByExample(Example example, S shardingKey) {
        Page<DO> page = new Page<>();
        int count = countByExample(example, shardingKey);
        page.setTotalCount(count);
        if (count > 0) {
            page.setItems(listByExample(example, shardingKey));
        } else {
            page.setItems(Collections.emptyList());
        }
        return page;
    }

    @Override
    public <T> Page<T> pageByExample(Example example, S shardingKey, Function<DO, T> function) {
        Page<T> page = new Page<>();
        int count = countByExample(example, shardingKey);
        page.setTotalCount(count);
        if (count > 0) {
            page.setItems(listByExample(example, shardingKey, function));
        } else {
            page.setItems(Collections.emptyList());
        }
        return page;
    }

    @Override
    public DO getByExample(Example example, S shardingKey) {
        example.setPage(new DalPage(0, 1));
        return listByExample(example, shardingKey).stream().findFirst().orElse(null);
    }

    @Override
    public <T> T getByExample(Example example, S shardingKey, Function<DO, T> function) {
        return Optional.ofNullable(getByExample(example, shardingKey)).map(function).orElse(null);
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
        record.setEnable(false);
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
        return GenericsUtils.getSuperClassGenericType(this.getClass(), 1);
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
    protected P getNewId() {
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