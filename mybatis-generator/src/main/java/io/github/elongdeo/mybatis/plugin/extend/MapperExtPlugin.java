package io.github.elongdeo.mybatis.plugin.extend;

import io.github.elongdeo.mybatis.logic.extend.MapperExtLogic;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.Document;

import java.util.List;

/**
 * 生成Mapper扩展
 * @author dingyinlong
 * @date 2019/03/01
 */
public class MapperExtPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * 更新生成的xml文件映射mapper类名
     * @param document xml
     * @param introspectedTable 被生成的表信息
     * @return 是否允许生成
     */
    @Override
    public boolean sqlMapDocumentGenerated(Document document,
                                           IntrospectedTable introspectedTable) {
        // 更新生成的xml文件映射mapper类名
        MapperExtLogic.updateDocumentNameSpace(introspectedTable, document.getRootElement());
        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    /**
     * 生成XXMapperExt.java
     *
     * @param introspectedTable 被生成的表信息
     * @return 执行结果
     */
    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(
            IntrospectedTable introspectedTable) {
        List<GeneratedJavaFile> generatedJavaFiles = super.contextGenerateAdditionalJavaFiles();
        MapperExtLogic.addMapperExtJava(context, introspectedTable, generatedJavaFiles);
        return generatedJavaFiles;
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
        List<GeneratedXmlFile> generatedXmlFiles = super.contextGenerateAdditionalXmlFiles(introspectedTable);
        MapperExtLogic.addMapperExtXml(context, introspectedTable, generatedXmlFiles);
        return generatedXmlFiles;
    }
}
