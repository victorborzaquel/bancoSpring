package tech.ada.banco.controller;

import org.junit.jupiter.api.Test;
import tech.ada.banco.model.Conta;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PixControllerTest extends BaseContaControllerTest {
    private final String baseUri = "/pix";

    @Test
    void testTransferirTodoSaldo() throws Exception {
        Conta conta = criarConta(BigDecimal.TEN);
        Conta destino = criarConta(BigDecimal.ONE);
        String uri = baseUri + "/" + conta.getNumeroConta();

        String response = mvc.perform(post(uri)
                        .param("destino", String.valueOf(destino.getNumeroConta()))
                        .param("valor", "10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("0", response);
        assertEquals(BigDecimal.ZERO, obtemContaDoBanco(conta).getSaldo());
        assertEquals(BigDecimal.valueOf(11), obtemContaDoBanco(destino).getSaldo());
    }

    @Test
    void testDestinoComSaldo() throws Exception {
        Conta conta = criarConta(BigDecimal.TEN);
        Conta destino = criarConta(BigDecimal.ONE);
        String uri = baseUri + "/" + conta.getNumeroConta();

        String response = mvc.perform(post(uri)
                        .param("destino", String.valueOf(destino.getNumeroConta()))
                        .param("valor", "3.7"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("6.3", response);
        assertEquals(BigDecimal.valueOf(6.3), obtemContaDoBanco(conta).getSaldo());
        assertEquals(BigDecimal.valueOf(4.7), obtemContaDoBanco(destino).getSaldo());
    }

    @Test
    void testDestinoSemSaldo() throws Exception {
        Conta conta = criarConta(BigDecimal.TEN);
        Conta destino = criarConta(BigDecimal.ZERO);
        String uri = baseUri + "/" + conta.getNumeroConta();

        String response = mvc.perform(post(uri)
                        .param("destino", String.valueOf(destino.getNumeroConta()))
                        .param("valor", "7"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("3", response);
        assertEquals(BigDecimal.valueOf(3), obtemContaDoBanco(conta).getSaldo());
        assertEquals(BigDecimal.valueOf(7), obtemContaDoBanco(destino).getSaldo());
    }

    @Test
    void testSaldoInsuficiente() throws Exception {
        Conta conta = criarConta(BigDecimal.TEN);
        Conta destino = criarConta(BigDecimal.ONE);
        String uri = baseUri + "/" + conta.getNumeroConta();

        String response = mvc.perform(post(uri)
                        .param("destino", String.valueOf(destino.getNumeroConta()))
                        .param("valor", "10.01"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getErrorMessage();

        assertEquals("Limite acima do saldo disponível!", response);
        assertEquals(BigDecimal.TEN, obtemContaDoBanco(conta).getSaldo());
        assertEquals(BigDecimal.ONE, obtemContaDoBanco(destino).getSaldo());
    }

    // TODO
    @Test
    void testPixNegativo() {
        Conta conta = criarConta(BigDecimal.TEN);
        Conta destino = criarConta(BigDecimal.ONE);
        String uri = baseUri + "/" + conta.getNumeroConta();

        try {
            mvc.perform(post(uri)
                            .param("destino", String.valueOf(destino.getNumeroConta()))
                            .param("valor", "-0.5"))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getErrorMessage();

            fail("Deveria ter lançado uma exceção de valor negativo.");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Operação não foi realizada pois o valor da transação é negativo."));
        }

        assertEquals(BigDecimal.TEN, obtemContaDoBanco(conta).getSaldo());
        assertEquals(BigDecimal.ONE, obtemContaDoBanco(destino).getSaldo());
    }

    // TODO: Não está arredondando o valor para duas casas decimais.
//    @Test
//    void testPixNegativo2() throws Exception {
//        Conta conta = criarConta(BigDecimal.TEN);
//        Conta destino = criarConta(BigDecimal.ONE);
//        String uri = baseUri + "/" + conta.getNumeroConta();
//
//        String response = mvc.perform(post(uri)
//                            .param("destino", String.valueOf(destino.getNumeroConta()))
//                            .param("valor", "-0.5"))
//                    .andExpect(status().isBadRequest())
//                    .andReturn().getResponse().getErrorMessage();
//
//        assertEquals("Operação não foi realizada pois o valor da transação é negativo.", response);
//        assertEquals(BigDecimal.TEN, obtemContaDoBanco(conta).getSaldo());
//        assertEquals(BigDecimal.ONE, obtemContaDoBanco(destino).getSaldo());
//    }

//    @Test
//    void testArredondamentoParaCima() throws Exception {
//        Conta conta = criarConta(BigDecimal.TEN);
//        Conta destino = criarConta(BigDecimal.ONE);
//        String uri = baseUri + "/" + conta.getNumeroConta();
//
//        String response = mvc.perform(post(uri)
//                        .param("destino", String.valueOf(destino.getNumeroConta()))
//                        .param("valor", "1.005"))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        assertEquals("8.99", response);
//        assertEquals(BigDecimal.valueOf(8.99), obtemContaDoBanco(conta).getSaldo());
//        assertEquals(BigDecimal.valueOf(2.01), obtemContaDoBanco(destino).getSaldo());
//    }
//
//    @Test
//    void testArredondamentoParaBaixo() throws Exception {
//        Conta conta = criarConta(BigDecimal.TEN);
//        Conta destino = criarConta(BigDecimal.ONE);
//        String uri = baseUri + "/" + conta.getNumeroConta();
//
//        String response = mvc.perform(post(uri)
//                        .param("destino", String.valueOf(destino.getNumeroConta()))
//                        .param("valor", "1.004"))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        assertEquals("9.00", response);
//        assertEquals(BigDecimal.valueOf(9).setScale(2, RoundingMode.HALF_UP), obtemContaDoBanco(conta).getSaldo());
//        assertEquals(BigDecimal.valueOf(2).setScale(2, RoundingMode.HALF_UP), obtemContaDoBanco(destino).getSaldo());
//    }

    @Test
    void testComCasaDecimal() throws Exception {
        Conta conta = criarConta(BigDecimal.TEN);
        Conta destino = criarConta(BigDecimal.ONE);
        String uri = baseUri + "/" + conta.getNumeroConta();

        String response = mvc.perform(post(uri)
                        .param("destino", String.valueOf(destino.getNumeroConta()))
                        .param("valor", "1.25"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("8.75", response);
        assertEquals(BigDecimal.valueOf(8.75).setScale(2, RoundingMode.HALF_UP), obtemContaDoBanco(conta).getSaldo());
        assertEquals(BigDecimal.valueOf(2.25).setScale(2, RoundingMode.HALF_UP), obtemContaDoBanco(destino).getSaldo());
    }

    @Test
    void testContaInexistente() throws Exception {
        Conta conta = criarConta(BigDecimal.TEN);
        Conta destino = criarConta(BigDecimal.ONE);
        String uri = baseUri + "/" + numeroContaInexistente;

        Optional<Conta> contaExiste = repository.findContaByNumeroConta(numeroContaInexistente);
        assertTrue(contaExiste.isEmpty());

        String response = mvc.perform(post(uri)
                        .param("destino", String.valueOf(destino.getNumeroConta()))
                        .param("valor", "5.2"))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getErrorMessage();

        assertEquals("Recurso não encontrado.", response);
        assertEquals(BigDecimal.TEN, obtemContaDoBanco(conta).getSaldo());
        assertEquals(BigDecimal.ONE, obtemContaDoBanco(destino).getSaldo());
    }

    @Test
    void testDestinoInexistente() throws Exception {
        Conta conta = criarConta(BigDecimal.TEN);
        Conta destino = criarConta(BigDecimal.ONE);
        String uri = baseUri + "/" + conta.getNumeroConta();

        Optional<Conta> contaExiste = repository.findContaByNumeroConta(numeroContaInexistente);
        assertTrue(contaExiste.isEmpty());

        String response = mvc.perform(post(uri)
                        .param("destino", String.valueOf(numeroContaInexistente))
                        .param("valor", "5.2"))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getErrorMessage();

        assertEquals("Recurso não encontrado.", response);
        assertEquals(BigDecimal.TEN, obtemContaDoBanco(conta).getSaldo());
        assertEquals(BigDecimal.ONE, obtemContaDoBanco(destino).getSaldo());
    }
}