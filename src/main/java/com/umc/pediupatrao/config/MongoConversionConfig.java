package com.umc.pediupatrao.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import com.umc.pediupatrao.entity.Role;

@Configuration
public class MongoConversionConfig {

    @Bean
    MongoCustomConversions mongoCustomConversions() {
        return MongoCustomConversions.create(adapter ->
                adapter.registerConverter(new LegacyRoleReadConverter()));
    }

    @ReadingConverter
    static class LegacyRoleReadConverter implements Converter<String, Role> {
        @Override
        public Role convert(String value) {
            return switch (value.toUpperCase(java.util.Locale.ROOT)) {
                case "USER" -> Role.ATENDENTE;
                case "ADMIN" -> Role.ADMIN;
                case "GERENTE" -> Role.GERENTE;
                case "ATENDENTE" -> Role.ATENDENTE;
                default -> throw new IllegalArgumentException("Perfil de usuÃ¡rio desconhecido: " + value);
            };
        }
    }
}
