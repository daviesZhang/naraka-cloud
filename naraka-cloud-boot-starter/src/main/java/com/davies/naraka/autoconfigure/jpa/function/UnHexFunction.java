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
import org.hibernate.query.criteria.internal.expression.function.BasicFunctionExpression;

import javax.persistence.criteria.Expression;
import java.io.Serializable;

/**
 * Models the ANSI SQL <tt>SUBSTRING</tt> function.
 *
 * @author Steve Ebersole
 */
public class UnHexFunction
		extends BasicFunctionExpression<String>
		implements Serializable {
	public static final String NAME = "UNHEX";

	private final Expression<String> value;



	public UnHexFunction(
			CriteriaBuilderImpl criteriaBuilder,
			Expression<String> value

			) {
		super( criteriaBuilder, String.class, NAME );
		this.value = value;


	}




	public Expression<String> getValue() {
		return value;
	}

	@Override
	public void registerParameters(ParameterRegistry registry) {

		Helper.possibleParameter( getValue(), registry );

	}

	@Override
	public String render(RenderingContext renderingContext) {
		renderingContext.getFunctionStack().push( this );

		try {
			return "unhex(" +
					((Renderable) getValue()).render(renderingContext) +

					')';
		}
		finally {
			renderingContext.getFunctionStack().pop();
		}
	}

}
