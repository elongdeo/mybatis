package io.github.elongdeo.mybatis.plugin;

import io.github.elongdeo.mybatis.logic.standard.BooleanPropertyLogic;
import io.github.elongdeo.mybatis.constants.BoltPluginConstants;
import io.github.elongdeo.mybatis.constants.PluginConstants;
import io.github.elongdeo.mybatis.logic.standard.DoSuffixLogic;
import io.github.elongdeo.mybatis.util.FileUtil;
import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.PropertyRegistry;

import java.util.*;

import static org.mybatis.generator.internal.util.JavaBeansUtil.*;

/**
 * 针对bolt做的代码插件
 *
 * @author dingyinlong
 * @date 2018年12月05日16:14:34
 */
public class BoltPlugin extends PluginAdapter {
    /**
     * 是否使用lombok，默认为true
     */
    private boolean isUsingLombok = true;
    /**
     * 是否使用lombok.Builder，默认为true
     */
    private boolean isUsingLombokBuilder = false;
    /**
     * 是否使用lombok.experimental.Accessors，默认为true
     */
    private boolean isUsingLombokAccessors = true;
    /**
     * 是否使用lombok.experimental.Accessors(fluent = true)，默认为true
     */
    private boolean isUsingLombokAccessorsFluent = true;
    /**
     * 是否使用lombok.experimental.Accessors(chain = true)，默认为false
     */
    private boolean isUsingLombokAccessorsChain = false;
    /**
     * 是否使用lombok.NoArgsConstructor，默认为true
     */
    private boolean isUsingLombokNoArgsConstructor = true;
    private List<String> toRemoveProperties = Arrays.asList("gmtCreate", "gmtModified", "creator", "modifier",
        "deleted");
    private List<String> toRemoveMethodeNames = Arrays.asList("getGmtCreate", "setGmtCreate",
        "getGmtModified", "setGmtModified", "getCreator", "setCreator",
        "getModifier", "setModifier", "getDeleted", "setDeleted");

    /**
     * This method is called after all the setXXX methods are called, but before any other method is called. This allows
     * the plugin to determine whether it can run or not. For example, if the plugin requires certain properties to be
     * set, and the properties are not set, then the plugin is invalid and will not run.
     *
     * @param warnings add strings to this list to specify warnings. For example, if the plugin is invalid, you should
     *                 specify why. Warnings are reported to users after the completion of the run.
     * @return true if the plugin is in a valid state. Invalid plugins will not be called
     */
    @Override
    public boolean validate(List<String> warnings) {
        if (!getProperties().containsKey(BoltPluginConstants.PROPERTY_SERVICE_PACKAGE)
                || !getProperties().containsKey(BoltPluginConstants.PROPERTY_SERVICE_IMPL_PACKAGE)
                || !getProperties().containsKey(BoltPluginConstants.PROPERTY_HSF_SERVICE_PACKAGE)
                || !getProperties().containsKey(BoltPluginConstants.PROPERTY_DTO_PACKAGE)
                || !getProperties().containsKey(BoltPluginConstants.PROPERTY_HSF_SERVICE_IMPL_PACKAGE)) {
            throw new RuntimeException("已下属性必须配置:" + Arrays.asList(BoltPluginConstants.PROPERTY_SERVICE_PACKAGE,
                    BoltPluginConstants.PROPERTY_SERVICE_IMPL_PACKAGE, BoltPluginConstants.PROPERTY_HSF_SERVICE_PACKAGE, BoltPluginConstants.PROPERTY_DTO_PACKAGE,
                    BoltPluginConstants.PROPERTY_HSF_SERVICE_IMPL_PACKAGE));
        }
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        // 配置不使用lombok
        if (PluginConstants.STRING_FALSE.equals(getProperties().getProperty(BoltPluginConstants.PROPERTY_USING_LOMBOK))) {
            this.isUsingLombok = false;
        }
        // 配置使用lombok.Builder
        if (PluginConstants.STRING_TRUE.equals(getProperties().getProperty(BoltPluginConstants.PROPERTY_USING_LOMBOK_BUILDER))) {
            this.isUsingLombokBuilder = true;
        }
        // 配置不使用lombok.experimental.Accessors
        if (PluginConstants.STRING_FALSE.equals(getProperties().getProperty(BoltPluginConstants.PROPERTY_USING_LOMBOK_ACCESSORS))) {
            this.isUsingLombokAccessors = false;
        }
        // 配置使用lombok.experimental.Accessors(chain = true)
        if (PluginConstants.STRING_TRUE.equals(getProperties().getProperty(BoltPluginConstants.PROPERTY_USING_LOMBOK_ACCESSORS_CHAIN))) {
            this.isUsingLombokAccessorsChain = true;
        }
        // 配置不使用lombok.experimental.Accessors(fluent = true)
        if (PluginConstants.STRING_FALSE.equals(getProperties().getProperty(BoltPluginConstants.PROPERTY_USING_LOMBOK_ACCESSORS_FLUENT))) {
            this.isUsingLombokAccessorsFluent = false;
        }
        // 配置不使用lombok.NoArgsConstructor
        if (PluginConstants.STRING_FALSE.equals(getProperties().getProperty(BoltPluginConstants.PROPERTY_USING_LOMBOK_NO_ARGS_CONSTRUCTOR))) {
            this.isUsingLombokNoArgsConstructor = false;
        }
        // 替换处理命名
        DoSuffixLogic.dealFileName(introspectedTable);
        // 将boolean/tinyint(1)类型带is_前缀字段生成的属性值改为不带is前缀,防止反序列化失败
        BooleanPropertyLogic.dealBooleanColumn(introspectedTable);
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        // 引入一定需要引入的类
        topLevelClass.addImportedType(BoltPluginConstants.ANNOTATION_TABLE);
        topLevelClass.addImportedType(BoltPluginConstants.CLASS_BOLT_BASE_DO);
        // 设置父类
        topLevelClass.setSuperClass(BoltPluginConstants.CLASS_BOLT_BASE_DO);
        // 增加注解
        topLevelClass.addAnnotation(
            "@Table(name = \"" + introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime() + "\")");
        // 是否有bizId
        boolean hasBizId = false;
        // 是否有片键
        boolean hasShardingKey = false;
        Set<Field> retainFields = new HashSet<>();
        for (IntrospectedColumn column : introspectedTable.getAllColumns()) {
            // 列属性bizIdForSelf为true时则给属性加上BizIdForSelf注解
            if (PluginConstants.STRING_TRUE.equals(column.getProperties().getProperty(BoltPluginConstants.PROPERTY_BIZ_ID_FOR_SELF))) {
                if (hasBizId) {
                    throw new RuntimeException(
                        "table " + introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime() +
                            " configured multi bizIdForSelf");
                }
                hasBizId = true;
                Field bizIdField = topLevelClass.getFields().stream().filter(
                    field -> field.getName().equals(column.getJavaProperty()))
                    .findFirst().get();
                bizIdField.addAnnotation("@BizIdForSelf");
                if (toRemoveProperties.contains(bizIdField.getName())) {
                    retainFields.add(bizIdField);
                }
            }
            // 列属性shardingKey为true时则给属性加上ShardingKey注解
            if (PluginConstants.STRING_TRUE.equals(column.getProperties().getProperty(BoltPluginConstants.PROPERTY_SHARDING_KEY))) {
                if (hasShardingKey) {
                    throw new RuntimeException(
                        "table " + introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime() +
                            " configured multi shardingKey");
                }
                hasShardingKey = true;
                Field shardingKeyField = topLevelClass.getFields().stream().filter(
                    field -> field.getName().equals(column.getJavaProperty()))
                    .findFirst().get();
                shardingKeyField.addAnnotation("@ShardingKey");
                if (toRemoveProperties.contains(shardingKeyField.getName())) {
                    retainFields.add(shardingKeyField);
                }
            }
        }
        // id增加注解
        Field idFiled = topLevelClass.getFields().stream().filter(field -> field.getName().equals("id"))
            .findFirst().orElse(null);
        if (idFiled != null) {
            idFiled.addAnnotation("@Id");
            idFiled.addAnnotation("@KeySql(genId = GenTddlSeqId.class)");
            topLevelClass.addImportedType("javax.persistence.Id");
            topLevelClass.addImportedType("tk.mybatis.mapper.annotation.KeySql");
            topLevelClass.addImportedType("com.zxhy.bolt.gen.GenTddlSeqId");
        }
        // 如果有bizId则引入com.zxhy.bolt.annotation.BizIdForSelf注解类
        if (hasBizId) {
            topLevelClass.addImportedType(BoltPluginConstants.ANNOTATION_BIZ_ID_FOR_SELF);
        }
        // 如果有shardingKey则引入com.zxhy.bolt.annotation.ShardingKey注解类
        if (hasShardingKey) {
            topLevelClass.addImportedType(BoltPluginConstants.ANNOTATION_SHARDING_KEY);
        }
        // 删除父类中有的字段
        topLevelClass.getFields().removeIf(
            field -> toRemoveProperties.contains(field.getName()) && !retainFields.contains(field)
        );
        // 删除父类中有的方法
        topLevelClass.getMethods().removeIf(method -> toRemoveMethodeNames.contains(method.getName()));
        // 剩余字段没有其他Date型字段则删除Date类引入
        if (!topLevelClass.getFields().stream().anyMatch(field -> Date.class.getName()
            .equals(field.getType().getFullyQualifiedName()))) {
            topLevelClass.getImportedTypes().removeIf(
                type -> Date.class.getName().equals(type.getFullyQualifiedName()));
        }
        // 如果使用lombok则引入包加注解并删除get/set方法
        if (isUsingLombok) {
            addLombokAnnotation(topLevelClass, true);
            topLevelClass.getMethods().clear();
        }
        // Boolean类型的字段需要指定列名(字段被重新命名)
        topLevelClass.getFields().stream().filter(
            field -> field.getType().getFullyQualifiedName().equals(Boolean.class.getName())).forEach
            (field -> field.addAnnotation("@Column(name=\"" + introspectedTable.getAllColumns().stream().filter
                (introspectedColumn -> introspectedColumn.getJavaProperty().equals(field.getName())).findFirst()
                .get().getActualColumnName() + "\")"));
        if (topLevelClass.getFields().stream().anyMatch(
            field -> field.getType().getFullyQualifiedName().equals(Boolean.class.getName()))) {
            topLevelClass.addImportedType(BoltPluginConstants.ANNOTATION_COLUMN);
        }
        return true;
    }


    /**
     * mapper.java生成
     *
     * @param interfaze         mapper接口
     * @param topLevelClass     类（此处为null）
     * @param introspectedTable 表及配置信息
     * @return true（生成）
     */
    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass,
                                   IntrospectedTable introspectedTable) {
        // 清空引入类
        interfaze.getImportedTypes().clear();
        // 引入Mapper注解类
        interfaze.addImportedType(new FullyQualifiedJavaType(PluginConstants.ANNOTATION_MAPPER));
        // Mapper注解
        interfaze.addAnnotation("@Mapper");
        // 引入BoltBaseMapper类
        interfaze.addImportedType(new FullyQualifiedJavaType(BoltPluginConstants.CLASS_BOLT_BASE_MAPPER));
        // 引入DO类
        interfaze.addImportedType(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));
        // 设置父类
        interfaze.addSuperInterface(new FullyQualifiedJavaType(
                BoltPluginConstants.CLASS_BOLT_BASE_MAPPER + "<" + introspectedTable.getBaseRecordType() + ">"));
        // 清空所有方法
        interfaze.getMethods().clear();
        // 设置合并老代码
        interfaze.setMergeable(true);
        return true;
    }

    /**
     * 生成附属文件
     *
     * @param introspectedTable 被生成的表信息
     * @return 执行结果
     */
    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(
        IntrospectedTable introspectedTable) {
        List<GeneratedJavaFile> generatedJavaFiles = new ArrayList<>(
            4);
        // 生成Service
        generateService(introspectedTable, generatedJavaFiles);
        // 生成ServiceImpl
        generateServiceImpl(introspectedTable, generatedJavaFiles);
        // 生成DTO
        generateDto(introspectedTable, generatedJavaFiles);
        // 生成DTO
        generateHsfService(introspectedTable, generatedJavaFiles);
        // 生成DTO
        generateHsfServiceImpl(introspectedTable, generatedJavaFiles);

        return generatedJavaFiles;
    }

    /**
     * 生成Service
     *
     * @param introspectedTable  表及配置信息
     * @param generatedJavaFiles 要生成的文件列表
     */
    private void generateService(IntrospectedTable introspectedTable, List<GeneratedJavaFile> generatedJavaFiles) {
        // 如果不存在service包配置则不生成
        if (!getProperties().containsKey(BoltPluginConstants.PROPERTY_SERVICE_PACKAGE)) {
            return;
        }
        // 声明service接口
        Interface interfaze = new Interface(new FullyQualifiedJavaType(
            getServiceClassName(introspectedTable.getBaseRecordType())));
        // 设置权限
        interfaze.setVisibility(JavaVisibility.PUBLIC);
        // 引用注释
        context.getCommentGenerator().addJavaFileComment(interfaze);
        // 引入需要的类
        interfaze.addImportedType(new FullyQualifiedJavaType(BoltPluginConstants.CLASS_BOLT_BASE_SERVICE));
        interfaze.addImportedType(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));
        // 设置父类
        interfaze.addSuperInterface(new FullyQualifiedJavaType
                (BoltPluginConstants.CLASS_BOLT_BASE_SERVICE + "<" + introspectedTable.getBaseRecordType() + ">"));

        GeneratedJavaFile generatedJavaFile = new GeneratedJavaFile(
            interfaze,
            getServiceProjectValue(),
            context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
            context.getJavaFormatter());
        // 判断是否已存在文件，存在则不覆盖
        if (!FileUtil.isExistFile(generatedJavaFile.getTargetProject(),
            generatedJavaFile.getTargetPackage(),
            generatedJavaFile.getFileName())) {
            generatedJavaFiles.add(generatedJavaFile);
        }
    }

    /**
     * 生成ServiceImpl
     *
     * @param introspectedTable  表及配置信息
     * @param generatedJavaFiles 要生成的文件列表
     */
    private void generateServiceImpl(IntrospectedTable introspectedTable, List<GeneratedJavaFile> generatedJavaFiles) {
        // 如果不存在service/serviceImpl包配置则不生成
        if (!getProperties().containsKey(BoltPluginConstants.PROPERTY_SERVICE_PACKAGE)
                || !getProperties().containsKey(BoltPluginConstants.PROPERTY_SERVICE_IMPL_PACKAGE)) {
            return;
        }
        // 声明serviceImpl类
        TopLevelClass topLevelClass = new TopLevelClass(new FullyQualifiedJavaType(
            getServiceImplClassName(introspectedTable.getBaseRecordType())));
        // 设置权限
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        // 引用注释
        context.getCommentGenerator().addJavaFileComment(topLevelClass);

        FullyQualifiedJavaType mapperType = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType());
        FullyQualifiedJavaType serviceType = new FullyQualifiedJavaType(
            getServiceClassName(introspectedTable.getBaseRecordType()));

        // 引入BoltMysqlServiceImpl类
        topLevelClass.addImportedType(new FullyQualifiedJavaType(BoltPluginConstants.CLASS_BOLT_BASE_SERVICE_IMPL));
        // 引入mapper类
        topLevelClass.addImportedType(mapperType);
        // 引入do类
        topLevelClass.addImportedType(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));
        // 引入service类
        topLevelClass.addImportedType(serviceType);
        // 引入Autowired注解
        topLevelClass.addImportedType(new FullyQualifiedJavaType(BoltPluginConstants.ANNOTATION_AUTOWIRED));
        // 引入Service注解
        topLevelClass.addImportedType(new FullyQualifiedJavaType(BoltPluginConstants.ANNOTATION_SERVICE));
        // 设置父类
        topLevelClass.setSuperClass(new FullyQualifiedJavaType
                (BoltPluginConstants.CLASS_BOLT_BASE_SERVICE_IMPL + "<" + introspectedTable.getBaseRecordType() + ">"));
        // 设置接口
        topLevelClass.addSuperInterface(serviceType);
        // 添加@Service注解
        topLevelClass.addAnnotation("@Service");
        // 增加mapper属性
        Field field = new Field();
        field.setName("mapper");
        field.setType(mapperType);
        field.setVisibility(JavaVisibility.DEFAULT);
        field.addAnnotation("@Autowired");
        topLevelClass.addField(field);

        GeneratedJavaFile generatedJavaFile = new GeneratedJavaFile(
            topLevelClass,
            getServiceImplProjectValue(),
            context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
            context.getJavaFormatter());
        // 判断是否已存在文件，存在则不覆盖
        if (!FileUtil.isExistFile(generatedJavaFile.getTargetProject(),
            generatedJavaFile.getTargetPackage(),
            generatedJavaFile.getFileName())) {
            generatedJavaFiles.add(generatedJavaFile);
        }
    }

    /**
     * 生成ServiceImpl
     *
     * @param introspectedTable  表及配置信息
     * @param generatedJavaFiles 要生成的文件列表
     */
    private void generateDto(IntrospectedTable introspectedTable, List<GeneratedJavaFile> generatedJavaFiles) {
        // 如果不存在dto包配置/hsf工程地址则不生成
        if (!getProperties().containsKey(BoltPluginConstants.PROPERTY_DTO_PACKAGE)
                || !getProperties().containsKey(BoltPluginConstants.PROPERTY_HSF_PROJECT)) {
            return;
        }
        // 声明service接口
        TopLevelClass topLevelClass = new TopLevelClass(new FullyQualifiedJavaType(
            getDtoClassName(introspectedTable.getBaseRecordType())));
        // 设置权限
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        // 引用注释
        context.getCommentGenerator().addJavaFileComment(topLevelClass);
        List<IntrospectedColumn> introspectedColumns = introspectedTable.getAllColumns();
        // 删除无用列
        introspectedColumns.removeIf(
            introspectedColumn -> toRemoveProperties.contains(introspectedColumn.getJavaProperty())
                && !"id".equals(introspectedColumn.getJavaProperty()));
        // 循环生成DTO属性
        for (IntrospectedColumn introspectedColumn : introspectedColumns) {
            // 引入字段
            Field field = getJavaBeansField(introspectedColumn, context, introspectedTable);
            topLevelClass.addField(field);
            topLevelClass.addImportedType(field.getType());
            if (isUsingLombok) {
                continue;
            }
            // 生成getter/setter
            topLevelClass.addMethod(getJavaBeansGetter(introspectedColumn, context, introspectedTable));
            topLevelClass.addMethod(getJavaBeansSetter(introspectedColumn, context, introspectedTable));
        }

        // 如果使用lombok则引入包加注解
        if (isUsingLombok) {
            addLombokAnnotation(topLevelClass, false);
        }

        GeneratedJavaFile generatedJavaFile = new GeneratedJavaFile(
            topLevelClass,
            getDtoProjectValue(),
            context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
            context.getJavaFormatter());
        generatedJavaFiles.add(generatedJavaFile);
    }

    /**
     * 生成HsfService
     *
     * @param introspectedTable  表及配置信息
     * @param generatedJavaFiles 要生成的文件列表
     */
    private void generateHsfService(IntrospectedTable introspectedTable, List<GeneratedJavaFile> generatedJavaFiles) {
        // 如果不存在service包配置则不生成
        if (!getProperties().containsKey(BoltPluginConstants.PROPERTY_HSF_SERVICE_PACKAGE)
                || !getProperties().containsKey(BoltPluginConstants.PROPERTY_HSF_PROJECT)
                || !getProperties().containsKey(BoltPluginConstants.PROPERTY_DTO_PACKAGE)) {
            return;
        }
        // 声明hsfService接口
        Interface interfaze = new Interface(new FullyQualifiedJavaType(
            getHsfServiceClassName(introspectedTable.getBaseRecordType())));
        // 设置权限
        interfaze.setVisibility(JavaVisibility.PUBLIC);
        // 引用注释
        context.getCommentGenerator().addJavaFileComment(interfaze);
        // 引入需要的类
        interfaze.addImportedType(new FullyQualifiedJavaType(BoltPluginConstants.CLASS_HSF_RESULT));
        interfaze.addImportedType(new FullyQualifiedJavaType(getDtoClassName(introspectedTable.getBaseRecordType())));
        interfaze.addImportedType(new FullyQualifiedJavaType(BoltPluginConstants.CLASS_LIST));

        interfaze.getMethods().add(builtGetById(introspectedTable));
        interfaze.getMethods().add(builtGetByBizId(introspectedTable));
        interfaze.getMethods().add(builtInsert(introspectedTable));
        interfaze.getMethods().add(builtInsertList(introspectedTable));
        interfaze.getMethods().add(builtDeleteById());
        interfaze.getMethods().add(builtDeleteByBizId());
        interfaze.getMethods().add(builtDeleteByIds());
        interfaze.getMethods().add(builtUpdateByBizId(introspectedTable));

        GeneratedJavaFile generatedJavaFile = new GeneratedJavaFile(
            interfaze,
            getHsfServiceProjectValue(),
            context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
            context.getJavaFormatter());
        // 判断是否已存在文件，存在则不覆盖
        if (!FileUtil.isExistFile(generatedJavaFile.getTargetProject(),
            generatedJavaFile.getTargetPackage(),
            generatedJavaFile.getFileName())) {
            generatedJavaFiles.add(generatedJavaFile);
        }
    }

    private Method builtGetById(IntrospectedTable introspectedTable) {
        Method method = new Method();
        method.setName("getById");
        method.setReturnType(new FullyQualifiedJavaType(BoltPluginConstants.CLASS_HSF_RESULT + "<"
            + getDtoClassName(introspectedTable.getBaseRecordType()) + ">"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("Long"), "id"));
        method.addJavaDocLine("/**\n"
            + "     * 通过主键获取\n"
            + "     *\n"
            + "     * @param id 主键\n"
            + "     * @return 封装的单条记录\n"
            + "     */");
        return method;
    }

    private Method builtGetByBizId(IntrospectedTable introspectedTable) {
        Method method = new Method();
        method.setName("getByBizId");
        method.setReturnType(new FullyQualifiedJavaType(BoltPluginConstants.CLASS_HSF_RESULT + "<"
            + getDtoClassName(introspectedTable.getBaseRecordType()) + ">"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "bizId"));
        method.addJavaDocLine("/**\n"
            + "     * 通过bizId获取\n"
            + "     *\n"
            + "     * @param bizId bizId\n"
            + "     * @return 封装的单条记录\n"
            + "     */");
        return method;
    }

    private Method builtInsert(IntrospectedTable introspectedTable) {
        Method method = new Method();
        method.setName("insert");
        method.setReturnType(new FullyQualifiedJavaType(BoltPluginConstants.CLASS_HSF_RESULT + "<Integer>"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType(getDtoClassName(introspectedTable
            .getBaseRecordType())), "dto"));
        method.addJavaDocLine("/**\n"
            + "     * 插入单条记录\n"
            + "     *\n"
            + "     * @param dto 单条记录\n"
            + "     * @return 封装的成功数量\n"
            + "     */");
        return method;
    }

    private Method builtInsertList(IntrospectedTable introspectedTable) {
        Method method = new Method();
        method.setName("insertList");
        method.setReturnType(new FullyQualifiedJavaType(BoltPluginConstants.CLASS_HSF_RESULT + "<Integer>"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("List<" + getDtoClassName(introspectedTable
            .getBaseRecordType()) + ">"), "list"));
        method.addJavaDocLine("/**\n"
            + "     * 批量插入\n"
            + "     * \n"
            + "     * @param list 待插入列表\n"
            + "     * @return 封装的成功数量\n"
            + "     */");
        return method;
    }

    private Method builtDeleteById() {
        Method method = new Method();
        method.setName("deleteById");
        method.setReturnType(new FullyQualifiedJavaType(BoltPluginConstants.CLASS_HSF_RESULT + "<Integer>"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("Long"), "id"));
        method.addJavaDocLine("/**\n"
            + "     * 通过主键删除\n"
            + "     * \n"
            + "     * @param id 主键\n"
            + "     * @return 封装的成功数量\n"
            + "     */");
        return method;
    }

    private Method builtDeleteByBizId() {
        Method method = new Method();
        method.setName("deleteByBizId");
        method.setReturnType(new FullyQualifiedJavaType(BoltPluginConstants.CLASS_HSF_RESULT + "<Integer>"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "bizId"));
        method.addJavaDocLine("/**\n"
            + "     * 通过bizId删除\n"
            + "     * \n"
            + "     * @param bizId bizId\n"
            + "     * @return 封装的成功数量\n"
            + "     */");
        return method;
    }

    private Method builtDeleteByIds() {
        Method method = new Method();
        method.setName("deleteByIds");
        method.setReturnType(new FullyQualifiedJavaType(BoltPluginConstants.CLASS_HSF_RESULT + "<Integer>"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "ids"));
        method.addJavaDocLine("/**\n"
            + "     * 通过id拼接串(,分割)批量删除\n"
            + "     * \n"
            + "     * @param ids id拼接串(,分割)\n"
            + "     * @return 封装的成功数量\n"
            + "     */");
        return method;
    }

    private Method builtUpdateByBizId(IntrospectedTable introspectedTable) {
        Method method = new Method();
        method.setName("updateByBizId");
        method.setReturnType(new FullyQualifiedJavaType(BoltPluginConstants.CLASS_HSF_RESULT + "<Integer>"));
        method.addParameter(new Parameter(new FullyQualifiedJavaType(getDtoClassName(introspectedTable
            .getBaseRecordType())), "dto"));
        method.addJavaDocLine("/**\n"
            + "     * 通过bizId修改\n"
            + "     * \n"
            + "     * @param dto 带bizId的对象\n"
            + "     * @return 封装的成功数量\n"
            + "     */");
        return method;
    }

    /**
     * 生成HsfServiceImpl
     *
     * @param introspectedTable  表及配置信息
     * @param generatedJavaFiles 要生成的文件列表
     */
    private void generateHsfServiceImpl(IntrospectedTable introspectedTable, List<GeneratedJavaFile>
        generatedJavaFiles) {
        // 如果不存在hsfService/hsfServiceImpl包配置则不生成
        if (!getProperties().containsKey(BoltPluginConstants.PROPERTY_HSF_SERVICE_IMPL_PACKAGE)
                || !getProperties().containsKey(BoltPluginConstants.PROPERTY_DTO_PACKAGE)
                || !getProperties().containsKey(BoltPluginConstants.PROPERTY_SERVICE_PACKAGE)) {
            return;
        }
        // 声明service接口
        TopLevelClass topLevelClass = new TopLevelClass(new FullyQualifiedJavaType(
            getHsfServiceImplClassName(introspectedTable.getBaseRecordType())));
        // 设置权限
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        // 引用注释
        context.getCommentGenerator().addJavaFileComment(topLevelClass);

        FullyQualifiedJavaType serviceType = new FullyQualifiedJavaType(
            getServiceClassName(introspectedTable.getBaseRecordType()));
        FullyQualifiedJavaType hsfServiceType = new FullyQualifiedJavaType(
            getHsfServiceClassName(introspectedTable.getBaseRecordType()));

        // 引入BoltHsfServiceImpl类
        topLevelClass.addImportedType(new FullyQualifiedJavaType(BoltPluginConstants.CLASS_BOLT_BASE_HSF_SERVICE_IMPL));
        // 引入service类
        topLevelClass.addImportedType(serviceType);
        // 引入hsfService类
        topLevelClass.addImportedType(hsfServiceType);
        // 引入do类
        topLevelClass.addImportedType(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));
        // 引入dto类
        topLevelClass.addImportedType(
            new FullyQualifiedJavaType(getDtoClassName(introspectedTable.getBaseRecordType())));
        // 引入HSFProvider注解类
        topLevelClass.addImportedType(new FullyQualifiedJavaType(BoltPluginConstants.ANNOTATION_HSF_PROVIDER));
        // 引入Autowired注解
        topLevelClass.addImportedType(new FullyQualifiedJavaType(BoltPluginConstants.ANNOTATION_AUTOWIRED));
        // 设置父类
        topLevelClass.setSuperClass(new FullyQualifiedJavaType
                (BoltPluginConstants.CLASS_BOLT_BASE_HSF_SERVICE_IMPL + "<" + getDtoClassName(introspectedTable.getBaseRecordType()) + ","
                + introspectedTable.getBaseRecordType() + ">"));
        // 设置接口
        topLevelClass.addSuperInterface(hsfServiceType);
        // 添加@HSFProvider注解
        topLevelClass.addAnnotation("@HSFProvider(serviceInterface = " + hsfServiceType.getShortName() + ".class)");
        // 增加service属性
        Field field = new Field();
        field.setName("service");
        field.setType(serviceType);
        field.setVisibility(JavaVisibility.DEFAULT);
        field.addAnnotation("@Autowired");
        topLevelClass.addField(field);

        GeneratedJavaFile generatedJavaFile = new GeneratedJavaFile(
            topLevelClass,
            getHsfServiceImplProjectValue(),
            context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
            context.getJavaFormatter());
        // 判断是否已存在文件，存在则不覆盖
        if (!FileUtil.isExistFile(generatedJavaFile.getTargetProject(),
            generatedJavaFile.getTargetPackage(),
            generatedJavaFile.getFileName())) {
            generatedJavaFiles.add(generatedJavaFile);
        }
    }

    private String getServiceClassName(String baseRecordType) {
        return getClassName(baseRecordType, BoltPluginConstants.SUFFIX_SERVICE,
                getProperties().getProperty(BoltPluginConstants.PROPERTY_SERVICE_PACKAGE));
    }

    private String getServiceImplClassName(String baseRecordType) {
        return getClassName(baseRecordType, BoltPluginConstants.SUFFIX_SERVICE_IMPL,
                getProperties().getProperty(BoltPluginConstants.PROPERTY_SERVICE_IMPL_PACKAGE));
    }

    private String getDtoClassName(String baseRecordType) {
        return getClassName(baseRecordType, BoltPluginConstants.SUFFIX_DTO,
                getProperties().getProperty(BoltPluginConstants.PROPERTY_DTO_PACKAGE));
    }

    private String getHsfServiceClassName(String baseRecordType) {
        return getClassName(baseRecordType, BoltPluginConstants.SUFFIX_HSF_SERVICE,
                getProperties().getProperty(BoltPluginConstants.PROPERTY_HSF_SERVICE_PACKAGE));
    }

    private String getHsfServiceImplClassName(String baseRecordType) {
        return getClassName(baseRecordType, BoltPluginConstants.SUFFIX_HSF_SERVICE_IMPL,
                getProperties().getProperty(BoltPluginConstants.PROPERTY_HSF_SERVICE_IMPL_PACKAGE));
    }

    private String getClassName(String baseRecordType, String suffix, String targetPackage) {
        String shortName;
        if (baseRecordType.endsWith(BoltPluginConstants.SUFFIX_DO)) {
            shortName = baseRecordType.substring(baseRecordType.lastIndexOf("."), baseRecordType.length() - 2) + suffix;
        } else {
            shortName = baseRecordType.substring(baseRecordType.lastIndexOf(".")) + suffix;
        }
        return targetPackage + shortName;
    }

    private String getServiceProjectValue() {
        String property = getProperties().getProperty(BoltPluginConstants.PROPERTY_SERVICE_PROJECT);
        if (property == null || property.length() == 0) {
            return getJavaProjectValue();
        }
        return property;
    }

    private String getServiceImplProjectValue() {
        String property = getProperties().getProperty(BoltPluginConstants.PROPERTY_SERVICE_IMPL_PROJECT);
        if (property == null || property.length() == 0) {
            return getJavaProjectValue();
        }
        return property;
    }

    private String getHsfServiceProjectValue() {
        String property = getProperties().getProperty(BoltPluginConstants.PROPERTY_HSF_SERVICE_PROJECT);
        if (property == null || property.length() == 0) {
            return getHsfProjectValue();
        }
        return property;
    }

    private String getHsfServiceImplProjectValue() {
        String property = getProperties().getProperty(BoltPluginConstants.PROPERTY_HSF_SERVICE_IMPL_PROJECT);
        if (property == null || property.length() == 0) {
            return getJavaProjectValue();
        }
        return property;
    }

    private String getDtoProjectValue() {
        String property = getProperties().getProperty(BoltPluginConstants.PROPERTY_DTO_PROJECT);
        if (property == null || property.length() == 0) {
            return getHsfProjectValue();
        }
        return property;
    }

    private String getHsfProjectValue() {
        String property = getProperties().getProperty(BoltPluginConstants.PROPERTY_HSF_PROJECT);
        if (property == null || property.length() == 0) {
            return getJavaProjectValue();
        }
        return property;
    }

    private void addLombokAnnotation(TopLevelClass topLevelClass, boolean callSuper) {
        topLevelClass.addImportedType(BoltPluginConstants.ANNOTATION_DATA);
        topLevelClass.addAnnotation("@Data");
        topLevelClass.addImportedType(BoltPluginConstants.ANNOTATION_EQUALS_AND_HASH_CODE);
        topLevelClass.addAnnotation("@EqualsAndHashCode(callSuper = " + callSuper + ")");
        if (isUsingLombokBuilder) {
            topLevelClass.addImportedType(BoltPluginConstants.ANNOTATION_BUILDER);
            topLevelClass.addAnnotation("@Builder");
        }
        if (isUsingLombokAccessors) {
            topLevelClass.addImportedType(BoltPluginConstants.ANNOTATION_ACCESSORS);
            String annotation = "@Accessors";
            if (isUsingLombokAccessorsFluent && isUsingLombokAccessorsChain) {
                annotation += "(fluent = true, chain = true)";
            } else if (isUsingLombokAccessorsFluent) {
                annotation += "(fluent = true)";
            } else if (isUsingLombokAccessorsChain) {
                annotation += "(chain = true)";
            }
            topLevelClass.addAnnotation(annotation);
        }
        if (isUsingLombokNoArgsConstructor) {
            topLevelClass.addImportedType(BoltPluginConstants.ANNOTATION_NO_ARGS_CONSTRUCTOR);
            topLevelClass.addAnnotation("@NoArgsConstructor");
        }
    }

    private String getJavaProjectValue() {
        return context.getJavaClientGeneratorConfiguration().getTargetProject();
    }

    /**
     * 生成Example(此处禁止生成)
     *
     * @param topLevelClass     目标类
     * @param introspectedTable 表及配置信息
     * @return false（不生成）
     */
    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        return false;
    }

    /**
     * xml文档内容生成后(此处仅保留Base_Column_List以及resultMap)
     *
     * @param document          xml文档
     * @param introspectedTable 表及配置信息
     * @return true
     */
    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        // 仅保留Base_Column_List以及resultMap
        document.getRootElement().getElements().removeIf(element -> {
            XmlElement xmlElement = (XmlElement)element;
            return !xmlElement.getName().equals("resultMap") && !(xmlElement.getName().equals("sql") && xmlElement
                .getAttributes().stream().anyMatch(attribute -> attribute.getName().equals("id")
                    && attribute.getValue().equals("Base_Column_List")));
        });
        // 添加table
        XmlElement element = new XmlElement("sql");
        element.addAttribute(new Attribute("id", "Table_Name"));
        element.addElement(new TextElement(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime()));
        document.getRootElement().getElements().add(element);
        return true;
    }

    @Override
    public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
        sqlMap.setMergeable(true);
        return true;
    }

}
