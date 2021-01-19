package org.simple.framework.inject;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.simple.framework.core.BeanContainer;
import org.simple.framework.inject.annotation.Autowired;
import org.simple.framework.utils.ClassUtil;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Set;

/**
 * @author pengfei.zhao
 * @date 2021/1/19 20:29
 */
@Slf4j
public class DependencyInjector {

    private BeanContainer beanContainer;

    public void doIoc() {
        beanContainer = BeanContainer.getInstance();

        Set<Class<?>> classes = beanContainer.getClasses();
        if (CollectionUtil.isEmpty(classes)) {
            log.warn("bean container have no beans.");
            return;
        }

        for (Class<?> clazz : classes) {
            // 遍历Class对象所有的成员变量
            Field[] fields = clazz.getDeclaredFields();
            if (ArrayUtil.isEmpty(fields)) {
                continue;
            }
            for (Field field : fields) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    Autowired annotation = field.getAnnotation(Autowired.class);
                    String autowiredValue = annotation.value();
                    // 获取这些成员变量的类型
                    Class<?> fieldClass = field.getType();
                    Object fieldValue = getFieldInstance(fieldClass, autowiredValue);
                    if (fieldValue == null) {
                        throw new RuntimeException("unable to inject relevant type, target fieldClass is"
                                + fieldClass.getName());
                    }
                    Object targetBean = beanContainer.getBean(clazz);
                    ClassUtil.setField(field, targetBean, fieldValue, true);
                }
            }
        }

    }

    /**
     * 根据Class在BeanContainer里获取其实例或实现类
     *
     * @param fieldClass 类实例
     * @return bean
     */
    private Object getFieldInstance(Class<?> fieldClass, String autowiredValue) {
        Object bean = beanContainer.getBean(fieldClass);
        if (bean == null) {
            Class<?> implementedClass = getImplementedClass(fieldClass, autowiredValue);
            return beanContainer.getBean(implementedClass);
        }
        return bean;
    }

    /**
     * 获取接口实现类
     *
     * @param fieldClass 类实例
     * @param autowiredValue Autowired#value
     * @return clazz
     */
    private Class<?> getImplementedClass(Class<?> fieldClass, String autowiredValue) {
        Set<Class<?>> classes = beanContainer.getClassesBySuper(fieldClass);
        if (CollectionUtil.isEmpty(classes)) {
            return null;
        }
        if (StrUtil.isBlank(autowiredValue)) {
            if (classes.size() == 1) {
                return classes.iterator().next();
            } else {
                //如果多于两个实现类且用户未指定其中一个实现类，则抛出异常
                throw new RuntimeException("multiple implemented classes for " + fieldClass.getName() + " please set @Autowired's value to pick one");
            }
        }

        for (Class<?> clazz : classes) {
            if (Objects.equals(autowiredValue, clazz.getSimpleName())) {
                return clazz;
            }
        }
        return null;
    }
}
