package com.deploymanager.config;   // ¡Debe coincidir con tu base package!

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component("auditorProvider")
public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        // Por ahora devolvemos un valor fijo; en el futuro lo leerás de tu contexto
        return Optional.of("system");
    }
}
