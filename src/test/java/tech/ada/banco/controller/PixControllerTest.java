package tech.ada.banco.controller;

import org.junit.jupiter.api.Test;
import tech.ada.banco.model.Conta;
import tech.ada.banco.utils.Uri;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static tech.ada.banco.utils.Format.format;

class PixControllerTest extends BaseContaControllerTest {
    private final Uri uri = new Uri("/pix");

    @Test
    void testTransferirTodoSaldo() throws Exception {
        final Conta conta = criarConta(10);
        final Conta destino = criarConta(0);

        final String response = mvc.perform(post(uri.criar(conta))
                        .param("destino", String.valueOf(destino.getNumeroConta()))
                        .param("valor", "10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("0.00", response);
        assertEquals(format(0), obtemContaDoBanco(conta).getSaldo());
        assertEquals(format(10), obtemContaDoBanco(destino).getSaldo());
    }

    @Test
    void testDestinoComSaldo() throws Exception {
        final Conta conta = criarConta(10);
        final Conta destino = criarConta(1);

        final String response = mvc.perform(post(uri.criar(conta))
                        .param("destino", String.valueOf(destino.getNumeroConta()))
                        .param("valor", "3.7"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("6.30", response);
        assertEquals(format(6.3), obtemContaDoBanco(conta).getSaldo());
        assertEquals(format(4.7), obtemContaDoBanco(destino).getSaldo());
    }

    @Test
    void testDestinoSemSaldo() throws Exception {
        final Conta conta = criarConta(10);
        final Conta destino = criarConta(0);

        final String response = mvc.perform(post(uri.criar(conta))
                        .param("destino", String.valueOf(destino.getNumeroConta()))
                        .param("valor", "7"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("3.00", response);
        assertEquals(format(3), obtemContaDoBanco(conta).getSaldo());
        assertEquals(format(7), obtemContaDoBanco(destino).getSaldo());
    }

    @Test
    void testSaldoInsuficiente() throws Exception {
        final Conta conta = criarConta(10);
        final Conta destino = criarConta(1);

        final String response = mvc.perform(post(uri.criar(conta))
                        .param("destino", String.valueOf(destino.getNumeroConta()))
                        .param("valor", "10.01"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getErrorMessage();

        assertEquals("Limite acima do saldo disponível!", response);
        assertEquals(format(10), obtemContaDoBanco(conta).getSaldo());
        assertEquals(format(1), obtemContaDoBanco(destino).getSaldo());
    }

    @Test
    void testPixNegativo() throws Exception {
        final Conta conta = criarConta(10);
        final Conta destino = criarConta(1);

        final String response = mvc.perform(post(uri.criar(conta))
                            .param("destino", String.valueOf(destino.getNumeroConta()))
                            .param("valor", "-0.5"))
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getErrorMessage();

        assertEquals("Valor informado está inválido.", response);
        assertEquals(format(10), obtemContaDoBanco(conta).getSaldo());
        assertEquals(format(1), obtemContaDoBanco(destino).getSaldo());
    }

    @Test
    void testArredondamentoParaCima() throws Exception {
        final Conta conta = criarConta(10);
        final Conta destino = criarConta(1);

        final String response = mvc.perform(post(uri.criar(conta))
                        .param("destino", String.valueOf(destino.getNumeroConta()))
                        .param("valor", "1.005"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("8.99", response);
        assertEquals(format(8.99), obtemContaDoBanco(conta).getSaldo());
        assertEquals(format(2.01), obtemContaDoBanco(destino).getSaldo());
    }

    @Test
    void testArredondamentoParaBaixo() throws Exception {
        final Conta conta = criarConta(10);
        final Conta destino = criarConta(1);

        final String response = mvc.perform(post(uri.criar(conta))
                        .param("destino", String.valueOf(destino.getNumeroConta()))
                        .param("valor", "1.004"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("9.00", response);
        assertEquals(format(9), obtemContaDoBanco(conta).getSaldo());
        assertEquals(format(2), obtemContaDoBanco(destino).getSaldo());
    }

    @Test
    void testComCasaDecimal() throws Exception {
        final Conta conta = criarConta(10);
        final Conta destino = criarConta(1);

        final String response = mvc.perform(post(uri.criar(conta))
                        .param("destino", String.valueOf(destino.getNumeroConta()))
                        .param("valor", "1.25"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("8.75", response);
        assertEquals(format(8.75), obtemContaDoBanco(conta).getSaldo());
        assertEquals(format(2.25), obtemContaDoBanco(destino).getSaldo());
    }

    @Test
    void testContaInexistente() throws Exception {
        final Conta conta = criarConta(10);
        final Conta destino = criarConta(1);

        assertNumeroContaInexistente();

        final String response = mvc.perform(post(uri.criar(numeroContaInexistente))
                        .param("destino", String.valueOf(destino.getNumeroConta()))
                        .param("valor", "5.2"))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getErrorMessage();

        assertEquals("Recurso não encontrado.", response);
        assertEquals(format(10), obtemContaDoBanco(conta).getSaldo());
        assertEquals(format(1), obtemContaDoBanco(destino).getSaldo());
    }

    @Test
    void testDestinoInexistente() throws Exception {
        final Conta conta = criarConta(10);
        final Conta destino = criarConta(1);

        assertNumeroContaInexistente();

        final String response = mvc.perform(post(uri.criar(conta))
                        .param("destino", String.valueOf(numeroContaInexistente))
                        .param("valor", "5.2"))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getErrorMessage();

        assertEquals("Recurso não encontrado.", response);
        assertEquals(format(10), obtemContaDoBanco(conta).getSaldo());
        assertEquals(format(1), obtemContaDoBanco(destino).getSaldo());
    }
}