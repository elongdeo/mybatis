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
import org.mybatis.generator.api.dom.java.Parameter;

import java.util.HashSet;
import java.util.Set;

public class BasicSelectOneMethodGenerator extends AbstractMethodGenerator {
    
    private FullyQualifiedJavaType recordType;
    private String resultMapId;
    private FragmentGenerator fragmentGenerator;
    
    private BasicSelectOneMethodGenerator(Builder builder) {
        super(builder);
        recordType = builder.recordType;
        resultMapId = builder.resultMapId;
        fragmentGenerator = builder.fragmentGenerator;
    }

    @Override
    public MethodAndImports generateMethodAndImports() {
        if (!introspectedTable.getRules().generateSelectByPrimaryKey()) {
            return null;
        }
        
        Set<FullyQualifiedJavaType> imports = new HashSet<FullyQualifiedJavaType>();
        
        boolean reuseResultMap = introspectedTable.getRules().generateSelectByExampleWithBLOBs()
                || introspectedTable.getRules().generateSelectByExampleWithoutBLOBs();

        FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType("org.mybatis.dynamic.sql.select.render.SelectStatementProvider");
        FullyQualifiedJavaType adapter = new FullyQualifiedJavaType("org.mybatis.dynamic.sql.util.SqlProviderAdapter");
        FullyQualifiedJavaType annotation = new FullyQualifiedJavaType("org.apache.ibatis.annotations.SelectProvider");
        
        imports.add(parameterType);
        imports.add(adapter);
        imports.add(annotation);

        Method method = new Method("selectOne");

        imports.add(recordType);
        method.setReturnType(recordType);
        method.addParameter(new Parameter(parameterType, "selectStatement"));
        context.getCommentGenerator().addGeneralMethodAnnotation(method, introspectedTable, imports);
        method.addAnnotation("@SelectProvider(type=SqlProviderAdapter.class, method=\"select\")");
        
        MethodAndImports.Builder builder = MethodAndImports.withMethod(method)
                .withImports(imports);

        if (introspectedTable.isConstructorBased()) {
            MethodParts methodParts = fragmentGenerator.getAnnotatedConstructorArgs();
            acceptParts(builder, method, methodParts);
        } else {
            if (reuseResultMap) {
                FullyQualifiedJavaType rmAnnotation = new FullyQualifiedJavaType("org.apache.ibatis.annotations.ResultMap");
                builder.withImport(rmAnnotation);
                method.addAnnotation("@ResultMap(\"" + resultMapId + "\")");
            } else {
                MethodParts methodParts = fragmentGenerator.getAnnotatedResults();
                acceptParts(builder, method, methodParts);
            }
        }

        return builder.build();
    }

    @Override
    public boolean callPlugins(Method method, Interface interfaze) {
        // we don't have a plugin method for this
        return true;
    }

    public static class Builder extends BaseBuilder<Builder, BasicSelectOneMethodGenerator> {

        private FullyQualifiedJavaType recordType;
        private String resultMapId;
        private FragmentGenerator fragmentGenerator;
        
        public Builder withRecordType(FullyQualifiedJavaType recordType) {
            this.recordType = recordType;
            return this;
        }
        
        public Builder withResultMapId(String resultMapId) {
            this.resultMapId = resultMapId;
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
        public BasicSelectOneMethodGenerator build() {
            return new BasicSelectOneMethodGenerator(this);
        }
    }
}
