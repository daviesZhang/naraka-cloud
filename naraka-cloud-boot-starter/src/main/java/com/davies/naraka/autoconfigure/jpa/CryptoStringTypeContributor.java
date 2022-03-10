package com.davies.naraka.autoconfigure.jpa;

import com.davies.naraka.cloud.common.StringConstants;
import com.google.common.base.Strings;
import org.hibernate.boot.model.TypeContributions;
import org.hibernate.boot.model.TypeContributor;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.CustomType;

import java.util.Map;

/**
 * @author davies
 */
public class CryptoStringTypeContributor implements TypeContributor {

    private final static String DEFAULT_CRYPTO_REGISTRATION_KEY = "crypto";


    private final Map<String, String> cryptoMap;

    private final String defaultKey;

    public CryptoStringTypeContributor(Map<String, String> cryptoMap, String defaultKey) {
        this.cryptoMap = cryptoMap;
        this.defaultKey = defaultKey;
    }

    @Override
    public void contribute(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        if (this.cryptoMap != null && !this.cryptoMap.isEmpty()) {
            this.cryptoMap.forEach((key, value) -> {
                CustomType customType = new CustomType(new CryptoStringUserType(value),
                        new String[]{DEFAULT_CRYPTO_REGISTRATION_KEY + StringConstants.UNDERSCORE + key, key});
                typeContributions.contributeType(customType);
            });
        }
        if (!Strings.isNullOrEmpty(defaultKey)) {
            CustomType customType = new CustomType(new CryptoStringUserType(this.defaultKey),
                    new String[]{DEFAULT_CRYPTO_REGISTRATION_KEY});
            typeContributions.contributeType(customType);
        }
    }
}
