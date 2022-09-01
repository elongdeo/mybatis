package io.github.elongdeo.mybatis.logic.standard;

import io.github.elongdeo.mybatis.constants.PluginConfig;
import org.mybatis.generator.api.IntrospectedTable;

import java.util.regex.Pattern;

/**
 * @author dingyinlong
 * @date 2019/03/01
 */
public class DoSuffixLogic {

    /**
     * 数据库对象后缀
     */
    private static final String DATABASE_OBJECT_SUFFIX = "DO";
    /**
     * 数据库对象后缀
     */
    private static final String SPLIT_SUFFIX = "_[0-9]+$";
    /**
     * 空字符串
     */
    private static final String EMPTY_STRING = "";

    private static final Pattern doPattern = Pattern.compile(DATABASE_OBJECT_SUFFIX);
    private static final Pattern splitPattern = Pattern.compile(SPLIT_SUFFIX);

    /**
     * 替换处理命名
     *
     * @param introspectedTable 表配置信息
     */
    public static void dealFileName(IntrospectedTable introspectedTable) {
        String tableName = introspectedTable.getFullyQualifiedTable().getIntrospectedTableName();
        String newTableName = splitPattern.matcher(tableName).replaceAll(EMPTY_STRING);
        if (!tableName.equals(newTableName)) {
            introspectedTable.renameTable(newTableName);
        }
        if (PluginConfig.doSuffixAble) {
            // 数据库对象非DO后缀加上DO后缀
            if (!introspectedTable.getBaseRecordType().endsWith(DATABASE_OBJECT_SUFFIX)) {
                introspectedTable.setBaseRecordType(introspectedTable.getBaseRecordType() + DATABASE_OBJECT_SUFFIX);
            }
            // 替换Example名称中DO为空
            if (introspectedTable.getExampleType() != null) {
                introspectedTable.setExampleType(doPattern.matcher(introspectedTable.getExampleType()).replaceAll(EMPTY_STRING));
            }
            // 替换Mapper类名称中DO为空
            if (introspectedTable.getMyBatis3JavaMapperType() != null) {
                introspectedTable.setMyBatis3JavaMapperType(
                        doPattern.matcher(introspectedTable.getMyBatis3JavaMapperType()).replaceAll(EMPTY_STRING));
            }
            // 替换Mapper.xml名称中DO为空
            if (introspectedTable.getMyBatis3XmlMapperFileName() != null) {
                introspectedTable.setMyBatis3XmlMapperFileName(
                        doPattern.matcher(introspectedTable.getMyBatis3XmlMapperFileName()).replaceAll(EMPTY_STRING));
            }
        }
    }
}
