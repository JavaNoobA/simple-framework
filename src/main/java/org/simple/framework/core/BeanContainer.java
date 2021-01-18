package org.simple.framework.core;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.simple.framework.core.annotation.Component;
import org.simple.framework.core.annotation.Controller;
import org.simple.framework.core.annotation.Repository;
import org.simple.framework.core.annotation.Service;
import org.simple.framework.utils.ClassUtil;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
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

    private enum ContainerHolder {
        HOLDER;

        private BeanContainer INSTANCE;

        ContainerHolder() {
            INSTANCE = new BeanContainer();
        }
    }
}
