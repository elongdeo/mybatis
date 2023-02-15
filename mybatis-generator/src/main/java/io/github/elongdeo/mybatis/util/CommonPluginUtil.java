package io.github.elongdeo.mybatis.util;

import io.github.elongdeo.mybatis.constants.BaseDoPropertyEnum;
import org.apache.commons.lang3.StringUtils;
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
import org.mybatis.generator.config.GeneratedRepo;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.*;
import java.util.stream.Collectors;

import static io.github.elongdeo.mybatis.constants.PluginConstants.*;
import static io.github.elongdeo.mybatis.util.PluginConfigConstants.*;

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
     * @param properties
     * @param introspectedTable 表配置信息
     */
    public static void generateInsertList(Interface interfaze, Properties properties, IntrospectedTable introspectedTable) {
        if (!isInsertListEnable(properties, introspectedTable)) {
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
     * @param properties
     * @param introspectedTable 表配置信息
     */
    public static void generateInsertList(Document document,
                                          Properties properties, IntrospectedTable introspectedTable) {
        if (!isInsertListEnable(properties, introspectedTable)) {
            return;
        }
        boolean useGeneratedKeys = introspectedTable.isUseGeneratedKeys();
        List<IntrospectedColumn> operatorColumns = new ArrayList<>();
        operatorColumns.addAll(CommonPluginUtil.getSpecialColumns(properties, introspectedTable, BaseDoPropertyEnum.CREATOR));
        operatorColumns.addAll(CommonPluginUtil.getSpecialColumns(properties, introspectedTable, BaseDoPropertyEnum.MODIFIER));
        List<IntrospectedColumn> gmtColumns = new ArrayList<>();
        gmtColumns.addAll(CommonPluginUtil.getSpecialColumns(properties, introspectedTable, BaseDoPropertyEnum.GMT_CREATE));
        gmtColumns.addAll(CommonPluginUtil.getSpecialColumns(properties, introspectedTable, BaseDoPropertyEnum.GMT_MODIFIED));
        IntrospectedColumn enableColumn = CommonPluginUtil.getSpecialColumn(properties, introspectedTable, BaseDoPropertyEnum.ENABLE);

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
            if (gmtColumns.contains(introspectedColumn)) {
                defaultValue = CommonPluginUtil.getCurrentTimestamp(introspectedTable);
            } else if (operatorColumns.contains(introspectedColumn)) {
                defaultValue = "'system'";
            } else if (introspectedColumn == enableColumn) {
                defaultValue = CommonPluginUtil.isEnableLogicalFlip(properties, introspectedTable) ? "0" : "1";
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

    public static boolean isInsertListEnable(Properties properties, IntrospectedTable introspectedTable) {
        return StringUtility.isTrue(getTableAndPluginProperty(properties, introspectedTable, PROPERTY_INSERT_LIST_ENABLE, "true"));
    }

    /**
     * 标记片键信息
     *
     * @param introspectedTable 当前表配置
     */
    public static void markShardingKey(IntrospectedTable introspectedTable) {
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
        String repoImplPackage = CommonPluginUtil.getTableAndPluginProperty(properties, introspectedTable, PROPERTY_REPO_IMPL_PACKAGE, null);
        String repoPackage = CommonPluginUtil.getTableAndPluginProperty(properties, introspectedTable, PROPERTY_REPO_PACKAGE, null);
        boolean repoNoInterface = STRING_TRUE.equals(properties.getProperty(PROPERTY_REPO_NO_INTERFACE));
        boolean repoEnable = StringUtility.isTrue(CommonPluginUtil.getTableAndPluginProperty(properties, introspectedTable, PROPERTY_REPO_ENABLE, "false"));
        if (StringUtils.isNotEmpty(repoImplPackage) && repoEnable) {
            String repoSuffix = CommonPluginUtil.getTableAndPluginProperty(properties, introspectedTable, PROPERTY_REPO_SUFFIX, "Repo");
            GeneratedRepo generatedRepo = new GeneratedRepo(repoSuffix, repoPackage, repoImplPackage, repoNoInterface || StringUtils.isNotEmpty(repoPackage),
                    introspectedTable.getTableConfigurationProperty(PROPERTY_REPO_AUTOWIRED_SEQUENCE_NAME));
            introspectedTable.setGeneratedRepo(generatedRepo);
        }
    }

    public static String getIgnoredCriterionValueNullResult(IntrospectedTable introspectedTable, String defaultValue) {
        return introspectedTable.isIgnoreCriterionValueNull() ? "return;\n" : defaultValue;
    }

    public static String getCurrentTimestamp(IntrospectedTable introspectedTable) {
        int datePrecision = introspectedTable.getDatePrecision();
        return datePrecision > 0 ? "current_timestamp(" + datePrecision + ")" : "current_timestamp";
    }

    /**
     * 获取指定列
     *
     * @param properties        插件配置信息
     * @param introspectedTable 表配置信息
     * @param propertyEnum      基础字段枚举
     * @return 匹配的列列表
     */
    public static List<IntrospectedColumn> getSpecialColumns(Properties properties, IntrospectedTable introspectedTable, BaseDoPropertyEnum propertyEnum) {
        String columnType = propertyEnum.getPropertyName() + "ColumnName";
        String columnName = Optional.ofNullable(introspectedTable.getTableConfigurationProperty(columnType))
                .orElse(Optional.ofNullable(properties.getProperty(columnType)).orElse(propertyEnum.getColumnName()));
        if (StringUtils.isEmpty(columnName)) {
            return Collections.emptyList();
        }
        Set<String> columnNames = Arrays.stream(columnName.split(",")).collect(Collectors.toSet());
        return introspectedTable.getAllColumns().stream()
                .filter(column -> columnNames.contains(column.getActualColumnName())).collect(Collectors.toList());
    }

    /**
     * 获取指定列
     *
     * @param properties        插件配置信息
     * @param introspectedTable 表配置信息
     * @param propertyEnum      基础字段枚举
     * @return 匹配的列列表
     */
    public static IntrospectedColumn getSpecialColumn(Properties properties, IntrospectedTable introspectedTable, BaseDoPropertyEnum propertyEnum) {
        List<IntrospectedColumn> specialColumns = getSpecialColumns(properties, introspectedTable, propertyEnum);
        return specialColumns.stream().findFirst().orElse(null);
    }

    /**
     * 获取表或者插件的配置
     *
     * @param properties        插件配置信息
     * @param introspectedTable 表配置信息
     * @return 表或者插件的配置
     */
    public static String getTableAndPluginProperty(Properties properties, IntrospectedTable introspectedTable, String propertyName, String defaultValue) {
        return Optional.ofNullable(introspectedTable.getTableConfigurationProperty(propertyName))
                .orElse(properties.getProperty(propertyName, defaultValue));
    }

    /**
     * 获取生效条件的sql
     *
     * @param properties        插件配置信息
     * @param introspectedTable 表配置信息
     * @return 生效条件的sql
     */
    public static String getEnableConditionSql(Properties properties, IntrospectedTable introspectedTable) {
        String enableCondition = getTableAndPluginProperty(properties, introspectedTable, PROPERTY_ENABLE_CONDITION_SQL, null);
        if (enableCondition != null) {
            return enableCondition;
        }
        IntrospectedColumn specialColumn = CommonPluginUtil.getSpecialColumn(properties, introspectedTable, BaseDoPropertyEnum.ENABLE);
        String columnName = Optional.ofNullable(specialColumn).map(IntrospectedColumn::getActualColumnName).orElse("is_enable");
        boolean logicalFlip = isEnableLogicalFlip(properties, introspectedTable);
        return columnName + " = " + (logicalFlip ? "0" : "1");
    }

    /**
     * 获取删除的sql
     *
     * @param properties        插件配置信息
     * @param introspectedTable 表配置信息
     * @return 删除的sql
     */
    public static String getDeleteSql(Properties properties, IntrospectedTable introspectedTable) {
        String deleteSql = getTableAndPluginProperty(properties, introspectedTable, PROPERTY_DELETE_SQL, null);
        if (deleteSql != null) {
            return deleteSql;
        }
        IntrospectedColumn specialColumn = CommonPluginUtil.getSpecialColumn(properties, introspectedTable, BaseDoPropertyEnum.ENABLE);
        String columnName = Optional.ofNullable(specialColumn).map(IntrospectedColumn::getActualColumnName).orElse("is_enable");
        boolean logicalFlip = isEnableLogicalFlip(properties, introspectedTable);
        return columnName + " = " + (logicalFlip ? "1" : "0");
    }

    /**
     * 获取是否生效的逻辑是否翻转
     *
     * @param properties        插件配置信息
     * @param introspectedTable 表配置信息
     * @return 是否生效的逻辑是否翻转
     */
    public static boolean isEnableLogicalFlip(Properties properties, IntrospectedTable introspectedTable) {
        return StringUtility.isTrue(getTableAndPluginProperty(properties, introspectedTable, PROPERTY_ENABLE_COLUMN_LOGICAL_FLIP, "false"));
    }

    /**
     * 获取是否使用UUID生成主键
     *
     * @param properties        插件配置信息
     * @param introspectedTable 表配置信息
     * @return 是否使用UUID生成主键
     */
    public static boolean isGeneratedUuidKey(Properties properties, IntrospectedTable introspectedTable) {
        return StringUtility.isTrue(getTableAndPluginProperty(properties, introspectedTable, PROPERTY_GENERATED_UUID_KEY, "false"));
    }

    /**
     * 获取主键类型
     *
     * @param introspectedTable 表配置信息
     * @return 主键类型
     */
    public static String getPrimaryKeyType(IntrospectedTable introspectedTable) {
        IntrospectedColumn column = introspectedTable.getColumn("id");
        if (column == null) {
            throw new RuntimeException("table '" + introspectedTable.getFullyQualifiedTable() + "' does not have a primary key named 'id'");
        }
        return column.getFullyQualifiedJavaType().getFullyQualifiedName();
    }
}
