package com.davies.naraka.system;

import com.davies.naraka.autoconfigure.CurrentUserNameSupplier;
import org.springframework.stereotype.Component;

/**
 * @author davies
 * @date 2022/5/29 21:27
 */
@Component
public class UserNameSupplier implements CurrentUserNameSupplier {


    @Override
    public String get() {
        return "root";
    }
}
