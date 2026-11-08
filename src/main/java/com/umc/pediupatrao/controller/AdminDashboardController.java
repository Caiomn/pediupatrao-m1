package com.umc.pediupatrao.controller;

import com.umc.pediupatrao.service.AdminDashboardService;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
public class AdminDashboardController {
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("content", "dashboard :: content");
        return "layout";
    }
}

