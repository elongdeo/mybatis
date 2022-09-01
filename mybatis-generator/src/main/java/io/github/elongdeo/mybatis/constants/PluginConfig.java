package io.github.elongdeo.mybatis.constants;

/**
 * @author dingyinlong
 * @date 2019/03/08
 */
public class PluginConfig {
    /**
     * 格式化bool类型
     */
    public static boolean boolFormatAble = true;
    /**
     * 大字符强行普通字符
     */
    public static boolean forceString = true;
    /**
     * DO后缀
     */
    public static boolean doSuffixAble = true;
    /**
     * 生成mapper扩展
     */
    public static boolean mapperExtAble = true;
    /**
     * 分页查询
     */
    public static boolean pageAble = true;
    /**
     * 软删除
     */
    public static boolean softDeleteAble = true;
    /**
     * 校验orderBy（防止sql入注）
     */
    public static boolean checkOrderByAble = true;
    /**
     * insert/update强制补全基础字段
     */
    public static boolean insertUpdateFillFeildAble = true;
    /**
     * 屏蔽高分享方法
     */
    public static boolean hideRiskAble = true;
    /**
     * 使用lombok
     */
    public static boolean lombokAble = true;
    /**
     * 批量插入
     */
    public static boolean batchInsertAble = true;
    /**
     * 片键支持
     */
    public static boolean shardingKeyAble = true;
}
