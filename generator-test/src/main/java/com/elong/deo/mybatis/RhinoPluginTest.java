package com.elong.deo.mybatis;

import com.elong.deo.mybatis.plugin.RhinoPlugin;
import org.mybatis.generator.api.ShellRunner;

public class RhinoPluginTest {
    public static void main(String[] args) {
        String config = RhinoPlugin.class.getClassLoader()
                .getResource("generatorConfig-main.xml").getFile();
        String[] arg = {"-configfile", config, "-overwrite"};
        ShellRunner.main(arg);
    }
}
