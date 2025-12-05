package com.idm.challenge.audit.infrastructure.adapters.secret;

import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
public class AzureKeyVaultSecretProvider implements SecretProvider {

    private final SecretClient secretClient;

    public AzureKeyVaultSecretProvider(SecretClient secretClient) {
        this.secretClient = secretClient;
    }

    @Override
    public String getSecret(String key) {
        KeyVaultSecret secret = secretClient.getSecret(key);
        if (secret == null || secret.getValue() == null) {
            throw new IllegalStateException("Secret not found in Azure Key Vault: " + key);
        }
        return secret.getValue();
    }
}
