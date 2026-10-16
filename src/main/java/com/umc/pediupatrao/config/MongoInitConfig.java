package com.umc.pediupatrao.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class MongoInitConfig implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(MongoInitConfig.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            if (!mongoTemplate.collectionExists("pedidos")) {
                mongoTemplate.createCollection("pedidos");
            }
            if (!mongoTemplate.collectionExists("clientes")) {
                mongoTemplate.createCollection("clientes");
            }
            if (!mongoTemplate.collectionExists("usuarios")) {
                mongoTemplate.createCollection("usuarios");
            }
            if (!mongoTemplate.collectionExists("auditorias")) {
                mongoTemplate.createCollection("auditorias");
            }
            if (!mongoTemplate.collectionExists("produtos")) {
                mongoTemplate.createCollection("produtos");
            }
            if (!mongoTemplate.collectionExists("horariosAtendimento")) {
                mongoTemplate.createCollection("horariosAtendimento");
            }
        } catch (DataAccessException exception) {
            logger.warn("Não foi possível inicializar as coleções do MongoDB: {}", exception.getMessage());
        }
    }
}
