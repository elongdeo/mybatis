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
package org.mybatis.generator.runtime.dynamic.sql.elements;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.internal.util.JavaBeansUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InsertSelectiveMethodGenerator extends AbstractMethodGenerator {
    private FullyQualifiedJavaType recordType;
    private String tableFieldName;
    
    private InsertSelectiveMethodGenerator(Builder builder) {
        super(builder);
        recordType = builder.recordType;
        tableFieldName = builder.tableFieldName;
    }

    @Override
    public MethodAndImports generateMethodAndImports() {
        if (!introspectedTable.getRules().generateInsertSelective()) {
            return null;
        }

        Set<FullyQualifiedJavaType> imports = new HashSet<FullyQualifiedJavaType>();

        imports.add(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.SqlBuilder"));
        imports.add(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.render.RenderingStrategy"));
        imports.add(recordType);

        Method method = new Method("insertSelective");
        method.setDefault(true);
        context.getCommentGenerator().addGeneralMethodAnnotation(method, introspectedTable, imports);
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.addParameter(new Parameter(recordType, "record"));

        method.addBodyLine("return insert(SqlBuilder.insert(record)");
        method.addBodyLine("        .into(" + tableFieldName + ")");
        
        List<IntrospectedColumn> columns = ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns());
        for (IntrospectedColumn column : columns) {
            if (column.isSequenceColumn()) {
                method.addBodyLine("        .map(" + column.getJavaProperty()
                        + ").toProperty(\"" + column.getJavaProperty()
                        + "\")");
            } else {
                String methodName = JavaBeansUtil.getGetterMethodName(column.getJavaProperty(), column.getFullyQualifiedJavaType());
                method.addBodyLine("        .map(" + column.getJavaProperty()
                        + ").toPropertyWhenPresent(\"" + column.getJavaProperty()
                        + "\", record::" + methodName
                        + ")");
            }
        }

        method.addBodyLine("        .build()");
        method.addBodyLine("        .render(RenderingStrategy.MYBATIS3));");
        
        return MethodAndImports.withMethod(method)
                .withImports(imports)
                .build();
    }

    @Override
    public boolean callPlugins(Method method, Interface interfaze) {
        return context.getPlugins().clientInsertSelectiveMethodGenerated(method, interfaze, introspectedTable);
    }

    public static class Builder extends BaseBuilder<Builder, InsertSelectiveMethodGenerator> {
        private FullyQualifiedJavaType recordType;
        private String tableFieldName;
        
        public Builder withRecordType(FullyQualifiedJavaType recordType) {
            this.recordType = recordType;
            return this;
        }
        
        public Builder withTableFieldName(String tableFieldName) {
            this.tableFieldName = tableFieldName;
            return this;
        }

        @Override
        public Builder getThis() {
            return this;
        }

        @Override
        public InsertSelectiveMethodGenerator build() {
            return new InsertSelectiveMethodGenerator(this);
        }
    }
}
