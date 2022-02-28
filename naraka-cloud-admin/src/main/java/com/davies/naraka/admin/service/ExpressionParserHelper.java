package com.davies.naraka.admin.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author davies
 * @date 2022/2/21 11:38 AM
 */

@Component
public class ExpressionParserHelper {
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    private final ExpressionParser parser = new SpelExpressionParser();

    private final ApplicationContext applicationContext;

    public ExpressionParserHelper(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 解析SqEL表达式
     * @param key 值
     * @param method 方法
     * @param args 方法参数
     * @return 解析后的内容
     */
    public String parserSqEL(@NotNull String key,@NotNull Method method,@NotNull Object[] args) {
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
