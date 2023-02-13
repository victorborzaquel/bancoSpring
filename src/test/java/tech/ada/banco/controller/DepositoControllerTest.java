package tech.ada.banco.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import tech.ada.banco.model.Conta;
import tech.ada.banco.utils.Uri;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static tech.ada.banco.utils.Format.format;

@SpringBootTest
@AutoConfigureMockMvc
class DepositoControllerTest extends BaseContaControllerTest {
    @Autowired
    private MockMvc mvc;
    private final Uri uri = new Uri("/deposito");

    @Test
    void testDepositoNegativo() throws Exception {
        final Conta conta = criarConta(0);

        final String response = mvc.perform(post(uri.criar(conta))
                        .param("valor", "-10"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getErrorMessage();

        assertEquals("Valor informado está inválido.", response);
        assertEquals(format(0), obtemContaDoBanco(conta).getSaldo());
    }

    @Test
    void testDepositoCasaDecimal() throws Exception {
        final Conta conta = criarConta(0);
        final String value = "3.79";

        final String response = mvc.perform(post(uri.criar(conta)).param("valor", value))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(value, response);
        assertEquals(format(value), obtemContaDoBanco(conta).getSaldo());
    }

    @Test
    void testArredondamentoParaCima() throws Exception {
        final Conta conta = criarConta(0);

        final String response = mvc.perform(post(uri.criar(conta))
                        .param("valor", "3.715"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("3.72", response);
        assertEquals(format(3.72), obtemContaDoBanco(conta).getSaldo());
    }

    @Test
    void testArredondamentoParaBaixo() throws Exception {
        final Conta conta = criarConta(0);

        final String response = mvc.perform(post(uri.criar(conta))
                        .param("valor", "3.714"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("3.71", response);
        assertEquals(format(3.71), obtemContaDoBanco(conta).getSaldo());
    }

    @Test
    void testContaSemSaldo() throws Exception {
        final Conta conta = criarConta(0);

        final String response = mvc.perform(post(uri.criar(conta))
                        .param("valor", "10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("10.00", response);
        assertEquals(format(10), obtemContaDoBanco(conta).getSaldo());
    }

    @Test
    void testContaComSaldo() throws Exception {
        final Conta conta = criarConta(7);

        final String response = mvc.perform(post(uri.criar(conta))
                        .param("valor", "10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("17.00", response);
        assertEquals(format(17), obtemContaDoBanco(conta).getSaldo());
    }

    @Test
    void testDepositoContaInexistente() throws Exception {
        final Conta conta = criarConta(0);

        assertNumeroContaInexistente();

        final String response = mvc.perform(post(uri.criar(numeroContaInexistente))
                        .param("valor", "10"))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getErrorMessage();

        assertEquals("Recurso não encontrado.", response);
        assertEquals(format(0), obtemContaDoBanco(conta).getSaldo());
    }
}