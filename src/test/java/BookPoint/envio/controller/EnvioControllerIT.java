package BookPoint.envio.controller;

import BookPoint.envio.model.Envio;
import BookPoint.envio.model.EstadoEnvio;
import BookPoint.envio.repository.EnvioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EnvioControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EnvioRepository envioRepository;

    @BeforeEach
    void cleanDb() {
        envioRepository.deleteAll();
    }

    @Test
    void testListarYObtenerEnvio() throws Exception {
        Envio envio = new Envio(null, 5L, "Calle 1", EstadoEnvio.PREPARANDO, LocalDate.now(), null);
        Envio guardado = envioRepository.save(envio);

        mockMvc.perform(get("/api/v1/envios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].direccionEnvio").value("Calle 1"))
                .andExpect(jsonPath("$[0].estadoEnvio").value("PREPARANDO"));

        mockMvc.perform(get("/api/v1/envios/" + guardado.getIdEnvio()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEnvio").value(guardado.getIdEnvio()))
                .andExpect(jsonPath("$.idPedido").value(5L));
    }

    @Test
    void testActualizarEstado() throws Exception {
        Envio envio = new Envio(null, 5L, "Calle 1", EstadoEnvio.EN_CAMINO, LocalDate.now(), null);
        Envio guardado = envioRepository.save(envio);

        mockMvc.perform(put("/api/v1/envios/" + guardado.getIdEnvio() + "/estado")
                .param("estadoEnvio", "ENTREGADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoEnvio").value("ENTREGADO"))
                .andExpect(jsonPath("$.fechaEntrega").exists());
    }

    @Test
    void testEliminarEnvio() throws Exception {
        Envio envio = new Envio(null, 5L, "Calle 1", EstadoEnvio.PREPARANDO, LocalDate.now(), null);
        Envio guardado = envioRepository.save(envio);

        mockMvc.perform(delete("/api/v1/envios/" + guardado.getIdEnvio()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/envios/" + guardado.getIdEnvio()))
                .andExpect(status().isNotFound());
    }
}