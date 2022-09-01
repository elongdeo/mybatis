package io.github.elongdeo.mybatis.plugin.standard;

import io.github.elongdeo.mybatis.logic.standard.BooleanPropertyLogic;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;

import java.util.List;

/**
 * 数据库布尔类型的is_开头的字段生成的Do中属性去掉is前缀，防止反序列化失败
 *
 * @author dingyinlong
 * @date 2018年12月05日11:05:55
 */
public class BooleanPropertyPlugin extends PluginAdapter {
    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        // 将boolean/tinyint(1)类型带is_前缀字段生成的属性值改为不带is前缀,防止反序列化失败
        BooleanPropertyLogic.dealBooleanColumn(introspectedTable);
    }


    /**
     * This method is called after all the setXXX methods are called, but before any other method is called. This allows
     * the plugin to determine whether it can run or not. For example, if the plugin requires certain properties to be
     * set, and the properties are not set, then the plugin is invalid and will not run.
     *
     * @param warnings add strings to this list to specify warnings. For example, if the plugin is invalid, you should
     *                 specify why. Warnings are reported to users after the completion of the run.
     * @return true if the plugin is in a valid state. Invalid plugins will not be called
     */
    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }
}
