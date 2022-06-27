package com.elong.deo.mybatis.core;

/**
 * int类型的枚举
 * @author dingyinlong
 * @date 2019/11/12
 */
public interface IIntEnum {
    /**
     * 获取枚举编码
     * @return 枚举编码
     */
    int getCode();

    /**
     * 获取枚举名称
     * @return 枚举名称
     */
    String getName();
}
