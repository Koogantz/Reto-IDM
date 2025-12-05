package com.idm.challenge.orders.infrastructure.adapters.secret;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"local", "test"})
public class LocalSecretProvider implements SecretProvider {

    private final LocalSecretProperties properties;

    public LocalSecretProvider(LocalSecretProperties properties) {
        this.properties = properties;
    }

    @Override
    public String getSecret(String key) {
        if (properties.getSecrets() == null || !properties.getSecrets().containsKey(key)) {
            throw new IllegalStateException("Secret not configured: " + key);
        }
        return properties.getSecrets().get(key);
    }
}
