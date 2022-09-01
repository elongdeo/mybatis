package io.github.elongdeo.mybatis.util;

import io.github.elongdeo.mybatis.core.SerializableFunction;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dingyinlong
 * @date 2019年11月22日14:57:23
 **/
public class LambdaUtils {

    private final static Map<String, String> CLASS_LAMBDA_FIELD_CACHE = new ConcurrentHashMap<>();
    private final static String GET = "get";
    private final static String IS = "is";
    private final static String WRITE_REPLACE = "writeReplace";

    /***
     * 转换方法引用为属性名
     * @param function lambda方法
     * @return 属性名
     */
    public static <T> String getGetterField(SerializableFunction<T> function) {
        // 优先从缓存读取
        String lambdaClassName = function.getClass().getName();
        if (CLASS_LAMBDA_FIELD_CACHE.containsKey(lambdaClassName)) {
            return CLASS_LAMBDA_FIELD_CACHE.get(lambdaClassName);
        }
        String filedName = "";
        // 获取序列化的lambda表达式
        SerializedLambda lambda = getSerializedLambda(function);
        if (lambda != null) {
            // 获取方法名
            String methodName = lambda.getImplMethodName();
            String prefix = null;
            if (methodName.startsWith(GET)) {
                prefix = GET;
            } else if (methodName.startsWith(IS)) {
                prefix = IS;
            }
            if (prefix != null) {
                filedName = toLowerCaseFirstOne(methodName.replace(prefix, ""));
            }
        }
        CLASS_LAMBDA_FIELD_CACHE.put(lambdaClassName, filedName);
        return filedName;
    }

    private static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            return s;
        } else {
            return Character.toLowerCase(s.charAt(0)) + s.substring(1);
        }
    }

    /**
     * 获取序列化的Lambda信息
     */
    private static SerializedLambda getSerializedLambda(Serializable function) {
        SerializedLambda lambda = null;
        try {
            // 提取SerializedLambda
            Method method = function.getClass().getDeclaredMethod(WRITE_REPLACE);
            method.setAccessible(Boolean.TRUE);
            lambda = (SerializedLambda) method.invoke(function);
        } catch (Exception e) {
            // do nothing
        }
        return lambda;
    }

    public static void main(String[] args) {
//        getGetterField(BaseExample::hashCode);
        String testValue = "gmtCreate != null";
        int i = testValue.indexOf(".");
        if (i >= 0) {
            testValue = testValue.substring(i + 1);
        }
        System.out.println(testValue.substring(0, testValue.length() - 8));
    }
}