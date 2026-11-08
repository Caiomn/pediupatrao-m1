package com.umc.pediupatrao.controller;

import com.umc.pediupatrao.service.AdminDashboardService;
import com.umc.pediupatrao.dto.FormaPagamentoDashboard;
import com.umc.pediupatrao.dto.HorarioPicoDashboard;
import com.umc.pediupatrao.dto.TaxaCancelamentoDashboard;
import java.util.List;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
public class AdminDashboardApiController {
    private final AdminDashboardService dashboardService;

    public AdminDashboardApiController(AdminDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    private LocalDate data(LocalDate data, boolean inicio) {
        if (data != null) return data;
        LocalDate hoje = LocalDate.now();
        return inicio ? hoje.withDayOfMonth(1) : hoje;
    }

    @GetMapping("/indicadores")
    public Object indicadores(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return dashboardService.indicadores(data(dataInicio, true), data(dataFim, false));
    }

    @GetMapping("/graficos")
    public Object graficos(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return dashboardService.graficos(data(dataInicio, true), data(dataFim, false));
    }

    @GetMapping("/status")
    public Object status(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return dashboardService.status(data(dataInicio, true), data(dataFim, false));
    }

    @GetMapping("/ranking-produtos")
    public Object rankingProdutos(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return dashboardService.rankingProdutos(data(dataInicio, true), data(dataFim, false));
    }

    @GetMapping("/taxa-cancelamento")
    public TaxaCancelamentoDashboard taxaCancelamento(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return dashboardService.taxaCancelamento(data(dataInicio, true), data(dataFim, false));
    }

    @GetMapping("/formas-pagamento")
    public List<FormaPagamentoDashboard> formasPagamento(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return dashboardService.formasPagamento(data(dataInicio, true), data(dataFim, false));
    }

    @GetMapping("/horarios-pico")
    public List<HorarioPicoDashboard> horariosPico(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return dashboardService.horariosPico(data(dataInicio, true), data(dataFim, false));
    }
}
