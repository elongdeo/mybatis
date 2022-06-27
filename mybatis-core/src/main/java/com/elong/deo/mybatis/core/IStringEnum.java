package com.elong.deo.mybatis.core;

/**
 * string类型的枚举
 * @author dingyinlong
 * @date 2019/11/12
 */
public interface IStringEnum {
    /**
     * 获取枚举编码
     * @return 枚举编码
     */
    String getCode();
    /**
     * 获取枚举名称
     * @return 枚举名称
     */
    String getName();
}
