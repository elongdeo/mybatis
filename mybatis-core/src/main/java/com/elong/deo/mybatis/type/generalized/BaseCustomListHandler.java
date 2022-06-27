package com.elong.deo.mybatis.type.generalized;

import com.alibaba.fastjson.JSON;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 自定义格式字符串和自定义类型转换
 *
 * @author dingyinlong
 * @date 2019年11月11日15:38:26
 */
public abstract class BaseCustomListHandler<T> extends BaseTypeHandler<List<T>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<T> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter == null ? null : JSON.toJSONString(parameter));
    }

    @Override
    public List<T> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toResult(cs.getString(columnIndex));
    }

    @Override
    public List<T> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toResult(rs.getString(columnIndex));
    }

    @Override
    public List<T> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toResult(rs.getString(columnName));
    }

    private List<T> toResult(String dbValue) throws SQLException {
        if (dbValue == null) {
            return null;
        }
        return JSON.parseArray(dbValue, (Class<T>) getRawType());
    }
}