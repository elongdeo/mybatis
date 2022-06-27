package com.elong.deo;

import com.taobao.eagleeye.EagleEye;
import com.taobao.eagleeye.EagleEyeSlf4jMdcUpdater;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Pandora Boot应用的入口类
 * <p>
 * 其中导入sentinel-tracer.xml是加sentinel限流，详情见
 * http://gitlab.alibaba-inc.com/middleware-container/pandora-boot/wikis/spring-boot-sentinel
 * <p>
 * 详情见http://gitlab.alibaba-inc.com/middleware-container/pandora-boot/wikis/spring-boot-diamond
 *
 * @author chengxu
 */
@SpringBootApplication(scanBasePackages = {"com.elong.deo.*"})
@EnableTransactionManagement
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        EagleEye.addRpcContextListener(EagleEyeSlf4jMdcUpdater.getInstance());
    }
}
