package com.davies.naraka.autoconfigure.jpa;

import com.davies.naraka.autoconfigure.CurrentUserNameSupplier;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

/**
 * @author davies
 */
public class UserNameAuditorAware implements AuditorAware<String> {

    private final CurrentUserNameSupplier currentUserNameSupplier;

    public UserNameAuditorAware(CurrentUserNameSupplier currentUserNameSupplier) {
        this.currentUserNameSupplier = currentUserNameSupplier;
    }

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(this.currentUserNameSupplier.get());
    }
}
