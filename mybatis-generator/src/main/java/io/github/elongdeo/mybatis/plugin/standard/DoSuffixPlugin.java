package io.github.elongdeo.mybatis.plugin.standard;

import io.github.elongdeo.mybatis.logic.standard.DoSuffixLogic;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;

import java.util.List;

/**
 * 生成的DOExample/DOMapper.java/DOMapper.XML文件将DO去除，防止代码扫描报错
 *
 * @author dingyinlong
 * @date 2018年12月05日11:09:49
 */
public class DoSuffixPlugin extends PluginAdapter {

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        // 替换处理命名
        DoSuffixLogic.dealFileName(introspectedTable);
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
