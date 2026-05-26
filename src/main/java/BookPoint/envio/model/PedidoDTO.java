package BookPoint.envio.model;

import lombok.Data;

@Data
public class PedidoDTO {
    private Long idPedido;
    private String nombreCliente;
    private String direccionEnvio;
}