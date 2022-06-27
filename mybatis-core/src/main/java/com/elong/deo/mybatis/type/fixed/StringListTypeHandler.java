package com.elong.deo.mybatis.type.fixed;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * 按,分割的List<String>
 * @author dingyinlong
 * @date 2019年11月11日15:38:26
 */
public class StringListTypeHandler extends BaseTypeHandler<List<String>> {
    private final static String SEPARATOR = ",";

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        String dbValue = StringUtils.join(parameter, SEPARATOR);
        ps.setString(i, dbValue);
    }

    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toResult(cs.getString(columnIndex));
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toResult(rs.getString(columnIndex));
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toResult(rs.getString(columnName));
    }

    private List<String> toResult(String dbValue) {
        if (dbValue == null) {
            return null;
        }
        return Arrays.asList(dbValue.split(","));
    }
}