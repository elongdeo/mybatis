package io.github.elongdeo.mybatis.core;

/**
 * long类型的枚举
 * @author dingyinlong
 * @date 2019/11/12
 */
public interface ILongEnum {
    /**
     * 获取枚举编码
     * @return 枚举编码
     */
    long getCode();

    /**
     * 获取枚举名称
     * @return 枚举名称
     */
    String getName();
}
