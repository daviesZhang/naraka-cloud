package com.davies.naraka.autoconfigure.redis;

import com.davies.naraka.autoconfigure.DistributedLockException;
import com.davies.naraka.autoconfigure.annotation.DistributedLock;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.NotNull;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Strings.lenientFormat;

/**
 * @author davies
 * @date 2022/2/21 10:42 AM
 * @see DistributedLock
 */

@Aspect
@Slf4j
@Component
public class DistributedLockAspect {

    private static final String LOCK_NAME_TEMPLATE = "dlock:%s";

    private final RedissonClient redissonClient;

    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    private final ExpressionParser parser = new SpelExpressionParser();

    private final ApplicationContext applicationContext;

    public DistributedLockAspect(RedissonClient redissonClient, ApplicationContext applicationContext) {
        this.redissonClient = redissonClient;
        this.applicationContext = applicationContext;
    }

    @Pointcut("@annotation(distributedLock)")
    public void cutDistributedLock(DistributedLock distributedLock) {
    }

    @Around(value = "cutDistributedLock(distributedLock)", argNames = "joinPoint,distributedLock")
    public Object doAround(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        String key;
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        if (Strings.isNullOrEmpty(distributedLock.key())) {
            key = lenientFormat(LOCK_NAME_TEMPLATE, className + methodName);
        } else {
            String realKey = parserSqEL(distributedLock.key(),
                    ((MethodSignature) joinPoint.getSignature()).getMethod(),
                    joinPoint.getArgs());
            key = lenientFormat(LOCK_NAME_TEMPLATE, realKey);
        }
        RLock rLock = redissonClient.getLock(key);
        boolean getLock = false;
        try {
            log.info("[{}-{}]尝试获取锁[{}]~", className, methodName, key);
            getLock = rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), TimeUnit.MILLISECONDS);
            if (getLock) {
                log.info("[{}-{}]获取锁[{}],开始执行操作~", className, methodName, key);
                return joinPoint.proceed();
            }
            log.info("[{}-{}]获取锁[{}]失败~", className, methodName, key);
            throw new DistributedLockException(lenientFormat("获取锁%s_%s[%s]时失败~", className, methodName, key));
        } catch (InterruptedException e) {
            throw new DistributedLockException(lenientFormat("获取锁%s_%s[%s]时线程被中断", className, methodName, key), e);
        } finally {
            if (getLock) {
                rLock.unlock();
            }
        }

    }


    /**
     * 解析SqEL表达式
     *
     * @param key    值
     * @param method 方法
     * @param args   方法参数
     * @return 解析后的内容
     */
    private String parserSqEL(@NotNull String key, @NotNull Method method, @NotNull Object[] args) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setBeanResolver(new BeanFactoryResolver(applicationContext));
        String[] params = parameterNameDiscoverer.getParameterNames(method);
        for (int i = 0; i < args.length; i++) {
            assert params != null;
            context.setVariable(params[i], args[i]);
        }
        return parser.parseExpression(key).getValue(context, String.class);
    }


}
