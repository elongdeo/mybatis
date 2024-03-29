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
package org.mybatis.generator.plugins;

import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.XmlConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * This plugin generates a MapperConfig file containing mapper entries for SQL
 * maps generated for MyBatis3. This demonstrates hooking into the code
 * generation lifecycle and generating additional XML files.
 * 
 * <p>This plugin accepts three properties:
 * 
 * <ul>
 * <li><tt>fileName</tt> (optional) the name of the generated file. this
 * defaults to "SqlMapConfig.xml" if not specified.</li>
 * <li><tt>targetPackage</tt> (required) the name of the package where the file
 * should be placed. Specified like "com.mycompany.sql".</li>
 * <li><tt>targetProject</tt> (required) the name of the project where the file
 * should be placed.</li>
 * </ul>
 * 
 * <p>Note: targetPackage and targetProject follow the same rules as the
 * targetPackage and targetProject values on the sqlMapGenerator configuration
 * element.
 * 
 * @author Jeff Butler
 * 
 */
public class MapperConfigPlugin extends PluginAdapter {

    private List<String> mapperFiles;

    public MapperConfigPlugin() {
        mapperFiles = new ArrayList<String>();
    }

    @Override
    public boolean validate(List<String> warnings) {
        boolean valid = true;

        if (!stringHasValue(properties
                .getProperty("targetProject"))) {
            warnings.add(getString("ValidationError.18",
                    "MapperConfigPlugin",
                    "targetProject"));
            valid = false;
        }

        if (!stringHasValue(properties
                .getProperty("targetPackage"))) {
            warnings.add(getString("ValidationError.18",
                    "MapperConfigPlugin",
                    "targetPackage"));
            valid = false;
        }

        return valid;
    }

    @Override
    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles() {
        Document document = new Document(
                XmlConstants.MYBATIS3_MAPPER_CONFIG_PUBLIC_ID,
                XmlConstants.MYBATIS3_MAPPER_CONFIG_SYSTEM_ID);

        XmlElement root = new XmlElement("configuration");
        document.setRootElement(root);

        root.addElement(new TextElement("<!--"));
        root.addElement(new TextElement(
                "  This file is generated by MyBatis Generator."));
        root.addElement(new TextElement(
                "  This file is the shell of a Mapper Config file - in many cases you will need to add"));
        root.addElement(new TextElement(
                "    to this file before it is usable by MyBatis."));

        StringBuilder sb = new StringBuilder();
        sb.append("  This file was generated on ");
        sb.append(new Date());
        sb.append('.');
        root.addElement(new TextElement(sb.toString()));

        root.addElement(new TextElement("-->"));

        XmlElement mappers = new XmlElement("mappers");
        root.addElement(mappers);

        XmlElement mapper;
        for (String mapperFile : mapperFiles) {
            mapper = new XmlElement("mapper");
            mapper.addAttribute(new Attribute("resource", mapperFile));
            mappers.addElement(mapper);
        }

        GeneratedXmlFile gxf = new GeneratedXmlFile(document, properties
                .getProperty("fileName", "MapperConfig.xml"),
                properties.getProperty("targetPackage"),
                properties.getProperty("targetProject"),
                false, context.getXmlFormatter());

        List<GeneratedXmlFile> answer = new ArrayList<GeneratedXmlFile>(1);
        answer.add(gxf);

        return answer;
    }

    /*
     * This method collects the name of every SqlMap file generated in
     * this context.
     */
    @Override
    public boolean sqlMapGenerated(GeneratedXmlFile sqlMap,
            IntrospectedTable introspectedTable) {
        StringBuilder sb = new StringBuilder();
        sb.append(sqlMap.getTargetPackage());
        sb.append('.');
        String temp = sb.toString();
        sb.setLength(0);
        sb.append(temp.replace('.', '/'));
        sb.append(sqlMap.getFileName());
        mapperFiles.add(sb.toString());

        return true;
    }
}
