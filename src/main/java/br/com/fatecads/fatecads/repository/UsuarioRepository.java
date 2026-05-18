import br.com.fatecads.fatecads.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import br.com.fatecads.fatecads.entity.Usuario;


public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByLoginUsuario(String loginUsuario);
}