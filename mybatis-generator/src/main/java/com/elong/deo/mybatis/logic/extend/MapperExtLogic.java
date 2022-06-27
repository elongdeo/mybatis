package com.elong.deo.mybatis.logic.extend;

import com.elong.deo.mybatis.constants.PluginConfig;
import com.elong.deo.mybatis.util.FileUtil;
import com.elong.deo.mybatis.constants.PluginConstants;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.XmlConstants;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.PropertyRegistry;

import java.util.List;

/**
 * 生成mapperExt
 *
 * @author dingyinlong
 * @date 2019/03/01
 */
public class MapperExtLogic {

    /**
     * Ext后缀
     */
    private static final String EXT_SUFFIX = "Ext";

    /**
     * 更新生成的xml文件映射mapper类名
     *
     * @param introspectedTable
     * @param parentElement
     */
    public static void updateDocumentNameSpace(IntrospectedTable introspectedTable,
                                               XmlElement parentElement) {
        if (!PluginConfig.mapperExtAble) {
            return;
        }
        Attribute namespaceAttribute = null;
        for (Attribute attribute : parentElement.getAttributes()) {
            if ("namespace".equals(attribute.getName())) {
                namespaceAttribute = attribute;
            }
        }
        parentElement.getAttributes().remove(namespaceAttribute);
        parentElement.getAttributes().add(
                new Attribute("namespace", introspectedTable.getMyBatis3JavaMapperType() + EXT_SUFFIX));
    }

    /**
     * 添加MapperExt.java文件
     *
     * @param context            上下文
     * @param introspectedTable  表信息
     * @param generatedJavaFiles 需要额外生成的java文件
     */
    public static void addMapperExtJava(Context context, IntrospectedTable introspectedTable,
                                        List<GeneratedJavaFile> generatedJavaFiles) {
        if (!PluginConfig.mapperExtAble) {
            return;
        }
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(
                introspectedTable.getMyBatis3JavaMapperType() + EXT_SUFFIX);
        Interface interfaze = new Interface(type);
        interfaze.setVisibility(JavaVisibility.PUBLIC);
        context.getCommentGenerator().addJavaFileComment(interfaze);

        FullyQualifiedJavaType baseInterfaze = new FullyQualifiedJavaType(
                introspectedTable.getMyBatis3JavaMapperType());
        interfaze.addSuperInterface(baseInterfaze);

        FullyQualifiedJavaType annotation = new FullyQualifiedJavaType(PluginConstants.ANNOTATION_MAPPER);
        interfaze.addAnnotation("@Mapper");
        interfaze.addImportedType(annotation);

        CompilationUnit compilationUnits = interfaze;
        GeneratedJavaFile generatedJavaFile = new GeneratedJavaFile(
                compilationUnits,
                context.getJavaClientGeneratorConfiguration().getTargetProject(),
                context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                context.getJavaFormatter());

        if (!FileUtil.isExistFile(generatedJavaFile.getTargetProject(),
                generatedJavaFile.getTargetPackage(),
                generatedJavaFile.getFileName())) {
            generatedJavaFiles.add(generatedJavaFile);
        }
    }

    /**
     * 添加MapperExt.xml文件
     *
     * @param context           上下文
     * @param introspectedTable 表信息
     * @param generatedXmlFiles 需要额外生成的xml文件
     */
    public static void addMapperExtXml(Context context, IntrospectedTable introspectedTable,
                                       List<GeneratedXmlFile> generatedXmlFiles) {
        if (!PluginConfig.mapperExtAble) {
            return;
        }
        String[] splitFile = introspectedTable.getMyBatis3XmlMapperFileName()
                .split("\\.");
        String fileNameExt = null;
        if (splitFile[0] != null) {
            fileNameExt = splitFile[0] + EXT_SUFFIX + ".xml";
        }

        if (FileUtil.isExistFile(context.getSqlMapGeneratorConfiguration()
                        .getTargetProject(),
                introspectedTable.getMyBatis3XmlMapperPackage(), fileNameExt)) {
            return;
        }

        Document document = new Document(
                XmlConstants.MYBATIS3_MAPPER_PUBLIC_ID,
                XmlConstants.MYBATIS3_MAPPER_SYSTEM_ID);

        XmlElement root = new XmlElement("mapper");
        document.setRootElement(root);
        String namespace = introspectedTable.getMyBatis3SqlMapNamespace() + EXT_SUFFIX;
        root.addAttribute(new Attribute("namespace", namespace));

        GeneratedXmlFile gxf = new GeneratedXmlFile(document, fileNameExt,
                introspectedTable.getMyBatis3XmlMapperPackage(), context
                .getSqlMapGeneratorConfiguration().getTargetProject(),
                false, context.getXmlFormatter());
        generatedXmlFiles.add(gxf);
    }
}
