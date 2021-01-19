package org.simple.framework.core;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.simple.framework.core.annotation.Component;
import org.simple.framework.core.annotation.Controller;
import org.simple.framework.core.annotation.Repository;
import org.simple.framework.core.annotation.Service;
import org.simple.framework.utils.ClassUtil;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bean 容器
 *
 * @author pengfei.zhao
 * @date 2021/1/18 19:52
 */
@Slf4j
public class BeanContainer {

    /**
     * singleton
     */
    public static final BeanContainer INSTANCE = ContainerHolder.HOLDER.INSTANCE;

    /**
     * store bean instance map
     */
    public Map<Class<?>, Object> beanMap = new ConcurrentHashMap<>();

    /**
     * marked bean
     */
    private static final List<Class<? extends Annotation>> ANNO_LIST =
            Arrays.asList(Component.class, Controller.class, Repository.class, Service.class);

    /**
     * 获取单例对象
     *
     * @return BeanContainer
     */
    public static BeanContainer getInstance() {
        return INSTANCE;
    }

    /**
     * 容器加载标识
     */
    private boolean loaded = false;

    /**
     * 容器是否被加载过
     *
     * @return true/false
     */
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * 存储 Bean 大小
     * @return size
     */
    public int size() {
        return beanMap.size();
    }

    public void loadBeans(String packageName) {
        if (isLoaded()) {
            log.warn("The container has been loaded");
            return;
        }

        Set<Class<?>> classSet = ClassUtil.extractPackageClass(packageName);
        if (CollectionUtil.isEmpty(classSet)) {
            log.warn("extract nothing from package...");
            return;
        }
        for (Class<?> clazz : classSet) {
            for (Class<? extends Annotation> anno : ANNO_LIST) {
                if (clazz.isAnnotationPresent(anno)) {
                    beanMap.put(clazz, ClassUtil.newInstance(clazz));
                }
            }
        }
    }

    /**
     * 添加 Bean 到容器
     *
     * @param clazz bean's clazz
     * @param obj   bean instance
     * @return
     */
    public Object addBean(Class<?> clazz, Object obj) {
        return beanMap.put(clazz, obj);
    }

    /**
     * 移除 bean
     *
     * @param clazz 指定类
     */
    public void removeBean(Class<?> clazz) {
        beanMap.remove(clazz);
    }

    public Object getBean(Class<?> clazz) {
        return beanMap.get(clazz);
    }

    /**
     * 获取容器管理的所有Class对象集合
     *
     * @return Class集合
     */
    public Set<Class<?>> getClasses() {
        return beanMap.keySet();
    }

    /**
     * 获取所有Bean集合
     *
     * @return Bean集合
     */
    public Set<Object> getBeans() {
        return new HashSet<>(beanMap.values());
    }

    /**
     * 根据注解筛选出Bean的Class集合
     *
     * @param annotation 注解
     * @return Class集合
     */
    public Set<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotation) {
        Set<Class<?>> set = new HashSet<>();

        for (Class<?> clazz : getClasses()) {
            if (clazz.isAnnotationPresent(annotation)) {
                set.add(clazz);
            }
        }
        return set.size() > 0 ? set : null;
    }

    /**
     * 通过接口或者父类获取实现类或者子类的Class集合，不包括其本身
     *
     * @param interfaceOrClass 接口Class或者父类Class
     * @return Class集合
     */
    public Set<Class<?>> getClassesBySuper(Class<?> interfaceOrClass) {
        Set<Class<?>> set = new HashSet<>();

        for (Class<?> clazz : getClasses()) {
            if (clazz.isAssignableFrom(interfaceOrClass) && !interfaceOrClass.equals(clazz)) {
                set.add(clazz);
            }
        }
        return set.size() > 0 ? set : null;

    }

    private enum ContainerHolder {
        HOLDER;

        private BeanContainer INSTANCE;

        ContainerHolder() {
            INSTANCE = new BeanContainer();
        }
    }
}
