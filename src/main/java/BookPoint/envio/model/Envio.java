package BookPoint.envio.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "envios")
public class Envio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEnvio;

    @Column(nullable = false)
    private Long idPedido;

    @Column(nullable = false)
    private String direccionEnvio;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoEnvio estadoEnvio;

    @Column(nullable = false)
    private LocalDate fechaEnvio;

    @Column(nullable = true)
    private LocalDate fechaEntrega;
}
