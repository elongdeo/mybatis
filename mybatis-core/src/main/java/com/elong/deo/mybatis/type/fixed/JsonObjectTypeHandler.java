package com.elong.deo.mybatis.type.fixed;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * json对象的转换
 * @author dingyinlong
 * @date 2019年11月11日15:38:26
 */
public class JsonObjectTypeHandler extends BaseTypeHandler<JSONObject> {
 
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JSONObject parameter, JdbcType jdbcType) throws SQLException {
        String dbValue = parameter.toJSONString();
        ps.setString(i, dbValue);
    }
 
    @Override
    public JSONObject getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toResult(cs.getString(columnIndex));
    }
 
    @Override
    public JSONObject getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toResult(rs.getString(columnIndex));
    }
 
    @Override
    public JSONObject getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toResult(rs.getString(columnName));
    }
    private JSONObject toResult(String dbValue){
        if(dbValue == null){
            return null;
        }
        return JSON.parseObject(dbValue);
    }
}