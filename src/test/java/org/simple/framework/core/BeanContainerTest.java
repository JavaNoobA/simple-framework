package org.simple.framework.core;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author pengfei.zhao
 * @date 2021/1/18 20:16
 */
public class BeanContainerTest {
    private static BeanContainer container = null;

    @BeforeAll
    static void init() {
        container = BeanContainer.getInstance();
    }

    @Test
    public void loadBeanTest() {
        container.loadBeans("me.erudev");
        Map<Class<?>, Object> beanMap = container.beanMap;
        for (Map.Entry<Class<?>, Object> entry : beanMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }


}