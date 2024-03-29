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

public class SelectDistinctByExampleMethodGenerator extends AbstractMethodGenerator {
    private FullyQualifiedJavaType recordType;
    private String tableFieldName;
    private FragmentGenerator fragmentGenerator;
    
    private SelectDistinctByExampleMethodGenerator(Builder builder) {
        super(builder);
        recordType = builder.recordType;
        tableFieldName = builder.tableFieldName;
        fragmentGenerator = builder.fragmentGenerator;
    }

    @Override
    public MethodAndImports generateMethodAndImports() {
        if (!introspectedTable.getRules().generateSelectByExampleWithBLOBs() 
                && !introspectedTable.getRules().generateSelectByExampleWithoutBLOBs()) {
            return null;
        }
        
        Set<FullyQualifiedJavaType> imports = new HashSet<FullyQualifiedJavaType>();

        imports.add(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.select.QueryExpressionDSL"));
        imports.add(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.select.MyBatis3SelectModelAdapter"));
        imports.add(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.select.SelectDSL"));
        imports.add(FullyQualifiedJavaType.getNewListInstance());
        imports.add(recordType);

        Method method = new Method("selectDistinctByExample");
        method.setDefault(true);
        context.getCommentGenerator().addGeneralMethodAnnotation(method, introspectedTable, imports);

        FullyQualifiedJavaType returnType = new FullyQualifiedJavaType("QueryExpressionDSL<MyBatis3SelectModelAdapter<List<"
                + recordType.getShortNameWithoutTypeArguments()
                + ">>>");
        method.setReturnType(returnType);
        StringBuilder sb = new StringBuilder();
        sb.append("return SelectDSL.selectDistinctWithMapper(this::selectMany, ");
        sb.append(fragmentGenerator.getSelectList());
        sb.append(')');
        method.addBodyLine(sb.toString());
        method.addBodyLine("        .from(" + tableFieldName + ");");
        
        return MethodAndImports.withMethod(method)
                .withImports(imports)
                .build();
    }

    @Override
    public boolean callPlugins(Method method, Interface interfaze) {
        return context.getPlugins().clientSelectByPrimaryKeyMethodGenerated(method, interfaze, introspectedTable);
    }

    public static class Builder extends BaseBuilder<Builder, SelectDistinctByExampleMethodGenerator> {
        private FullyQualifiedJavaType recordType;
        private String tableFieldName;
        private FragmentGenerator fragmentGenerator;
        
        public Builder withRecordType(FullyQualifiedJavaType recordType) {
            this.recordType = recordType;
            return this;
        }
        
        public Builder withTableFieldName(String tableFieldName) {
            this.tableFieldName = tableFieldName;
            return this;
        }

        public Builder withFragmentGenerator(FragmentGenerator fragmentGenerator) {
            this.fragmentGenerator = fragmentGenerator;
            return this;
        }

        @Override
        public Builder getThis() {
            return this;
        }

        @Override
        public SelectDistinctByExampleMethodGenerator build() {
            return new SelectDistinctByExampleMethodGenerator(this);
        }
    }
}
