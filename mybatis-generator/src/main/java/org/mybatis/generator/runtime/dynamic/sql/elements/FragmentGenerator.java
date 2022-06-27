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
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.internal.util.JavaBeansUtil;

import java.util.*;

import static org.mybatis.generator.api.dom.OutputUtilities.javaIndent;
import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

public class FragmentGenerator {

    private IntrospectedTable introspectedTable;
    private String resultMapId;
    
    private FragmentGenerator(Builder builder) {
        this.introspectedTable = builder.introspectedTable;
        this.resultMapId = builder.resultMapId;
    }
    
    public String getSelectList() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (IntrospectedColumn column : introspectedTable.getAllColumns()) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(column.getJavaProperty());
        }
        
        return sb.toString();
    }
    
    public MethodParts getPrimaryKeyWhereClauseAndParameters() {
        MethodParts.Builder builder = new MethodParts.Builder();
        
        boolean first = true;
        for (IntrospectedColumn column : introspectedTable.getPrimaryKeyColumns()) {
            builder.withImport(column.getFullyQualifiedJavaType());
            builder.withParameter(new Parameter(column.getFullyQualifiedJavaType(), column.getJavaProperty() + "_"));
            if (first) {
                builder.withBodyLine("        .where(" + column.getJavaProperty()
                        + ", isEqualTo(" + column.getJavaProperty()
                        + "_))");
                first = false;
            } else {
                builder.withBodyLine("        .and(" + column.getJavaProperty()
                        + ", isEqualTo(" + column.getJavaProperty()
                        + "_))");
            }
        }
        
        return builder.build();
    }

    public List<String> getPrimaryKeyWhereClauseForUpdate() {
        List<String> lines = new ArrayList<String>();
        
        boolean first = true;
        for (IntrospectedColumn column : introspectedTable.getPrimaryKeyColumns()) {
            String methodName = JavaBeansUtil.getGetterMethodName(column.getJavaProperty(), column.getFullyQualifiedJavaType());
            if (first) {
                lines.add("        .where(" + column.getJavaProperty()
                        + ", isEqualTo(record::" + methodName
                        + "))");
                first = false;
            } else {
                lines.add("        .and(" + column.getJavaProperty()
                        + ", isEqualTo(record::" + methodName
                        + "))");
            }
        }
        
        return lines;
    }
    
    public MethodParts getAnnotatedConstructorArgs() {
        MethodParts.Builder builder = new MethodParts.Builder();

        builder.withImport(new FullyQualifiedJavaType("org.apache.ibatis.type.JdbcType"));
        builder.withImport(new FullyQualifiedJavaType("org.apache.ibatis.annotations.ConstructorArgs"));
        builder.withImport(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Arg"));

        builder.withAnnotation("@ConstructorArgs({");

        StringBuilder sb = new StringBuilder();

        Set<FullyQualifiedJavaType> imports = new HashSet<FullyQualifiedJavaType>();
        Iterator<IntrospectedColumn> iterPk = introspectedTable.getPrimaryKeyColumns().iterator();
        Iterator<IntrospectedColumn> iterNonPk = introspectedTable.getNonPrimaryKeyColumns().iterator();
        while (iterPk.hasNext()) {
            IntrospectedColumn introspectedColumn = iterPk.next();
            sb.setLength(0);
            javaIndent(sb, 1);
            sb.append(getArgAnnotation(imports, introspectedColumn, true));
            
            if (iterPk.hasNext() || iterNonPk.hasNext()) {
                sb.append(',');
            }

            builder.withAnnotation(sb.toString());
        }

        while (iterNonPk.hasNext()) {
            IntrospectedColumn introspectedColumn = iterNonPk.next();
            sb.setLength(0);
            javaIndent(sb, 1);
            sb.append(getArgAnnotation(imports, introspectedColumn, false));
            
            if (iterNonPk.hasNext()) {
                sb.append(',');
            }

            builder.withAnnotation(sb.toString());
        }

        builder.withAnnotation("})")
            .withImports(imports);
        
        return builder.build();
    }

    private String getArgAnnotation(Set<FullyQualifiedJavaType> imports, IntrospectedColumn introspectedColumn, boolean idColumn) {
        StringBuilder sb = new StringBuilder();
        sb.append("@Arg(column=\"");
        sb.append(introspectedColumn.getActualColumnName());
        
        imports.add(introspectedColumn.getFullyQualifiedJavaType());
        sb.append("\", javaType=");
        sb.append(introspectedColumn.getFullyQualifiedJavaType().getShortName());
        sb.append(".class");

        if (stringHasValue(introspectedColumn.getTypeHandler())) {
            FullyQualifiedJavaType fqjt =
                    new FullyQualifiedJavaType(introspectedColumn.getTypeHandler());
            imports.add(fqjt);
            sb.append(", typeHandler=");
            sb.append(fqjt.getShortName());
            sb.append(".class");
        }

        sb.append(", jdbcType=JdbcType.");
        sb.append(introspectedColumn.getJdbcTypeName());
        if (idColumn) {
            sb.append(", id=true");
        }
        sb.append(')');

        return sb.toString();
    }

    public MethodParts getAnnotatedResults() {
        MethodParts.Builder builder = new MethodParts.Builder();

        builder.withImport(new FullyQualifiedJavaType("org.apache.ibatis.type.JdbcType"));
        builder.withImport(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Result"));
        builder.withImport(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Results"));

        builder.withAnnotation("@Results(id=\"" + resultMapId + "\", value = {");

        StringBuilder sb = new StringBuilder();

        Set<FullyQualifiedJavaType> imports = new HashSet<FullyQualifiedJavaType>();
        Iterator<IntrospectedColumn> iterPk = introspectedTable.getPrimaryKeyColumns().iterator();
        Iterator<IntrospectedColumn> iterNonPk = introspectedTable.getNonPrimaryKeyColumns().iterator();
        while (iterPk.hasNext()) {
            IntrospectedColumn introspectedColumn = iterPk.next();
            sb.setLength(0);
            javaIndent(sb, 1);
            sb.append(getResultAnnotation(imports, introspectedColumn, true));
            
            if (iterPk.hasNext() || iterNonPk.hasNext()) {
                sb.append(',');
            }

            builder.withAnnotation(sb.toString());
        }

        while (iterNonPk.hasNext()) {
            IntrospectedColumn introspectedColumn = iterNonPk.next();
            sb.setLength(0);
            javaIndent(sb, 1);
            sb.append(getResultAnnotation(imports, introspectedColumn, false));
            
            if (iterNonPk.hasNext()) {
                sb.append(',');
            }

            builder.withAnnotation(sb.toString());
        }

        builder.withAnnotation("})")
            .withImports(imports);
        
        return builder.build();
    }
    
    private String getResultAnnotation(Set<FullyQualifiedJavaType> imports, IntrospectedColumn introspectedColumn, boolean idColumn) {
        StringBuilder sb = new StringBuilder();
        sb.append("@Result(column=\"");
        sb.append(introspectedColumn.getActualColumnName());
        sb.append("\", property=\"");
        sb.append(introspectedColumn.getJavaProperty());
        sb.append('\"');

        if (stringHasValue(introspectedColumn.getTypeHandler())) {
            FullyQualifiedJavaType fqjt =
                    new FullyQualifiedJavaType(introspectedColumn.getTypeHandler());
            imports.add(fqjt);
            sb.append(", typeHandler=");
            sb.append(fqjt.getShortName());
            sb.append(".class");
        }

        sb.append(", jdbcType=JdbcType.");
        sb.append(introspectedColumn.getJdbcTypeName());
        if (idColumn) {
            sb.append(", id=true");
        }
        sb.append(')');

        return sb.toString();
    }
    
    public MethodParts getGeneratedKeyAnnotation(GeneratedKey gk) {
        MethodParts.Builder builder = new MethodParts.Builder();
        
        StringBuilder sb = new StringBuilder();
        IntrospectedColumn introspectedColumn = introspectedTable.getColumn(gk.getColumn());
        if (introspectedColumn != null) {
            if (gk.isJdbcStandard()) {
                builder.withImport(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Options"));
                sb.append("@Options(useGeneratedKeys=true,keyProperty=\"record.");
                sb.append(introspectedColumn.getJavaProperty());
                sb.append("\")");
                builder.withAnnotation(sb.toString());
            } else {
                builder.withImport(new FullyQualifiedJavaType("org.apache.ibatis.annotations.SelectKey"));
                FullyQualifiedJavaType fqjt = introspectedColumn.getFullyQualifiedJavaType();
                sb.append("@SelectKey(statement=\"");
                sb.append(gk.getRuntimeSqlStatement());
                sb.append("\", keyProperty=\"record.");
                sb.append(introspectedColumn.getJavaProperty());
                sb.append("\", before=");
                sb.append(gk.isIdentity() ? "false" : "true");
                sb.append(", resultType=");
                sb.append(fqjt.getShortName());
                sb.append(".class)");
                builder.withAnnotation(sb.toString());
            }
        }
        
        return builder.build();
    }
    
    public List<String> getSetEqualLines(List<IntrospectedColumn> columnList, boolean terminate) {
        List<String> lines = new ArrayList<String>();
        List<IntrospectedColumn> columns = ListUtilities.removeIdentityAndGeneratedAlwaysColumns(columnList);
        Iterator<IntrospectedColumn> iter = columns.iterator();
        while (iter.hasNext()) {
            IntrospectedColumn column = iter.next();
            String methodName = JavaBeansUtil.getGetterMethodName(column.getJavaProperty(), column.getFullyQualifiedJavaType());
            String line = "        .set(" + column.getJavaProperty()
                    + ").equalTo(record::" + methodName
                    + ")";
            if (terminate && !iter.hasNext()) {
                line += ";";
            }
            lines.add(line);
        }
        
        return lines;
    }
    
    public List<String> getSetEqualWhenPresentLines(List<IntrospectedColumn> columnList, boolean terminate) {
        List<String> lines = new ArrayList<String>();
        List<IntrospectedColumn> columns = ListUtilities.removeIdentityAndGeneratedAlwaysColumns(columnList);
        Iterator<IntrospectedColumn> iter = columns.iterator();
        while (iter.hasNext()) {
            IntrospectedColumn column = iter.next();
            String methodName = JavaBeansUtil.getGetterMethodName(column.getJavaProperty(), column.getFullyQualifiedJavaType());
            String line = "        .set(" + column.getJavaProperty()
                    + ").equalToWhenPresent(record::"
                    + methodName + ")";
            if (terminate && !iter.hasNext()) {
                line += ";";
            }
            lines.add(line);
        }
        return lines;
    }

    public static class Builder {
        private IntrospectedTable introspectedTable;
        private String resultMapId;
        
        public Builder withIntrospectedTable(IntrospectedTable introspectedTable) {
            this.introspectedTable = introspectedTable;
            return this;
        }
        
        public Builder withResultMapId(String resultMapId) {
            this.resultMapId = resultMapId;
            return this;
        }
        
        public FragmentGenerator build() {
            return new FragmentGenerator(this);
        }
    }
}
