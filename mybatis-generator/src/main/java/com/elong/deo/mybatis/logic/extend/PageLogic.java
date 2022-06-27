package com.elong.deo.mybatis.logic.extend;

import com.elong.deo.mybatis.constants.PluginConfig;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.Context;

/**
 * 支持分页
 *
 * @author dingyinlong
 * @date 2019/03/01
 */
public class PageLogic {

    /**
     * 分页对象类型
     */
    private static final String FULLY_QUALIFIED_PAGE = "com.elong.deo.common.dal.DalPage";

    /**
     * Example增加page对象
     *
     * @param context           上下文
     * @param topLevelClass     Example类
     * @param introspectedTable 表配置信息
     */
    public static void addExamplePage(Context context, TopLevelClass topLevelClass,
                                      IntrospectedTable introspectedTable) {
        if (!PluginConfig.pageAble) {
            return;
        }
        topLevelClass.addImportedType(new FullyQualifiedJavaType(
                FULLY_QUALIFIED_PAGE));
        CommentGenerator commentGenerator = context.getCommentGenerator();
        Field field = new Field();
        field.setVisibility(JavaVisibility.PROTECTED);
        field.setType(new FullyQualifiedJavaType(FULLY_QUALIFIED_PAGE));
        field.setName("page");
        commentGenerator.addFieldComment(field, introspectedTable);
        topLevelClass.addField(field);
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName("setPage");
        method.addParameter(new Parameter(new FullyQualifiedJavaType(
                FULLY_QUALIFIED_PAGE), "page"));
        method.addBodyLine("this.page = page;");
        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);
        method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(new FullyQualifiedJavaType(
                FULLY_QUALIFIED_PAGE));
        method.setName("getPage");
        method.addBodyLine("return page;");
        commentGenerator.addGeneralMethodComment(method, introspectedTable);
        topLevelClass.addMethod(method);
    }

    /**
     * 生成分页语句
     *
     * @param context       上下文
     * @param parentElement xml文件root节点
     */
    public static void generatePageSql(Context context, XmlElement parentElement) {
        if (!PluginConfig.pageAble) {
            return;
        }
        // mysql分页语句后半部分
        XmlElement mysqlSuffixElement = new XmlElement("sql");
        context.getCommentGenerator().addComment(mysqlSuffixElement);
        mysqlSuffixElement.addAttribute(new Attribute("id",
                "PageSuffix"));
        XmlElement mysqlPageEnd = new XmlElement("if");
        mysqlPageEnd.addAttribute(new Attribute("test", "page != null"));
        mysqlPageEnd.addElement(new TextElement(
                "<![CDATA[ limit #{page.begin}, #{page.length} ]]>"));
        mysqlSuffixElement.addElement(mysqlPageEnd);
        parentElement.addElement(mysqlSuffixElement);
    }


    public static void includePageSql(XmlElement element) {
        if (!PluginConfig.pageAble) {
            return;
        }
        XmlElement mysqlSuffix = new XmlElement("include");
        mysqlSuffix.addAttribute(new Attribute("refid", "PageSuffix"));
        element.addElement(mysqlSuffix);
    }
}
