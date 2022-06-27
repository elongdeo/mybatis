package com.elong.deo.mybatis.logic.standard;

import com.elong.deo.mybatis.constants.PluginConfig;
import com.elong.deo.mybatis.util.CommonPluginUtil;
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
import org.mybatis.generator.config.Context;

import java.util.List;


/**
 * 软删除
 *
 * @author dingyinlong
 * @date 2019/03/01
 */
public class SoftDeleteLogic {
    private static final String AND_NOT_DELETE = "and is_deleted = 0";
    private static final String WHERE_NOT_DELETE = "where is_deleted = 0";

    /**
     * 替换mapper.xml中deleteByPrimaryKey
     *
     * @param context           上下文
     * @param deleteElement     按主键删除的代码
     * @param introspectedTable 表配置信息
     */
    public static void softDeleteSqlMapDeleteByPrimaryKey(Context context, XmlElement deleteElement,
                                                          IntrospectedTable introspectedTable) {
        if (!PluginConfig.softDeleteAble) {
            return;
        }
        // 重置xml内容
        deleteElement.setName("update");
        deleteElement.getElements().clear();
        deleteElement.getAttributes().clear();
        context.getCommentGenerator().addComment(deleteElement);
        Attribute idAttr = new Attribute("id", "deleteByPrimaryKey");
        Attribute parameterTypeAttr = new Attribute("parameterType", introspectedTable
                .getBaseRecordType());

        deleteElement.getAttributes().add(idAttr);
        deleteElement.getAttributes().add(parameterTypeAttr);

        List<IntrospectedColumn> primaryKeyColumns = CommonPluginUtil.getPrimaryKeyColumnsWithShardingKey(introspectedTable);

        StringBuilder sb = new StringBuilder("update ");
        sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
        sb.append(" set ");
        sb.append("is_deleted = 1,modifier=#{modifier,jdbcType=VARCHAR},");
        sb.append("gmt_Modified=" + CommonPluginUtil.getCurrentTimestamp(introspectedTable));
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
     * @param element 当前方法xml代码
     */
    public static void softDeleteSqlMapSeleteByPrimaryKey(XmlElement element) {
        if (!PluginConfig.softDeleteAble) {
            return;
        }
        element.addElement(createAndNotDeleteElement());
    }

    /**
     * 替换mapper.xml中countByExample
     *
     * @param element           当前方法xml代码
     * @param introspectedTable 表配置
     */
    public static void softDeleteSqlMapCountByExample(XmlElement element, IntrospectedTable introspectedTable) {
        if (!PluginConfig.softDeleteAble) {
            return;
        }
        XmlElement isNotNullElement = new XmlElement("if");
        isNotNullElement.addAttribute(new Attribute("test",
                "oredCriteria.size != 0"));
        isNotNullElement.addElement(createAndNotDeleteElement());
        XmlElement shardingKeyEq = getShardingKeyEq(introspectedTable, "");
        if (shardingKeyEq != null) {
            isNotNullElement.addElement(shardingKeyEq);
        }
        element.addElement(isNotNullElement);
        isNotNullElement = new XmlElement("if");
        isNotNullElement.addAttribute(new Attribute("test",
                "oredCriteria.size == 0"));
        isNotNullElement.addElement(createWhereNotDeleteElement());
        if (shardingKeyEq != null) {
            isNotNullElement.addElement(shardingKeyEq);
        }
        element.addElement(isNotNullElement);
    }

    /**
     * 替换mapper.xml中updateByPrimaryKeySelective
     *
     * @param element           当前方法xml代码
     * @param introspectedTable
     */
    public static void softDeleteSqlMapUpdateByPrimaryKeySelective(XmlElement element, IntrospectedTable introspectedTable) {
        if (!PluginConfig.softDeleteAble) {
            return;
        }
        List<Element> elements = element.getElements();
        XmlElement setItem = null;
        int modifierItemIndex = -1;
        int gmtModifiedItemIndex = -1;
        for (Element e : elements) {
            if (e instanceof XmlElement && "set" .equals(((XmlElement) e).getName())) {
                setItem = (XmlElement) e;
                for (int i = 0; i < setItem.getElements().size(); i++) {
                    Element element1 = setItem.getElements().get(i);
                    XmlElement xmlElement = (XmlElement) element1;
                    for (Attribute att : xmlElement.getAttributes()) {
                        if ("modifier != null" .equals(att.getValue())) {
                            modifierItemIndex = i;
                            break;
                        }

                        if ("gmtModified != null" .equals(att.getValue())) {
                            gmtModifiedItemIndex = i;
                            break;
                        }

                    }
                }
            }

        }

        if (modifierItemIndex != -1) {
            addXmlElementModifier(setItem, modifierItemIndex, "");
        }

        if (gmtModifiedItemIndex != -1) {
            addGmtModifiedXmlElement(setItem, gmtModifiedItemIndex, "", introspectedTable);
        }

        element.addElement(createAndNotDeleteElement());
    }

    /**
     * 替换mapper.xml中updateByExampleSelective
     *
     * @param element           当前方法xml代码
     * @param introspectedTable
     */
    public static void softDeleteSqlMapUpdateByExampleSelective(XmlElement element, IntrospectedTable introspectedTable) {
        if (!PluginConfig.softDeleteAble) {
            return;
        }
        List<Element> elements = element.getElements();
        XmlElement setItem = null;
        int modifierItemIndex = -1;
        int gmtModifiedItemIndex = -1;
        for (Element e : elements) {
            if (e instanceof XmlElement) {
                if ("set" .equals(((XmlElement) e).getName())) {
                    setItem = (XmlElement) e;
                    setItem.getElements().removeIf(element1 -> {
                        if (element1 instanceof XmlElement) {
                            XmlElement xmlElement = (XmlElement) element1;
                            return xmlElement.getAttributes().stream()
                                    .anyMatch(attribute -> "test" .equals(attribute.getName())
                                            && ("record.id != null" .equals(attribute.getValue())
                                            || "record.gmtCreate != null" .equals(attribute.getValue())
                                            || "record.creator != null" .equals(attribute.getValue())));
                        }
                        return false;
                    });
                    for (int i = 0; i < setItem.getElements().size(); i++) {
                        XmlElement xmlElement = (XmlElement) setItem.getElements().get(i);
                        for (Attribute att : xmlElement.getAttributes()) {
                            if ("record.modifier != null" .equals(att.getValue())) {
                                modifierItemIndex = i;
                                break;
                            }
                            if ("record.gmtModified != null" .equals(att.getValue())) {
                                gmtModifiedItemIndex = i;
                                break;
                            }
                        }
                    }
                }
            }

        }

        if (modifierItemIndex != -1) {
            addXmlElementModifier(setItem, modifierItemIndex, "record.");
        }

        if (gmtModifiedItemIndex != -1) {
            addGmtModifiedXmlElement(setItem, gmtModifiedItemIndex, "record.", introspectedTable);
        }

        XmlElement isdeletedElement = new XmlElement("if");
        isdeletedElement.addAttribute(new Attribute("test",
                "example.oredCriteria.size == 0"));
        isdeletedElement.addElement(createWhereNotDeleteElement());
        XmlElement shardingKeyEq = getShardingKeyEq(introspectedTable, "example.");
        if (shardingKeyEq != null) {
            isdeletedElement.addElement(shardingKeyEq);
        }
        element.addElement(isdeletedElement);
        isdeletedElement = new XmlElement("if");
        isdeletedElement.addAttribute(new Attribute("test",
                "example.oredCriteria.size != 0"));
        isdeletedElement.addElement(createAndNotDeleteElement());
        if (shardingKeyEq != null) {
            isdeletedElement.addElement(shardingKeyEq);
        }
        element.addElement(isdeletedElement);
    }

    /**
     * 替换mapper.xml中SelectByExample
     *
     * @param element           mapper根节点
     * @param introspectedTable 表配置
     */
    public static void softDeleteSqlMapSelectByExample(XmlElement element, IntrospectedTable introspectedTable) {
        if (!PluginConfig.softDeleteAble) {
            return;
        }
        Element orderByXml = element.getElements().stream().filter(element1 -> {
                    if (element1 instanceof XmlElement) {
                        XmlElement element2 = (XmlElement) element1;
                        return "if" .equals(element2.getName()) && element2.getAttributes().stream()
                                .anyMatch(attribute -> "test" .equals(attribute.getName())
                                        && "orderByClause != null" .equals(attribute.getValue()));
                    } else {
                        return false;
                    }
                }
        ).findAny().get();
        int index = element.getElements().indexOf(orderByXml);

        XmlElement isdeletedElement = new XmlElement("if");
        isdeletedElement.addAttribute(new Attribute("test",
                "oredCriteria.size == 0"));
        isdeletedElement.addElement(createWhereNotDeleteElement());
        XmlElement shardingKeyEq = getShardingKeyEq(introspectedTable, "");
        if (shardingKeyEq != null) {
            isdeletedElement.addElement(shardingKeyEq);
        }
        element.addElement(index, isdeletedElement);
        isdeletedElement = new XmlElement("if");
        isdeletedElement.addAttribute(new Attribute("test",
                "oredCriteria.size != 0"));
        isdeletedElement.addElement(createAndNotDeleteElement());
        if (shardingKeyEq != null) {
            isdeletedElement.addElement(shardingKeyEq);
        }
        element.addElement(index, isdeletedElement);
    }

    private static Element createAndNotDeleteElement() {
        return new TextElement(AND_NOT_DELETE);
    }

    private static Element createWhereNotDeleteElement() {
        return new TextElement(WHERE_NOT_DELETE);
    }

    /**
     * 增加修改时间
     *
     * @param setItem              被修改元素
     * @param gmtModifiedItemIndex 坐标
     * @param s
     * @param introspectedTable
     */
    private static void addGmtModifiedXmlElement(XmlElement setItem,
                                                 int gmtModifiedItemIndex, String s, IntrospectedTable introspectedTable) {
        XmlElement defaultGmtModified = new XmlElement("if");
        defaultGmtModified.addAttribute(new Attribute("test", s + "gmtModified == null"));
        defaultGmtModified.addElement(new TextElement("gmt_modified = " + CommonPluginUtil.getCurrentTimestamp(introspectedTable) + ","));

        setItem.getElements().add(gmtModifiedItemIndex + 1, defaultGmtModified);
    }

    /**
     * 增加修改人
     *
     * @param setItem           被修改元素
     * @param modifierItemIndex 坐标
     * @param prefix            前缀
     */
    private static void addXmlElementModifier(XmlElement setItem, int modifierItemIndex, String prefix) {
        XmlElement defaultModifier = new XmlElement("if");
        defaultModifier.addAttribute(new Attribute("test", prefix + "modifier == null"));
        defaultModifier.addElement(new TextElement("modifier = 'system',"));
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
