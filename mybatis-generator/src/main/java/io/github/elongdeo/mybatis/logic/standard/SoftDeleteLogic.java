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
        sb.append(CommonPluginUtil.getEnableCondition(properties, introspectedTable, false));
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
        if (!PluginConfig.softDeleteAble) {
            return;
        }
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
        element.addElement(createAndNotDeleteElement(properties, introspectedTable));
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
        isNotNullElement.addElement(createAndNotDeleteElement(properties, introspectedTable));
        XmlElement shardingKeyEq = getShardingKeyEq(introspectedTable, "");
        if (shardingKeyEq != null) {
            isNotNullElement.addElement(shardingKeyEq);
        }
        element.addElement(isNotNullElement);
        isNotNullElement = new XmlElement("if");
        isNotNullElement.addAttribute(new Attribute("test",
                "oredCriteria.size == 0"));
        isNotNullElement.addElement(createWhereNotDeleteElement(properties, introspectedTable));
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
        if (!PluginConfig.softDeleteAble) {
            return;
        }
        List<Element> elements = element.getElements();
        XmlElement setItem = null;
        int modifierItemIndex = -1;
        int gmtModifiedItemIndex = -1;
        IntrospectedColumn modifierColumn = CommonPluginUtil.getSpecialColumn(properties, introspectedTable, BaseDoPropertyEnum.MODIFIER);
        IntrospectedColumn gmtModifiedColumn = CommonPluginUtil.getSpecialColumn(properties, introspectedTable, BaseDoPropertyEnum.GMT_MODIFIED);
        String modifierIsNotNull = Optional.ofNullable(modifierColumn).map(IntrospectedColumn::getJavaProperty).orElse("modifier") + " != null";
        String gmtModifiedIsNotNull = Optional.ofNullable(gmtModifiedColumn).map(IntrospectedColumn::getJavaProperty).orElse("gmtModified") + " != null";
        for (Element e : elements) {
            if (e instanceof XmlElement && "set".equals(((XmlElement) e).getName())) {
                setItem = (XmlElement) e;
                for (int i = 0; i < setItem.getElements().size(); i++) {
                    Element element1 = setItem.getElements().get(i);
                    XmlElement xmlElement = (XmlElement) element1;
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
            }

        }

        if (modifierItemIndex != -1) {
            addXmlElementModifier(modifierColumn, setItem, modifierItemIndex, "");
        }

        if (gmtModifiedItemIndex != -1) {
            addGmtModifiedXmlElement(gmtModifiedColumn, setItem, gmtModifiedItemIndex, "", introspectedTable);
        }

        element.addElement(createAndNotDeleteElement(properties, introspectedTable));
    }

    /**
     * 替换mapper.xml中updateByExampleSelective
     *
     * @param element           当前方法xml代码
     * @param properties        插件配置信息
     * @param introspectedTable 表配置信息
     */
    public static void softDeleteSqlMapUpdateByExampleSelective(XmlElement element, Properties properties, IntrospectedTable introspectedTable) {
        if (!PluginConfig.softDeleteAble) {
            return;
        }
        List<Element> elements = element.getElements();
        XmlElement setItem = null;
        int modifierItemIndex = -1;
        int gmtModifiedItemIndex = -1;
        IntrospectedColumn creatorColumn = CommonPluginUtil.getSpecialColumn(properties, introspectedTable, BaseDoPropertyEnum.CREATOR);
        IntrospectedColumn gmtCreateColumn = CommonPluginUtil.getSpecialColumn(properties, introspectedTable, BaseDoPropertyEnum.GMT_CREATE);
        String creatorIsNotNull = "record." + Optional.ofNullable(creatorColumn).map(IntrospectedColumn::getJavaProperty).orElse("creator") + " != null";
        String gmtCreateIsNotNull = "record." + Optional.ofNullable(gmtCreateColumn).map(IntrospectedColumn::getJavaProperty).orElse("gmtCreate") + " != null";
        IntrospectedColumn modifierColumn = CommonPluginUtil.getSpecialColumn(properties, introspectedTable, BaseDoPropertyEnum.MODIFIER);
        IntrospectedColumn gmtModifiedColumn = CommonPluginUtil.getSpecialColumn(properties, introspectedTable, BaseDoPropertyEnum.GMT_MODIFIED);
        String modifierIsNotNull = "record." + Optional.ofNullable(modifierColumn).map(IntrospectedColumn::getJavaProperty).orElse("modifier") + " != null";
        String gmtModifiedIsNotNull = "record." + Optional.ofNullable(gmtModifiedColumn).map(IntrospectedColumn::getJavaProperty).orElse("gmtModified") + " != null";
        for (Element e : elements) {
            if (e instanceof XmlElement) {
                if ("set".equals(((XmlElement) e).getName())) {
                    setItem = (XmlElement) e;
                    setItem.getElements().removeIf(element1 -> {
                        if (element1 instanceof XmlElement) {
                            XmlElement xmlElement = (XmlElement) element1;
                            return xmlElement.getAttributes().stream()
                                    .anyMatch(attribute -> "test".equals(attribute.getName())
                                            && ("record.id != null".equals(attribute.getValue())
                                            || creatorIsNotNull.equals(attribute.getValue())
                                            || gmtCreateIsNotNull.equals(attribute.getValue())));
                        }
                        return false;
                    });
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
                }
            }

        }

        if (modifierItemIndex != -1) {
            addXmlElementModifier(modifierColumn, setItem, modifierItemIndex, "record.");
        }

        if (gmtModifiedItemIndex != -1) {
            addGmtModifiedXmlElement(gmtModifiedColumn, setItem, gmtModifiedItemIndex, "record.", introspectedTable);
        }

        XmlElement isdeletedElement = new XmlElement("if");
        isdeletedElement.addAttribute(new Attribute("test",
                "example.oredCriteria.size == 0"));
        isdeletedElement.addElement(createWhereNotDeleteElement(properties, introspectedTable));
        XmlElement shardingKeyEq = getShardingKeyEq(introspectedTable, "example.");
        if (shardingKeyEq != null) {
            isdeletedElement.addElement(shardingKeyEq);
        }
        element.addElement(isdeletedElement);
        isdeletedElement = new XmlElement("if");
        isdeletedElement.addAttribute(new Attribute("test",
                "example.oredCriteria.size != 0"));
        isdeletedElement.addElement(createAndNotDeleteElement(properties, introspectedTable));
        if (shardingKeyEq != null) {
            isdeletedElement.addElement(shardingKeyEq);
        }
        element.addElement(isdeletedElement);
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
        isdeletedElement.addElement(createWhereNotDeleteElement(properties, introspectedTable));
        XmlElement shardingKeyEq = getShardingKeyEq(introspectedTable, "");
        if (shardingKeyEq != null) {
            isdeletedElement.addElement(shardingKeyEq);
        }
        element.addElement(index, isdeletedElement);
        isdeletedElement = new XmlElement("if");
        isdeletedElement.addAttribute(new Attribute("test",
                "oredCriteria.size != 0"));
        isdeletedElement.addElement(createAndNotDeleteElement(properties, introspectedTable));
        if (shardingKeyEq != null) {
            isdeletedElement.addElement(shardingKeyEq);
        }
        element.addElement(index, isdeletedElement);
    }

    private static Element createAndNotDeleteElement(Properties properties, IntrospectedTable introspectedTable) {
        return new TextElement("and " + CommonPluginUtil.getEnableCondition(properties, introspectedTable, true));
    }

    private static Element createWhereNotDeleteElement(Properties properties, IntrospectedTable introspectedTable) {
        return new TextElement("where " + CommonPluginUtil.getEnableCondition(properties, introspectedTable, true));
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
