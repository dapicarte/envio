package BookPoint.envio.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import BookPoint.envio.model.Envio;
import BookPoint.envio.model.EstadoEnvio;
import BookPoint.envio.service.EnvioService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EnvioController.class)
@ActiveProfiles("test")
public class EnvioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockitoBean
    private EnvioService envioService;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void testCrearEnvio() throws Exception {
            Envio nuevo = new Envio(null, 5L, "Calle 1", null, null, null);
            Envio guardado = new Envio(1L, 5L, "Calle 1", EstadoEnvio.PREPARANDO, LocalDate.now(), null);

            Mockito.when(envioService.crearEnvio(any(Envio.class))).thenReturn(guardado);

            mockMvc.perform(post("/api/v1/envios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(nuevo)))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.idEnvio").value(1L))
                            .andExpect(jsonPath("$.idPedido").value(5L))
                            .andExpect(jsonPath("$.estadoEnvio").value("PREPARANDO"));
    }

    @Test
    void testCrearEnvioPedidoNoEncontrado() throws Exception {
            Envio nuevo = new Envio(null, 5L, "Calle 1", null, null, null);

            Mockito.when(envioService.crearEnvio(any(Envio.class))).thenReturn(null);

            mockMvc.perform(post("/api/v1/envios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(nuevo)))
                            .andExpect(status().isNotFound());
    }

    @Test
    void testCrearEnvioError() throws Exception {
            Envio nuevo = new Envio(null, 5L, "Calle 1", null, null, null);

            Mockito.when(envioService.crearEnvio(any(Envio.class)))
                            .thenThrow(new RuntimeException("Error inesperado"));

            mockMvc.perform(post("/api/v1/envios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(nuevo)))
                            .andExpect(status().isConflict());
    }

    @Test
    void testListarEnvios() throws Exception {
            Envio e1 = new Envio(1L, 5L, "Calle 1", EstadoEnvio.PREPARANDO, LocalDate.now(), null);
            Envio e2 = new Envio(2L, 6L, "Calle 2", EstadoEnvio.ENTREGADO, LocalDate.now(), LocalDate.now());

            Mockito.when(envioService.listarEnvios()).thenReturn(Arrays.asList(e1, e2));

            mockMvc.perform(get("/api/v1/envios"))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$", hasSize(2)))
                            .andExpect(jsonPath("$[0].direccionEnvio", is("Calle 1")))
                            .andExpect(jsonPath("$[1].estadoEnvio", is("ENTREGADO")));
    }

    @Test
    void testListarEnviosVacio() throws Exception {
            Mockito.when(envioService.listarEnvios()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/v1/envios"))
                            .andExpect(status().isNotFound());
    }

    @Test
    void testFindByIdExistente() throws Exception {
            Envio buscado = new Envio(1L, 5L, "Calle 1", EstadoEnvio.PREPARANDO, LocalDate.now(), null);

            Mockito.when(envioService.findById(1L)).thenReturn(Optional.of(buscado));

            mockMvc.perform(get("/api/v1/envios/1"))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.idEnvio").value(1L))
                            .andExpect(jsonPath("$.direccionEnvio").value("Calle 1"));
    }

    @Test
    void testFindByIdNoExistente() throws Exception {
            Mockito.when(envioService.findById(99L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/v1/envios/99"))
                            .andExpect(status().isNotFound());
    }

    @Test
    void testActualizarEstado() throws Exception {
            Envio actualizado = new Envio(1L, 5L, "Calle 1", EstadoEnvio.ENTREGADO, LocalDate.now(), LocalDate.now());

            Mockito.when(envioService.actualizarEstado(eq(1L), eq(EstadoEnvio.ENTREGADO)))
                            .thenReturn(actualizado);

            mockMvc.perform(put("/api/v1/envios/1/estado")
                            .param("estadoEnvio", "ENTREGADO"))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.idEnvio").value(1L))
                            .andExpect(jsonPath("$.estadoEnvio").value("ENTREGADO"));
    }

    @Test
    void testActualizarEstadoNoExistente() throws Exception {
            Mockito.when(envioService.actualizarEstado(eq(99L), eq(EstadoEnvio.ENTREGADO)))
                            .thenReturn(null);

            mockMvc.perform(put("/api/v1/envios/99/estado")
                            .param("estadoEnvio", "ENTREGADO"))
                            .andExpect(status().isNotFound());
    }

    @Test
    void testEliminarEnvio() throws Exception {
            Mockito.when(envioService.eliminarEnvio(1L)).thenReturn(true);

            mockMvc.perform(delete("/api/v1/envios/1"))
                            .andExpect(status().isOk());
    }

    @Test
    void testEliminarEnvioNoExistente() throws Exception {
            Mockito.when(envioService.eliminarEnvio(99L)).thenReturn(false);

            mockMvc.perform(delete("/api/v1/envios/99"))
                            .andExpect(status().isNotFound());
    }
}