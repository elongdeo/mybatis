package com.elong.deo.mybatis.type.generalized;

import com.elong.deo.mybatis.core.IStringEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

/**
 * string类型枚举转换
 *
 * @author dingyinlong
 * @date 2019年11月11日15:38:26
 */
public abstract class BaseStringEnumTypeHandler<T extends Enum<T> & IStringEnum> extends BaseTypeHandler<T> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getCode());
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toResult(cs.getString(columnIndex));
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toResult(rs.getString(columnIndex));
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toResult(rs.getString(columnName));
    }

    private T toResult(String dbValue) throws SQLException {
        if (dbValue == null) {
            return null;
        }
        Class<T> type = ((Class<T>)(getRawType()));
        Optional<T> first = Arrays.stream(type.getEnumConstants())
                .filter(ele -> dbValue.equals(ele.getCode())).findFirst();
        if (first.isPresent()) {
            return first.get();
        } else {
            throw new SQLException("Illegal argument " + dbValue + " for " + type.getCanonicalName());
        }
    }
}