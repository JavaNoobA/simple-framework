package org.simple.framework.utils;

import com.google.common.collect.Sets;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * @author pengfei.zhao
 * @date 2021/1/17 10:19
 */
@Slf4j
public class ClassUtil {

    /**
     * 获取指定包下的所有类
     *
     * @param packageName 包名
     * @return class集合
     */
    public static Set<Class<?>> extractPackageClass(String packageName) {
        // classLoader加载指定包名
        // URL protocol 为file
        // 递归过滤出file(可能包含dir, file)为 .class(反射实例化) 文件
        ClassLoader classLoader = getClassLoader();
        String packageNameWithSlash = packageName.replace(".", "/");
        URL url = classLoader.getResource(packageNameWithSlash);
        if (url == null) {
            log.warn("Unable to extract the class file under the specified package name.");
            return Sets.newHashSet();
        }
        File file = new File(url.getPath());
        Set<Class<?>> set = null;
        if (url.getProtocol().equalsIgnoreCase("file")) {
            set = new HashSet<>();
            extractClassFile(set, file, packageNameWithSlash);
        }
        return set;
    }

    private static void extractClassFile(Set<Class<?>> set, File fileSource, String packagePath) {
        if (!fileSource.isDirectory()) {
            return;
        }
        File[] files = fileSource.listFiles(new FileFilter() {
            @SneakyThrows
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return true;
                } else {
                    String absoluteFilePath = file.getAbsolutePath();
                    if (absoluteFilePath.endsWith(".class")) {
                        addClassToSet(absoluteFilePath);
                    }

                }
                return false;
            }

            private void addClassToSet(String absoluteFilePath) throws Exception {
                String src = packagePath.replace("/", ".");
                String className = absoluteFilePath.replace(File.separator, ".");
                className = className.substring(className.indexOf(src), className.lastIndexOf("."));
                Class clazz = loadClass(className);
                set.add(clazz);
            }
        });
        if (files != null) {
            for (File f : files) {
                extractClassFile(set, f, packagePath);
            }
        }
    }

    public static Class loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (Exception ex) {
            log.error("load class error", ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * 获取当前 classLoader
     *
     * @return 类加载器
     */
    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 实例化 clazz
     *
     * @param clazz 目标类
     * @param <T>   泛型类
     * @return 实例化对象
     */
    public static <T> T newInstance(Class<?> clazz) {
        return newInstance(clazz, true);
    }

    /**
     * 实例化 clazz
     *
     * @param clazz 目标类
     * @param <T>   泛型类
     * @param accessible 是否允许设置私有属性
     * @return 实例化对象
     */
    public static <T> T newInstance(Class<?> clazz, boolean accessible) {
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(accessible);
            return (T) constructor.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 设置类的属性值
     *
     * @param field 成员变量
     * @param targetBean 类实例
     * @param fieldValue 成员变量的值
     * @param accessible 是否允许设置私有属性
     */
    public static void setField(Field field, Object targetBean, Object fieldValue, boolean accessible) {
        field.setAccessible(accessible);
        try {
            field.set(targetBean, fieldValue);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
