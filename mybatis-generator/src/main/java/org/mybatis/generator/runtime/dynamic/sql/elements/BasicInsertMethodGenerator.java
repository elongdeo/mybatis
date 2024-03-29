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
import org.mybatis.generator.config.GeneratedKey;

import java.util.HashSet;
import java.util.Set;

public class BasicInsertMethodGenerator extends AbstractMethodGenerator {
    
    private FullyQualifiedJavaType recordType;
    private FragmentGenerator fragmentGenerator;
    
    private BasicInsertMethodGenerator(Builder builder) {
        super(builder);
        recordType = builder.recordType;
        fragmentGenerator = builder.fragmentGenerator;
    }

    @Override
    public MethodAndImports generateMethodAndImports() {
        if (!introspectedTable.getRules().generateInsert()
                && !introspectedTable.getRules().generateInsertSelective()) {
            return null;
        }

        Set<FullyQualifiedJavaType> imports = new HashSet<FullyQualifiedJavaType>();

        FullyQualifiedJavaType adapter = new FullyQualifiedJavaType("org.mybatis.dynamic.sql.util.SqlProviderAdapter");
        FullyQualifiedJavaType annotation = new FullyQualifiedJavaType("org.apache.ibatis.annotations.InsertProvider");

        imports.add(new FullyQualifiedJavaType("org.mybatis.dynamic.sql.insert.render.InsertStatementProvider"));
        imports.add(adapter);
        imports.add(annotation);

        FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType("org.mybatis.dynamic.sql.insert.render.InsertStatementProvider");
        imports.add(recordType);
        parameterType.addTypeArgument(recordType);

        Method method = new Method("insert");
        method.setReturnType(FullyQualifiedJavaType.getIntInstance());
        method.addParameter(new Parameter(parameterType, "insertStatement"));
        context.getCommentGenerator().addGeneralMethodAnnotation(method, introspectedTable, imports);
        method.addAnnotation("@InsertProvider(type=SqlProviderAdapter.class, method=\"insert\")");

      MethodAndImports.Builder builder = MethodAndImports.withMethod(method)
              .withImports(imports);
      
        GeneratedKey gk = introspectedTable.getGeneratedKey();
        if (gk != null) {
            MethodParts methodParts = fragmentGenerator.getGeneratedKeyAnnotation(gk);
            acceptParts(builder, method, methodParts);
        }

        return builder.build();
    }

    @Override
    public boolean callPlugins(Method method, Interface interfaze) {
        // we don't have a plugin method for this
        return true;
    }

    public static class Builder extends BaseBuilder<Builder, BasicInsertMethodGenerator> {

        private FullyQualifiedJavaType recordType;
        private FragmentGenerator fragmentGenerator;
        
        public Builder withRecordType(FullyQualifiedJavaType recordType) {
            this.recordType = recordType;
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
        public BasicInsertMethodGenerator build() {
            return new BasicInsertMethodGenerator(this);
        }
    }
}
