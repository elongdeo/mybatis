/**
 *    Copyright ${license.git.copyrightYears} the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.codegen.mybatis3.javamapper.elements.annotated;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.codegen.mybatis3.javamapper.elements.SelectByPrimaryKeyMethodGenerator;

import java.util.Iterator;

import static org.mybatis.generator.api.dom.OutputUtilities.javaIndent;
import static org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities.*;
import static org.mybatis.generator.internal.util.StringUtility.escapeStringForJava;

/**
 * 
 * @author Jeff Butler
 */
public class AnnotatedSelectByPrimaryKeyMethodGenerator extends SelectByPrimaryKeyMethodGenerator {
    
    private boolean useResultMapIfAvailable;

    public AnnotatedSelectByPrimaryKeyMethodGenerator(boolean useResultMapIfAvailable, boolean isSimple) {
        super(isSimple);
        this.useResultMapIfAvailable = useResultMapIfAvailable;
    }

    @Override
    public void addMapperAnnotations(Interface interfaze, Method method) {

        StringBuilder sb = new StringBuilder();
        method.addAnnotation("@Select({");
        javaIndent(sb, 1);
        sb.append("\"select\",");
        method.addAnnotation(sb.toString());
        
        sb.setLength(0);
        javaIndent(sb, 1);
        sb.append('"');
        boolean hasColumns = false;
        Iterator<IntrospectedColumn> iter = introspectedTable.getAllColumns().iterator();
        while (iter.hasNext()) {
            sb.append(escapeStringForJava(getSelectListPhrase(iter.next())));
            hasColumns = true;

            if (iter.hasNext()) {
                sb.append(", ");
            }

            if (sb.length() > 80) {
                sb.append("\",");
                method.addAnnotation(sb.toString());

                sb.setLength(0);
                javaIndent(sb, 1);
                sb.append('"');
                hasColumns = false;
            }
        }

        if (hasColumns) {
            sb.append("\",");
            method.addAnnotation(sb.toString());
        }

        sb.setLength(0);
        javaIndent(sb, 1);
        sb.append("\"from ");
        sb.append(escapeStringForJava(introspectedTable
                .getAliasedFullyQualifiedTableNameAtRuntime()));
        sb.append("\",");
        method.addAnnotation(sb.toString());

        boolean and = false;
        iter = introspectedTable.getPrimaryKeyColumns().iterator();
        while (iter.hasNext()) {
            sb.setLength(0);
            javaIndent(sb, 1);
            if (and) {
                sb.append("  \"and ");
            } else {
                sb.append("\"where ");
                and = true;
            }

            IntrospectedColumn introspectedColumn = iter.next();
            sb.append(escapeStringForJava(getAliasedEscapedColumnName(introspectedColumn)));
            sb.append(" = ");
            sb.append(getParameterClause(introspectedColumn));
            sb.append('\"');
            if (iter.hasNext()) {
                sb.append(',');
            }
            method.addAnnotation(sb.toString());
        }

        method.addAnnotation("})");

        if (useResultMapIfAvailable) {
            if (introspectedTable.getRules().generateBaseResultMap()
                    || introspectedTable.getRules().generateResultMapWithBLOBs()) {
                addResultMapAnnotation(method);
            } else {
                addAnnotatedResults(interfaze, method);
            }
        } else {
            addAnnotatedResults(interfaze, method);
        }
    }

    private void addResultMapAnnotation(Method method) {

        String annotation = String.format("@ResultMap(\"%s.%s\")",
                introspectedTable.getMyBatis3SqlMapNamespace(),
                introspectedTable.getRules().generateResultMapWithBLOBs()
                    ? introspectedTable.getResultMapWithBLOBsId() : introspectedTable.getBaseResultMapId());
        method.addAnnotation(annotation);
    }

    private void addAnnotatedResults(Interface interfaze, Method method) {

        if (introspectedTable.isConstructorBased()) {
            method.addAnnotation("@ConstructorArgs({");
        } else {
            method.addAnnotation("@Results({");
        }

        StringBuilder sb = new StringBuilder();

        Iterator<IntrospectedColumn> iterPk = introspectedTable.getPrimaryKeyColumns().iterator();
        Iterator<IntrospectedColumn> iterNonPk = introspectedTable.getNonPrimaryKeyColumns().iterator();
        while (iterPk.hasNext()) {
            IntrospectedColumn introspectedColumn = iterPk.next();
            sb.setLength(0);
            javaIndent(sb, 1);
            sb.append(getResultAnnotation(interfaze, introspectedColumn, true,
                    introspectedTable.isConstructorBased()));
            
            if (iterPk.hasNext() || iterNonPk.hasNext()) {
                sb.append(',');
            }

            method.addAnnotation(sb.toString());
        }

        while (iterNonPk.hasNext()) {
            IntrospectedColumn introspectedColumn = iterNonPk.next();
            sb.setLength(0);
            javaIndent(sb, 1);
            sb.append(getResultAnnotation(interfaze, introspectedColumn, false,
                    introspectedTable.isConstructorBased()));
            
            if (iterNonPk.hasNext()) {
                sb.append(',');
            }

            method.addAnnotation(sb.toString());
        }

        method.addAnnotation("})");
    }

    @Override
    public void addExtraImports(Interface interfaze) {
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Select"));

        if (useResultMapIfAvailable) {
            if (introspectedTable.getRules().generateBaseResultMap()
                    || introspectedTable.getRules().generateResultMapWithBLOBs()) {
                interfaze.addImportedType(
                        new FullyQualifiedJavaType("org.apache.ibatis.annotations.ResultMap"));
            } else {
                addAnnotationImports(interfaze);
            }
        } else {
            addAnnotationImports(interfaze);
        }
    }

    private void addAnnotationImports(Interface interfaze) {
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.type.JdbcType"));

        if (introspectedTable.isConstructorBased()) {
            interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Arg"));
            interfaze.addImportedType(
                    new FullyQualifiedJavaType("org.apache.ibatis.annotations.ConstructorArgs"));
        } else {
            interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Result"));
            interfaze.addImportedType(
                    new FullyQualifiedJavaType("org.apache.ibatis.annotations.Results"));
        }
    }
}
