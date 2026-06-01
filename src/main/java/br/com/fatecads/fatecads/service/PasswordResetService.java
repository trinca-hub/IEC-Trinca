package br.com.fatecads.fatecads.service;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import br.com.fatecads.fatecads.entity.Usuario;
import br.com.fatecads.fatecads.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

@Service
public class PasswordResetService {
	private final UsuarioService usuarioService;
	private final JwtService jwtService;
	private final EmailService emailService;

	@Value("${app.reset.base-url}")
	private String resetBaseUrl;

	public PasswordResetService(UsuarioService usuarioService, JwtService jwtService, EmailService emailService) {
		this.usuarioService = usuarioService;
		this.jwtService = jwtService;
		this.emailService = emailService;
	}

	public void requestPasswordReset(String email) {
		Usuario usuario = usuarioService.findByEmail(email);
		if (usuario == null) {
			throw new PasswordResetException("Email nao encontrado.");
		}
		if (usuario.getEmailUsuario() == null || usuario.getEmailUsuario().isBlank()) {
			throw new PasswordResetException("Usuario sem email cadastrado.");
		}

		String token = jwtService.generatePasswordResetToken(usuario);
		String encodedToken = UriUtils.encode(token, StandardCharsets.UTF_8);
		String link = resetBaseUrl + "?token=" + encodedToken;
		emailService.sendPasswordResetEmail(usuario.getEmailUsuario(), usuario.getNomeUsuario(), link);
	}

	public void resetPassword(String token, String novaSenha) {
		Claims claims = validateToken(token);
		Integer userId = parseUserId(claims);
		String email = claims.get("email", String.class);

		Usuario usuario = usuarioService.findbyId(userId);
		if (usuario == null) {
			throw new PasswordResetException("Usuario nao encontrado.");
		}
		if (email == null || !email.equalsIgnoreCase(usuario.getEmailUsuario())) {
			throw new PasswordResetException("Token invalido para este usuario.");
		}

		usuarioService.updatePassword(usuario, novaSenha);
	}

	private Claims validateToken(String token) {
		try {
			return jwtService.parseClaims(token);
		} catch (ExpiredJwtException ex) {
			throw new PasswordResetException("Token expirado.", ex);
		} catch (JwtException ex) {
			throw new PasswordResetException("Token invalido.", ex);
		}
	}

	private Integer parseUserId(Claims claims) {
		try {
			return Integer.parseInt(claims.getSubject());
		} catch (NumberFormatException ex) {
			throw new PasswordResetException("Token invalido.", ex);
		}
	}
}
