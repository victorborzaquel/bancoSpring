package tech.ada.banco.controller;

import org.junit.jupiter.api.Test;
import tech.ada.banco.model.Conta;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SaqueControllerTest extends BaseContaControllerTest {

    private final String baseUri = "/saque";

    @Test
    void testSaqueSaldoTotal() throws Exception {
        Conta conta = criarConta(BigDecimal.TEN);
        String uri = baseUri + "/" + conta.getNumeroConta();

        String response = mvc.perform(post(uri).param("valor", "10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("0.00", response);
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), obtemContaDoBanco(conta).getSaldo());
    }

    @Test
    void testSaqueCasaDecimal() throws Exception {
        Conta conta = criarConta(BigDecimal.TEN);
        String uri = baseUri + "/" + conta.getNumeroConta();

        String response = mvc.perform(post(uri).param("valor", "3.7"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("6.30", response);
        assertEquals(BigDecimal.valueOf(6.3).setScale(2, RoundingMode.HALF_UP), obtemContaDoBanco(conta).getSaldo());
    }

    @Test
    void testArredondamentoParaCima() throws Exception {
        Conta conta = criarConta(BigDecimal.valueOf(4));
        String uri = baseUri + "/" + conta.getNumeroConta();

        String response = mvc.perform(post(uri).param("valor", "3.005"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("0.99", response);
        assertEquals(BigDecimal.valueOf(0.99).setScale(2, RoundingMode.HALF_UP), obtemContaDoBanco(conta).getSaldo());
    }

    @Test
    void testArredondamentoParaBaixo() throws Exception {
        Conta conta = criarConta(BigDecimal.valueOf(4));
        String uri = baseUri + "/" + conta.getNumeroConta();

        String response = mvc.perform(post(uri).param("valor", "3.004"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("1.00", response);
        assertEquals(BigDecimal.valueOf(1).setScale(2, RoundingMode.HALF_UP), obtemContaDoBanco(conta).getSaldo());
    }

    @Test
    void testSaqueSaldoInsuficiente() throws Exception {
        Conta conta = criarConta(BigDecimal.ONE);
        String uri = baseUri + "/" + conta.getNumeroConta();

        String response = mvc.perform(post(uri).param("valor", "10"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getErrorMessage();

        assertEquals("Limite acima do saldo disponível!", response);
        assertEquals(BigDecimal.ONE, obtemContaDoBanco(conta).getSaldo());
    }

    @Test
    void testSaqueNegativo() throws Exception {
        Conta conta = criarConta(BigDecimal.ONE);
        String uri = baseUri + "/" + conta.getNumeroConta();

        String response = mvc.perform(post(uri).param("valor", "-10"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getErrorMessage();

        assertEquals("Valor informado está inválido.", response);
        assertEquals(BigDecimal.ONE, obtemContaDoBanco(conta).getSaldo());
    }

    @Test
    void testSaqueParcial() throws Exception {
        Conta conta = criarConta(BigDecimal.TEN);
        String uri = baseUri + "/" + conta.getNumeroConta();

        String response = mvc.perform(post(uri).param("valor", "3"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("7.00", response);
        assertEquals(BigDecimal.valueOf(7).setScale(2, RoundingMode.HALF_UP), obtemContaDoBanco(conta).getSaldo());
    }

    @Test
    void testSaqueContaInexistente() throws Exception {
        Conta conta = criarConta(BigDecimal.TEN);
        String uri = baseUri + "/" + numeroContaInexistente;

        Optional<Conta> contaInexistente = repository.findContaByNumeroConta(9999);
        assertTrue(contaInexistente.isEmpty());

        String response = mvc.perform(post(uri).param("valor", "3.7"))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getErrorMessage();

        assertEquals("Recurso não encontrado.", response);
        assertEquals(BigDecimal.valueOf(10), obtemContaDoBanco(conta).getSaldo());
    }
}