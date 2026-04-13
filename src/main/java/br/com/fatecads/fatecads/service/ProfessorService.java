package br.com.fatecads.fatecads.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.fatecads.fatecads.entity.Professor;
import br.com.fatecads.fatecads.repository.ProfessorRepository;

@Service
public class ProfessorService {
    // Injeção de dependências do repositório de professores
    @Autowired
    private ProfessorRepository professorRepository;

    //Método para salvar um professor
    public Professor save(Professor professor){
        return professorRepository.save(professor);
    }

    //Método para listar todos professores
    public List<Professor> findAll(){
        return professorRepository.findAll();
    }

    //Método para excluir um professor pelo id
    public void deleteById(Integer id){
        professorRepository.deleteById(id);
    }

    //Método para encontrar um professor pelo id
    public Professor findById(Integer id){
        return professorRepository.findById(id).orElse(null);
    }
}
