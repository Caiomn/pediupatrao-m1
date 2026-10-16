package com.umc.pediupatrao.service;

import com.umc.pediupatrao.entity.Cliente;
import com.umc.pediupatrao.repository.ClienteRepository;
import com.umc.pediupatrao.entity.Auditoria.CampoModificado;
import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final AuditoriaService auditoriaService;

    public ClienteService(ClienteRepository clienteRepository, AuditoriaService auditoriaService) {
        this.clienteRepository = clienteRepository;
        this.auditoriaService = auditoriaService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE') or (hasRole('ATENDENTE') and #p0.id == null)")
    public Cliente novoCliente(Cliente cliente) {
        return salvar(cliente);
    }

    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    // Método para excluir cliente
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public void excluir(String id) {
        Cliente anterior = clienteRepository.findById(java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> new IllegalArgumentException("Cliente nao encontrado."));
        clienteRepository.deleteById(id);
        auditoriaService.registrar("EXCLUSAO", "Cliente", id,
                snapshotCliente(anterior, null), null);
    }

    public Optional<Cliente> buscarPorId(String id) {
        return clienteRepository.findById(java.util.Objects.requireNonNull(id));
    }

    // Método para salvar um novo cliente ou atualizar um cliente existente
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE') or (hasRole('ATENDENTE') and #p0.id == null)")
    public Cliente salvar(Cliente cliente) {
        Cliente clienteValidado = java.util.Objects.requireNonNull(cliente);
        String clienteId = clienteValidado.getId();
        if (clienteId == null) {
            Cliente salvo = clienteRepository.save(clienteValidado);
            auditoriaService.registrar("CRIACAO", "Cliente", salvo.getId(),
                    snapshotCliente(null, salvo), null);
            return salvo;
        }
        else {
            // Verifica se o cliente existe antes de atualizar
            if (clienteRepository.existsById(clienteId)) {
                Cliente anterior = clienteRepository.findById(clienteId)
                        .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));
                Cliente salvo = clienteRepository.save(clienteValidado);
                LinkedHashMap<String, CampoModificado> campos = new LinkedHashMap<>();
                adicionarSeAlterado(campos, "nome", anterior.getNome(), salvo.getNome());
                adicionarSeAlterado(campos, "telefone", anterior.getTelefone(), salvo.getTelefone());
                adicionarSeAlterado(campos, "cep", anterior.getCep(), salvo.getCep());
                adicionarSeAlterado(campos, "logradouro", anterior.getLogradouro(), salvo.getLogradouro());
                adicionarSeAlterado(campos, "numero", anterior.getNumero(), salvo.getNumero());
                adicionarSeAlterado(campos, "complemento", anterior.getComplemento(), salvo.getComplemento());
                adicionarSeAlterado(campos, "bairro", anterior.getBairro(), salvo.getBairro());
                adicionarSeAlterado(campos, "cidade", anterior.getCidade(), salvo.getCidade());
                adicionarSeAlterado(campos, "estado", anterior.getEstado(), salvo.getEstado());
                adicionarSeAlterado(campos, "endereco", anterior.getEndereco(), salvo.getEndereco());
                if (!campos.isEmpty()) {
                    auditoriaService.registrar("ALTERACAO_CADASTRAL", "Cliente", salvo.getId(), campos, null);
                }
                return salvo;
            } else {
                throw new IllegalArgumentException("Cliente não encontrado para atualização.");
            }
        }
    }

    private void adicionarSeAlterado(Map<String, CampoModificado> campos, String campo,
            Object anterior, Object novoValor) {
        if (!java.util.Objects.equals(anterior, novoValor)) {
            campos.put(campo, new CampoModificado(anterior, novoValor));
        }
    }

    private LinkedHashMap<String, CampoModificado> snapshotCliente(Cliente anterior, Cliente novo) {
        LinkedHashMap<String, CampoModificado> campos = new LinkedHashMap<>();
        adicionarSnapshot(campos, "nome", anterior == null ? null : anterior.getNome(), novo == null ? null : novo.getNome());
        adicionarSnapshot(campos, "telefone", anterior == null ? null : anterior.getTelefone(), novo == null ? null : novo.getTelefone());
        adicionarSnapshot(campos, "cep", anterior == null ? null : anterior.getCep(), novo == null ? null : novo.getCep());
        adicionarSnapshot(campos, "logradouro", anterior == null ? null : anterior.getLogradouro(), novo == null ? null : novo.getLogradouro());
        adicionarSnapshot(campos, "numero", anterior == null ? null : anterior.getNumero(), novo == null ? null : novo.getNumero());
        adicionarSnapshot(campos, "complemento", anterior == null ? null : anterior.getComplemento(), novo == null ? null : novo.getComplemento());
        adicionarSnapshot(campos, "bairro", anterior == null ? null : anterior.getBairro(), novo == null ? null : novo.getBairro());
        adicionarSnapshot(campos, "cidade", anterior == null ? null : anterior.getCidade(), novo == null ? null : novo.getCidade());
        adicionarSnapshot(campos, "estado", anterior == null ? null : anterior.getEstado(), novo == null ? null : novo.getEstado());
        adicionarSnapshot(campos, "endereco", anterior == null ? null : anterior.getEndereco(), novo == null ? null : novo.getEndereco());
        return campos;
    }

    private void adicionarSnapshot(Map<String, CampoModificado> campos, String campo,
            Object anterior, Object novoValor) {
        campos.put(campo, new CampoModificado(anterior, novoValor));
    }

}
