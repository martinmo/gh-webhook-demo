package net.mmorgenstern.gh;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StringUtilsTest {
    @Test
    public void test() {
        assertTrue(StringUtils.constantTimeCompare("a", "a"));
        assertTrue(StringUtils.constantTimeCompare("", ""));
        assertFalse(StringUtils.constantTimeCompare("a", "b"));
    }
}
