package com.ms.core.infrastructure.persistence.config;

import com.ms.core.infrastructure.InfrastructurePackageMarker;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class InfrastructureJpaPackageRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                        BeanDefinitionRegistry registry) {
        AutoConfigurationPackages.register(registry, InfrastructurePackageMarker.class.getPackageName());
    }
}
