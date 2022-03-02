package com.davies.naraka.autoconfigure.security;

import com.davies.naraka.autoconfigure.CurrentUserNameSupplier;
import com.google.common.base.Strings;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author davies
 * @date 2022/3/2 8:41 PM
 */
@Aspect
public class CheckHasUserAspect {

    private final CurrentUserNameSupplier currentUserNameSupplier;

    public CheckHasUserAspect(CurrentUserNameSupplier currentUserNameSupplier) {
        this.currentUserNameSupplier = currentUserNameSupplier;
    }

    @Pointcut("@annotation(hasUser)")
    public void cutHasUser(HasUser hasUser) {
    }

    @Before(value = "cutHasUser(hasUser)", argNames = "hasUser")
    public void before(HasUser hasUser) throws Throwable {
        if (currentUserNameSupplier == null) {
            throw new IllegalArgumentException("currentUserNameSupplier bean could not find~");
        }
        if (Strings.isNullOrEmpty(currentUserNameSupplier.get())) {
            throw new UserNameMissingException();
        }

    }


}
