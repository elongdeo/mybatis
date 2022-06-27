package com.elong.deo.mybatis.type.generalized;

import com.elong.deo.mybatis.core.ILongEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

/**
 * Long类型枚举转换
 *
 * @author dingyinlong
 * @date 2019年11月11日15:38:26
 */
public abstract class BaseLongEnumTypeHandler<T extends Enum<T> & ILongEnum> extends BaseTypeHandler<T> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        ps.setLong(i, parameter.getCode());
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        long dbValue = cs.getLong(columnIndex);
        if(cs.wasNull()){
            return null;
        }
        return toResult(dbValue);
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        long dbValue = rs.getLong(columnIndex);
        if(rs.wasNull()){
            return null;
        }
        return toResult(dbValue);
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        long dbValue = rs.getLong(columnName);
        if(rs.wasNull()){
            return null;
        }
        return toResult(dbValue);
    }

    private T toResult(Long dbValue) throws SQLException {
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