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

import org.mybatis.generator.api.dom.java.*;

import java.util.Set;
import java.util.TreeSet;

/**
 * Generates the Update By primary Key without BLOBs method.
 * 
 * @author Jeff Butler
 * 
 */
public class UpdateByPrimaryKeyWithoutBLOBsMethodGenerator extends
        AbstractDAOElementGenerator {

    private boolean generateForJava5;

    public UpdateByPrimaryKeyWithoutBLOBsMethodGenerator(boolean generateForJava5) {
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

        StringBuilder sb = new StringBuilder();
        sb.append("int rows = ");
        sb.append(daoTemplate.getUpdateMethod(introspectedTable
                .getIbatis2SqlMapNamespace(), introspectedTable
                .getUpdateByPrimaryKeyStatementId(), "record"));
        method.addBodyLine(sb.toString());

        method.addBodyLine("return rows;");

        if (context.getPlugins()
                .clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(method,
                        topLevelClass, introspectedTable)) {
            topLevelClass.addImportedTypes(importedTypes);
            topLevelClass.addMethod(method);
        }
    }

    @Override
    public void addInterfaceElements(Interface interfaze) {
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
        Method method = getMethodShell(importedTypes);

        if (context.getPlugins()
                .clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(method,
                        interfaze, introspectedTable)) {
            interfaze.addImportedTypes(importedTypes);
            interfaze.addMethod(method);
        }
    }

    private Method getMethodShell(Set<FullyQualifiedJavaType> importedTypes) {
        FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType(
                introspectedTable.getBaseRecordType());
        importedTypes.add(parameterType);

        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method
                .setName(getDAOMethodNameCalculator()
                        .getUpdateByPrimaryKeyWithoutBLOBsMethodName(
                                introspectedTable));
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
