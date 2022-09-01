package io.github.elongdeo.mybatis.type.fixed;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * 按,分割的Long[]
 * @author dingyinlong
 * @date 2019年11月11日15:38:26
 */
public class LongArrayTypeHandler extends BaseTypeHandler<Long[]> {
    private final static String SEPARATOR = ",";

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Long[] parameter, JdbcType jdbcType) throws SQLException {
        String dbValue = StringUtils.join(parameter, SEPARATOR);
        ps.setString(i, dbValue);
    }

    @Override
    public Long[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toResult(cs.getString(columnIndex));
    }

    @Override
    public Long[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toResult(rs.getString(columnIndex));
    }

    @Override
    public Long[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toResult(rs.getString(columnName));
    }

    private Long[] toResult(String dbValue) {
        if (dbValue == null) {
            return null;
        }
        return Arrays.stream(dbValue.split(",")).map(Long::valueOf).toArray(Long[]::new);
    }
}