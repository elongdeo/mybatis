/**
 *    Copyright ${license.git.copyrightYears} the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.config.xml;

import org.mybatis.generator.config.*;
import org.mybatis.generator.exception.XMLParserException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Properties;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

/**
 * This class parses old Ibator configuration files into the new Configuration
 * API
 * 
 * @author Jeff Butler
 */
public class IbatorConfigurationParser extends MyBatisGeneratorConfigurationParser {

    public IbatorConfigurationParser(Properties properties) {
        super(properties);
    }

    public Configuration parseIbatorConfiguration(Element rootNode)
            throws XMLParserException {

        Configuration configuration = new Configuration();

        NodeList nodeList = rootNode.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if ("properties".equals(childNode.getNodeName())) {
                parseProperties(configuration, childNode);
            } else if ("classPathEntry".equals(childNode.getNodeName())) {
                parseClassPathEntry(configuration, childNode);
            } else if ("ibatorContext".equals(childNode.getNodeName())) {
                parseIbatorContext(configuration, childNode);
            }
        }

        return configuration;
    }

    private void parseIbatorContext(Configuration configuration, Node node) {

        Properties attributes = parseAttributes(node);
        String defaultModelType = attributes.getProperty("defaultModelType");
        String targetRuntime = attributes.getProperty("targetRuntime");
        String introspectedColumnImpl = attributes
                .getProperty("introspectedColumnImpl");
        String id = attributes.getProperty("id");

        ModelType mt = defaultModelType == null ? null : ModelType
                .getModelType(defaultModelType);

        Context context = new Context(mt);
        context.setId(id);
        if (stringHasValue(introspectedColumnImpl)) {
            context.setIntrospectedColumnImpl(introspectedColumnImpl);
        }
        if (stringHasValue(targetRuntime)) {
            context.setTargetRuntime(targetRuntime);
        }

        configuration.addContext(context);

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if ("property".equals(childNode.getNodeName())) {
                parseProperty(context, childNode);
            } else if ("ibatorPlugin".equals(childNode.getNodeName())) {
                parseIbatorPlugin(context, childNode);
            } else if ("commentGenerator".equals(childNode.getNodeName())) {
                parseCommentGenerator(context, childNode);
            } else if ("jdbcConnection".equals(childNode.getNodeName())) {
                parseJdbcConnection(context, childNode);
            } else if ("javaModelGenerator".equals(childNode.getNodeName())) {
                parseJavaModelGenerator(context, childNode);
            } else if ("javaTypeResolver".equals(childNode.getNodeName())) {
                parseJavaTypeResolver(context, childNode);
            } else if ("sqlMapGenerator".equals(childNode.getNodeName())) {
                parseSqlMapGenerator(context, childNode);
            } else if ("daoGenerator".equals(childNode.getNodeName())) {
                parseDaoGenerator(context, childNode);
            } else if ("table".equals(childNode.getNodeName())) {
                parseTable(context, childNode);
            }
        }
    }

    private void parseIbatorPlugin(Context context, Node node) {
        PluginConfiguration pluginConfiguration = new PluginConfiguration();

        context.addPluginConfiguration(pluginConfiguration);

        Properties attributes = parseAttributes(node);
        String type = attributes.getProperty("type");

        pluginConfiguration.setConfigurationType(type);

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if ("property".equals(childNode.getNodeName())) {
                parseProperty(pluginConfiguration, childNode);
            }
        }
    }

    private void parseDaoGenerator(Context context, Node node) {
        JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();

        context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);

        Properties attributes = parseAttributes(node);
        String type = attributes.getProperty("type");
        String targetPackage = attributes.getProperty("targetPackage");
        String targetProject = attributes.getProperty("targetProject");
        String implementationPackage = attributes
                .getProperty("implementationPackage");

        javaClientGeneratorConfiguration.setConfigurationType(type);
        javaClientGeneratorConfiguration.setTargetPackage(targetPackage);
        javaClientGeneratorConfiguration.setTargetProject(targetProject);
        javaClientGeneratorConfiguration
                .setImplementationPackage(implementationPackage);

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node childNode = nodeList.item(i);

            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            if ("property".equals(childNode.getNodeName())) {
                parseProperty(javaClientGeneratorConfiguration, childNode);
            }
        }
    }
}
