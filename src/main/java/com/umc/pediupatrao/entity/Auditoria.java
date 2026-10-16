package com.umc.pediupatrao.entity;

import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "auditorias")
public record Auditoria(
        @Id String id,
        String usuarioLogado,
        String perfilUsuario,
        LocalDateTime dataHora,
        String tipoOperacao,
        String entidadeAfetada,
        String identificadorEntidade,
        Map<String, CampoModificado> camposModificados,
        String justificativa) {

    public Auditoria {
        camposModificados = camposModificados == null ? Map.of() : Map.copyOf(camposModificados);
    }

    public record CampoModificado(Object valorAnterior, Object novoValor) {
    }
}
