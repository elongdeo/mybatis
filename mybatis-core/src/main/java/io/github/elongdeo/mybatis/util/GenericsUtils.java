package io.github.elongdeo.mybatis.util;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 泛型工具类
 *
 * @author dingyinlong
 * @date 2019年12月06日20:03:45
 */
public class GenericsUtils {

    private final static Map<String, Class> CLASS_GENERIC_TYPE_CACHE = new ConcurrentHashMap<>();

    /**
     * 通过反射,获得定义Class时声明的父类的范型参数的类型
     *
     * @param clazz 定义的Class
     * @return 返回第一个类型
     */
    public static Class getSuperClassGenericType(Class clazz) {
        return getSuperClassGenericType(clazz, 0);
    }

    /**
     * 通过反射,获得定义Class时声明的父类的范型参数的类型
     *
     * @param clazz 定义的Class
     * @return 返回某下标的类型
     */
    public static Class getSuperClassGenericType(Class clazz, int index)
            throws IndexOutOfBoundsException {
        String key = clazz.getName() + index;
        if (CLASS_GENERIC_TYPE_CACHE.containsKey(key)) {
            return CLASS_GENERIC_TYPE_CACHE.get(key);
        }
        Class result;
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            result = Object.class;
        } else {
            Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
            if (index >= params.length || index < 0 || !(params[index] instanceof Class)) {
                result = Object.class;
            } else {
                result = (Class) params[index];
            }
        }
        CLASS_GENERIC_TYPE_CACHE.put(key, result);
        return result;
    }

    public static void main(String[] args) {
        List<String> arrays = getStrings();
        Class<? extends List> aClass = arrays.getClass();
        System.out.println(aClass.getName());
        try {
            Method method = GenericsUtils.class.getDeclaredMethod("getStrings");
            Class<?> bClass = method.getReturnType();
            System.out.println(bClass.getName());
            System.out.println(getSuperClassGenericType(bClass));
            System.out.println(method.getReturnType());
            System.out.println(method.getGenericReturnType());
            System.out.println(getMethodGenericReturnType(method));
            System.out.println(List.class.isAssignableFrom(List.class));
            System.out.println(List.class.isAssignableFrom(ArrayList.class));
            System.out.println(Object.class.isAssignableFrom(ArrayList.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private static List<String> getStrings() {
        return Arrays.asList("11", "22");
    }


    public static Class<?> getMethodGenericReturnType(Method method) {
        return chooseOneType(method.getReturnType(), method.getGenericReturnType());
    }

    static Class<?> chooseOneType(Class<?> literalType, Type genericType) {
        if (genericType instanceof ParameterizedType) {
            return digFromGenericType(genericType);
        }
        return literalType;
    }

    static Class<?> digFromGenericType(Type genericType) {
        Type[] typeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
        Type type = typeArguments[0];
        if (type instanceof ParameterizedType) {
            return digFromGenericType(type);
        } else {
            return (Class<?>) type;
        }
    }
}