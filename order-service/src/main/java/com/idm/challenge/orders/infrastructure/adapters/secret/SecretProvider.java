package com.idm.challenge.orders.infrastructure.adapters.secret;

public interface SecretProvider {

    String getSecret(String key);
}
