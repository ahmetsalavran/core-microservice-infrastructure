package com.ms.core.infrastructure.persistence.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.JpaRepository;

@AutoConfiguration
@ConditionalOnClass(JpaRepository.class)
@Import(InfrastructureJpaPackageRegistrar.class)
public class InfrastructureJpaAutoConfiguration {
}
