package org.simple.framework.inject;

import me.erudev.controller.UserController;
import me.erudev.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.simple.framework.core.BeanContainer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author pengfei.zhao
 * @date 2021/1/19 21:22
 */
public class DependencyInjectorTest {

    @Test
    public void doIocTest() {
        BeanContainer beanContainer = BeanContainer.getInstance();
        beanContainer.loadBeans("me.erudev");
        UserController userController = (UserController) beanContainer.getBean(UserController.class);
        userController.getUserService();
        Assertions.assertNull(userController.getUserService());
        new DependencyInjector().doIoc();
        Assertions.assertNotNull(userController.getUserService());
    }
}