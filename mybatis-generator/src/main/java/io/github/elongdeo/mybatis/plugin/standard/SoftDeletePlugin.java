package io.github.elongdeo.mybatis.plugin.standard;

import io.github.elongdeo.mybatis.logic.standard.SoftDeleteLogic;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

/**
 * 逻辑删除
 * @author dingyinlong
 * @date 2019/03/01
 */
public class SoftDeletePlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * mapper.xml中deleteByPrimaryKey重写
     *
     * @param element           生成的xml根元素
     * @param introspectedTable 被生成的表信息
     * @return 执行结果
     */
    @Override
    public boolean sqlMapDeleteByPrimaryKeyElementGenerated(XmlElement element,
                                                            IntrospectedTable introspectedTable) {
        //原有的deleteByPrimaryKey不需要，进行自定义
        SoftDeleteLogic.softDeleteSqlMapDeleteByPrimaryKey(context, properties, element, introspectedTable);
        return true;
    }

    /**
     * mapper.java中deleteByPrimaryKey重写
     * @param method mapper.java中deleteByPrimaryKey方法
     * @param interfaze
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method,
                                                           Interface interfaze, IntrospectedTable introspectedTable) {
        SoftDeleteLogic.softDeleteClientDeleteByPrimaryKey(method, introspectedTable);
        return super.clientDeleteByPrimaryKeyMethodGenerated(method, interfaze,
                introspectedTable);
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
        SoftDeleteLogic.softDeleteSqlMapSelectByPrimaryKey(properties, introspectedTable, element);
        return super.sqlMapSelectByPrimaryKeyElementGenerated(element, introspectedTable);
    }
}
