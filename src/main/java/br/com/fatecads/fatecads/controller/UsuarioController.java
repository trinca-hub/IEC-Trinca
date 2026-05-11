package br.com.fatecads.fatecads.controller;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

     @GetMapping("/criar}")
     public String criarForm(Model model) {
         model.addAttribute("usuario", new Usuario());
         return "usuario/formularioUsuario";
     }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Usuario usuario) {
        usuarioService.save(usuario);
        return "/login";
    }
}