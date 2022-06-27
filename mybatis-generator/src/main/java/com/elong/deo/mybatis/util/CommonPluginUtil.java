package com.elong.deo.mybatis.util;

import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.GeneratedRepo;
import org.mybatis.generator.config.PropertyRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static com.elong.deo.mybatis.constants.PluginConstants.*;

/**
 * 文件工具
 *
 * @author dingyinlong
 * @date 2018年12月05日16:14:34
 */
public class CommonPluginUtil {


    /**
     * 添加批量插入方法
     *
     * @param interfaze         mapper接口类
     * @param introspectedTable 表配置信息
     */
    public static void generateInsertList(Interface interfaze, IntrospectedTable introspectedTable) {
        if (!isInsertListEnable(introspectedTable)) {
            return;
        }
        Method method = new Method();
        method.addJavaDocLine("/**\n"
                + "     * 批量插入\n"
                + "     * <p>多库多表请调用处防止跨库事务</p>\n"
                + "     * <p>请注意list大小</p>\n"
                + "     *\n"
                + "     * @param list 待插入列表\n"
                + "     * @return 影响数据库行数\n"
                + "     */");
        method.setName("insertList");
        method.setReturnType(new FullyQualifiedJavaType("int"));
        method.getParameters().add(
                new Parameter(new FullyQualifiedJavaType("java.util.List<? extends " + introspectedTable.getBaseRecordType() + ">")
                        , "list"));
        interfaze.getMethods().add(method);
    }

    /**
     * 添加批量插入方法
     *
     * @param document          XML文档
     * @param introspectedTable 表配置信息
     */
    public static void generateInsertList(Document document,
                                          IntrospectedTable introspectedTable) {
        if (!isInsertListEnable(introspectedTable)) {
            return;
        }
        boolean useGeneratedKeys = introspectedTable.isUseGeneratedKeys();

        XmlElement answer = new XmlElement("insert");
        answer.addAttribute(new Attribute("id", "insertList"));
        FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType("java.util.List");
        answer.addAttribute(new Attribute("parameterType", parameterType.getFullyQualifiedName()));
        // 数据库自增则增加自增标签
        if (useGeneratedKeys) {
            answer.addAttribute(new Attribute("useGeneratedKeys", "true"));
            answer.addAttribute(new Attribute("keyProperty", "id"));
        }

        StringBuilder insertClause = new StringBuilder();
        insertClause.append("insert into ");
        insertClause.append(introspectedTable
                .getFullyQualifiedTableNameAtRuntime());
        insertClause.append(" (");


        List<IntrospectedColumn> columns = ListUtilities.removeIdentityAndGeneratedAlwaysColumns(
                introspectedTable.getAllColumns());
        // 数据库自增则忽略id字段
        if (useGeneratedKeys) {
            columns.removeIf(introspectedColumn -> introspectedColumn.getJavaProperty().equals("id"));
        }
        XmlElement foreach = new XmlElement("foreach");
        foreach.addAttribute(new Attribute("collection", "list"));
        foreach.addAttribute(new Attribute("item", "item"));
        foreach.addAttribute(new Attribute("index", "index"));
        foreach.addAttribute(new Attribute("separator", ","));
        foreach.addElement(new TextElement("("));
        for (int i = 0; i < columns.size(); i++) {
            IntrospectedColumn introspectedColumn = columns.get(i);

            insertClause.append(MyBatis3FormattingUtilities
                    .getEscapedColumnName(introspectedColumn));
            if (i + 1 < columns.size()) {
                insertClause.append(", ");
            }
            if (insertClause.length() > 80) {
                answer.addElement(new TextElement(insertClause.toString()));
                insertClause.setLength(0);
                OutputUtilities.xmlIndent(insertClause, 1);
            }
            String value = MyBatis3FormattingUtilities
                    .getParameterClause(introspectedColumn, "item.");
            String defaultValue;
            if ("gmtCreate".equals(introspectedColumn.getJavaProperty())
                    || "gmtModified".equals(introspectedColumn.getJavaProperty())) {
                defaultValue = CommonPluginUtil.getCurrentTimestamp(introspectedTable);
            } else if ("creator".equals(introspectedColumn.getJavaProperty())
                    || "modifier".equals(introspectedColumn.getJavaProperty())) {
                defaultValue = "'system'";
            } else if ("deleted".equals(introspectedColumn.getJavaProperty())) {
                defaultValue = "0";
            } else {
                defaultValue = "default";
            }
            XmlElement ifNotNull = new XmlElement("if");
            ifNotNull.addAttribute(new Attribute("test", introspectedColumn.getJavaProperty("item.") + " != null"));
            ifNotNull.addElement(new TextElement(value));
            XmlElement ifNull = new XmlElement("if");
            ifNull.addAttribute(new Attribute("test", introspectedColumn.getJavaProperty("item.") + " == null"));
            ifNull.addElement(new TextElement(defaultValue));
            foreach.addElement(ifNotNull);
            foreach.addElement(ifNull);
            if (i < columns.size() - 1) {
                foreach.addElement(new TextElement(","));
            }
        }
        insertClause.append(')');
        foreach.addElement(new TextElement(")"));
        answer.addElement(new TextElement(insertClause.toString()));
        answer.addElement(new TextElement("values"));
        answer.addElement(foreach);
        document.getRootElement().addElement(answer);
    }

    private static boolean isInsertListEnable(IntrospectedTable introspectedTable) {
        String property = introspectedTable.getTableConfigurationProperty(PROPERTY_INSERT_LIST_ENABLE);
        return !STRING_FALSE.equals(property);
    }

    public static boolean isUseGeneratedKeys(IntrospectedTable introspectedTable) {
        return introspectedTable.isUseGeneratedKeys();
    }


    /**
     * 标记片键信息
     *
     * @param context           上下文
     * @param introspectedTable 当前表配置
     */
    public static void markShardingKey(Context context, IntrospectedTable introspectedTable) {
        // 主动标记
        introspectedTable.getAllColumns().stream()
                .filter(column -> STRING_TRUE.equals(column.getProperties().getProperty(PROPERTY_INSERT_IS_SHARDING_KEY)))
                .forEach(column -> {
                    column.setShardingKey(true);
                    System.out.println("[INFO] 找到" + introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime() + "表标记的片键：" + column.getActualColumnName());
                });
        // 限制只能有一个片键
        List<IntrospectedColumn> shardingKeyColumns = introspectedTable.getAllColumns().stream().filter(IntrospectedColumn::isShardingKey).collect(Collectors.toList());
        if (shardingKeyColumns.size() > 1) {
            throw new RuntimeException(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime() + "表不能有多个片键");
        } else if (shardingKeyColumns.size() == 1) {
            introspectedTable.setShardingKeyColumn(shardingKeyColumns.get(0));
        }
    }

    /**
     * 获取主键及片键列列表
     *
     * @param introspectedTable 表信息
     * @return 主键及片键列列表
     */
    public static List<IntrospectedColumn> getPrimaryKeyColumnsWithShardingKey(IntrospectedTable introspectedTable) {
        List<IntrospectedColumn> primaryKeyColumns = new ArrayList<>(introspectedTable.getPrimaryKeyColumns());
        introspectedTable.getAllColumns().stream()
                .filter(IntrospectedColumn::isShardingKey)
                .filter(introspectedColumn -> !introspectedTable.getPrimaryKeyColumns().contains(introspectedColumn))
                .forEach(primaryKeyColumns::add);
        return primaryKeyColumns;
    }

    /**
     * 初始化Repo配置
     *
     * @param introspectedTable 表配置
     * @param properties        插件级别属性
     */
    public static void initRepoConfig(IntrospectedTable introspectedTable, Properties properties) {
        // 使用生成Repo
        String repoImplPackage = properties.getProperty(PROPERTY_REPO_IMPL_PACKAGE);
        String repoPackage = properties.getProperty(PROPERTY_REPO_PACKAGE);
        Boolean repoNoInterface = STRING_TRUE.equals(properties.getProperty(PROPERTY_REPO_NO_INTERFACE));
        String repoEnable = introspectedTable.getTableConfigurationProperty(PROPERTY_REPO_ENABLE);
        if (StringUtils.isNotEmpty(repoImplPackage) && StringUtils.isNotEmpty(repoPackage) && STRING_TRUE.equals(repoEnable)) {
            String repoSuffix = properties.getProperty(PROPERTY_REPO_SUFFIX);
            if (repoSuffix == null) {
                repoSuffix = "Repo";
            }
            GeneratedRepo generatedRepo = new GeneratedRepo(repoSuffix, repoPackage, repoImplPackage, repoNoInterface,
                    introspectedTable.getTableConfigurationProperty(PROPERTY_REPO_AUTOWIRED_SEQUENCE_NAME));
            introspectedTable.setGeneratedRepo(generatedRepo);
        }
    }

    /**
     * 添加Repo.java文件
     *
     * @param context            上下文
     * @param introspectedTable  表信息
     * @param generatedJavaFiles 需要额外生成的java文件
     */
    public static void addRepoJava(Context context, IntrospectedTable introspectedTable,
                                   List<GeneratedJavaFile> generatedJavaFiles) {
        GeneratedRepo generatedRepo = introspectedTable.getGeneratedRepo();
        if (generatedRepo == null) {
            return;
        }
        String shardingKeyType = introspectedTable.getShardingKeyType();
        String baseRecordType = introspectedTable.getBaseRecordType();
        String baseName = baseRecordType.substring(baseRecordType.lastIndexOf("."), baseRecordType.length() - 2);
        if (generatedRepo.getRepoNoInterface()) {
            TopLevelClass topLevelClass = new TopLevelClass(new FullyQualifiedJavaType(
                    generatedRepo.getRepoImplPackage() + baseName + generatedRepo.getRepoSuffix()));
            topLevelClass.setVisibility(JavaVisibility.PUBLIC);
            context.getCommentGenerator().addJavaFileComment(topLevelClass);
            topLevelClass.addImportedType("org.springframework.stereotype.Repository");
            topLevelClass.addAnnotation("@Repository");
            topLevelClass.addImportedType(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));
            topLevelClass.addImportedType(new FullyQualifiedJavaType(introspectedTable.getExampleType()));
            if (shardingKeyType == null) {
                topLevelClass.addImportedType(new FullyQualifiedJavaType(CLASS_BASE_NORMAL_REPO_IMPL));
                topLevelClass.setSuperClass(new FullyQualifiedJavaType(CLASS_BASE_NORMAL_REPO_IMPL + "<"
                        + introspectedTable.getBaseRecordType() + ", " + introspectedTable.getExampleType() + ">"));
            } else {
                topLevelClass.addImportedType(new FullyQualifiedJavaType(CLASS_BASE_SHARDED_REPO_IMPL));
                topLevelClass.setSuperClass(new FullyQualifiedJavaType(CLASS_BASE_SHARDED_REPO_IMPL + "<"
                        + introspectedTable.getBaseRecordType() + ", " + introspectedTable.getExampleType() + ", " + shardingKeyType + ">"));
            }

            Field mapper = new Field();
            mapper.setVisibility(JavaVisibility.PRIVATE);
            topLevelClass.addImportedType("org.springframework.beans.factory.annotation.Autowired");
            mapper.addAnnotation("@Autowired");
            FullyQualifiedJavaType mapperType = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType() + "Ext");
            topLevelClass.addImportedType(mapperType);
            mapper.setType(mapperType);
            mapper.setName("mapper");
            topLevelClass.addField(mapper);

            Method isUseGeneratedKeys = new Method();
            isUseGeneratedKeys.addAnnotation("@Override");
            isUseGeneratedKeys.setVisibility(JavaVisibility.PROTECTED);
            isUseGeneratedKeys.setReturnType(new FullyQualifiedJavaType("boolean"));
            isUseGeneratedKeys.setName("isUseGeneratedKeys");
            isUseGeneratedKeys.addBodyLine("return " + introspectedTable.isUseGeneratedKeys() + ";");
            topLevelClass.addMethod(isUseGeneratedKeys);

            Method isInsertListEnable = new Method();
            isInsertListEnable.addAnnotation("@Override");
            isInsertListEnable.setVisibility(JavaVisibility.PROTECTED);
            isInsertListEnable.setReturnType(new FullyQualifiedJavaType("boolean"));
            isInsertListEnable.setName("isInsertListEnable");
            isInsertListEnable.addBodyLine("return " + isInsertListEnable(introspectedTable) + ";");
            topLevelClass.addMethod(isInsertListEnable);

            if (!introspectedTable.isUseGeneratedKeys()) {
                Method getNewId = new Method();
                getNewId.addAnnotation("@Override");
                getNewId.setVisibility(JavaVisibility.PROTECTED);
                getNewId.setReturnType(new FullyQualifiedJavaType("java.lang.Long"));
                getNewId.setName("getNewId");
                String autowiredSequenceName = generatedRepo.getAutowiredSequenceName();
                if (autowiredSequenceName != null) {
                    Field sequence = new Field();
                    sequence.setVisibility(JavaVisibility.PRIVATE);
                    sequence.addAnnotation("@Autowired");
                    FullyQualifiedJavaType sequenceType = new FullyQualifiedJavaType("com.taobao.tddl.client.sequence.Sequence");
                    topLevelClass.addImportedType(sequenceType);
                    sequence.setType(sequenceType);
                    sequence.setName(autowiredSequenceName);
                    topLevelClass.addField(sequence);
                    getNewId.addBodyLine("return " + autowiredSequenceName + ".nextValue();");
                    topLevelClass.addMethod(getNewId);
                } else {
                    getNewId.addBodyLine("// TODO 补充获取主键方式");
                    getNewId.addBodyLine("return null;");
                    topLevelClass.addMethod(getNewId);
                }
            }

            GeneratedJavaFile generatedJavaFile = new GeneratedJavaFile(
                    topLevelClass,
                    context.getJavaClientGeneratorConfiguration().getTargetProject(),
                    context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                    context.getJavaFormatter());

            if (!FileUtil.isExistFile(generatedJavaFile.getTargetProject(),
                    generatedJavaFile.getTargetPackage(),
                    generatedJavaFile.getFileName())) {
                generatedJavaFiles.add(generatedJavaFile);
            }
            return;
        }
        Interface interfaze = new Interface(new FullyQualifiedJavaType(
                generatedRepo.getRepoPackage() + baseName + generatedRepo.getRepoSuffix()));
        interfaze.setVisibility(JavaVisibility.PUBLIC);
        context.getCommentGenerator().addJavaFileComment(interfaze);
        interfaze.addImportedType(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));
        interfaze.addImportedType(new FullyQualifiedJavaType(introspectedTable.getExampleType()));
        if (shardingKeyType == null) {
            interfaze.addImportedType(new FullyQualifiedJavaType(CLASS_I_NORMAL_REPO));
            interfaze.addSuperInterface(new FullyQualifiedJavaType(CLASS_I_NORMAL_REPO + "<"
                    + introspectedTable.getBaseRecordType() + ", " + introspectedTable.getExampleType() + ">"));
        } else {
            interfaze.addImportedType(new FullyQualifiedJavaType(CLASS_I_SHARDED_REPO));
            interfaze.addSuperInterface(new FullyQualifiedJavaType(CLASS_I_SHARDED_REPO + "<"
                    + introspectedTable.getBaseRecordType() + ", " + introspectedTable.getExampleType() + ", " + shardingKeyType + ">"));
        }

        GeneratedJavaFile generatedJavaFile = new GeneratedJavaFile(
                interfaze,
                context.getJavaClientGeneratorConfiguration().getTargetProject(),
                context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                context.getJavaFormatter());

        if (!FileUtil.isExistFile(generatedJavaFile.getTargetProject(),
                generatedJavaFile.getTargetPackage(),
                generatedJavaFile.getFileName())) {
            generatedJavaFiles.add(generatedJavaFile);
        }

        TopLevelClass topLevelClass = new TopLevelClass(new FullyQualifiedJavaType(
                generatedRepo.getRepoImplPackage() + baseName + generatedRepo.getRepoSuffix() + "Impl"));
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        context.getCommentGenerator().addJavaFileComment(topLevelClass);
        topLevelClass.addImportedType("org.springframework.stereotype.Repository");
        topLevelClass.addAnnotation("@Repository");
        topLevelClass.addImportedType(interfaze.getType());
        topLevelClass.addSuperInterface(interfaze.getType());
        topLevelClass.addImportedType(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));
        topLevelClass.addImportedType(new FullyQualifiedJavaType(introspectedTable.getExampleType()));
        if (shardingKeyType == null) {
            topLevelClass.addImportedType(new FullyQualifiedJavaType(CLASS_BASE_NORMAL_REPO_IMPL));
            topLevelClass.setSuperClass(new FullyQualifiedJavaType(CLASS_BASE_NORMAL_REPO_IMPL + "<"
                    + introspectedTable.getBaseRecordType() + ", " + introspectedTable.getExampleType() + ">"));
        } else {
            topLevelClass.addImportedType(new FullyQualifiedJavaType(CLASS_BASE_SHARDED_REPO_IMPL));
            topLevelClass.setSuperClass(new FullyQualifiedJavaType(CLASS_BASE_SHARDED_REPO_IMPL + "<"
                    + introspectedTable.getBaseRecordType() + ", " + introspectedTable.getExampleType() + ", " + shardingKeyType + ">"));
        }

        Field mapper = new Field();
        mapper.setVisibility(JavaVisibility.PRIVATE);
        topLevelClass.addImportedType("org.springframework.beans.factory.annotation.Autowired");
        mapper.addAnnotation("@Autowired");
        FullyQualifiedJavaType mapperType = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType() + "Ext");
        topLevelClass.addImportedType(mapperType);
        mapper.setType(mapperType);
        mapper.setName("mapper");
        topLevelClass.addField(mapper);

        Method isUseGeneratedKeys = new Method();
        isUseGeneratedKeys.addAnnotation("@Override");
        isUseGeneratedKeys.setVisibility(JavaVisibility.PROTECTED);
        isUseGeneratedKeys.setReturnType(new FullyQualifiedJavaType("boolean"));
        isUseGeneratedKeys.setName("isUseGeneratedKeys");
        isUseGeneratedKeys.addBodyLine("return " + introspectedTable.isUseGeneratedKeys() + ";");
        topLevelClass.addMethod(isUseGeneratedKeys);

        Method isInsertListEnable = new Method();
        isInsertListEnable.addAnnotation("@Override");
        isInsertListEnable.setVisibility(JavaVisibility.PROTECTED);
        isInsertListEnable.setReturnType(new FullyQualifiedJavaType("boolean"));
        isInsertListEnable.setName("isInsertListEnable");
        isInsertListEnable.addBodyLine("return " + isInsertListEnable(introspectedTable) + ";");
        topLevelClass.addMethod(isInsertListEnable);

        Method getNewId = new Method();
        getNewId.addAnnotation("@Override");
        getNewId.setVisibility(JavaVisibility.PROTECTED);
        getNewId.setReturnType(new FullyQualifiedJavaType("java.lang.Long"));
        getNewId.setName("getNewId");
        if (!introspectedTable.isUseGeneratedKeys()) {
            String autowiredSequenceName = generatedRepo.getAutowiredSequenceName();
            if (autowiredSequenceName != null) {
                Field sequence = new Field();
                sequence.setVisibility(JavaVisibility.PRIVATE);
                sequence.addAnnotation("@Autowired");
                FullyQualifiedJavaType sequenceType = new FullyQualifiedJavaType("com.taobao.tddl.client.sequence.Sequence");
                topLevelClass.addImportedType(sequenceType);
                sequence.setType(sequenceType);
                sequence.setName(autowiredSequenceName);
                topLevelClass.addField(sequence);
                getNewId.addBodyLine("return " + autowiredSequenceName + ".nextValue();");
            } else {
                getNewId.addBodyLine("// TODO 补充获取主键方式");
                getNewId.addBodyLine("return null;");
            }
        } else {
            getNewId.addBodyLine("return null;");
        }
        topLevelClass.addMethod(getNewId);

        generatedJavaFile = new GeneratedJavaFile(
                topLevelClass,
                context.getJavaClientGeneratorConfiguration().getTargetProject(),
                context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                context.getJavaFormatter());

        if (!FileUtil.isExistFile(generatedJavaFile.getTargetProject(),
                generatedJavaFile.getTargetPackage(),
                generatedJavaFile.getFileName())) {
            generatedJavaFiles.add(generatedJavaFile);
        }
    }

    public static String getIgnoredCriterionValueNullResult(IntrospectedTable introspectedTable, String defaultValue) {
        return introspectedTable.isIgnoreCriterionValueNull() ? "return;\n" : defaultValue;
    }

    public static String getCurrentTimestamp(IntrospectedTable introspectedTable) {
        int datePrecision = introspectedTable.getDatePrecision();
        return datePrecision > 0 ? "current_timestamp(" + datePrecision + ")" : "current_timestamp";
    }
}
