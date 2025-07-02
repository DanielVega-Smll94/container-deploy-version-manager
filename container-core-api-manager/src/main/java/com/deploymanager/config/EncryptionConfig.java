package com.deploymanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

@Configuration
public class EncryptionConfig {
    @Value("${encryption.key}")
    private String key;

    @Bean
    public TextEncryptor textEncryptor() {
        // Usa un salt fijo o también configurable; aquí un salt hardcodeado
        return Encryptors.text(key, "yourconfig");
    }
}
