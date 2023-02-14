package tech.ada.banco.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import tech.ada.banco.model.Conta;
import tech.ada.banco.model.ModalidadeConta;
import tech.ada.banco.utils.Uri;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ContaControllerTest extends BaseContaControllerTest {

    private final Uri uri = new Uri("/contas");
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());


    @Test
    void testGetContaExistente() throws Exception {
        final Conta conta = criarConta(BigDecimal.TEN);

        final String response = mvc.perform(get(uri.base()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final Conta contaResposta = mapper.readValue(response, Conta.class);

        assertEquals(conta, contaResposta);
    }

    @Test
    void testGetContas() throws Exception {
        final Conta conta1 = criarConta(BigDecimal.TEN);
        final Conta conta2 = criarConta(BigDecimal.ONE);

        final String response = mvc.perform(get(uri.base()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final List<Conta> contasResposta = List.of(mapper.readValue(response, Conta[].class));

        assertTrue(contasResposta.contains(conta1));
        assertTrue(contasResposta.contains(conta2));
        assertEquals(conta1, obtemContaDoBanco(conta1));
        assertEquals(conta2, obtemContaDoBanco(conta2));
    }

    @Test
    void testGetContaInexistente() throws Exception {
        assertNumeroContaInexistente();

        final String response = mvc.perform(get(uri.criar(numeroContaInexistente)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getErrorMessage();

        assertEquals("Recurso não encontrado.", response);
    }

    @Test
    void testCreateContaSemPessoa() throws Exception {
        String response = mvc.perform(post(uri.base())
                        .param("modalidade", ModalidadeConta.CC.toString()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final Conta conta = mapper.readValue(response, Conta.class);

        assertEquals(ModalidadeConta.CC, conta.getTipo());
    }

    @Test
    void testDeleteConta() throws Exception {
        final Conta conta = criarConta(10);

        mvc.perform(delete(uri.criar(conta))).andExpect(status().isAccepted());

        assertThrows(NullPointerException.class, () -> obtemContaDoBanco(conta));
    }

    @Test
    void testDeleteContaInexistente() throws Exception {
        criarConta(10);

        assertNumeroContaInexistente();

        final String response = mvc.perform(delete(uri.criar(numeroContaInexistente)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getErrorMessage();

        assertEquals("Recurso não encontrado.", response);
    }
}