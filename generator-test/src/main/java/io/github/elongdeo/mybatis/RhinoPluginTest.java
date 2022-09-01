package io.github.elongdeo.mybatis;

import io.github.elongdeo.mybatis.plugin.Plugin;
import org.mybatis.generator.api.ShellRunner;

public class RhinoPluginTest {
    public static void main(String[] args) {
        String config = Plugin.class.getClassLoader()
                .getResource("generatorConfig-main.xml").getFile();
        String[] arg = {"-configfile", config, "-overwrite"};
        ShellRunner.main(arg);
    }
}
