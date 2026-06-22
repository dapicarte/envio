package BookPoint.envio.service;

import BookPoint.envio.model.Envio;
import BookPoint.envio.model.EstadoEnvio;
import BookPoint.envio.model.PedidoDTO;
import BookPoint.envio.repository.EnvioRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnvioServiceTest {

    @Mock
    private EnvioRepository envioRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EnvioService envioService;

    @Test
    void testCrearEnvioPedidoExistenteSinEstado() {
        Envio envio = new Envio(null, 5L, null, null, null, null);
        Envio guardado = new Envio(1L, 5L, "Av. Principal 123", EstadoEnvio.PREPARANDO, LocalDate.now(), null);

        PedidoDTO pedido = new PedidoDTO();
        pedido.setIdPedido(5L);
        pedido.setNombreCliente("Juan Perez");
        pedido.setDireccionEnvio("Av. Principal 123");

        when(restTemplate.getForObject(anyString(), eq(PedidoDTO.class))).thenReturn(pedido);
        when(envioRepository.save(any(Envio.class))).thenReturn(guardado);

        Envio resultado = envioService.crearEnvio(envio);

        assertNotNull(resultado);
        assertEquals("Av. Principal 123", envio.getDireccionEnvio());
        assertEquals(EstadoEnvio.PREPARANDO, envio.getEstadoEnvio());
        assertEquals(LocalDate.now(), envio.getFechaEnvio());

        verify(restTemplate, times(1)).getForObject(anyString(), eq(PedidoDTO.class));
        verify(envioRepository, times(1)).save(envio);
    }

    @Test
    void testCrearEnvioPedidoExistenteConEstado() {
        Envio envio = new Envio(null, 5L, null, EstadoEnvio.EN_CAMINO, null, null);
        Envio guardado = new Envio(1L, 5L, "Av. Principal 123", EstadoEnvio.EN_CAMINO, LocalDate.now(), null);

        PedidoDTO pedido = new PedidoDTO();
        pedido.setIdPedido(5L);
        pedido.setDireccionEnvio("Av. Principal 123");

        when(restTemplate.getForObject(anyString(), eq(PedidoDTO.class))).thenReturn(pedido);
        when(envioRepository.save(any(Envio.class))).thenReturn(guardado);

        Envio resultado = envioService.crearEnvio(envio);

        assertNotNull(resultado);
        assertEquals("Av. Principal 123", envio.getDireccionEnvio());
        assertEquals(EstadoEnvio.EN_CAMINO, envio.getEstadoEnvio());

        verify(envioRepository, times(1)).save(envio);
    }

    @Test
    void testCrearEnvioPedidoNull() {
        Envio envio = new Envio(null, 5L, null, null, null, null);

        when(restTemplate.getForObject(anyString(), eq(PedidoDTO.class))).thenReturn(null);

        Envio resultado = envioService.crearEnvio(envio);

        assertNull(resultado);

        verify(restTemplate, times(1)).getForObject(anyString(), eq(PedidoDTO.class));
        verify(envioRepository, never()).save(any(Envio.class));
    }

    @Test
    void testCrearEnvioPedidoNotFound() {
        Envio envio = new Envio(null, 5L, null, null, null, null);

        HttpClientErrorException notFound = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY, new byte[0], null);

        when(restTemplate.getForObject(anyString(), eq(PedidoDTO.class))).thenThrow(notFound);

        Envio resultado = envioService.crearEnvio(envio);

        assertNull(resultado);

        verify(restTemplate, times(1)).getForObject(anyString(), eq(PedidoDTO.class));
        verify(envioRepository, never()).save(any(Envio.class));
    }

    @Test
    void testCrearEnvioPedidoNoDisponible() {
        Envio envio = new Envio(null, 5L, null, null, null, null);

        when(restTemplate.getForObject(anyString(), eq(PedidoDTO.class)))
                .thenThrow(new RuntimeException("Conexión rechazada"));

        Envio resultado = envioService.crearEnvio(envio);

        assertNull(resultado);

        verify(restTemplate, times(1)).getForObject(anyString(), eq(PedidoDTO.class));
        verify(envioRepository, never()).save(any(Envio.class));
    }

    @Test
    void testListarEnvios() {
        Envio e1 = new Envio(1L, 5L, "Calle 1", EstadoEnvio.PREPARANDO, LocalDate.now(), null);
        Envio e2 = new Envio(2L, 6L, "Calle 2", EstadoEnvio.ENTREGADO, LocalDate.now(), LocalDate.now());

        when(envioRepository.findAll()).thenReturn(Arrays.asList(e1, e2));

        List<Envio> resultado = envioService.listarEnvios();

        assertEquals(2, resultado.size());
        assertEquals("Calle 1", resultado.get(0).getDireccionEnvio());
        assertEquals("Calle 2", resultado.get(1).getDireccionEnvio());

        verify(envioRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdExistente() {
        Envio envio = new Envio(1L, 5L, "Calle 1", EstadoEnvio.PREPARANDO, LocalDate.now(), null);

        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));

        Optional<Envio> resultado = envioService.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getIdEnvio());
        assertEquals("Calle 1", resultado.get().getDireccionEnvio());

        verify(envioRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdNoExistente() {
        when(envioRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Envio> resultado = envioService.findById(99L);

        assertFalse(resultado.isPresent());

        verify(envioRepository, times(1)).findById(99L);
    }

    @Test
    void testActualizarEstadoEntregado() {
        Envio existente = new Envio(1L, 5L, "Calle 1", EstadoEnvio.EN_CAMINO, LocalDate.now(), null);
        Envio actualizado = new Envio(1L, 5L, "Calle 1", EstadoEnvio.ENTREGADO, LocalDate.now(), LocalDate.now());

        when(envioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(envioRepository.save(existente)).thenReturn(actualizado);

        Envio resultado = envioService.actualizarEstado(1L, EstadoEnvio.ENTREGADO);

        assertNotNull(resultado);
        assertEquals(EstadoEnvio.ENTREGADO, existente.getEstadoEnvio());
        assertEquals(LocalDate.now(), existente.getFechaEntrega());

        verify(envioRepository, times(1)).findById(1L);
        verify(envioRepository, times(1)).save(existente);
    }

    @Test
    void testActualizarEstadoNoEntregado() {
        Envio existente = new Envio(1L, 5L, "Calle 1", EstadoEnvio.PREPARANDO, LocalDate.now(), null);
        Envio actualizado = new Envio(1L, 5L, "Calle 1", EstadoEnvio.EN_CAMINO, LocalDate.now(), null);

        when(envioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(envioRepository.save(existente)).thenReturn(actualizado);

        Envio resultado = envioService.actualizarEstado(1L, EstadoEnvio.EN_CAMINO);

        assertNotNull(resultado);
        assertEquals(EstadoEnvio.EN_CAMINO, existente.getEstadoEnvio());
        assertNull(existente.getFechaEntrega());

        verify(envioRepository, times(1)).findById(1L);
        verify(envioRepository, times(1)).save(existente);
    }

    @Test
    void testActualizarEstadoNoExistente() {
        when(envioRepository.findById(99L)).thenReturn(Optional.empty());

        Envio resultado = envioService.actualizarEstado(99L, EstadoEnvio.ENTREGADO);

        assertNull(resultado);

        verify(envioRepository, times(1)).findById(99L);
        verify(envioRepository, never()).save(any(Envio.class));
    }

    @Test
    void testEliminarEnvioExistente() {
        when(envioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(envioRepository).deleteById(1L);

        boolean resultado = envioService.eliminarEnvio(1L);

        assertTrue(resultado);

        verify(envioRepository, times(1)).existsById(1L);
    }
    @Test
    void testEliminarEnvioNoExistente() {
        when(envioRepository.existsById(99L)).thenReturn(false);

        boolean resultado = envioService.eliminarEnvio(99L);

        assertFalse(resultado);

        verify(envioRepository, times(1)).existsById(99L);
        verify(envioRepository, never()).deleteById(anyLong());
    }
}