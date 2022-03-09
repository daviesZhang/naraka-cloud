/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package com.davies.naraka.autoconfigure.jpa.function;

import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.Renderable;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.LiteralExpression;
import org.hibernate.query.criteria.internal.expression.function.BasicFunctionExpression;

import javax.persistence.criteria.Expression;
import java.io.Serializable;

/**
 * Models the ANSI SQL <tt>SUBSTRING</tt> function.
 *
 * @author Steve Ebersole
 */
public class AesDecryptFunction
        extends BasicFunctionExpression<String>
        implements Serializable {
    public static final String NAME = "AES_DECRYPT";

    private final Expression<String> value;
    private final Expression<String> key;


    public AesDecryptFunction(
            CriteriaBuilderImpl criteriaBuilder,
            Expression<String> value,
            String key
    ) {
        super(criteriaBuilder, String.class, NAME);
        this.value = value;
        this.key = new LiteralExpression<>(criteriaBuilder, key);

    }


    public Expression<String> getKey() {
        return key;
    }

    public Expression<String> getValue() {
        return value;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {

        Helper.possibleParameter(getValue(), registry);
        Helper.possibleParameter(getKey(), registry);
    }

    @Override
    public String render(RenderingContext renderingContext) {
        renderingContext.getFunctionStack().push(this);
        try {
            return "aes_decrypt(" +
                    ((Renderable) getValue()).render(renderingContext) +
                    ',' +
                    ((Renderable) getKey()).render(renderingContext) +
                    ')';
        } finally {
            renderingContext.getFunctionStack().pop();
        }
    }

}
