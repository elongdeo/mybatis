package io.github.elongdeo.mybatis.logic.standard;

import io.github.elongdeo.mybatis.constants.BaseDoPropertyEnum;
import io.github.elongdeo.mybatis.util.CommonPluginUtil;
import io.github.elongdeo.mybatis.constants.PluginConfig;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import java.util.*;


/**
 * 软删除
 *
 * @author dingyinlong
 * @date 2019/03/01
 */
public class SoftDeleteLogic {

    /**
     * 替换mapper.xml中deleteByPrimaryKey
     *
     * @param deleteElement     按主键删除的代码
     * @param introspectedTable 表配置信息
     */
    public static void softDeleteSqlMapDeleteByPrimaryKey(Properties properties, XmlElement deleteElement,
                                                          IntrospectedTable introspectedTable) {
        if (!PluginConfig.softDeleteAble) {
            // 删除原有属性
            deleteElement.getAttributes().removeIf(attr -> "parameterType".equals(attr.getName()));
            // 新增新的属性
            Attribute parameterTypeAttr = new Attribute("parameterType", introspectedTable
                    .getBaseRecordType());
            deleteElement.getAttributes().add(parameterTypeAttr);
            return;
        }
        // 重置xml内容
        deleteElement.setName("update");
        deleteElement.getElements().clear();
        deleteElement.getAttributes().clear();
        Attribute idAttr = new Attribute("id", "deleteByPrimaryKey");
        Attribute parameterTypeAttr = new Attribute("parameterType", introspectedTable
                .getBaseRecordType());

        deleteElement.getAttributes().add(idAttr);
        deleteElement.getAttributes().add(parameterTypeAttr);

        List<IntrospectedColumn> primaryKeyColumns = CommonPluginUtil.getPrimaryKeyColumnsWithShardingKey(introspectedTable);

        StringBuilder sb = new StringBuilder("update ");
        sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
        sb.append(" set ");
        // 逻辑删除字段
        sb.append(CommonPluginUtil.getDeleteSql(properties, introspectedTable));
        // 修改人字段(可选)
        List<IntrospectedColumn> modifierColumns = CommonPluginUtil.getSpecialColumns(properties, introspectedTable, BaseDoPropertyEnum.MODIFIER);
        for (IntrospectedColumn modifierColumn : modifierColumns) {
            sb.append(", ");
            sb.append(MyBatis3FormattingUtilities
                    .getEscapedColumnName(modifierColumn));
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities
                    .getParameterClause(modifierColumn));
        }
        // 修改时间字段(可选)
        List<IntrospectedColumn> gmtModifiedColumns = CommonPluginUtil.getSpecialColumns(properties, introspectedTable, BaseDoPropertyEnum.GMT_MODIFIED);
        for (IntrospectedColumn gmtModifiedColumn : gmtModifiedColumns) {
            sb.append(", ");
            sb.append(MyBatis3FormattingUtilities
                    .getEscapedColumnName(gmtModifiedColumn));
            sb.append(" = ");
            sb.append(CommonPluginUtil.getCurrentTimestamp(introspectedTable));
        }
        TextElement addElement = new TextElement(sb.toString());
        deleteElement.addElement(addElement);
        boolean and = false;
        for (IntrospectedColumn introspectedColumn : primaryKeyColumns) {
            sb.setLength(0);
            if (and) {
                sb.append("  and ");
            } else {
                sb.append("where ");
                and = true;
            }
            sb.append(MyBatis3FormattingUtilities
                    .getEscapedColumnName(introspectedColumn));
            sb.append(" = ");
            sb.append(MyBatis3FormattingUtilities
                    .getParameterClause(introspectedColumn));
            Element element = new TextElement(sb.toString());
            if (introspectedColumn.isShardingKey()) {
                XmlElement xmlElement = new XmlElement("if");
                xmlElement.addAttribute(new Attribute("test", introspectedColumn.getJavaProperty() + " != null"));
                xmlElement.addElement(element);
                element = xmlElement;
            }
            deleteElement.addElement(element);
        }
    }

    /**
     * 替换mapper.java中deleteByPrimaryKey的参数
     *
     * @param method            方法描述
     * @param introspectedTable 表配置信息
     */
    public static void softDeleteClientDeleteByPrimaryKey(Method method, IntrospectedTable introspectedTable) {
        Parameter parameter = new Parameter(new FullyQualifiedJavaType(
                introspectedTable.getBaseRecordType()), "record");
        method.getParameters().clear();
        method.addParameter(parameter);
    }

    /**
     * 替换mapper.xml中seleteByPrimaryKey
     *
     * @param properties        插件配置信息
     * @param introspectedTable 表配置信息
     * @param element           当前方法xml代码
     */
    public static void softDeleteSqlMapSelectByPrimaryKey(Properties properties, IntrospectedTable introspectedTable, XmlElement element) {
        if (!PluginConfig.softDeleteAble) {
            return;
        }
        element.addElement(createAndEnableElement(properties, introspectedTable));
    }

    /**
     * 替换mapper.xml中countByExample
     *
     * @param element           当前方法xml代码
     * @param properties        插件配置信息
     * @param introspectedTable 表配置
     */
    public static void softDeleteSqlMapCountByExample(XmlElement element, Properties properties, IntrospectedTable introspectedTable) {
        if (!PluginConfig.softDeleteAble) {
            return;
        }
        XmlElement isNotNullElement = new XmlElement("if");
        isNotNullElement.addAttribute(new Attribute("test",
                "oredCriteria.size != 0"));
        isNotNullElement.addElement(createAndEnableElement(properties, introspectedTable));
        XmlElement shardingKeyEq = getShardingKeyEq(introspectedTable, "");
        if (shardingKeyEq != null) {
            isNotNullElement.addElement(shardingKeyEq);
        }
        element.addElement(isNotNullElement);
        isNotNullElement = new XmlElement("if");
        isNotNullElement.addAttribute(new Attribute("test",
                "oredCriteria.size == 0"));
        isNotNullElement.addElement(createWhereEnableElement(properties, introspectedTable));
        if (shardingKeyEq != null) {
            isNotNullElement.addElement(shardingKeyEq);
        }
        element.addElement(isNotNullElement);
    }

    /**
     * 替换mapper.xml中updateByPrimaryKeySelective
     *
     * @param element           当前方法xml代码
     * @param properties        插件配置信息
     * @param introspectedTable 表配置信息
     */
    public static void softDeleteSqlMapUpdateByPrimaryKeySelective(XmlElement element, Properties properties, IntrospectedTable introspectedTable) {
        modifyUpdateSetXml(element, properties, introspectedTable, "");
        if (!PluginConfig.softDeleteAble) {
            return;
        }
        element.addElement(createAndEnableElement(properties, introspectedTable));
    }

    /**
     * 替换mapper.xml中updateByExampleSelective
     *
     * @param element           当前方法xml代码
     * @param properties        插件配置信息
     * @param introspectedTable 表配置信息
     */
    public static void softDeleteSqlMapUpdateByExampleSelective(XmlElement element, Properties properties, IntrospectedTable introspectedTable) {
        modifyUpdateSetXml(element, properties, introspectedTable, "record.");
        if (!PluginConfig.softDeleteAble) {
            return;
        }
        // 增加逻辑删除的条件
        XmlElement isdeletedElement = new XmlElement("if");
        isdeletedElement.addAttribute(new Attribute("test",
                "example.oredCriteria.size == 0"));
        isdeletedElement.addElement(createWhereEnableElement(properties, introspectedTable));
        XmlElement shardingKeyEq = getShardingKeyEq(introspectedTable, "example.");
        if (shardingKeyEq != null) {
            isdeletedElement.addElement(shardingKeyEq);
        }
        element.addElement(isdeletedElement);
        isdeletedElement = new XmlElement("if");
        isdeletedElement.addAttribute(new Attribute("test",
                "example.oredCriteria.size != 0"));
        isdeletedElement.addElement(createAndEnableElement(properties, introspectedTable));
        if (shardingKeyEq != null) {
            isdeletedElement.addElement(shardingKeyEq);
        }
        element.addElement(isdeletedElement);
    }

    private static void modifyUpdateSetXml(XmlElement element, Properties properties, IntrospectedTable introspectedTable, String prefix) {
        List<Element> elements = element.getElements();
        XmlElement setItem = null;
        int modifierItemIndex = -1;
        int gmtModifiedItemIndex = -1;
        IntrospectedColumn modifierColumn = CommonPluginUtil.getSpecialColumn(properties, introspectedTable, BaseDoPropertyEnum.MODIFIER);
        IntrospectedColumn gmtModifiedColumn = CommonPluginUtil.getSpecialColumn(properties, introspectedTable, BaseDoPropertyEnum.GMT_MODIFIED);
        String modifierIsNotNull = prefix + Optional.ofNullable(modifierColumn).map(IntrospectedColumn::getJavaProperty).orElse("modifier") + " != null";
        String gmtModifiedIsNotNull = prefix + Optional.ofNullable(gmtModifiedColumn).map(IntrospectedColumn::getJavaProperty).orElse("gmtModified") + " != null";
        for (Element e : elements) {
            if (e instanceof XmlElement) {
                if ("set".equals(((XmlElement) e).getName())) {
                    setItem = (XmlElement) e;
                }
            }
        }
        if(setItem == null){
            return;
        }
        // 删除id、创建人、创建时间更新字段
        IntrospectedColumn creatorColumn = CommonPluginUtil.getSpecialColumn(properties, introspectedTable, BaseDoPropertyEnum.CREATOR);
        IntrospectedColumn gmtCreateColumn = CommonPluginUtil.getSpecialColumn(properties, introspectedTable, BaseDoPropertyEnum.GMT_CREATE);
        String creatorIsNotNull = prefix + Optional.ofNullable(creatorColumn).map(IntrospectedColumn::getJavaProperty).orElse("creator") + " != null";
        String gmtCreateIsNotNull = prefix + Optional.ofNullable(gmtCreateColumn).map(IntrospectedColumn::getJavaProperty).orElse("gmtCreate") + " != null";
        setItem.getElements().removeIf(element1 -> {
            if (element1 instanceof XmlElement) {
                XmlElement xmlElement = (XmlElement) element1;
                return xmlElement.getAttributes().stream()
                        .anyMatch(attribute -> "test".equals(attribute.getName())
                                && ((prefix + "id != null").equals(attribute.getValue())
                                || creatorIsNotNull.equals(attribute.getValue())
                                || gmtCreateIsNotNull.equals(attribute.getValue())));
            }
            return false;
        });
        // 找到修改人、修改时间的节点，增加默认修改人和修改时间
        for (int i = 0; i < setItem.getElements().size(); i++) {
            XmlElement xmlElement = (XmlElement) setItem.getElements().get(i);
            for (Attribute att : xmlElement.getAttributes()) {
                if (modifierIsNotNull.equals(att.getValue())) {
                    modifierItemIndex = i;
                    break;
                }
                if (gmtModifiedIsNotNull.equals(att.getValue())) {
                    gmtModifiedItemIndex = i;
                    break;
                }
            }
        }
        if (modifierItemIndex != -1) {
            addXmlElementModifier(modifierColumn, setItem, modifierItemIndex, prefix);
        }
        if (gmtModifiedItemIndex != -1) {
            addGmtModifiedXmlElement(gmtModifiedColumn, setItem, gmtModifiedItemIndex, prefix, introspectedTable);
        }
        if (!PluginConfig.softDeleteAble) {
            return;
        }
        // 处理逻辑删除取反
        if(CommonPluginUtil.isEnableLogicalFlip(properties, introspectedTable)){
            IntrospectedColumn enableColumn = CommonPluginUtil.getSpecialColumn(properties, introspectedTable, BaseDoPropertyEnum.ENABLE);
            String javaPropertyName = Optional.ofNullable(enableColumn).map(IntrospectedColumn::getJavaProperty).orElse("enable");
            String enableIsNotNull = prefix + javaPropertyName + " != null";
            setItem.getElements().forEach(e -> {
                XmlElement xmlElement = (XmlElement) e;
                if(xmlElement.getAttributes().stream()
                        .anyMatch(attribute -> "test".equals(attribute.getName())
                                && enableIsNotNull.equals(attribute.getValue()))){
                    String newContent = null;
                    for (Element elementElement : xmlElement.getElements()) {
                        if(elementElement instanceof TextElement){
                            newContent = ((TextElement) elementElement).getContent().replace("#{","(#{").replace("},","}=0),");
                        }
                    }
                    if(newContent != null) {
                        xmlElement.getElements().clear();
                        xmlElement.getElements().add(new TextElement(newContent));
                    }
                }
            });
        }
    }

    /**
     * 替换mapper.xml中SelectByExample
     *
     * @param element           mapper根节点
     * @param properties        插件配置信息
     * @param introspectedTable 表配置
     */
    public static void softDeleteSqlMapSelectByExample(XmlElement element, Properties properties, IntrospectedTable introspectedTable) {
        if (!PluginConfig.softDeleteAble) {
            return;
        }
        Optional<Element> optional = element.getElements().stream().filter(element1 -> {
                    if (element1 instanceof XmlElement) {
                        XmlElement element2 = (XmlElement) element1;
                        return "if".equals(element2.getName()) && element2.getAttributes().stream()
                                .anyMatch(attribute -> "test".equals(attribute.getName())
                                        && "orderByClause != null".equals(attribute.getValue()));
                    } else {
                        return false;
                    }
                }
        ).findAny();
        if(!optional.isPresent()){
            return ;
        }
        Element orderByXml = optional.get();
        int index = element.getElements().indexOf(orderByXml);

        XmlElement isdeletedElement = new XmlElement("if");
        isdeletedElement.addAttribute(new Attribute("test",
                "oredCriteria.size == 0"));
        isdeletedElement.addElement(createWhereEnableElement(properties, introspectedTable));
        XmlElement shardingKeyEq = getShardingKeyEq(introspectedTable, "");
        if (shardingKeyEq != null) {
            isdeletedElement.addElement(shardingKeyEq);
        }
        element.addElement(index, isdeletedElement);
        isdeletedElement = new XmlElement("if");
        isdeletedElement.addAttribute(new Attribute("test",
                "oredCriteria.size != 0"));
        isdeletedElement.addElement(createAndEnableElement(properties, introspectedTable));
        if (shardingKeyEq != null) {
            isdeletedElement.addElement(shardingKeyEq);
        }
        element.addElement(index, isdeletedElement);
    }

    private static Element createAndEnableElement(Properties properties, IntrospectedTable introspectedTable) {
        return new TextElement("and " + CommonPluginUtil.getEnableConditionSql(properties, introspectedTable));
    }

    private static Element createWhereEnableElement(Properties properties, IntrospectedTable introspectedTable) {
        return new TextElement("where " + CommonPluginUtil.getEnableConditionSql(properties, introspectedTable));
    }

    /**
     * 增加修改时间
     *
     * @param gmtModifiedColumn 修改时间字段
     * @param setItem              被修改元素
     * @param gmtModifiedItemIndex 坐标
     * @param prefix               前缀
     * @param introspectedTable    表配置信息
     */
    private static void addGmtModifiedXmlElement(IntrospectedColumn gmtModifiedColumn, XmlElement setItem,
                                                 int gmtModifiedItemIndex, String prefix, IntrospectedTable introspectedTable) {
        XmlElement defaultGmtModified = new XmlElement("if");
        defaultGmtModified.addAttribute(new Attribute("test", prefix + Optional.ofNullable(gmtModifiedColumn).map(IntrospectedColumn::getJavaProperty).orElse("gmtModified") +" == null"));
        defaultGmtModified.addElement(new TextElement(Optional.ofNullable(gmtModifiedColumn).map(IntrospectedColumn::getActualColumnName).orElse("gmt_modified") + " = " + CommonPluginUtil.getCurrentTimestamp(introspectedTable) + ","));

        setItem.getElements().add(gmtModifiedItemIndex + 1, defaultGmtModified);
    }

    /**
     * 增加修改人
     *
     * @param modifierColumn 修改人字段
     * @param setItem           被修改元素
     * @param modifierItemIndex 坐标
     * @param prefix            前缀
     */
    private static void addXmlElementModifier(IntrospectedColumn modifierColumn, XmlElement setItem, int modifierItemIndex, String prefix) {
        XmlElement defaultModifier = new XmlElement("if");
        defaultModifier.addAttribute(new Attribute("test", prefix + Optional.ofNullable(modifierColumn).map(IntrospectedColumn::getJavaProperty).orElse("modifier") +" == null"));
        defaultModifier.addElement(new TextElement(Optional.ofNullable(modifierColumn).map(IntrospectedColumn::getActualColumnName).orElse("modifier") + " = 'system',"));
        setItem.getElements().add(modifierItemIndex + 1, defaultModifier);
    }

    private static XmlElement getShardingKeyEq(IntrospectedTable introspectedTable, String prefix) {
        IntrospectedColumn shardingKeyColumn = introspectedTable.getShardingKeyColumn();
        if (shardingKeyColumn == null) {
            return null;
        }
        XmlElement xmlElement = new XmlElement("if");
        xmlElement.addAttribute(new Attribute("test", prefix + "shardingKey != null"));
        xmlElement.addElement(new TextElement("and " + shardingKeyColumn.getActualColumnName() + " = #{" + prefix + "shardingKey}"));
        return xmlElement;
    }
}
