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

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;

import java.util.HashSet;
import java.util.Set;

public class CountByExampleMethodGenerator extends AbstractMethodGenerator {

    private String tableFieldName;
    
    private CountByExampleMethodGenerator(Builder builder) {
        super(builder);
        this.tableFieldName = builder.tableFieldName;
    }
    
    @Override
    public MethodAndImports generateMethodAndImports() {
        if (!introspectedTable.getRules().generateCountByExample()) {
            return null;
        }
        
        Set<FullyQualifiedJavaType> imports = new HashSet<FullyQualifiedJavaType>();

        imports.add(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.select.QueryExpressionDSL"));
        imports.add(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.select.MyBatis3SelectModelAdapter"));
        imports.add(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.SqlBuilder"));
        imports.add(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.select.SelectDSL"));

        Method method = new Method("countByExample");
        method.setDefault(true);
        context.getCommentGenerator().addGeneralMethodAnnotation(method, introspectedTable, imports);

        FullyQualifiedJavaType returnType = new FullyQualifiedJavaType("QueryExpressionDSL<MyBatis3SelectModelAdapter<Long>>");
        method.setReturnType(returnType);
        method.addBodyLine("return SelectDSL.selectWithMapper(this::count, SqlBuilder.count())");
        method.addBodyLine("        .from(" + tableFieldName + ");");
        
        return MethodAndImports.withMethod(method)
                .withImports(imports)
                .build();
    }

    @Override
    public boolean callPlugins(Method method, Interface interfaze) {
        return context.getPlugins().clientCountByExampleMethodGenerated(method, interfaze, introspectedTable);
    }

    public static class Builder extends BaseBuilder<Builder, CountByExampleMethodGenerator> {
        private String tableFieldName;
        
        public Builder withTableFieldName(String tableFieldName) {
            this.tableFieldName = tableFieldName;
            return this;
        }
        
        @Override
        public Builder getThis() {
            return this;
        }

        @Override
        public CountByExampleMethodGenerator build() {
            return new CountByExampleMethodGenerator(this);
        }
    }
}
