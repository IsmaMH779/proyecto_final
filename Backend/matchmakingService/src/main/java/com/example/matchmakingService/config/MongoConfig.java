package com.example.matchmakingService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
public class MongoConfig {

//    @Bean
    public MappingMongoConverter mappingMongoConverter(
            MongoDatabaseFactory factory,
            MongoMappingContext context,
            MongoCustomConversions conversions
    ) {
        // Resolver para referencias DBRef (no usas DBRef, pero es requerido por el constructor)
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);

        // Crea el conversor principal de Spring Data MongoDB
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, context);

        // Evita agregar el campo "_class" en los documentos de MongoDB
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));

        // Usa las conversiones por defecto (ya configuradas por Spring Boot)
        converter.setCustomConversions(conversions);

        // Aplica la configuración después de establecer propiedades
        converter.afterPropertiesSet();

        return converter;
    }
}