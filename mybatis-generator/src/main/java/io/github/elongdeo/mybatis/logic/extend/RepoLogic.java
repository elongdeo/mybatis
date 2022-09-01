package io.github.elongdeo.mybatis.logic.extend;

import io.github.elongdeo.mybatis.util.CommonPluginUtil;
import io.github.elongdeo.mybatis.util.FileUtil;
import io.github.elongdeo.mybatis.constants.PluginConstants;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.GeneratedRepo;
import org.mybatis.generator.config.PropertyRegistry;

import java.util.List;
import java.util.Properties;

/**
 * 生成mapperExt
 *
 * @author dingyinlong
 * @date 2019/03/01
 */
public class RepoLogic {


    /**
     * 添加Repo.java文件
     *
     * @param context            上下文
     * @param properties
     * @param introspectedTable  表信息
     * @param generatedJavaFiles 需要额外生成的java文件
     */
    public static void addRepoJava(Context context, Properties properties, IntrospectedTable introspectedTable,
                                   List<GeneratedJavaFile> generatedJavaFiles) {
        GeneratedRepo generatedRepo = introspectedTable.getGeneratedRepo();
        if (generatedRepo == null) {
            return;
        }
        String shardingKeyType = introspectedTable.getShardingKeyType();
        String baseRecordType = introspectedTable.getBaseRecordType();
        String baseName = baseRecordType.substring(baseRecordType.lastIndexOf("."), baseRecordType.length() - 2);
        if (generatedRepo.getRepoNoInterface()) {
            TopLevelClass topLevelClass = new TopLevelClass(new FullyQualifiedJavaType(
                    generatedRepo.getRepoImplPackage() + baseName + generatedRepo.getRepoSuffix()));
            topLevelClass.setVisibility(JavaVisibility.PUBLIC);
            context.getCommentGenerator().addJavaFileComment(topLevelClass);
            topLevelClass.addImportedType("org.springframework.stereotype.Repository");
            topLevelClass.addAnnotation("@Repository");
            topLevelClass.addImportedType(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));
            topLevelClass.addImportedType(new FullyQualifiedJavaType(introspectedTable.getExampleType()));
            if (shardingKeyType == null) {
                topLevelClass.addImportedType(new FullyQualifiedJavaType(PluginConstants.CLASS_BASE_NORMAL_REPO_IMPL));
                topLevelClass.setSuperClass(new FullyQualifiedJavaType(PluginConstants.CLASS_BASE_NORMAL_REPO_IMPL + "<"
                        + CommonPluginUtil.getPrimaryKeyType(introspectedTable) + ", "
                        + introspectedTable.getBaseRecordType() + ", " + introspectedTable.getExampleType() + ">"));
            } else {
                topLevelClass.addImportedType(new FullyQualifiedJavaType(PluginConstants.CLASS_BASE_SHARDED_REPO_IMPL));
                topLevelClass.setSuperClass(new FullyQualifiedJavaType(PluginConstants.CLASS_BASE_SHARDED_REPO_IMPL + "<"
                        + CommonPluginUtil.getPrimaryKeyType(introspectedTable) + ", "
                        + introspectedTable.getBaseRecordType() + ", " + introspectedTable.getExampleType() + ", " + shardingKeyType + ">"));
            }

            Field mapper = new Field();
            mapper.setVisibility(JavaVisibility.PRIVATE);
            topLevelClass.addImportedType("org.springframework.beans.factory.annotation.Autowired");
            mapper.addAnnotation("@Autowired");
            FullyQualifiedJavaType mapperType = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType() + "Ext");
            topLevelClass.addImportedType(mapperType);
            mapper.setType(mapperType);
            mapper.setName("mapper");
            topLevelClass.addField(mapper);

            Method isUseGeneratedKeys = new Method();
            isUseGeneratedKeys.addAnnotation("@Override");
            isUseGeneratedKeys.setVisibility(JavaVisibility.PROTECTED);
            isUseGeneratedKeys.setReturnType(new FullyQualifiedJavaType("boolean"));
            isUseGeneratedKeys.setName("isUseGeneratedKeys");
            isUseGeneratedKeys.addBodyLine("return " + introspectedTable.isUseGeneratedKeys() + ";");
            topLevelClass.addMethod(isUseGeneratedKeys);

            Method isInsertListEnable = new Method();
            isInsertListEnable.addAnnotation("@Override");
            isInsertListEnable.setVisibility(JavaVisibility.PROTECTED);
            isInsertListEnable.setReturnType(new FullyQualifiedJavaType("boolean"));
            isInsertListEnable.setName("isInsertListEnable");
            isInsertListEnable.addBodyLine("return " + CommonPluginUtil.isInsertListEnable(properties, introspectedTable) + ";");
            topLevelClass.addMethod(isInsertListEnable);

            if (!introspectedTable.isUseGeneratedKeys()) {
                Method getNewId = new Method();
                getNewId.addAnnotation("@Override");
                getNewId.setVisibility(JavaVisibility.PROTECTED);
                getNewId.setReturnType(new FullyQualifiedJavaType(CommonPluginUtil.getPrimaryKeyType(introspectedTable)));
                getNewId.setName("getNewId");
                String autowiredSequenceName = generatedRepo.getAutowiredSequenceName();
                if (autowiredSequenceName != null) {
                    Field sequence = new Field();
                    sequence.setVisibility(JavaVisibility.PRIVATE);
                    sequence.addAnnotation("@Autowired");
                    FullyQualifiedJavaType sequenceType = new FullyQualifiedJavaType("com.taobao.tddl.client.sequence.Sequence");
                    topLevelClass.addImportedType(sequenceType);
                    sequence.setType(sequenceType);
                    sequence.setName(autowiredSequenceName);
                    topLevelClass.addField(sequence);
                    getNewId.addBodyLine("return " + autowiredSequenceName + ".nextValue();");
                    topLevelClass.addMethod(getNewId);
                } else if (CommonPluginUtil.isGeneratedUuidKey(properties, introspectedTable)) {
                    getNewId.addBodyLine("return UUID.randomUUID().toString().replace(\"-\", \"\");");
                    topLevelClass.addImportedType("java.util.UUID");
                    topLevelClass.addMethod(getNewId);
                } else {
                    getNewId.addBodyLine("// TODO 补充获取主键方式");
                    getNewId.addBodyLine("return null;");
                    topLevelClass.addMethod(getNewId);
                }
            }

            GeneratedJavaFile generatedJavaFile = new GeneratedJavaFile(
                    topLevelClass,
                    context.getJavaClientGeneratorConfiguration().getTargetProject(),
                    context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                    context.getJavaFormatter());

            if (!FileUtil.isExistFile(generatedJavaFile.getTargetProject(),
                    generatedJavaFile.getTargetPackage(),
                    generatedJavaFile.getFileName())) {
                generatedJavaFiles.add(generatedJavaFile);
            }
            return;
        }
        Interface interfaze = new Interface(new FullyQualifiedJavaType(
                generatedRepo.getRepoPackage() + baseName + generatedRepo.getRepoSuffix()));
        interfaze.setVisibility(JavaVisibility.PUBLIC);
        context.getCommentGenerator().addJavaFileComment(interfaze);
        interfaze.addImportedType(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));
        interfaze.addImportedType(new FullyQualifiedJavaType(introspectedTable.getExampleType()));
        if (shardingKeyType == null) {
            interfaze.addImportedType(new FullyQualifiedJavaType(PluginConstants.CLASS_I_NORMAL_REPO));
            interfaze.addSuperInterface(new FullyQualifiedJavaType(PluginConstants.CLASS_I_NORMAL_REPO + "<"
                    + introspectedTable.getBaseRecordType() + ", " + introspectedTable.getExampleType() + ">"));
        } else {
            interfaze.addImportedType(new FullyQualifiedJavaType(PluginConstants.CLASS_I_SHARDED_REPO));
            interfaze.addSuperInterface(new FullyQualifiedJavaType(PluginConstants.CLASS_I_SHARDED_REPO + "<"
                    + introspectedTable.getBaseRecordType() + ", " + introspectedTable.getExampleType() + ", " + shardingKeyType + ">"));
        }

        GeneratedJavaFile generatedJavaFile = new GeneratedJavaFile(
                interfaze,
                context.getJavaClientGeneratorConfiguration().getTargetProject(),
                context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                context.getJavaFormatter());

        if (!FileUtil.isExistFile(generatedJavaFile.getTargetProject(),
                generatedJavaFile.getTargetPackage(),
                generatedJavaFile.getFileName())) {
            generatedJavaFiles.add(generatedJavaFile);
        }

        TopLevelClass topLevelClass = new TopLevelClass(new FullyQualifiedJavaType(
                generatedRepo.getRepoImplPackage() + baseName + generatedRepo.getRepoSuffix() + "Impl"));
        topLevelClass.setVisibility(JavaVisibility.PUBLIC);
        context.getCommentGenerator().addJavaFileComment(topLevelClass);
        topLevelClass.addImportedType("org.springframework.stereotype.Repository");
        topLevelClass.addAnnotation("@Repository");
        topLevelClass.addImportedType(interfaze.getType());
        topLevelClass.addSuperInterface(interfaze.getType());
        topLevelClass.addImportedType(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));
        topLevelClass.addImportedType(new FullyQualifiedJavaType(introspectedTable.getExampleType()));
        if (shardingKeyType == null) {
            topLevelClass.addImportedType(new FullyQualifiedJavaType(PluginConstants.CLASS_BASE_NORMAL_REPO_IMPL));
            topLevelClass.setSuperClass(new FullyQualifiedJavaType(PluginConstants.CLASS_BASE_NORMAL_REPO_IMPL + "<"
                    + CommonPluginUtil.getPrimaryKeyType(introspectedTable) + ", "
                    + introspectedTable.getBaseRecordType() + ", " + introspectedTable.getExampleType() + ">"));
        } else {
            topLevelClass.addImportedType(new FullyQualifiedJavaType(PluginConstants.CLASS_BASE_SHARDED_REPO_IMPL));
            topLevelClass.setSuperClass(new FullyQualifiedJavaType(PluginConstants.CLASS_BASE_SHARDED_REPO_IMPL + "<"
                    + CommonPluginUtil.getPrimaryKeyType(introspectedTable) + ", "
                    + introspectedTable.getBaseRecordType() + ", " + introspectedTable.getExampleType() + ", " + shardingKeyType + ">"));
        }

        Field mapper = new Field();
        mapper.setVisibility(JavaVisibility.PRIVATE);
        topLevelClass.addImportedType("org.springframework.beans.factory.annotation.Autowired");
        mapper.addAnnotation("@Autowired");
        FullyQualifiedJavaType mapperType = new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType() + "Ext");
        topLevelClass.addImportedType(mapperType);
        mapper.setType(mapperType);
        mapper.setName("mapper");
        topLevelClass.addField(mapper);

        Method isUseGeneratedKeys = new Method();
        isUseGeneratedKeys.addAnnotation("@Override");
        isUseGeneratedKeys.setVisibility(JavaVisibility.PROTECTED);
        isUseGeneratedKeys.setReturnType(new FullyQualifiedJavaType("boolean"));
        isUseGeneratedKeys.setName("isUseGeneratedKeys");
        isUseGeneratedKeys.addBodyLine("return " + introspectedTable.isUseGeneratedKeys() + ";");
        topLevelClass.addMethod(isUseGeneratedKeys);

        Method isInsertListEnable = new Method();
        isInsertListEnable.addAnnotation("@Override");
        isInsertListEnable.setVisibility(JavaVisibility.PROTECTED);
        isInsertListEnable.setReturnType(new FullyQualifiedJavaType("boolean"));
        isInsertListEnable.setName("isInsertListEnable");
        isInsertListEnable.addBodyLine("return " + CommonPluginUtil.isInsertListEnable(properties, introspectedTable) + ";");
        topLevelClass.addMethod(isInsertListEnable);

        Method getNewId = new Method();
        getNewId.addAnnotation("@Override");
        getNewId.setVisibility(JavaVisibility.PROTECTED);
        getNewId.setReturnType(new FullyQualifiedJavaType("java.lang.Long"));
        getNewId.setName("getNewId");
        if (!introspectedTable.isUseGeneratedKeys()) {
            String autowiredSequenceName = generatedRepo.getAutowiredSequenceName();
            if (autowiredSequenceName != null) {
                Field sequence = new Field();
                sequence.setVisibility(JavaVisibility.PRIVATE);
                sequence.addAnnotation("@Autowired");
                FullyQualifiedJavaType sequenceType = new FullyQualifiedJavaType("com.taobao.tddl.client.sequence.Sequence");
                topLevelClass.addImportedType(sequenceType);
                sequence.setType(sequenceType);
                sequence.setName(autowiredSequenceName);
                topLevelClass.addField(sequence);
                getNewId.addBodyLine("return " + autowiredSequenceName + ".nextValue();");
            } else if (CommonPluginUtil.isGeneratedUuidKey(properties, introspectedTable)) {
                getNewId.addBodyLine("return UUID.randomUUID().toString().replace(\"-\", \"\");");
                topLevelClass.addImportedType("java.util.UUID");
                topLevelClass.addMethod(getNewId);
            } else {
                getNewId.addBodyLine("// TODO 补充获取主键方式");
                getNewId.addBodyLine("return null;");
            }
        } else {
            getNewId.addBodyLine("return null;");
        }
        topLevelClass.addMethod(getNewId);

        generatedJavaFile = new GeneratedJavaFile(
                topLevelClass,
                context.getJavaClientGeneratorConfiguration().getTargetProject(),
                context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                context.getJavaFormatter());

        if (!FileUtil.isExistFile(generatedJavaFile.getTargetProject(),
                generatedJavaFile.getTargetPackage(),
                generatedJavaFile.getFileName())) {
            generatedJavaFiles.add(generatedJavaFile);
        }
    }
}
