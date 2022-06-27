package com.elong.deo.mybatis.plugin.extend;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

import static com.elong.deo.mybatis.logic.extend.PageLogic.*;

/**
 * 生成Mapper扩展
 * @author dingyinlong
 * @date 2019/03/01
 */
public class PagePlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * 重写生成Example文件
     */
    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass,
                                              IntrospectedTable introspectedTable) {
        // 增加分页信息
        addExamplePage(context, topLevelClass, introspectedTable);
        return super.modelExampleClassGenerated(topLevelClass,
                introspectedTable);
    }

    /**
     * 重写生成xml文件
     * @param document
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean sqlMapDocumentGenerated(Document document,
                                           IntrospectedTable introspectedTable) {
        XmlElement parentElement = document.getRootElement();
        // 生成分页方法
        generatePageSql(context,parentElement);
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    @Override
    public boolean sqlMapSelectByExampleWithBLOBsElementGenerated(
            XmlElement element, IntrospectedTable introspectedTable) {
        includePageSql(element);
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
        includePageSql(element);
        return super.sqlMapSelectByExampleWithoutBLOBsElementGenerated(element,
                introspectedTable);
    }
}
