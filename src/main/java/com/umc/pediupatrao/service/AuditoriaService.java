package com.umc.pediupatrao.service;

import com.umc.pediupatrao.entity.Auditoria;
import com.umc.pediupatrao.entity.Auditoria.CampoModificado;
import com.umc.pediupatrao.repository.AuditoriaRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.MongoTemplate;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final MongoTemplate mongoTemplate;

    public AuditoriaService(AuditoriaRepository auditoriaRepository, MongoTemplate mongoTemplate) {
        this.auditoriaRepository = auditoriaRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public Auditoria registrar(String tipoOperacao, String entidadeAfetada,
            String identificadorEntidade, Map<String, CampoModificado> camposModificados,
            String justificativa) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("É necessário um usuário autenticado para registrar auditoria.");
        }
        String perfil = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.substring("ROLE_".length()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("O usuário autenticado não possui perfil válido."));

        Auditoria auditoria = new Auditoria(
                UUID.randomUUID().toString(),
                authentication.getName(),
                perfil,
                LocalDateTime.now(),
                tipoOperacao,
                entidadeAfetada,
                identificadorEntidade,
                camposModificados,
                justificativa);
        return mongoTemplate.insert(auditoria);
    }

    public String usuarioAtual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("É necessário um usuário autenticado.");
        }
        return authentication.getName();
    }

    public java.util.List<Auditoria> listar() {
        return auditoriaRepository.findAllByOrderByDataHoraDesc();
    }
}
