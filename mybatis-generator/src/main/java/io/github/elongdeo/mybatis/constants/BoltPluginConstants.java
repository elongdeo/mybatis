package io.github.elongdeo.mybatis.constants;

/**
 * bolt插件所需常量
 *
 * @author dingyinlong
 * @date 2018年12月05日15:17:05
 */
public class BoltPluginConstants {
    /**
     * Table注解类路径
     */
    public static final String ANNOTATION_TABLE = "javax.persistence.Table";
    /**
     * Column注解类路径
     */
    public static final String ANNOTATION_COLUMN = "javax.persistence.Column";
    /**
     * Autowired注解类路径
     */
    public static final String ANNOTATION_AUTOWIRED = "org.springframework.beans.factory.annotation.Autowired";
    /**
     * Service注解类路径
     */
    public static final String ANNOTATION_SERVICE = "org.springframework.stereotype.Service";
    /**
     * HSFProvider注解类路径
     */
    public static final String ANNOTATION_HSF_PROVIDER = "com.alibaba.boot.hsf.annotation.HSFProvider";
    /**
     * Data注解类路径
     */
    public static final String ANNOTATION_DATA = "lombok.Data";
    /**
     * EqualsAndHashCode注解类路径
     */
    public static final String ANNOTATION_EQUALS_AND_HASH_CODE = "lombok.EqualsAndHashCode";
    /**
     * Accessors注解类路径
     */
    public static final String ANNOTATION_ACCESSORS = "lombok.experimental.Accessors";
    /**
     * NoArgsConstructor注解类路径
     */
    public static final String ANNOTATION_NO_ARGS_CONSTRUCTOR = "lombok.NoArgsConstructor";
    /**
     * Builder注解类路径
     */
    public static final String ANNOTATION_BUILDER = "lombok.Builder";
    /**
     * BizIdForSelf注解类路径
     */
    public static final String ANNOTATION_BIZ_ID_FOR_SELF = "com.zxhy.bolt.annotation.BizIdForSelf";
    /**
     * ShardingKey注解类路径
     */
    public static final String ANNOTATION_SHARDING_KEY = "com.zxhy.bolt.annotation.ShardingKey";
    /**
     * BoltBaseDO类路径
     */
    public static final String CLASS_BOLT_BASE_DO = "com.zxhy.bolt.model.BoltBaseDO";
    /**
     * BoltBaseMapper类路径
     */
    public static final String CLASS_BOLT_BASE_MAPPER = "com.zxhy.bolt.mapper.BoltBaseMapper";
    /**
     * BoltBaseService类路径
     */
    public static final String CLASS_BOLT_BASE_SERVICE = "com.zxhy.bolt.service.BoltBaseService";
    /**
     * HSFResult类路径
     */
    public static final String CLASS_HSF_RESULT = "com.zxhy.common.result.HSFResult";
    /**
     * List类路径
     */
    public static final String CLASS_LIST = "java.util.List";
    /**
     * BoltMysqlServiceImpl类路径
     */
    public static final String CLASS_BOLT_BASE_SERVICE_IMPL
        = "com.zxhy.bolt.service.impl.BoltMysqlServiceImpl";
    /**
     * BoltBaseHsfServiceImpl类路径
     */
    public static final String CLASS_BOLT_BASE_HSF_SERVICE_IMPL
        = "com.zxhy.bolt.hsf.impl.BoltBaseHsfServiceImpl";
    /**
     * 属性名-是否使用lombok
     */
    public static final String PROPERTY_USING_LOMBOK = "usingLombok";
    /**
     * 属性名-是否使用lombok.Builder
     */
    public static final String PROPERTY_USING_LOMBOK_BUILDER = "usingLombokBuilder";
    /**
     * 属性名-是否使用lombok.experimental.Accessors
     */
    public static final String PROPERTY_USING_LOMBOK_ACCESSORS = "usingLombokAccessors";
    /**
     * 属性名-是否使用lombok.experimental.Accessors(fluent = true)
     */
    public static final String PROPERTY_USING_LOMBOK_ACCESSORS_FLUENT = "usingLombokAccessorsFluent";
    /**
     * 属性名-是否使用lombok.experimental.Accessors(chain = true)
     */
    public static final String PROPERTY_USING_LOMBOK_ACCESSORS_CHAIN = "usingLombokAccessorsChain";
    /**
     * 属性名-是否使用lombok.NoArgsConstructor
     */
    public static final String PROPERTY_USING_LOMBOK_NO_ARGS_CONSTRUCTOR = "usingLombokNoArgsConstructor";
    /**
     * 属性名-是否是本表bizId
     */
    public static final String PROPERTY_BIZ_ID_FOR_SELF = "bizIdForSelf";
    /**
     * 属性名-是否是数据库片键
     */
    public static final String PROPERTY_SHARDING_KEY = "shardingKey";

    /**
     * 属性名-service包路径
     */
    public static final String PROPERTY_SERVICE_PACKAGE = "servicePackage";
    /**
     * 属性名-serviceImpl包路径
     */
    public static final String PROPERTY_SERVICE_IMPL_PACKAGE = "serviceImplPackage";
    /**
     * 属性名-hsfServiceImpl包路径
     */
    public static final String PROPERTY_HSF_SERVICE_IMPL_PACKAGE = "hsfServiceImplPackage";
    /**
     * 属性名-hsfService包路径
     */
    public static final String PROPERTY_HSF_SERVICE_PACKAGE = "hsfServicePackage";
    /**
     * 属性名-DTO包路径
     */
    public static final String PROPERTY_DTO_PACKAGE = "dtoPackage";
    /**
     * 属性名-hsf基本目录
     */
    public static final String PROPERTY_HSF_PROJECT = "hsfProject";

    /**
     * 属性名-service基本目录
     */
    public static final String PROPERTY_SERVICE_PROJECT = "serviceProject";
    /**
     * 属性名-serviceImpl基本目录
     */
    public static final String PROPERTY_SERVICE_IMPL_PROJECT = "serviceImplProject";
    /**
     * 属性名-hsfServiceImpl基本目录
     */
    public static final String PROPERTY_HSF_SERVICE_IMPL_PROJECT = "hsfServiceImplProject";
    /**
     * 属性名-hsfService基本目录
     */
    public static final String PROPERTY_HSF_SERVICE_PROJECT = "hsfServiceProject";
    /**
     * 属性名-DTO基本目录
     */
    public static final String PROPERTY_DTO_PROJECT = "dtoProject";

    /**
     * DO后缀
     */
    public static final String SUFFIX_DO = "DO";

    /**
     * DTO后缀
     */
    public static final String SUFFIX_DTO = "DTO";
    /**
     * Service后缀
     */
    public static final String SUFFIX_SERVICE = "Service";
    /**
     * ServiceImpl后缀
     */
    public static final String SUFFIX_SERVICE_IMPL = "ServiceImpl";
    /**
     * HsfService后缀
     */
    public static final String SUFFIX_HSF_SERVICE = "HsfService";
    /**
     * HsfServiceImpl后缀
     */
    public static final String SUFFIX_HSF_SERVICE_IMPL = "HsfServiceImpl";
}
