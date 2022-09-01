package io.github.elongdeo.mybatis.type.fixed;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * JSONArray的类型转换
 * @author dingyinlong
 * @date 2019年11月11日15:38:26
 */
public class JsonArrayTypeHandler extends BaseTypeHandler<JSONArray> {
 
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JSONArray parameter, JdbcType jdbcType) throws SQLException {
        String dbValue = parameter.toJSONString();
        ps.setString(i, dbValue);
    }
 
    @Override
    public JSONArray getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toResult(cs.getString(columnIndex));
    }
 
    @Override
    public JSONArray getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toResult(rs.getString(columnIndex));
    }
 
    @Override
    public JSONArray getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toResult(rs.getString(columnName));
    }
    private JSONArray toResult(String dbValue){
        if(dbValue == null){
            return null;
        }
        return JSON.parseArray(dbValue);
    }
}