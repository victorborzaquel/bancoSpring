package tech.ada.banco.controller;

import org.junit.jupiter.api.Test;
import tech.ada.banco.model.Conta;
import tech.ada.banco.utils.Uri;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static tech.ada.banco.utils.Format.format;

class SaqueControllerTest extends BaseContaControllerTest {

    private final Uri uri = new Uri("/saque");

    @Test
    void testSaqueSaldoTotal() throws Exception {
        final Conta conta = criarConta(10);

        final String response = mvc.perform(post(uri.criar(conta))
                        .param("valor", "10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("0.00", response);
        assertEquals(format(0), obtemContaDoBanco(conta).getSaldo());
    }

    @Test
    void testSaqueCasaDecimal() throws Exception {
        final Conta conta = criarConta(10);

        final String response = mvc.perform(post(uri.criar(conta))
                        .param("valor", "3.7"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("6.30", response);
        assertEquals(format(6.3), obtemContaDoBanco(conta).getSaldo());
    }

    @Test
    void testArredondamentoParaCima() throws Exception {
        final Conta conta = criarConta(4);

        final String response = mvc.perform(post(uri.criar(conta))
                        .param("valor", "3.005"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("0.99", response);
        assertEquals(format(0.99), obtemContaDoBanco(conta).getSaldo());
    }

    @Test
    void testArredondamentoParaBaixo() throws Exception {
        final Conta conta = criarConta(4);

        final String response = mvc.perform(post(uri.criar(conta))
                        .param("valor", "3.004"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("1.00", response);
        assertEquals(format(1), obtemContaDoBanco(conta).getSaldo());
    }

    @Test
    void testSaqueSaldoInsuficiente() throws Exception {
        final Conta conta = criarConta(1);

        final String response = mvc.perform(post(uri.criar(conta))
                        .param("valor", "10"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getErrorMessage();

        assertEquals("Limite acima do saldo disponível!", response);
        assertEquals(format(1), obtemContaDoBanco(conta).getSaldo());
    }

    @Test
    void testSaqueNegativo() throws Exception {
        final Conta conta = criarConta(1);

        final String response = mvc.perform(post(uri.criar(conta))
                        .param("valor", "-10"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getErrorMessage();

        assertEquals("Valor informado está inválido.", response);
        assertEquals(format(1), obtemContaDoBanco(conta).getSaldo());
    }

    @Test
    void testSaqueParcial() throws Exception {
        final Conta conta = criarConta(10);

        final String response = mvc.perform(post(uri.criar(conta))
                        .param("valor", "3"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("7.00", response);
        assertEquals(format(7), obtemContaDoBanco(conta).getSaldo());
    }

    @Test
    void testSaqueContaInexistente() throws Exception {
        final Conta conta = criarConta(10);

        assertNumeroContaInexistente();

        final String response = mvc.perform(post(uri.criar(numeroContaInexistente))
                        .param("valor", "3.7"))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getErrorMessage();

        assertEquals("Recurso não encontrado.", response);
        assertEquals(format(10), obtemContaDoBanco(conta).getSaldo());
    }
}