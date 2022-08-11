package springbook.user.test.learningtest.pointcut;

import org.junit.Test;
import static org.junit.Assert.*;

import org.springframework.aop.aspectj.AspectJExpressionPointcut;

public class pointcutTest {
    @Test
    public void methodSignaturePointcut() throws SecurityException, NoSuchMethodException {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(public int " +
                "springbook.user.test.learningtest.pointcut.Target.minus(int, int) " +
                "throws java.lang.RuntimeException)");

        assertTrue(
                pointcut.getClassFilter().matches(Target.class) &&
                pointcut.getMethodMatcher().matches(
                    Target.class.getMethod("minus", int.class, int.class), null)
        );

        assertFalse(
                pointcut.getClassFilter().matches(Target.class) &&
                        pointcut.getMethodMatcher().matches(
                                Target.class.getMethod("plus", int.class, int.class), null
                        )
        );

        assertFalse(
                pointcut.getClassFilter().matches(Bean.class) &&
                        pointcut.getMethodMatcher().matches(
                                Target.class.getMethod("method"), null
                        )
        );
    }
}
