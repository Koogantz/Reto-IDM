package com.idm.challenge.audit.infrastructure.adapters.secret;

public interface SecretProvider {

    String getSecret(String key);
}
