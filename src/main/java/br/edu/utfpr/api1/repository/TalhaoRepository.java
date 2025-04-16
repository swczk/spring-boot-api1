package br.edu.utfpr.api1.repository;

import br.edu.utfpr.api1.model.Talhao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TalhaoRepository extends JpaRepository<Talhao, UUID> {

    Page<Talhao> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    Page<Talhao> findByCultura(String cultura, Pageable pageable);
}
