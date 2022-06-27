package com.elong.deo.mybatis.logic.standard;

import com.elong.deo.mybatis.plugin.RhinoPlugin;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.Optional;


/**
 * 指定字段
 * @author dingyinlong
 * @date 2019-11-22 21:39:20
 */
public class AssignedFieldLogic {


    /**
     * 启用允许更新null
     *
     * @param parentElement mapper根节点
     */
    public static void enableUpdateNullField(XmlElement parentElement) {
        parentElement.getElements().stream().filter(element -> {
            XmlElement xmlElement = (XmlElement) element;
            return "update".equals(xmlElement.getName()) && xmlElement.getAttributes().stream().anyMatch(attr -> "id".equals(attr.getName()) && attr.getValue().startsWith("updateBy"));
        }).forEach(element -> {
            XmlElement xmlElement = (XmlElement) element;
            xmlElement = (XmlElement) (xmlElement.getElements().stream().filter(element1 -> element1 instanceof XmlElement && "set".equals(((XmlElement) element1).getName())).findFirst().orElse(null));
            if (xmlElement != null) {
                xmlElement.getElements().forEach(element1 -> {
                    if (element1 instanceof XmlElement) {
                        XmlElement ifXmlElement = (XmlElement) element1;
                        if ("if".equals(ifXmlElement.getName())) {
                            Optional<Attribute> test = ifXmlElement.getAttributes().stream().filter(attr -> "test".equals(attr.getName())).findFirst();
                            if (test.isPresent()) {
                                Attribute attr = test.get();
                                String fieldName = getUpdateTestFieldName(attr.getValue());
                                if (!RhinoPlugin.toRemoveProperties.contains(fieldName)) {
                                    ifXmlElement.getAttributes().remove(attr);
                                    boolean updateByExample = ((XmlElement) element).getAttributes().stream().anyMatch(attr1 -> "id".equals(attr1.getName()) && "updateByExampleSelective".equals(attr1.getValue()));
                                    StringBuilder newValue = new StringBuilder(attr.getValue());
                                    newValue.append(" or ");
                                    if (updateByExample) {
                                        newValue.append("record.");
                                    }
                                    newValue.append("enableUpdateNullFields.contains('").append(fieldName).append("')");
                                    ifXmlElement.getAttributes().add(new Attribute(attr.getName(), newValue.toString()));
                                }
                            }
                        }
                    }
                });
            }
        });
    }

    private static String getUpdateTestFieldName(String testValue) {
        int i = testValue.indexOf(".");
        if (i >= 0) {
            testValue = testValue.substring(i + 1);
        }
        return testValue.substring(0, testValue.length() - 8);
    }


    public static void dealSelectFields(XmlElement element, IntrospectedTable introspectedTable) {
        int index;
        Element includeBaseColumnList = element.getElements().stream().filter(element1 -> {
                    if (element1 instanceof XmlElement) {
                        XmlElement element2 = (XmlElement) element1;
                        return "include".equals(element2.getName()) && element2.getAttributes().stream()
                                .anyMatch(attribute -> "refid".equals(attribute.getName())
                                        && "Base_Column_List".equals(attribute.getValue()));
                    } else {
                        return false;
                    }
                }
        ).findAny().get();
        index = element.getElements().indexOf(includeBaseColumnList);
        XmlElement choose = new XmlElement("choose");
        element.addElement(index, choose);
        element.getElements().remove(includeBaseColumnList);
        XmlElement when = new XmlElement("when");
        when.addAttribute(new Attribute("test", "_parameter != null and selectFields.size > 0"));
        XmlElement trim = new XmlElement("trim");
        when.addElement(trim);
        trim.addAttribute(new Attribute("suffixOverrides", ","));
        introspectedTable.getAllColumns().forEach(column -> {
            XmlElement ifXml = new XmlElement("if");
            trim.addElement(ifXml);
            ifXml.addAttribute(new Attribute("test", "selectFields.contains('" + column.getJavaProperty() + "')"));
            ifXml.addElement(new TextElement(column.getActualColumnName() + ","));
        });
        choose.addElement(when);
        XmlElement otherwise = new XmlElement("otherwise");
        otherwise.addElement(includeBaseColumnList);
        choose.addElement(otherwise);
    }
}
