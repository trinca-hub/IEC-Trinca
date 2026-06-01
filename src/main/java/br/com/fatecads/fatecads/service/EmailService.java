package br.com.fatecads.fatecads.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Value("${app.mail.mode:log}")
    private String mailMode;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(String to, String name, String resetLink) {
        String subject = "Redefinicao de senha - FatecADS";
        String body = buildResetEmailBody(name, resetLink);

        if ("log".equalsIgnoreCase(mailMode)) {
            LOGGER.info("Password reset link for {}: {}", to, resetLink);
            return;
        }

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
        } catch (MessagingException ex) {
            throw new IllegalStateException("Nao foi possivel enviar o email de redefinicao.", ex);
        } catch (MailException ex) {
            throw new IllegalStateException("Falha na autenticacao do email. Verifique usuario e senha.", ex);
        }
    }

    private String buildResetEmailBody(String name, String resetLink) {
                String displayName = (name == null || name.isBlank()) ? "usuario" : name;
                return String.format("""
                                <div style=\"font-family: Arial, sans-serif; background: #f4f7fb; padding: 24px;\">
                                    <div style=\"max-width: 540px; margin: 0 auto; background: #ffffff; border-radius: 12px; padding: 24px; border: 1px solid #e2e8f0;\">
                                        <h2 style=\"margin: 0 0 12px; color: #0f172a;\">Redefinicao de senha</h2>
                                        <p style=\"margin: 0 0 12px; color: #475569;\">Ola, %s.</p>
                                        <p style=\"margin: 0 0 20px; color: #475569;\">Recebemos uma solicitacao para redefinir sua senha. Clique no botao abaixo para continuar. Este link expira em 15 minutos.</p>
                                        <a href=\"%s\" style=\"display: inline-block; background: #2563eb; color: #ffffff; text-decoration: none; padding: 12px 18px; border-radius: 8px; font-weight: 700;\">Redefinir senha</a>
                                        <p style=\"margin: 20px 0 0; color: #64748b; font-size: 13px;\">Se voce nao solicitou esta redefinicao, ignore este email.</p>
                                    </div>
                                </div>
                                """, displayName, resetLink);
    }
}
