package com.elong.deo.mybatis.type.fixed;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 按,分割的String[]
 * @author dingyinlong
 * @date 2019年11月11日15:38:26
 */
public class StringArrayTypeHandler extends BaseTypeHandler<String[]> {
    private final static String SEPARATOR = ",";

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String[] parameter, JdbcType jdbcType) throws SQLException {
        String dbValue = StringUtils.join(parameter, SEPARATOR);
        ps.setString(i, dbValue);
    }

    @Override
    public String[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toResult(cs.getString(columnIndex));
    }

    @Override
    public String[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toResult(rs.getString(columnIndex));
    }

    @Override
    public String[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toResult(rs.getString(columnName));
    }

    private String[] toResult(String dbValue) {
        if (dbValue == null) {
            return null;
        }
        return dbValue.split(",");
    }
}