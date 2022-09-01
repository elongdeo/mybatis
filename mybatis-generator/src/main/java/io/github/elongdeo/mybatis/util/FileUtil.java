package io.github.elongdeo.mybatis.util;

import java.io.File;
import java.util.StringTokenizer;

/**
 * 文件工具
 *
 * @author dingyinlong
 * @date 2018年12月05日16:14:34
 */
public class FileUtil {

    /**
     * 目标project下对应包的文件是否已存在
     *
     * @param targetProject 目标project
     * @param targetPackage 目标包
     * @param fileName      目标文件
     * @return 是否存在
     */
    public static boolean isExistFile(String targetProject, String targetPackage,
                                      String fileName) {

        File project = new File(targetProject);
        if (!project.isDirectory()) {
            return true;
        }

        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(targetPackage, ".");
        while (st.hasMoreTokens()) {
            sb.append(st.nextToken());
            sb.append(File.separatorChar);
        }

        File directory = new File(project, sb.toString());
        if (!directory.isDirectory()) {
            boolean rc = directory.mkdirs();
            if (!rc) {
                return true;
            }
        }

        File testFile = new File(directory, fileName);
        if (testFile.exists()) {
            return true;
        } else {
            return false;
        }
    }
}
