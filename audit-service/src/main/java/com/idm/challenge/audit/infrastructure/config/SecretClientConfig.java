package com.idm.challenge.audit.infrastructure.config;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.idm.challenge.audit.infrastructure.adapters.secret.AzureKeyVaultProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableConfigurationProperties(AzureKeyVaultProperties.class)
public class SecretClientConfig {

    @Bean
    @Profile("prod")
    public SecretClient secretClient(AzureKeyVaultProperties properties) {
        if (properties.getVaultUrl() == null || properties.getVaultUrl().isBlank()) {
            throw new IllegalStateException("Azure Key Vault URL must be configured when using the prod profile");
        }
        return new SecretClientBuilder()
                .vaultUrl(properties.getVaultUrl())
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();
    }
}
