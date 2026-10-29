package br.com.fatecads.fatecads.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.fatecads.fatecads.entity.Usuario;
import br.com.fatecads.fatecads.entity.Aluno;
import br.com.fatecads.fatecads.repository.UsuarioRepository;
import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario save(Usuario usuario) {
        // Criptografa a senha antes de salvar
        usuario.setSenhaUsuario(passwordEncoder.encode(usuario.getSenhaUsuario()));
        if (usuario.getRole() == null || usuario.getRole().isBlank()) {
            usuario.setRole("ROLE_USER");
        }
        return usuarioRepository.save(usuario);
    }

    public Usuario savePublic(Usuario usuario) {
        usuario.setRole("ROLE_USER");
        return save(usuario);
    }

    public Usuario findbyId(Integer id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmailUsuario(email).orElse(null);
    }

    public Usuario findByLogin(String login) {
        return usuarioRepository.findByLoginUsuario(login).orElse(null);
    }

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Usuario vincularAluno(Integer usuarioId, Aluno aluno) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        usuario.setAluno(aluno);
        return usuarioRepository.save(usuario);
    }

    public Usuario updatePassword(Usuario usuario, String rawPassword) {
        usuario.setSenhaUsuario(passwordEncoder.encode(rawPassword));
        return usuarioRepository.save(usuario);
    }

}