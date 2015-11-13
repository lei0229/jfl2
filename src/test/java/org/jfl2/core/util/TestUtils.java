package org.jfl2.core.util;

import org.junit.Assert;

/**
 * Utilities for junit test
 */
public class TestUtils {

    /**
     * Exceptionのテスト用
     * <p>
     * <pre></pre>TestUtil.assetThrows( NumberFormatException.class, ()-> {
     * JPASupport.transaction(em -> {
     * Integer.parseInt("string");
     * });
     * });</pre>
     *
     * @param excptionClass
     * @param block
     * @param <X>
     * @return
     */
    public static <X extends Throwable> Throwable assetThrows(final Class<X> excptionClass, final Runnable block) {
        try {
            block.run();
            Assert.fail("Failed to require exception ");
        } catch (Throwable ex) {
            if (excptionClass.isInstance(ex)) {
                return ex;
            }
            ex.printStackTrace();
            Assert.fail("Failed to throw expected exception : " + ex.getClass());
        }
        return null;
    }
}
