package it.polito.ai.es2.repositories;

import it.polito.ai.es2.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
  Optional<Image> findByName(String id);
}
