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
package org.mybatis.generator.codegen.ibatis2.dao.elements;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.*;

import java.util.Set;
import java.util.TreeSet;

/**
 * Generates the Insert Selective method.
 * 
 * @author Jeff Butler
 * 
 */
public class InsertSelectiveMethodGenerator extends AbstractDAOElementGenerator {

    private boolean generateForJava5;

    public InsertSelectiveMethodGenerator(boolean generateForJava5) {
        super();
        this.generateForJava5 = generateForJava5;
    }

    @Override
    public void addImplementationElements(TopLevelClass topLevelClass) {
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        Method method = getMethodShell(importedTypes);
        if (generateForJava5) {
            method.addAnnotation("@Override");
        }

        FullyQualifiedJavaType returnType = method.getReturnType();
        StringBuilder sb = new StringBuilder();

        if (returnType != null) {
            sb.append("Object newKey = ");
        }

        sb.append(daoTemplate.getInsertMethod(introspectedTable
                .getIbatis2SqlMapNamespace(), introspectedTable
                .getInsertSelectiveStatementId(), "record"));
        method.addBodyLine(sb.toString());

        if (returnType != null) {
            if ("Object".equals(returnType.getShortName())) {
                // no need to cast if the return type is Object
                method.addBodyLine("return newKey;");
            } else {
                sb.setLength(0);

                if (returnType.isPrimitive()) {
                    PrimitiveTypeWrapper ptw = returnType
                            .getPrimitiveTypeWrapper();
                    sb.append("return ((");
                    sb.append(ptw.getShortName());
                    sb.append(") newKey");
                    sb.append(").");
                    sb.append(ptw.getToPrimitiveMethod());
                    sb.append(';');
                } else {
                    sb.append("return (");
                    sb.append(returnType.getShortName());
                    sb.append(") newKey;");
                }

                method.addBodyLine(sb.toString());
            }
        }

        if (context.getPlugins().clientInsertSelectiveMethodGenerated(
                method, topLevelClass, introspectedTable)) {
            topLevelClass.addImportedTypes(importedTypes);
            topLevelClass.addMethod(method);
        }
    }

    @Override
    public void addInterfaceElements(Interface interfaze) {
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        Method method = getMethodShell(importedTypes);

        if (context.getPlugins().clientInsertSelectiveMethodGenerated(
                method, interfaze, introspectedTable)) {
            interfaze.addImportedTypes(importedTypes);
            interfaze.addMethod(method);
        }
    }

    private Method getMethodShell(Set<FullyQualifiedJavaType> importedTypes) {
        Method method = new Method();

        FullyQualifiedJavaType returnType;
        if (introspectedTable.getGeneratedKey() != null) {
            IntrospectedColumn introspectedColumn = introspectedTable
                    .getColumn(introspectedTable.getGeneratedKey().getColumn());
            if (introspectedColumn == null) {
                // the specified column doesn't exist, so don't do the generated
                // key
                // (the warning has already been reported)
                returnType = null;
            } else {
                returnType = introspectedColumn.getFullyQualifiedJavaType();
                importedTypes.add(returnType);
            }
        } else {
            returnType = null;
        }
        method.setReturnType(returnType);
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setName(getDAOMethodNameCalculator()
                .getInsertSelectiveMethodName(introspectedTable));

        FullyQualifiedJavaType parameterType = introspectedTable.getRules()
                .calculateAllFieldsClass();

        importedTypes.add(parameterType);
        method.addParameter(new Parameter(parameterType, "record"));

        for (FullyQualifiedJavaType fqjt : daoTemplate.getCheckedExceptions()) {
            method.addException(fqjt);
            importedTypes.add(fqjt);
        }

        context.getCommentGenerator().addGeneralMethodComment(method,
                introspectedTable);

        return method;
    }
}
