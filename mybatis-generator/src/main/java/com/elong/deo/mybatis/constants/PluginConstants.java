package com.elong.deo.mybatis.constants;

/**
 * 插件所需常量
 *
 * @author dingyinlong
 * @date 2018年12月05日15:17:05
 */
public class PluginConstants {
    /**
     * Mapper注解类路径
     */
    public static final String ANNOTATION_MAPPER = "org.apache.ibatis.annotations.Mapper";
    /**
     * 属性-开启@Data+EqualsAndHashCode注解(插件级别)
     */
    public static final String PROPERTY_ENABLE_ANNOTATION_DATA = "enableAnnotationData";
    /**
     * 属性-允许强行大字符修改为字符(插件级别)
     */
    public static final String PROPERTY_ENABLE_FORCE_STRING = "enableForceString";
    /**
     * 属性-开启@Accessors(chain = "true")注解(插件级别)
     */
    public static final String PROPERTY_ENABLE_ANNOTATION_ACCESSORS = "enableAnnotationAccessors";
    /**
     * 属性-开启@Builder注解(插件级别)
     */
    public static final String PROPERTY_ENABLE_ANNOTATION_BUILDER = "enableAnnotationBuilder";
    /**
     * 属性-tddlAppName(插件级别)
     */
    public static final String PROPERTY_TDDL_APP_NAME = "tddlAppName";
    /**
     * 属性-BaseDO字段重写(插件级别)
     */
    public static final String PROPERTY_BASE_DO_OVERRIDE = "baseDoOverride";
    /**
     * 属性-repo后缀(插件级别)
     */
    public static final String PROPERTY_REPO_SUFFIX = "repoSuffix";
    /**
     * 属性-repo包路径(插件级别)
     */
    public static final String PROPERTY_REPO_PACKAGE = "repoPackage";
    /**
     * 属性-repo实现包路径(插件级别)
     */
    public static final String PROPERTY_REPO_IMPL_PACKAGE = "repoImplPackage";
    /**
     * 属性-是否不使用Repo接口(插件级别)
     */
    public static final String PROPERTY_REPO_NO_INTERFACE = "repoNoInterface";
    /**
     * 属性-是否忽略Criterion值为空(插件级别)
     */
    public static final String PROPERTY_IGNORE_CRITERION_VALUE_NULL = "ignoreCriterionValueNull";
    /**
     * 属性-创建修改时间精度
     */
    public static final String PROPERTY_DATE_PRECISION = "datePrecision";
    /**
     * 属性-允许批量插入(表级别)
     */
    public static final String PROPERTY_INSERT_LIST_ENABLE = "insertListEnable";
    /**
     * 属性-插入使用数据库自增(表级别)
     */
    public static final String PROPERTY_INSERT_USE_GENERATED_KEYS = "insertUseGeneratedKeys";
    /**
     * 属性-禁用一级缓存方法(表级别)
     */
    public static final String PROPERTY_FLUSH_CACHE_METHODS = "flushCacheMethods";
    /**
     * 属性-是否自动生成Repo(表级别)
     */
    public static final String PROPERTY_REPO_ENABLE = "repoEnable";
    /**
     * 属性-repo自动注入的Sequence名称(表级别)
     */
    public static final String PROPERTY_REPO_AUTOWIRED_SEQUENCE_NAME = "repoAutowiredSequenceName";
    /**
     * 属性-是否数据库片键(字段级别)
     */
    public static final String PROPERTY_INSERT_IS_SHARDING_KEY = "isShardingKey";
    /**
     * false字符串
     */
    public static final String STRING_FALSE = "false";
    /**
     * true字符串
     */
    public static final String STRING_TRUE = "true";
    /**
     * BaseDO类名
     */
    public static final String CLASS_BASE_DO = "com.elong.deo.mybatis.core.BaseDO";
    /**
     * BaseShardedDO类名
     */
    public static final String CLASS_BASE_SHARDED_DO = "com.elong.deo.mybatis.core.BaseShardedDO";
    /**
     * INormalMapper类名
     */
    public static final String CLASS_NORMAL_MAPPER = "com.elong.deo.mybatis.core.INormalMapper";
    /**
     * IShardedMapper类名
     */
    public static final String CLASS_SHARDED_MAPPER = "com.elong.deo.mybatis.core.IShardedMapper";
    /**
     * BaseExample类名
     */
    public static final String CLASS_BASE_EXAMPLE = "com.elong.deo.mybatis.core.BaseExample";
    /**
     * BaseShardedExample类名
     */
    public static final String CLASS_BASE_SHARDED_EXAMPLE = "com.elong.deo.mybatis.core.BaseShardedExample";
    /**
     * INormalRepo类名
     */
    public static final String CLASS_I_NORMAL_REPO = "com.elong.deo.mybatis.core.INormalRepo";
    /**
     * IShardedRepo类名
     */
    public static final String CLASS_I_SHARDED_REPO = "com.elong.deo.mybatis.core.IShardedRepo";
    /**
     * BaseNormalRepoImpl类名
     */
    public static final String CLASS_BASE_NORMAL_REPO_IMPL = "com.elong.deo.mybatis.core.BaseNormalRepoImpl";
    /**
     * BaseShardedRepoImpl类名
     */
    public static final String CLASS_BASE_SHARDED_REPO_IMPL = "com.elong.deo.mybatis.core.BaseShardedRepoImpl";
}
