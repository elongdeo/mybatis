package io.github.elongdeo.mybatis.util;

import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 类SqlSafeCheckUtil.java的实现描述：sql安全校验工具
 *
 * @author dingyinlong
 * @date 2018年04月28日10:45:04
 */
public class SqlSafeCheckUtil {

    private static final char UNDERLINE = '_';
    private static final String DESC = "desc";
    private static final String ASC = "asc";
    private static final Pattern PATTERN = Pattern.compile("\\s+");
    private static final Map<Class<?>, List<String>> CACHED_CLASS_KEY_WORD_MAP = new HashMap<>();

    public static boolean checkOrderByClause(String orderByClause, Class<?> objectClass) {

        boolean isSafe = false;

        if (!StringUtils.isEmpty(orderByClause) && objectClass != null) {

            List<String> keyList = buildKeyWord(objectClass);
            Matcher m = PATTERN.matcher(orderByClause);
            orderByClause = m.replaceAll(" ");

            String[] list = orderByClause.split(",");
            for (int i = 0; i < list.length; i++) {

                String item[] = list[i].split("\\s+");
                for (int j = 0; j < item.length; j++) {
                    if (!keyList.contains(item[j].toLowerCase())) {
                        isSafe = false;
                        return isSafe;
                    }

                }

            }

            isSafe = true;
        }
        return isSafe;
    }

    /**
     * 构建关键词
     *
     * @param objectClass
     * @return
     */
    private static List<String> buildKeyWord(Class<?> objectClass) {
        // 优先从缓存中获取
        List<String> keyList = CACHED_CLASS_KEY_WORD_MAP.get(objectClass);
        if (keyList != null) {
            return keyList;
        }
        List<Field> allFields = new ArrayList<>();
        do {
            // 获取所有字段
            Field[] fields = objectClass.getDeclaredFields();
            allFields.addAll(Arrays.asList(fields));
            // 循环取父类
            objectClass = objectClass.getSuperclass();
        } while (objectClass != null);
        List<String> allKeyWord = new ArrayList<>();
        allFields.forEach(field -> {
            // Boolean类型转换需要加is_前缀再转下滑格式，其他直接转下滑格式
            if (Boolean.class.equals(field.getType())) {
                allKeyWord.add("is_" + toUnderline(field.getName()));
            } else {
                allKeyWord.add(toUnderline(field.getName()));
            }
        });
        allKeyWord.add(DESC);
        allKeyWord.add(ASC);
        allKeyWord.add("");
        CACHED_CLASS_KEY_WORD_MAP.put(objectClass, allKeyWord);
        return allKeyWord;
    }

    /**
     * 转换 驼峰命名到下划线的名
     *
     * @param param
     * @return
     */
    private static String toUnderline(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(UNDERLINE);
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static boolean checkOrderByClause(String orderByClause, Class<?> objectClass, String[] allColumnNames) {

        boolean isSafe = false;

        if (!StringUtils.isEmpty(orderByClause) && objectClass != null) {

            List<String> keyList = buildKeyWord(objectClass, Arrays.asList(allColumnNames));
            Matcher m = PATTERN.matcher(orderByClause);
            orderByClause = m.replaceAll(" ");

            String[] list = orderByClause.split(",");
            for (int i = 0; i < list.length; i++) {

                String item[] = list[i].split("\\s+");
                for (int j = 0; j < item.length; j++) {
                    if (!keyList.contains(item[j].toLowerCase())) {
                        return false;
                    }

                }

            }

            isSafe = true;
        }
        return isSafe;
    }

    /**
     * 构建关键词
     *
     * @param objectClass
     * @return
     */
    private static List<String> buildKeyWord(Class<?> objectClass, List<String> allColumnNames) {
        // 优先从缓存中获取
        List<String> keyList = CACHED_CLASS_KEY_WORD_MAP.get(objectClass);
        if (keyList != null) {
            return keyList;
        }
        List<String> allKeyWord = new ArrayList<>();
        allKeyWord.addAll(allColumnNames);
        allKeyWord.add(DESC);
        allKeyWord.add(ASC);
        allKeyWord.add("");
        CACHED_CLASS_KEY_WORD_MAP.put(objectClass, allKeyWord);
        return allKeyWord;
    }
}
