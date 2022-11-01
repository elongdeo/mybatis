package io.github.elongdeo.mybatis.plugin;

import io.github.elongdeo.mybatis.logic.extend.MapperExtLogic;
import io.github.elongdeo.mybatis.logic.extend.PageLogic;
import io.github.elongdeo.mybatis.logic.standard.BooleanPropertyLogic;
import io.github.elongdeo.mybatis.logic.standard.FlushCacheLogic;
import io.github.elongdeo.mybatis.constants.BaseDoPropertyEnum;
import io.github.elongdeo.mybatis.constants.PluginConfig;
import io.github.elongdeo.mybatis.constants.PluginConstants;
import io.github.elongdeo.mybatis.logic.standard.BLOBPropertyLogic;
import io.github.elongdeo.mybatis.logic.standard.DoSuffixLogic;
import io.github.elongdeo.mybatis.util.CommonPluginUtil;
import io.github.elongdeo.mybatis.logic.standard.AssignedFieldLogic;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.*;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.internal.util.StringUtility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.github.elongdeo.mybatis.constants.PluginConstants.*;
import static io.github.elongdeo.mybatis.logic.extend.RepoLogic.addRepoJava;
import static io.github.elongdeo.mybatis.logic.standard.SoftDeleteLogic.*;
import static io.github.elongdeo.mybatis.util.CommonPluginUtil.initRepoConfig;
import static org.mybatis.generator.internal.util.StringUtility.isTrue;

/**
 * 定制化插件 主要功能: 1.数据库布尔类型的is_开头的字段生成的Do中属性去掉is前缀，防止反序列化失败 2.生成的Do增加原表字段注释 @see com.zxhy.common.dal.mybatis
 * .core.plugin.RhinoCommentGenerator
 * 3.自动生成MapperExt文件，将自定义的sql及方法放入Ext文件，实现动静分离。（好处是Ext文件不会被覆盖，表结构变更时重新生成即可，不会有丢失自定义方法的风险）
 * 4.实现分页查询功能 5.将生成的物理删除改成逻辑删除，查询时自动加上非逻辑删除条件 6.对OrderBy条件进行安全校验（防止sql注入）(已注释关闭) 7.insert方法默认补足基础6个字段(id,gmtCreate,
 * creator, gmtModified,modifier,isDeleted)，如果表缺少这些字段执行insert方法时会报错 8.屏蔽批量更新、删除方法，以及全字段更新等高风险操作
 *
 * @author dingyinlong
 * @date 2018年04月28日10:45:04
 */
public class Plugin extends PluginAdapter {

    /**
     * 是否使用Lombok的@Data注解
     */
    private static boolean enableAnnotationData = false;

    /**
     * 是否使用Lombok的@Accessors注解
     */
    private static boolean enableAnnotationAccessors = false;

    /**
     * 是否使用Lombok的@Builder注解
     */
    private static boolean enableAnnotationBuilder = false;

    /**
     * BaseDO字段重写
     */
    private static boolean baseDoOverride = false;

    private static final String SQL_SAFE_ORDER_BY_CHECK = "io.github.elongdeo.mybatis.util.SqlSafeCheckUtil";

    public static List<String> toRemoveProperties = Arrays.asList("id", "gmtCreate", "gmtModified", "creator",
            "modifier", "enable");
    public static List<String> toRemoveMethods = Arrays.asList("insert", "updateByPrimaryKey", "updateByPrimaryKeyWithBLOBs",
            "updateByExample", "updateByExampleWithBLOBs", "deleteByExample");
    public static List<String> toOverrideMethods = Arrays.asList("countByExample", "deleteByPrimaryKey",
            "insertSelective", "selectByExample", "selectByPrimaryKey",
            "updateByExampleSelective", "updateByPrimaryKeySelective", "insertList");

    /**
     * *Maper.xml不进行内容合并
     *
     * @param sqlMap
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean sqlMapGenerated(GeneratedXmlFile sqlMap,
                                   IntrospectedTable introspectedTable) {
        sqlMap.setMergeable(false);
        return true;
    }

    /**
     * 重写生成Example的方法
     */
    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass,
                                              IntrospectedTable introspectedTable) {
        // 增加sql安全校验方法
        addCheckOrderByMethod(topLevelClass, introspectedTable);
        // 设置父类
        IntrospectedColumn shardingKeyColumn = introspectedTable.getShardingKeyColumn();
        if (shardingKeyColumn != null) {
            topLevelClass.addImportedType(CLASS_BASE_SHARDED_EXAMPLE);
            topLevelClass.setSuperClass(CLASS_BASE_SHARDED_EXAMPLE + "<" + CommonPluginUtil.getPrimaryKeyType(introspectedTable) + ", " + introspectedTable.getShardingKeyType() + ">");
        } else {
            topLevelClass.addImportedType(CLASS_BASE_EXAMPLE);
            topLevelClass.setSuperClass(CLASS_BASE_EXAMPLE);
        }
        return super.modelExampleClassGenerated(topLevelClass,
                introspectedTable);
    }

    /**
     * 添加sql安全校验工具
     *
     * @param topLevelClass
     * @param introspectedTable
     */
    private void addCheckOrderByMethod(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        topLevelClass.addImportedType(new FullyQualifiedJavaType(SQL_SAFE_ORDER_BY_CHECK));
        List<Method> methodsList = topLevelClass.getMethods();
        String baseRecordType = introspectedTable.getBaseRecordType();
        String domainObejct = baseRecordType.substring(baseRecordType.lastIndexOf(".") + 1);
        StringBuilder checkLine = new StringBuilder();
        checkLine.append("if (SqlSafeCheckUtil.checkOrderByClause(orderByClause, ");
        checkLine.append(domainObejct + ".class, ALL_COLUMN_NAMES)){");
        StringBuilder checkLineSecond = new StringBuilder();
        checkLineSecond.append("}");
        for (Method item : methodsList) {
            if ("setOrderByClause".equals(item.getName())) {
                item.addBodyLine(0, checkLine.toString());
                item.addBodyLine(checkLineSecond.toString());
            }
        }
        // 添加所有字段名称
        Field field = new Field();
        field.setType(new FullyQualifiedJavaType("java.lang.String[]"));
        field.setName("ALL_COLUMN_NAMES");
        field.setFinal(true);
        field.setStatic(true);
        field.setVisibility(JavaVisibility.PRIVATE);
        String initCode = "new String[]{";
        initCode += introspectedTable.getAllColumns().stream().map(column -> "\"" + column.getActualColumnName() +
                "\"").collect(Collectors.joining(", "));
        initCode += "}";
        field.setInitializationString(initCode);
        topLevelClass.addField(field);
    }

    /**
     * 重写添删改Document的sql语句及属性
     *
     * @param document
     * @param introspectedTable
     * @return
     */

    @Override
    public boolean sqlMapDocumentGenerated(Document document,
                                           IntrospectedTable introspectedTable) {

        XmlElement parentElement = document.getRootElement();
        // 更新生成的xml文件映射mapper类名
        MapperExtLogic.updateDocumentNameSpace(introspectedTable, parentElement);
        // 删除无用元素
        removeUselessElements(parentElement, introspectedTable);
        // 优化Example_Where_Clause
        optimizeExampleWhereClause(parentElement, introspectedTable);
        // 修改insert的sql实现
        sqlMapInsertSelectiveGenerated(parentElement, introspectedTable);
        // 生成分页方法
        PageLogic.generatePageSql(context, parentElement);
        // 生成批量插入方法
        CommonPluginUtil.generateInsertList(document, properties, introspectedTable);
        // 标记flushCache=true
        FlushCacheLogic.markFlushCacheTrue(parentElement, introspectedTable);
        // 更新允许空值
        AssignedFieldLogic.enableUpdateNullField(parentElement);
        // 将UpdateByExample中的set元素抽取出来
        extractUpdateByExampleSetElement(document);
        // 将selectByExample中的choose元素抽取出来
        extractSelectByExampleChooseElement(document);
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    private void extractSelectByExampleChooseElement(Document document) {
        // 找到selectByExample元素
        Optional<Element> selectByExampleElementOptional = document.getRootElement().getElements()
                .stream()
                .filter(element -> element instanceof XmlElement
                        && ((XmlElement) element).getName().equals("select")
                        && ((XmlElement) element).getAttributes().stream().anyMatch(attr -> attr.getName().equals("id") && attr.getValue().equals("selectByExample")))
                .findFirst();
        if (!selectByExampleElementOptional.isPresent()) {
            return;
        }
        // 找到selectByExample中的choose元素
        XmlElement selectByExampleElement = (XmlElement) selectByExampleElementOptional.get();
        Optional<Element> selectByExampleChooseElementOptional = selectByExampleElement.getElements()
                .stream().filter(element -> element instanceof XmlElement
                        && ((XmlElement) element).getName().equals("choose")).findFirst();
        if (!selectByExampleChooseElementOptional.isPresent()) {
            return;
        }
        // 将selectByExample中的choose元素抽成Select_By_Example_Choose的sql
        XmlElement selectByExampleChooseElement = (XmlElement) selectByExampleChooseElementOptional.get();
        XmlElement selectByExampleChooseSqlElement = new XmlElement("sql");
        selectByExampleChooseSqlElement.addAttribute(new Attribute("id", "Select_By_Example_Choose"));
        selectByExampleChooseSqlElement.addElement(selectByExampleChooseElement);
        document.getRootElement().getElements().add(1, selectByExampleChooseSqlElement);
        // 将updateByExampleSelective中的set元素替换成Update_By_Example_Set的sql引用
        int idx = selectByExampleElement.getElements().indexOf(selectByExampleChooseElement);
        XmlElement includeElement = new XmlElement("include");
        includeElement.addAttribute(new Attribute("refid", "Select_By_Example_Choose"));
        selectByExampleElement.getElements().add(idx, includeElement);
        selectByExampleElement.getElements().remove(selectByExampleChooseElement);
    }


    private void extractUpdateByExampleSetElement(Document document) {
        // 找到updateByExampleSelective元素
        Optional<Element> updateByExampleSelectiveElementOptional = document.getRootElement().getElements()
                .stream()
                .filter(element -> element instanceof XmlElement
                        && ((XmlElement) element).getName().equals("update")
                        && ((XmlElement) element).getAttributes().stream().anyMatch(attr -> attr.getName().equals("id") && attr.getValue().equals("updateByExampleSelective")))
                .findFirst();
        if (!updateByExampleSelectiveElementOptional.isPresent()) {
            return;
        }
        // 找到updateByExampleSelective中的set元素
        XmlElement updateByExampleSelectiveElement = (XmlElement) updateByExampleSelectiveElementOptional.get();
        Optional<Element> updateByExampleSetElementOptional = updateByExampleSelectiveElement.getElements()
                .stream().filter(element -> element instanceof XmlElement
                        && ((XmlElement) element).getName().equals("set")).findFirst();
        if (!updateByExampleSetElementOptional.isPresent()) {
            return;
        }
        // 将updateByExampleSelective中的set元素抽成Update_By_Example_Set的sql
        XmlElement updateByExampleSetElement = (XmlElement) updateByExampleSetElementOptional.get();
        XmlElement updateByExampleSetSqlElement = new XmlElement("sql");
        updateByExampleSetSqlElement.addAttribute(new Attribute("id", "Update_By_Example_Set"));
        updateByExampleSetSqlElement.addElement(updateByExampleSetElement);
        document.getRootElement().getElements().add(1, updateByExampleSetSqlElement);
        // 将updateByExampleSelective中的set元素替换成Update_By_Example_Set的sql引用
        int idx = updateByExampleSelectiveElement.getElements().indexOf(updateByExampleSetElement);
        XmlElement includeElement = new XmlElement("include");
        includeElement.addAttribute(new Attribute("refid", "Update_By_Example_Set"));
        updateByExampleSelectiveElement.getElements().add(idx, includeElement);
        updateByExampleSelectiveElement.getElements().remove(updateByExampleSetElement);
    }

    /**
     * 修改insert的sql实现
     *
     * @param parentElement     根元素
     * @param introspectedTable
     */
    private void sqlMapInsertSelectiveGenerated(XmlElement parentElement, IntrospectedTable introspectedTable) {
        XmlElement oldElement = (XmlElement) parentElement.getElements().stream().filter(element -> {
            XmlElement xmlElement = (XmlElement) element;
            return "insert".equals(xmlElement.getName()) && xmlElement.getAttributes().stream().anyMatch(attribute ->
                    "id".equals(attribute.getName()) && "insertSelective".equals(attribute.getValue()));
        }).findFirst().get();
        this.sqlInsertSelectiveGenerated(oldElement, introspectedTable);
    }


    /**
     * 修改insert的sql实现
     *
     * @param element           insert元素
     * @param introspectedTable
     */
    private void sqlInsertSelectiveGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        List<Element> elements = element.getElements();

        XmlElement fieldItem = null;
        XmlElement valueItem = null;
        for (Element e : elements) {
            if (e instanceof XmlElement) {
                XmlElement xmlElement = (XmlElement) e;
                if ("trim".equals(xmlElement.getName())) {
                    for (Attribute arr : xmlElement.getAttributes()) {
                        if ("(".equals(arr.getValue())) {
                            fieldItem = xmlElement;
                            break;
                        }

                        if ("values (".equals(arr.getValue())) {
                            valueItem = xmlElement;
                            break;
                        }
                    }
                }

            }

        }

        if (fieldItem != null && valueItem != null) {
            // 创建人(可选)
            IntrospectedColumn specialColumn = CommonPluginUtil.getSpecialColumn(properties, introspectedTable, BaseDoPropertyEnum.CREATOR);
            if (specialColumn != null) {
                fieldItem.addElement(1, buildIfXml(specialColumn.getJavaProperty() + " == null", specialColumn.getActualColumnName() + ","));
                valueItem.addElement(1, buildIfXml(specialColumn.getJavaProperty() + " == null", "'system',"));
            }
            // 修改人(可选)
            specialColumn = CommonPluginUtil.getSpecialColumn(properties, introspectedTable, BaseDoPropertyEnum.MODIFIER);
            if (specialColumn != null) {
                fieldItem.addElement(1, buildIfXml(specialColumn.getJavaProperty() + " == null", specialColumn.getActualColumnName() + ","));
                valueItem.addElement(1, buildIfXml(specialColumn.getJavaProperty() + " == null", "'system',"));
            }
            // 创建时间(可选)
            specialColumn = CommonPluginUtil.getSpecialColumn(properties, introspectedTable, BaseDoPropertyEnum.GMT_CREATE);
            if (specialColumn != null) {
                fieldItem.addElement(1, buildIfXml(specialColumn.getJavaProperty() + " == null", specialColumn.getActualColumnName() + ","));
                valueItem.addElement(1, buildIfXml(specialColumn.getJavaProperty() + " == null", CommonPluginUtil.getCurrentTimestamp(introspectedTable) + ","));
            }
            // 修改时间(可选)
            specialColumn = CommonPluginUtil.getSpecialColumn(properties, introspectedTable, BaseDoPropertyEnum.GMT_MODIFIED);
            if (specialColumn != null) {
                fieldItem.addElement(1, buildIfXml(specialColumn.getJavaProperty() + " == null", specialColumn.getActualColumnName() + ","));
                valueItem.addElement(1, buildIfXml(specialColumn.getJavaProperty() + " == null", CommonPluginUtil.getCurrentTimestamp(introspectedTable) + ","));
            }
            // 逻辑删除
            specialColumn = CommonPluginUtil.getSpecialColumn(properties, introspectedTable, BaseDoPropertyEnum.ENABLE);
            if (specialColumn != null) {
                fieldItem.addElement(1, buildIfXml(specialColumn.getJavaProperty() + " == null", specialColumn.getActualColumnName() + ","));
                valueItem.addElement(1, buildIfXml(specialColumn.getJavaProperty() + " == null", CommonPluginUtil.isEnableLogicalFlip(properties, introspectedTable) ? "0" : "1" + ","));
            } else {
                fieldItem.addElement(1, buildIfXml("enable == null", "is_enable,"));
                fieldItem.addElement(1, buildIfXml("enable == null", CommonPluginUtil.isEnableLogicalFlip(properties, introspectedTable) ? "0" : "1" + ","));
            }
        }
    }

    private static XmlElement buildIfXml(String test, String value) {
        XmlElement xmlElement = new XmlElement("if");
        xmlElement.addAttribute(new Attribute("test", test));
        xmlElement.addElement(new TextElement(value));
        return xmlElement;
    }


    private void removeUselessElements(XmlElement parentElement, IntrospectedTable introspectedTable) {
        List<XmlElement> toDeleteElements = new ArrayList<XmlElement>();
        for (Element element : parentElement.getElements()) {
            XmlElement xmlElement = (XmlElement) element;
            if ("insert".equals(xmlElement.getName())) {
                for (Attribute attribute : xmlElement.getAttributes()) {
                    if ("id".equals(attribute.getName()) && "insert".equals(attribute.getValue())) {
                        toDeleteElements.add(xmlElement);
                        break;
                    }
                }

            }
            if ("update".equals(xmlElement.getName())) {
                for (Attribute attribute : xmlElement.getAttributes()) {
                    if ("id".equals(attribute.getName())) {
                        if ("updateByPrimaryKey".equals(attribute.getValue())
                                || "updateByPrimaryKeyWithBLOBs".equals(attribute.getValue())
                                || "updateByExample".equals(attribute.getValue())
                                || "updateByExampleWithBLOBs".equals(attribute.getValue())) {
                            toDeleteElements.add(xmlElement);
                            break;
                        }
                    }
                }

            }
            if (!introspectedTable.getTableConfiguration().isUpdateByExampleStatementEnabled()) {
                if ("sql".equals(xmlElement.getName())) {
                    for (Attribute attribute : xmlElement.getAttributes()) {
                        if ("id".equals(attribute.getName()) && "Update_By_Example_Where_Clause".equals(
                                attribute.getValue())) {
                            toDeleteElements.add(xmlElement);
                            break;
                        }
                    }
                }
            }
        }
        parentElement.getElements().removeAll(toDeleteElements);
    }

    /**
     * 优化Example_Where_Clause
     * 兼容以下异常情况：
     * 1.new Example()后不createCriteria
     * 2.example.createCriteria()后不and条件（补1=1）
     *
     * @param parentElement
     * @param introspectedTable
     */
    private void optimizeExampleWhereClause(XmlElement parentElement, IntrospectedTable introspectedTable) {
        for (Element element : parentElement.getElements()) {
            XmlElement xmlElement = (XmlElement) element;
            if ("sql".equals(xmlElement.getName())) {
                for (Attribute attribute : xmlElement.getAttributes()) {
                    if ("id".equals(attribute.getName()) && "Example_Where_Clause".equals(attribute.getValue())) {
                        // <foreach close=")" collection="oredCriteria" item="criteria" open="(" separator="or">
                        XmlElement forElement = (XmlElement) (((XmlElement) (xmlElement.getElements().get(0))).getElements().get(0));
                        List<Attribute> attributes = forElement.getAttributes();
                        attributes.add(new Attribute("close", ")"));
                        attributes.add(new Attribute("open", "("));
                        // <if test="criteria.valid">
                        XmlElement oldIfCriteriaValid = (XmlElement) forElement.getElements().get(0);
                        forElement.getElements().clear();
                        XmlElement chooseElement = new XmlElement("choose");
                        forElement.getElements().add(chooseElement);
                        // <when test="criteria.valid">
                        oldIfCriteriaValid.setName("when");
                        chooseElement.addElement(oldIfCriteriaValid);
                        /*
                        <otherwise>
                          1 = 1
                        </otherwise>
                         */
                        chooseElement.addElement(new XmlElement("otherwise") {{
                            addElement(new TextElement("1 = 1"));
                        }});
                        break;
                    }
                }
            }
        }
    }


    /**
     * selectByPrimaryKey重写
     *
     * @param element           生成的xml根元素
     * @param introspectedTable 被生成的表信息
     * @return 执行结果
     */
    @Override
    public boolean sqlMapSelectByPrimaryKeyElementGenerated(XmlElement element,
                                                            IntrospectedTable introspectedTable) {
        softDeleteSqlMapSelectByPrimaryKey(properties, introspectedTable, element);
        return super.sqlMapSelectByPrimaryKeyElementGenerated(element, introspectedTable);
    }

    /**
     * updateByPrimaryKeySelective重写
     *
     * @param element           生成的xml根元素
     * @param introspectedTable 被生成的表信息
     * @return 执行结果
     */
    @Override
    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {
        softDeleteSqlMapUpdateByPrimaryKeySelective(element, properties, introspectedTable);
        return super.sqlMapUpdateByPrimaryKeySelectiveElementGenerated(element,
                introspectedTable);
    }

    /**
     * updateByPrimaryKey重写
     *
     * @param element           生成的xml根元素
     * @param introspectedTable 被生成的表信息
     * @return 执行结果
     */
    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {
        softDeleteSqlMapUpdateByPrimaryKeySelective(element, properties, introspectedTable);
        return super.sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(
                element, introspectedTable);
    }

    @Override
    public boolean sqlMapUpdateByExampleSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        softDeleteSqlMapUpdateByExampleSelective(element, properties, introspectedTable);
        return super.sqlMapUpdateByExampleSelectiveElementGenerated(element, introspectedTable);
    }

    /**
     * deleteByPrimaryKey
     *
     * @param element           生成的xml根元素
     * @param introspectedTable 被生成的表信息
     * @return 执行结果
     */
    @Override
    public boolean sqlMapDeleteByPrimaryKeyElementGenerated(XmlElement element,
                                                            IntrospectedTable introspectedTable) {
        //原有的deleteByPrimaryKey不需要，进行自定义
        softDeleteSqlMapDeleteByPrimaryKey(properties, element, introspectedTable);
        return true;
    }

    @Override
    public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method,
                                                           Interface interfaze, IntrospectedTable introspectedTable) {
        softDeleteClientDeleteByPrimaryKey(method, introspectedTable);
        return super.clientDeleteByPrimaryKeyMethodGenerated(method, interfaze,
                introspectedTable);
    }

    @Override
    public boolean clientGenerated(Interface interfaze,
                                   TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        List<Method> methods = interfaze.getMethods();
        methods.removeIf(method -> toRemoveMethods.contains(method.getName()));
        // 继承基类
        if (introspectedTable.getShardingKeyColumn() == null) {
            interfaze.addImportedType(new FullyQualifiedJavaType(CLASS_NORMAL_MAPPER));
            interfaze.addSuperInterface(new FullyQualifiedJavaType("INormalMapper<" + CommonPluginUtil.getPrimaryKeyType(introspectedTable) + "," + introspectedTable.getBaseRecordType() + "," + introspectedTable.getExampleType() + ">"));
        } else {
            interfaze.addImportedType(new FullyQualifiedJavaType(CLASS_SHARDED_MAPPER));
            interfaze.addSuperInterface(new FullyQualifiedJavaType("IShardedMapper<" + CommonPluginUtil.getPrimaryKeyType(introspectedTable) + "," + introspectedTable.getBaseRecordType() + "," + introspectedTable.getExampleType() + "," + introspectedTable.getShardingKeyType() + ">"));
        }
        // 添加批量插入方法
        CommonPluginUtil.generateInsertList(interfaze, properties, introspectedTable);
        methods.stream().filter(method -> toOverrideMethods.contains(method.getName()))
                .forEach(method -> method.addAnnotation("@Override"));
        return super.clientGenerated(interfaze, topLevelClass,
                introspectedTable);
    }

    @Override
    public boolean sqlMapSelectByExampleWithBLOBsElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {

        sqlMapSelectByExampleElementGeneratedExt(element, introspectedTable);

        return super.sqlMapSelectByExampleWithBLOBsElementGenerated(element,
                introspectedTable);
    }

    /**
     * selectByExample
     *
     * @param element           生成的xml根元素
     * @param introspectedTable 被生成的表信息
     * @return 执行结果
     */
    @Override
    public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {

        sqlMapSelectByExampleElementGeneratedExt(element, introspectedTable);

        return super.sqlMapSelectByExampleWithoutBLOBsElementGenerated(element,
                introspectedTable);
    }

    private void sqlMapSelectByExampleElementGeneratedExt(XmlElement element, IntrospectedTable introspectedTable) {
        softDeleteSqlMapSelectByExample(element, properties, introspectedTable);

        PageLogic.includePageSql(element);

        AssignedFieldLogic.dealSelectFields(element, introspectedTable);
    }

    /**
     * countByExample
     *
     * @param element           生成的xml根元素
     * @param introspectedTable 被生成的表信息
     * @return 执行结果
     */
    @Override
    public boolean sqlMapCountByExampleElementGenerated(XmlElement element,
                                                        IntrospectedTable introspectedTable) {
        softDeleteSqlMapCountByExample(element, properties, introspectedTable);
        return super.sqlMapCountByExampleElementGenerated(element,
                introspectedTable);
    }

    /**
     * 生成XXMapperExt.xml
     *
     * @param introspectedTable 被生成的表信息
     * @return 执行结果
     */
    @Override
    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles(
            IntrospectedTable introspectedTable) {
        List<GeneratedXmlFile> generatedXmlFiles = new ArrayList<>();
        MapperExtLogic.addMapperExtXml(context, introspectedTable, generatedXmlFiles);
        return generatedXmlFiles;
    }

    /**
     * 生成XXMapperExt.java/Repo.java
     *
     * @param introspectedTable 被生成的表信息
     * @return 执行结果
     */
    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(
            IntrospectedTable introspectedTable) {
        List<GeneratedJavaFile> generatedJavaFiles = new ArrayList<>();
        MapperExtLogic.addMapperExtJava(context, introspectedTable, generatedJavaFiles);
        addRepoJava(context, properties, introspectedTable, generatedJavaFiles);
        return generatedJavaFiles;
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * 屏蔽按条件删除的方法
     *
     * @param element
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean sqlMapDeleteByExampleElementGenerated(XmlElement element,
                                                         IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        enableAnnotationAccessors = isTrue(properties.getProperty(PROPERTY_ENABLE_ANNOTATION_ACCESSORS));
        enableAnnotationData = isTrue(properties.getProperty(PROPERTY_ENABLE_ANNOTATION_DATA));
        enableAnnotationBuilder = isTrue(properties.getProperty(PROPERTY_ENABLE_ANNOTATION_BUILDER));
        baseDoOverride = isTrue(properties.getProperty(PROPERTY_BASE_DO_OVERRIDE));
        PluginConfig.forceString = isTrue(properties.getProperty(PROPERTY_ENABLE_FORCE_STRING));
        // 处理基础字段别名映射
        for (BaseDoPropertyEnum propertyEnum : BaseDoPropertyEnum.values()) {
            for (IntrospectedColumn specialColumn : CommonPluginUtil.getSpecialColumns(properties, introspectedTable, propertyEnum)) {
                specialColumn.setJavaProperty(propertyEnum.getPropertyName());
            }
        }
        // 将boolean/tinyint(1)类型带is_前缀字段生成的属性值改为不带is前缀,防止反序列化失败
        BooleanPropertyLogic.dealBooleanColumn(introspectedTable);
        // 处理大字符
        BLOBPropertyLogic.dealBLOBColumn(introspectedTable);
        // 替换DO为空
        DoSuffixLogic.dealFileName(introspectedTable);
        // 标记片键
        CommonPluginUtil.markShardingKey(introspectedTable);
        // 使用数据库自增值
        introspectedTable.setUseGeneratedKeys(StringUtility.isTrue(CommonPluginUtil.getTableAndPluginProperty(properties, introspectedTable, PluginConstants.PROPERTY_INSERT_USE_GENERATED_KEYS, "false")));
        if (introspectedTable.isUseGeneratedKeys()) {
            introspectedTable.getTableConfiguration().setGeneratedKey(new GeneratedKey("id", "JDBC", true, "JDBC"));
        }
        // 初始化Repo配置
        initRepoConfig(introspectedTable, properties);
        // 解析是否忽略Criterion值为空
        introspectedTable.setIgnoreCriterionValueNull(STRING_TRUE.equals(properties.getProperty(PROPERTY_IGNORE_CRITERION_VALUE_NULL)));
        // 解析创建修改时间精度
        introspectedTable.setDatePrecision(Optional.ofNullable(properties.getProperty(PROPERTY_DATE_PRECISION)).filter(StringUtils::isNumeric).map(Integer::valueOf).orElse(0));
        Optional<Integer> tableDatePrecisionOptional = Optional.ofNullable(introspectedTable.getTableConfigurationProperty(PROPERTY_DATE_PRECISION)).filter(StringUtils::isNumeric).map(Integer::valueOf);
        if (tableDatePrecisionOptional.isPresent()) {
            introspectedTable.setDatePrecision(tableDatePrecisionOptional.orElse(0));
        }
    }

    /**
     * 生成toString方法
     *
     * @param introspectedTable
     * @param topLevelClass
     */
    private void generateToString(IntrospectedTable introspectedTable,
                                  TopLevelClass topLevelClass) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getStringInstance());
        method.setName("toString");
        if (introspectedTable.isJava5Targeted()) {
            method.addAnnotation("@Override");
        }

        method.addBodyLine("StringBuilder sb = new StringBuilder();");
        method.addBodyLine("sb.append(getClass().getSimpleName());");
        method.addBodyLine("sb.append(\" [\");");
        method.addBodyLine("sb.append(\"Hash = \").append(hashCode());");
        StringBuilder sb = new StringBuilder();
        for (Field field : topLevelClass.getFields()) {
            String property = field.getName();
            sb.setLength(0);
            sb.append("sb.append(\"").append(", ").append(property)
                    .append("=\")").append(".append(").append(property)
                    .append(");");
            method.addBodyLine(sb.toString());
        }

        method.addBodyLine("sb.append(\"]\");");
        method.addBodyLine("sb.append(\", from super class \");");
        method.addBodyLine("sb.append(super.toString());");
        method.addBodyLine("return sb.toString();");

        topLevelClass.addMethod(method);
    }

    private void generateAnnotationData(TopLevelClass topLevelClass, boolean callSuper) {
        topLevelClass.addImportedType("lombok.Data");
        topLevelClass.addImportedType("lombok.EqualsAndHashCode");
        topLevelClass.addAnnotation("@Data");
        topLevelClass.addAnnotation("@EqualsAndHashCode(callSuper = " + callSuper + ")");
    }

    private void generateAnnotationBuilder(TopLevelClass topLevelClass) {
        topLevelClass.addImportedType("lombok.Builder");
        topLevelClass.addAnnotation("@Builder");
    }

    private void generateAnnotationAccessors(TopLevelClass topLevelClass) {
        topLevelClass.addImportedType("lombok.experimental.Accessors");
        topLevelClass.addAnnotation("@Accessors(chain = true)");
    }


    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        // 是否使用@Data注解，不使用则需要生成ToString方法
        if (enableAnnotationData) {
            generateAnnotationData(topLevelClass, true);
        } else {
            generateToString(introspectedTable, topLevelClass);
        }
        // 是否使用@Builder注解
        if (enableAnnotationBuilder) {
            generateAnnotationBuilder(topLevelClass);
        }
        // 是否开启链式注解
        if (enableAnnotationAccessors) {
            generateAnnotationAccessors(topLevelClass);
        }
        // 设置父类
        if (introspectedTable.getShardingKeyColumn() == null) {
            topLevelClass.addImportedType(CLASS_BASE_DO);
            topLevelClass.setSuperClass(CLASS_BASE_DO + "<" + CommonPluginUtil.getPrimaryKeyType(introspectedTable) + ">");
        } else {
            topLevelClass.addImportedType(CLASS_BASE_SHARDED_DO);
            topLevelClass.setSuperClass(CLASS_BASE_SHARDED_DO + "<" + CommonPluginUtil.getPrimaryKeyType(introspectedTable) + ", " + introspectedTable.getShardingKeyType() + ">");
            Method method = new Method();
            method.addAnnotation("@Override");
            method.setVisibility(JavaVisibility.PUBLIC);
            method.setName("setShardingKey");
            method.addParameter(new Parameter(new FullyQualifiedJavaType(introspectedTable.getShardingKeyType()), "shardingKey"));
            method.addBodyLine("this." + introspectedTable.getShardingKeyColumn().getJavaProperty() + " = shardingKey;");
            topLevelClass.addMethod(method);
        }
        return true;
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(TopLevelClass topLevelClass,
                                                      IntrospectedTable introspectedTable) {
        // 是否使用@Data注解，不使用则需要生成ToString方法
        if (enableAnnotationData) {
            generateAnnotationData(topLevelClass, true);
        } else {
            generateToString(introspectedTable, topLevelClass);
        }
        // 是否使用@Builder注解
        if (enableAnnotationBuilder) {
            generateAnnotationBuilder(topLevelClass);
        }
        // 是否开启链式注解
        if (enableAnnotationAccessors) {
            generateAnnotationAccessors(topLevelClass);
        }
        return true;
    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        // 是否使用@Data注解，不使用则需要生成ToString方法
        if (enableAnnotationData) {
            generateAnnotationData(topLevelClass, false);
        } else {
            generateToString(introspectedTable, topLevelClass);
        }
        // 是否使用@Builder注解
        if (enableAnnotationBuilder) {
            generateAnnotationBuilder(topLevelClass);
        }
        // 是否开启链式注解
        if (enableAnnotationAccessors) {
            generateAnnotationAccessors(topLevelClass);
        }
        return true;
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return !(toRemoveProperties.contains(introspectedColumn.getJavaProperty()) && !baseDoOverride);
    }

    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass,
                                              IntrospectedColumn introspectedColumn,
                                              IntrospectedTable introspectedTable,
                                              ModelClassType modelClassType) {
        // 如果启用了Lombok的@Data注解或是基础字段，则不生成Setter方法
        if (enableAnnotationData) {
            return false;
        }
        if (baseDoOverride && toRemoveProperties.contains(introspectedColumn.getJavaProperty())) {
            method.addAnnotation("@Override");
        }
        return !(toRemoveProperties.contains(introspectedColumn.getJavaProperty()) && !baseDoOverride);
    }

    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass,
                                              IntrospectedColumn introspectedColumn,
                                              IntrospectedTable introspectedTable,
                                              ModelClassType modelClassType) {
        // 如果启用了Lombok的@Data注解或是基础字段，则不生成Getter方法
        if (enableAnnotationData) {
            return false;
        }
        if (baseDoOverride && toRemoveProperties.contains(introspectedColumn.getJavaProperty())) {
            method.addAnnotation("@Override");
        }
        return !(toRemoveProperties.contains(introspectedColumn.getJavaProperty()) && !baseDoOverride);
    }

    public static void main(String[] args) {
        String config = Plugin.class.getClassLoader()
                .getResource("generatorConfig.xml").getFile();
        String[] arg = {"-configfile", config, "-overwrite"};
        ShellRunner.main(arg);
    }
}
