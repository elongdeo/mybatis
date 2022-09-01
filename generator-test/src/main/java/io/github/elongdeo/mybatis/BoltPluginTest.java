package io.github.elongdeo.mybatis;

import io.github.elongdeo.mybatis.plugin.BoltPlugin;
import org.mybatis.generator.api.ShellRunner;

public class BoltPluginTest {
    public static void main(String[] args) {
        String config = BoltPlugin.class.getClassLoader()
            .getResource("generatorConfig-bolt-main.xml").getFile();
        String[] arg = {"-configfile", config, "-overwrite"};
        ShellRunner.main(arg);
    }
}
