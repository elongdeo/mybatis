package io.github.elongdeo.mybatis.comment;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.MergeConstants;
import org.mybatis.generator.config.PropertyRegistry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

/**
 * 生成的Do增加原表字段注释
 *
 * @author dingyinlong
 * @date 2018年04月28日10:45:04
 */
public class CommentGenerator implements org.mybatis.generator.api.CommentGenerator {

    private Properties properties;
    private Properties systemPro;
    private boolean suppressDate;
    private boolean suppressAllComments;
    private String currentDateStr;

    public CommentGenerator() {
        super();
        properties = new Properties();
        systemPro = System.getProperties();
        suppressDate = false;
        suppressAllComments = false;
        currentDateStr = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
    }

    @Override
    public void addJavaFileComment(CompilationUnit compilationUnit) {
        if (compilationUnit instanceof JavaElement) {
            ((JavaElement) compilationUnit).addJavaDocLine("/**\n"
                    + " * " + compilationUnit.getType().getShortName() + "\n"
                    + " * @author " + System.getProperty("user.name") + " 创建于 " + currentDateStr + "\n"
                    + " */");
            return;
        }
        compilationUnit.addFileCommentLine("/**");
        compilationUnit.addFileCommentLine(" * " + compilationUnit.getType().getShortName());
        compilationUnit.addFileCommentLine(" * @author " + System.getProperty("user.name") + " 创建于 " + currentDateStr);
        compilationUnit.addFileCommentLine(" */");
    }

    /**
     * Adds a suitable comment to warn users that the element was generated, and when it was generated.
     */
    @Override
    public void addComment(XmlElement xmlElement) {
        return;
    }

    @Override
    public void addRootComment(XmlElement rootElement) {
        // add no document level comments by default
        return;
    }

    @Override
    public void addConfigurationProperties(Properties properties) {
        this.properties.putAll(properties);

        suppressDate = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_DATE));

        suppressAllComments = isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_SUPPRESS_ALL_COMMENTS));
    }

    /**
     * This method adds the custom javadoc tag for. You may do nothing if you do not wish to include the Javadoc tag -
     * however, if you do not include the Javadoc tag then the Java merge capability of the eclipse plugin will break.
     *
     * @param javaElement the java element
     */
    protected void addJavadocTag(JavaElement javaElement, boolean markAsDoNotDelete) {
        javaElement.addJavaDocLine(" *");
        StringBuilder sb = new StringBuilder();
        sb.append(" * ");
        sb.append(MergeConstants.NEW_ELEMENT_TAG);
        if (markAsDoNotDelete) {
            sb.append(" do_not_delete_during_merge");
        }
        String s = getDateString();
        if (s != null) {
            sb.append(' ');
            sb.append(s);
        }
        javaElement.addJavaDocLine(sb.toString());
    }

    /**
     * This method returns a formated date string to include in the Javadoc tag and XML comments. You may return null if
     * you do not want the date in these documentation elements.
     *
     * @return a string representing the current timestamp, or null
     */
    protected String getDateString() {
        String result = null;
        if (!suppressDate) {
            result = currentDateStr;
        }
        return result;
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        innerClass.addJavaDocLine("/**");
        sb.append(" * ");
        sb.append(introspectedTable.getFullyQualifiedTable());
        sb.append(" ");
        sb.append(getDateString());
        innerClass.addJavaDocLine(sb.toString());
        innerClass.addJavaDocLine(" */");
    }

    @Override
    public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        innerEnum.addJavaDocLine("/**");
        //      addJavadocTag(innerEnum, false);
        sb.append(" * ");
        sb.append(introspectedTable.getFullyQualifiedTable());
        innerEnum.addJavaDocLine(sb.toString());
        innerEnum.addJavaDocLine(" */");
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable,
                                IntrospectedColumn introspectedColumn) {
        if (suppressAllComments) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        field.addJavaDocLine("/**");
        sb.append(" * ");
        sb.append(introspectedColumn.getRemarks());
        field.addJavaDocLine(sb.toString());

        //      addJavadocTag(field, false);

        field.addJavaDocLine(" */");
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        field.addJavaDocLine("/**");
        sb.append(" * ");
        sb.append(introspectedTable.getFullyQualifiedTable());
        field.addJavaDocLine(sb.toString());
        field.addJavaDocLine(" */");
    }

    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
        if (suppressAllComments) {
            return;
        }
        method.addJavaDocLine("/**");
        if ("countByExample".equals(method.getName())) {
            method.addJavaDocLine(" * 通过条件查询数量");
            method.addJavaDocLine(" * ");
            method.addJavaDocLine(" * @param example 条件");
            method.addJavaDocLine(" * @return 满足条件的数量");
        } else if ("deleteByPrimaryKey".equals(method.getName())) {
            method.addJavaDocLine(" * 通过主键删除");
            method.addJavaDocLine(" * ");
            method.addJavaDocLine(" * @param record 要删除的记录信息（包含主键）");
            method.addJavaDocLine(" * @return 影响数据库行数");
        } else if ("insert".equals(method.getName()) || "insertSelective".equals(method.getName())) {
            method.addJavaDocLine(" * 插入记录信息");
            method.addJavaDocLine(" * ");
            method.addJavaDocLine(" * @param record 要插入的记录信息");
            method.addJavaDocLine(" * @return 影响数据库行数");
        } else if ("selectByExample".equals(method.getName()) || "selectByExampleWithBLOBs".equals(method.getName())) {
            method.addJavaDocLine(" * 通过条件分页查询列表");
            method.addJavaDocLine(" * ");
            method.addJavaDocLine(" * @param example 条件");
            method.addJavaDocLine(" * @return 满足条件的分页列表");
        } else if ("deleteByExample".equals(method.getName())) {
            method.addJavaDocLine(" * 通过条件删除");
            method.addJavaDocLine(" * ");
            method.addJavaDocLine(" * @param example 条件");
            method.addJavaDocLine(" * @return 影响数据库行数");
        } else if ("selectByPrimaryKey".equals(method.getName())) {
            method.addJavaDocLine(" * 通过主键查询");
            method.addJavaDocLine(" * ");
            method.addJavaDocLine(" * @param id 数据库主键");
            if (method.getParameters().size() == 2) {
                Parameter parameter = method.getParameters().get(1);
                method.addJavaDocLine(" * @param " + parameter.getName() + " 数据库片键");
            }
            method.addJavaDocLine(" * @return 数据记录");
        } else if ("updateByExampleSelective".equals(method.getName()) || "updateByExample".equals(method.getName()) || "updateByExampleWithBLOBs".equals(method.getName())) {
            method.addJavaDocLine(" * 通过条件更新");
            method.addJavaDocLine(" * ");
            method.addJavaDocLine(" * @param record 要改成的记录信息");
            method.addJavaDocLine(" * @param example 条件");
            method.addJavaDocLine(" * @return 影响数据库行数");
        } else if ("updateByPrimaryKeySelective".equals(method.getName()) || "updateByPrimaryKeyWithBLOBs".equals(method.getName()) || "updateByPrimaryKey".equals(method.getName())) {
            method.addJavaDocLine(" * 通过主键更新");
            method.addJavaDocLine(" * ");
            method.addJavaDocLine(" * @param record 要改成的记录信息（包含主键）");
            method.addJavaDocLine(" * @return 影响数据库行数");
        }
        method.addJavaDocLine(" */");
    }

    @Override
    public void addGetterComment(Method method, IntrospectedTable introspectedTable,
                                 IntrospectedColumn introspectedColumn) {
        if (suppressAllComments) {
            return;
        }

        method.addJavaDocLine("/**");

        StringBuilder sb = new StringBuilder();
        sb.append(" * ");
        sb.append(introspectedColumn.getRemarks());
        method.addJavaDocLine(sb.toString());

        sb.setLength(0);
        sb.append(" * @return ");
        sb.append(introspectedColumn.getActualColumnName());
        sb.append(" ");
        sb.append(introspectedColumn.getRemarks());
        method.addJavaDocLine(sb.toString());

        //      addJavadocTag(method, false);

        method.addJavaDocLine(" */");
    }

    @Override
    public void addSetterComment(Method method, IntrospectedTable introspectedTable,
                                 IntrospectedColumn introspectedColumn) {
        if (suppressAllComments) {
            return;
        }

        method.addJavaDocLine("/**");
        StringBuilder sb = new StringBuilder();
        sb.append(" * ");
        sb.append(introspectedColumn.getRemarks());
        method.addJavaDocLine(sb.toString());

        Parameter parm = method.getParameters().get(0);
        sb.setLength(0);
        sb.append(" * @param ");
        sb.append(parm.getName());
        sb.append(" ");
        sb.append(introspectedColumn.getRemarks());
        method.addJavaDocLine(sb.toString());

        //      addJavadocTag(method, false);

        method.addJavaDocLine(" */");
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
        if (suppressAllComments) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        innerClass.addJavaDocLine("/**");
        sb.append(" * ");
        sb.append(introspectedTable.getFullyQualifiedTable());
        innerClass.addJavaDocLine(sb.toString());

        sb.setLength(0);
        sb.append(" * @author ");
        sb.append(systemPro.getProperty("user.name"));
        sb.append(" ");
        sb.append(currentDateStr);

        //      addJavadocTag(innerClass, markAsDoNotDelete);

        innerClass.addJavaDocLine(" */");
    }

    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

    }

    @Override
    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable,
                                           Set<FullyQualifiedJavaType> imports) {

    }

    @Override
    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable,
                                           IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> imports) {

    }

    @Override
    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable,
                                   Set<FullyQualifiedJavaType> imports) {

    }

    @Override
    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable,
                                   IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> imports) {

    }

    @Override
    public void addClassAnnotation(InnerClass innerClass, IntrospectedTable introspectedTable,
                                   Set<FullyQualifiedJavaType> imports) {

    }
}