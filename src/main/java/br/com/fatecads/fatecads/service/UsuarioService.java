package br.com.fatecads.fatecads.service;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario save(Usuario usuario) {
        // Criptografa a senha antes de salvar
        usuario.setSenhaUsario(passwordEncoder.encode(usuario.getSenhaUsuario()));
        return usuarioRepository.save(usuario);
    }

    public Usuario findbyId(Integer id) {
        return usuarioRepository.findById(id).orElse(null);
    }