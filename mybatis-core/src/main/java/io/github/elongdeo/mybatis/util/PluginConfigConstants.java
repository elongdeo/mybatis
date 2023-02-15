package io.github.elongdeo.mybatis.util;

/**
 * 插件配置常量
 *
 * @author dingyinlong
 * @date 2023年02月15日14:47:56
 */
public class PluginConfigConstants {
    /**
     * 属性-开启@Data+EqualsAndHashCode注解(插件级别/表级别,指定true/false,默认false)
     */
    public static final String PROPERTY_ENABLE_ANNOTATION_DATA = "enableAnnotationData";
    /**
     * 属性-主键使用UUID生成(插件级别/表级别,指定true/false,默认false)
     */
    public static final String PROPERTY_GENERATED_UUID_KEY = "generatedUuidKey";
    /**
     * 属性-插入使用数据库自增(插件级别/表级别指定true/false,默认false)
     */
    public static final String PROPERTY_INSERT_USE_GENERATED_KEYS = "insertUseGeneratedKeys";
    /**
     * 属性-是否自动生成Repo(插件级别/表级别指定true/false,默认false)
     */
    public static final String PROPERTY_REPO_ENABLE = "repoEnable";
    /**
     * 属性-允许批量插入(插件级别/表级别,指定true/false,默认true)
     */
    public static final String PROPERTY_INSERT_LIST_ENABLE = "insertListEnable";
    /**
     * 属性-指定创建时间列(插件级别/表级别指定列名称,默认:gmt_create)
     */
    public static final String PROPERTY_GMT_CREATE_COLUMN_NAME = "gmtCreateColumnName";
    /**
     * 属性-指定创建人列(插件级别/表级别指定列名称,默认:creator)
     */
    public static final String PROPERTY_CREATOR_COLUMN_NAME = "creatorColumnName";
    /**
     * 属性-指定修改时间列(插件级别/表级别指定列名称,默认:gmt_modified)
     */
    public static final String PROPERTY_GMT_MODIFIED_COLUMN_NAME = "gmtModifiedColumnName";
    /**
     * 属性-指定修改人列(插件级别/表级别指定列名称,默认:modifier)
     */
    public static final String PROPERTY_MODIFIER_COLUMN_NAME = "modifierColumnName";
    /**
     * 属性-指定是否启用列(插件级别/表级别指定列名称,默认:is_enable)
     */
    public static final String PROPERTY_ENABLE_COLUMN_NAME = "enableColumnName";
    /**
     * 属性-指定是否启用列逻辑反转,true表示0为生效数据,false表示1为生效数据(插件级别/表级别指定true/false,默认:false)
     */
    public static final String PROPERTY_ENABLE_COLUMN_LOGICAL_FLIP = "enableColumnLogicalFlip";
    /**
     * 属性-指定生效条件sql(插件级别/表级别指定,eg:"is_deleted = 0")
     */
    public static final String PROPERTY_ENABLE_CONDITION_SQL = "enableConditionSql";
    /**
     * 属性-指定删除sql(插件级别/表级别指定,eg:"is_deleted = unix_timestamp(now())")
     */
    public static final String PROPERTY_DELETE_SQL = "deleteSql";
    /**
     * 属性-允许强行大字符修改为字符(插件级别/表级别,指定true/false,默认false)
     */
    public static final String PROPERTY_ENABLE_FORCE_STRING = "enableForceString";
    /**
     * 属性-开启@Accessors(chain = "true")注解(插件级别/表级别,指定true/false,默认false)
     */
    public static final String PROPERTY_ENABLE_ANNOTATION_ACCESSORS = "enableAnnotationAccessors";
    /**
     * 属性-开启@Builder注解(插件级别/表级别,指定true/false,默认false)
     */
    public static final String PROPERTY_ENABLE_ANNOTATION_BUILDER = "enableAnnotationBuilder";
    /**
     * 属性-BaseDO字段重写(插件级别/表级别,指定true/false,默认false)
     */
    public static final String PROPERTY_BASE_DO_OVERRIDE = "baseDoOverride";
    /**
     * 属性-repo后缀(插件级别/表级别)
     */
    public static final String PROPERTY_REPO_SUFFIX = "repoSuffix";
    /**
     * 属性-repo包路径(插件级别/表级别)
     */
    public static final String PROPERTY_REPO_PACKAGE = "repoPackage";
    /**
     * 属性-repo实现包路径(插件级别/表级别)
     */
    public static final String PROPERTY_REPO_IMPL_PACKAGE = "repoImplPackage";
    /**
     * 属性-是否不使用Repo接口(插件级别/表级别,指定true/false,默认false)
     */
    public static final String PROPERTY_REPO_NO_INTERFACE = "repoNoInterface";
    /**
     * 属性-是否忽略Criterion值为空(插件级别/表级别,指定true/false,默认false)
     */
    public static final String PROPERTY_IGNORE_CRITERION_VALUE_NULL = "ignoreCriterionValueNull";
    /**
     * 属性-创建修改时间精度(插件级别/表级别,指定正整数,默认0)
     */
    public static final String PROPERTY_DATE_PRECISION = "datePrecision";
    /**
     * 属性-禁用一级缓存方法(插件级别/表级别,指定方法名,多个用','分割,默认空)
     */
    public static final String PROPERTY_FLUSH_CACHE_METHODS = "flushCacheMethods";
    /**
     * 属性-repo自动注入的Sequence名称(表级别)
     */
    public static final String PROPERTY_REPO_AUTOWIRED_SEQUENCE_NAME = "repoAutowiredSequenceName";
    /**
     * 属性-是否数据库片键(字段级别)
     */
    public static final String PROPERTY_INSERT_IS_SHARDING_KEY = "isShardingKey";
}
