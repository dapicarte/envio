package BookPoint.envio.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import BookPoint.envio.model.Envio;
import BookPoint.envio.model.EstadoEnvio;
import BookPoint.envio.model.PedidoDTO;
import BookPoint.envio.repository.EnvioRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class EnvioService {

    @Autowired
    private EnvioRepository envioRepository;

    @Autowired
    private RestTemplate restTemplate;

    public Envio crearEnvio(Envio envio) {
        try {
            String urlPedido = "http://localhost:8081/api/v1/pedidos/" + envio.getIdPedido();
            PedidoDTO pedido = restTemplate.getForObject(urlPedido, PedidoDTO.class);

            if (pedido != null) {
                envio.setDireccionEnvio(pedido.getDireccionEnvio());
                envio.setFechaEnvio(LocalDate.now());
                if (envio.getEstadoEnvio() == null) {
                envio.setEstadoEnvio(EstadoEnvio.PREPARANDO);
            }
                System.out.println("*************************");
                System.out.println(envio);
                System.out.println("*************************");
                return envioRepository.save(envio);
            }
            return null;
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            System.out.println("*************************");
            System.out.println("Pedido no disponible: " + e.getMessage());
            System.out.println("*************************");
            return null;
        }
    }

    public List<Envio> listarEnvios() {
        return envioRepository.findAll();
    }

    public Optional<Envio> findById(Long id) {
        return envioRepository.findById(id);
    }

    public Envio actualizarEstado(Long id, EstadoEnvio estadoEnvio) {
        Envio buscado = envioRepository.findById(id).orElse(null);
        if (buscado == null) return null;

        buscado.setEstadoEnvio(estadoEnvio);
        if (estadoEnvio == EstadoEnvio.ENTREGADO) {
            buscado.setFechaEntrega(LocalDate.now());
        }
        return envioRepository.save(buscado);
    }

    public boolean eliminarEnvio(Long id) {
        if (envioRepository.existsById(id)) {
            envioRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
