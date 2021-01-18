package org.simple.framework.core.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.simple.framework.utils.ClassUtil;

import java.util.Set;


/**
 * @author pengfei.zhao
 * @date 2021/1/17 19:08
 */
public class ClassUtilTest {

    @Test
    public void extractPackageClassTest() {
        Set<Class<?>> set = ClassUtil.extractPackageClass("me/erudev/entity");
        Assertions.assertEquals(2, set.size());
    }

}