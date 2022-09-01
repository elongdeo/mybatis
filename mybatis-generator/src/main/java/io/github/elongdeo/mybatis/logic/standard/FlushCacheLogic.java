package io.github.elongdeo.mybatis.logic.standard;

import io.github.elongdeo.mybatis.constants.PluginConstants;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.Arrays;
import java.util.List;


/**
 * 禁用缓存
 * @author dingyinlong
 * @date 2019-11-22 21:39:20
 */
public class FlushCacheLogic {


    public static void markFlushCacheTrue(XmlElement parentElement, IntrospectedTable introspectedTable) {
        String property = introspectedTable.getTableConfigurationProperty(PluginConstants.PROPERTY_FLUSH_CACHE_METHODS);
        if (StringUtils.isEmpty(property)) {
            return;
        }
        List<String> flushCacheMethods = Arrays.asList(property.split(","));
        parentElement.getElements().forEach(element -> {
            if (element instanceof XmlElement) {
                XmlElement xmlElement = (XmlElement) element;
                if (xmlElement.getAttributes().stream().anyMatch(attr -> "id".equals(attr.getName()) && flushCacheMethods.contains(attr.getValue()))) {
                    xmlElement.addAttribute(new Attribute("flushCache", "true"));
                }
            }
        });
    }
}
