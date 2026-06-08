package br.com.fatecads.fatecads.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.fatecads.fatecads.dto.PasswordResetConfirmDto;
import br.com.fatecads.fatecads.dto.PasswordResetRequestDto;
import br.com.fatecads.fatecads.service.PasswordResetException;
import br.com.fatecads.fatecads.service.PasswordResetService;

@Controller
public class PasswordResetController {
    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/esqueci-senha")
    public String forgotPassword(Model model) {
        model.addAttribute("request", new PasswordResetRequestDto());
        return "auth/forgot-password";
    }

    @PostMapping("/esqueci-senha")
    public String handleForgotPassword(@ModelAttribute("request") PasswordResetRequestDto request, Model model) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            model.addAttribute("errorMessage", "Informe um email valido.");
            return "auth/forgot-password";
        }
        try {
            passwordResetService.requestPasswordReset(request.getEmail());
            model.addAttribute("successMessage", "Enviamos um email com o link de redefinicao.");
        } catch (PasswordResetException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        } catch (IllegalStateException ex) {
            model.addAttribute("errorMessage", "Falha ao enviar o email. Verifique as configuracoes SMTP.");
        }
        return "auth/forgot-password";
    }

    @GetMapping("/redefinir-senha")
    public String resetPassword(@RequestParam(value = "token", required = false) String token, Model model) {
        if (token == null || token.isBlank()) {
            model.addAttribute("errorMessage", "Token ausente.");
        }
        PasswordResetConfirmDto form = new PasswordResetConfirmDto();
        form.setToken(token);
        model.addAttribute("form", form);
        return "auth/reset-password";
    }

    @PostMapping("/redefinir-senha")
    public String handleResetPassword(@ModelAttribute("form") PasswordResetConfirmDto form, Model model) {
        if (form.getToken() == null || form.getToken().isBlank()) {
            model.addAttribute("errorMessage", "Token ausente.");
            return "auth/reset-password";
        }
        if (form.getNovaSenha() == null || form.getNovaSenha().isBlank()) {
            model.addAttribute("errorMessage", "Informe a nova senha.");
            return "auth/reset-password";
        }
        if (!form.getNovaSenha().equals(form.getConfirmarSenha())) {
            model.addAttribute("errorMessage", "As senhas nao conferem.");
            return "auth/reset-password";
        }

        try {
            passwordResetService.resetPassword(form.getToken(), form.getNovaSenha());
            model.addAttribute("successMessage", "Senha redefinida com sucesso.");
        } catch (PasswordResetException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }
        return "auth/reset-password";
    }
}
