package com.davies.naraka.autoconfigure.mybatis;

import com.baomidou.mybatisplus.extension.plugins.handler.DataPermissionHandler;
import com.davies.naraka.autoconfigure.AuthorityRowFunction;
import com.davies.naraka.autoconfigure.CurrentUserNameSupplier;
import com.davies.naraka.cloud.common.domain.AuthorityRow;
import com.google.common.base.Strings;
import net.sf.jsqlparser.expression.Expression;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 配合 @InterceptorIgnore()
 *
 * @author davies
 * @date 2022/4/8 21:16
 */
public class CustomDataPermissionHandler implements DataPermissionHandler {

    @Autowired
    private HttpServletRequest request;

    private final CurrentUserNameSupplier currentUserNameSupplier;


    private final AuthorityRowFunction authorityRowFunction;

    public CustomDataPermissionHandler(AuthorityRowFunction authorityRowFunction, CurrentUserNameSupplier currentUserNameSupplier) {
        this.authorityRowFunction = authorityRowFunction;
        this.currentUserNameSupplier = currentUserNameSupplier;

    }

    @Override
    public Expression getSqlSegment(Expression where, String mappedStatementId) {
        //CCJSqlParserUtil.parseCondExpression()
        //ExpressionVisitorAdapter.
        String user = currentUserNameSupplier.get();
        if (Strings.isNullOrEmpty(user)) {
            return where;
        }
        List<AuthorityRow> authorityRows = authorityRowFunction.apply(request.getRequestURI(), user);

        return where;
    }
}
