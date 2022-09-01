package io.github.elongdeo.mybatis.type.fixed;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 按,分割的List<Long>
 * @author dingyinlong
 * @date 2019年11月11日15:38:26
 */
public class LongListTypeHandler extends BaseTypeHandler<List<Long>> {
    private final static String SEPARATOR = ",";

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<Long> parameter, JdbcType jdbcType) throws SQLException {
        String dbValue = StringUtils.join(parameter, SEPARATOR);
        ps.setString(i, dbValue);
    }

    @Override
    public List<Long> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toResult(cs.getString(columnIndex));
    }

    @Override
    public List<Long> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toResult(rs.getString(columnIndex));
    }

    @Override
    public List<Long> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toResult(rs.getString(columnName));
    }

    private List<Long> toResult(String dbValue) {
        if (dbValue == null) {
            return null;
        }
        return Arrays.stream(dbValue.split(",")).map(Long::valueOf).collect(Collectors.toList());
    }
}