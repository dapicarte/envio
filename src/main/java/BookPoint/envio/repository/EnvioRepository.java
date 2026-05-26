package BookPoint.envio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import BookPoint.envio.model.Envio;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long> {
}
