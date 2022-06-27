package com.elong.deo.mybatis.core;

import com.elong.deo.common.dal.DalPage;
import com.elong.deo.mybatis.util.LambdaUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Example基类
 *
 * @author dingyinlong
 * @date 2019年10月05日19:34:08
 */
public class BaseExample {
    /**
     * 分页条件
     */
    protected DalPage page;

    /**
     * 查询的字段
     */
    private Set<String> selectFields = new HashSet<>();

    public void setPage(DalPage page) {
        this.page = page;
    }

    public DalPage getPage() {
        return page;
    }

    /**
     * 添加查询的字段
     *
     * @param function get方法引用
     */
    public <T> void addSelectField(SerializableFunction<T> function) {
        this.selectFields.add(LambdaUtils.getGetterField(function));
    }

    /**
     * 添加查询的字段
     *
     * @param functions get方法引用列表
     */
    public <T> void addSelectFields(List<SerializableFunction<T>> functions) {
        this.selectFields.addAll(functions.stream().map(LambdaUtils::getGetterField).collect(Collectors.toList()));
    }

    /**
     * 添加查询的字段
     *
     * @param fieldName 要查询的字段名称（DO中字段名称）
     */
    public <T> void addSelectFieldName(String fieldName) {
        this.selectFields.add(fieldName);
    }

    /**
     * 添加查询的字段
     *
     * @param fieldNames 要查询的字段名称列表（DO中字段名称）
     */
    public <T> void addSelectFieldNames(List<String> fieldNames) {
        this.selectFields.addAll(fieldNames);
    }

    public Set<String> getSelectFields() {
        return selectFields;
    }
}
