package io.github.elongdeo.mybatis.core;

import io.github.elongdeo.mybatis.util.LambdaUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DO基类
 *
 * @author dingyinlong
 * @date 2019年10月05日19:34:08
 */
public class BaseDO<P> implements Serializable {

    /**
     * 主键
     */
    private P id;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 是否启用(false表示逻辑删除)
     */
    private Boolean enable;

    /**
     * 允许更新空值的字段
     */
    private final Set<String> enableUpdateNullFields = new HashSet<>();

    /**
     * 主键
     *
     * @return id 主键
     */
    public P getId() {
        return id;
    }

    /**
     * 主键
     *
     * @param id 主键
     */
    public void setId(P id) {
        this.id = id;
    }

    /**
     * 创建时间
     *
     * @return gmt_create 创建时间
     */
    public Date getGmtCreate() {
        return gmtCreate;
    }

    /**
     * 创建时间
     *
     * @param gmtCreate 创建时间
     */
    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    /**
     * 创建人
     *
     * @return creator 创建人
     */
    public String getCreator() {
        return creator;
    }

    /**
     * 创建人
     *
     * @param creator 创建人
     */
    public void setCreator(String creator) {
        this.creator = creator == null ? null : creator.trim();
    }

    /**
     * 修改时间
     *
     * @return gmt_modified 修改时间
     */
    public Date getGmtModified() {
        return gmtModified;
    }

    /**
     * 修改时间
     *
     * @param gmtModified 修改时间
     */
    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    /**
     * 修改人
     *
     * @return modifier 修改人
     */
    public String getModifier() {
        return modifier;
    }

    /**
     * 修改人
     *
     * @param modifier 修改人
     */
    public void setModifier(String modifier) {
        this.modifier = modifier == null ? null : modifier.trim();
    }

    /**
     * 是否启用(false表示逻辑删除)
     *
     * @return 是否启用(false表示逻辑删除)
     */
    public Boolean getEnable() {
        return enable;
    }

    /**
     * 是否启用(false表示逻辑删除)
     *
     * @param enable 是否启用(false表示逻辑删除)
     */
    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    /**
     * 添加允许更新空值的字段
     *
     * @param function get方法引用
     */
    public <T> void addEnableUpdateNullField(SerializableFunction<T> function) {
        this.enableUpdateNullFields.add(LambdaUtils.getGetterField(function));
    }

    /**
     * 添加允许更新空值的字段
     *
     * @param functions get方法引用列表
     */
    public <T> void addEnableUpdateNullFields(List<SerializableFunction<T>> functions) {
        this.enableUpdateNullFields.addAll(functions.stream().map(LambdaUtils::getGetterField).collect(Collectors.toList()));
    }

    /**
     * 添加允许更新空值的字段
     *
     * @param fieldName 字段名称（DO中字段名称）
     */
    public void addEnableUpdateNullFieldName(String fieldName) {
        this.enableUpdateNullFields.add(fieldName);
    }

    /**
     * 添加允许更新空值的字段
     *
     * @param fieldNames 字段名称列表（DO中字段名称）
     */
    public void addEnableUpdateNullFieldNames(List<String> fieldNames) {
        this.enableUpdateNullFields.addAll(fieldNames);
    }

    /**
     * 获取允许更新空值的字段
     *
     * @return 允许更新空值的字段
     */
    public Set<String> getEnableUpdateNullFields() {
        return enableUpdateNullFields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BaseDO)) {
            return false;
        }
        BaseDO<P> baseDO = (BaseDO) o;
        return Objects.equals(id, baseDO.id) &&
                Objects.equals(gmtCreate, baseDO.gmtCreate) &&
                Objects.equals(creator, baseDO.creator) &&
                Objects.equals(gmtModified, baseDO.gmtModified) &&
                Objects.equals(modifier, baseDO.modifier) &&
                Objects.equals(enable, baseDO.enable) &&
                Objects.equals(enableUpdateNullFields, baseDO.enableUpdateNullFields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, gmtCreate, creator, gmtModified, modifier, enable, enableUpdateNullFields);
    }
}
