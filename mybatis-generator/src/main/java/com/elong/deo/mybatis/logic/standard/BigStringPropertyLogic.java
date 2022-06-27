package com.elong.deo.mybatis.logic.standard;

import com.elong.deo.mybatis.constants.PluginConfig;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

import java.sql.Types;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 处理大字符
 *
 * @author dingyinlong
 * @date 2021年04月29日18:04:12
 */
public class BigStringPropertyLogic {
    /**
     * 将大字符强行普通字符
     *
     * @param introspectedTable 表配置信息
     */
    public static void dealBigStringColumn(IntrospectedTable introspectedTable) {
        if (!PluginConfig.forceString) {
            return;
        }
        List<IntrospectedColumn> blobColumns = introspectedTable.getBLOBColumns();
        List<IntrospectedColumn> baseColumns = introspectedTable.getBaseColumns();
        if (baseColumns != null && blobColumns != null && blobColumns.size() > 0) {
            List<IntrospectedColumn> matched = blobColumns.stream()
                    .filter(column -> column.isJdbcCharacterColumn() && column.isBLOBColumn())
                    .peek(column -> {
                        column.setJdbcTypeName("VARCHAR");
                        column.setFullyQualifiedJavaType(new FullyQualifiedJavaType("java.lang.String"));
                        column.setJdbcType(Types.VARCHAR);
                    }).collect(Collectors.toList());
            baseColumns.addAll(matched);
            blobColumns.removeAll(matched);
        }
    }
}
