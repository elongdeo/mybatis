package com.elong.deo.mybatis.logic.standard;

import com.elong.deo.mybatis.constants.PluginConfig;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

import java.sql.Types;
import java.util.List;

/**
 * 处理boolean属性
 *
 * @author dingyinlong
 * @date 2019/03/01
 */
public class BooleanPropertyLogic {
    /**
     * 将boolean/tinyint(1)类型带is_前缀字段生成的属性值改为不带is前缀,防止反序列化失败
     *
     * @param introspectedTable 表配置信息
     */
    public static void dealBooleanColumn(IntrospectedTable introspectedTable) {
        if (!PluginConfig.boolFormatAble) {
            return;
        }
        List<IntrospectedColumn> baseColumns = introspectedTable.getBaseColumns();
        if (baseColumns != null) {
            for (IntrospectedColumn column : baseColumns) {
                if ("BIT".equals(column.getJdbcTypeName()) || "TINYINT".equals(column.getJdbcTypeName())) {
                    if (column.getActualColumnName().startsWith("is_")) {
                        if (column.getJavaProperty().startsWith("is")) {
                            String property = column.getJavaProperty();
                            column.setJavaProperty(Character.toLowerCase(property.charAt(2)) + property.substring(3));
                        }
                        column.setJdbcTypeName("BIT");
                        column.setFullyQualifiedJavaType(new FullyQualifiedJavaType("java.lang.Boolean"));
                        column.setJdbcType(Types.BIT);
                    }
                }
            }
        }
    }
}
