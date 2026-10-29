package br.com.fatecads.fatecads.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.fatecads.fatecads.entity.Usuario;
import br.com.fatecads.fatecads.entity.Aluno;
import br.com.fatecads.fatecads.service.AlunoService;
import br.com.fatecads.fatecads.service.UsuarioService;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AlunoService alunoService;

     @GetMapping("/criar")
     public String criarForm(Model model) {
         model.addAttribute("usuario", new Usuario());
         return "usuario/formularioUsuario";
     }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Usuario usuario) {
        usuarioService.savePublic(usuario);
        return "redirect:/login";
    }

    @GetMapping("/gerenciar")
    public String gerenciar(Model model) {
        model.addAttribute("usuarios", usuarioService.findAll());
        model.addAttribute("alunos", alunoService.findAll());
        return "usuario/gerenciarUsuarios";
    }

    @PostMapping("/{id}/aluno")
    public String vincularAluno(@PathVariable Integer id, @RequestParam Integer alunoId) {
        Aluno aluno = alunoService.findById(alunoId);
        if (aluno != null) {
            usuarioService.vincularAluno(id, aluno);
        }
        return "redirect:/usuarios/gerenciar";
    }
}