package tech.ada.banco.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import tech.ada.banco.model.Conta;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DepositoControllerTest extends BaseContaControllerTest {
    @Autowired
    private MockMvc mvc;
    private final String baseUri = "/deposito";

    @Test
    void testDepositoPositivo() throws Exception {
        Conta conta = criarConta(BigDecimal.ZERO);
        String uri = baseUri + "/" + conta.getNumeroConta();

        String response =
                mvc.perform(post(uri).param("valor", "10"))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();

        assertEquals("10.00", response);
        assertEquals(BigDecimal.TEN.setScale(2, RoundingMode.HALF_UP), obtemContaDoBanco(conta).getSaldo());
    }

    @Test
    void testDepositoNegativo() throws Exception {
        Conta conta = criarConta(BigDecimal.ZERO);
        String uri = baseUri + "/" + conta.getNumeroConta();

        String response =
                mvc.perform(post(uri).param("valor", "-10"))
                        .andExpect(status().isBadRequest())
                        .andReturn().getResponse().getErrorMessage();

        assertEquals("Valor informado está inválido.", response);
        assertEquals(BigDecimal.ZERO, conta.getSaldo());
    }

    @Test
    void testDepositoCasaDecimal() throws Exception {
        Conta conta = criarConta(BigDecimal.ZERO);
        String uri = baseUri + "/" + conta.getNumeroConta();
        String value = "3.79";

        String response =
                mvc.perform(post(uri).param("valor", value))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();

        assertEquals(value, response);
        assertEquals(new BigDecimal(value).setScale(2, RoundingMode.HALF_UP), obtemContaDoBanco(conta).getSaldo());

    }

    @Test
    void testArredondamentoParaCima() throws Exception {
        Conta conta = criarConta(BigDecimal.ZERO);
        String uri = baseUri + "/" + conta.getNumeroConta();

        String response =
                mvc.perform(post(uri).param("valor", "3.715"))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();

        assertEquals("3.72", response);
        assertEquals(BigDecimal.valueOf(3.72).setScale(2, RoundingMode.HALF_UP), obtemContaDoBanco(conta).getSaldo());
    }

    @Test
    void testArredondamentoParaBaixo() throws Exception {
        Conta conta = criarConta(BigDecimal.ZERO);
        String uri = baseUri + "/" + conta.getNumeroConta();

        String response =
                mvc.perform(post(uri).param("valor", "3.714"))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();

        assertEquals("3.71", response);
        assertEquals(BigDecimal.valueOf(3.71), obtemContaDoBanco(conta).getSaldo());
    }

    @Test
    void testContaSemSaldo() throws Exception {
        Conta conta = criarConta(BigDecimal.ZERO);
        String uri = baseUri + "/" + conta.getNumeroConta();

        String response =
                mvc.perform(post(uri).param("valor", "10"))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();

        assertEquals("10.00", response);
        assertEquals(BigDecimal.TEN.setScale(2, RoundingMode.HALF_UP), obtemContaDoBanco(conta).getSaldo());
    }

    @Test
    void testContaComSaldo() throws Exception {
        Conta conta = criarConta(BigDecimal.valueOf(7));
        String uri = baseUri + "/" + conta.getNumeroConta();

        String response =
                mvc.perform(post(uri).param("valor", "10"))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();

        assertEquals("17.00", response);
        assertEquals(BigDecimal.valueOf(17).setScale(2, RoundingMode.HALF_UP), obtemContaDoBanco(conta).getSaldo());
    }

    @Test
    void testDepositoContaInexistente() throws Exception {
        Conta conta = criarConta(BigDecimal.ZERO);
        String uri = baseUri + "/" + numeroContaInexistente;

        Optional<Conta> contaExiste = repository.findContaByNumeroConta(numeroContaInexistente);
        assertTrue(contaExiste.isEmpty());

        String response =
                mvc.perform(post(uri).param("valor", "10"))
                        .andExpect(status().isNotFound())
                        .andReturn().getResponse().getErrorMessage();

        assertEquals("Recurso não encontrado.", response);
        assertEquals(BigDecimal.ZERO, conta.getSaldo());
    }
}