package tech.ada.banco.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import tech.ada.banco.model.Conta;
import tech.ada.banco.model.ModalidadeConta;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ContaControllerTest extends BaseContaControllerTest {

    private final String baseUri = "/contas";

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    // TODO: como fazer o toString de uma conta?
//    @Test
//    void testGetContaExistente() throws Exception {
//        Conta conta = criarConta(BigDecimal.TEN);
//        String uri = baseUri + "/" + conta.getNumeroConta();
//
//        String response = mvc.perform(get(uri))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        assertEquals(conta.toString(), response);
//    }
//    @Test
//    void testGetContas() throws Exception {
//        criarConta(BigDecimal.TEN);
//        criarConta(BigDecimal.ONE);
//
//        String response = mvc.perform(get(baseUri))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        String expected = "[{\"numeroConta\":10000,\"tipo\":\"CC\",\"saldo\":10,\"agencia\":\"0001\",\"titular\":null},{\"numeroConta\":10001,\"tipo\":\"CC\",\"saldo\":1,\"agencia\":\"0001\",\"titular\":null}]";
//        assertEquals(expected, response);
//    }

    @Test
    void testGetContaInexistente() throws Exception {
        String uri = baseUri + "/" + numeroContaInexistente;

        String response = mvc.perform(get(uri))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getErrorMessage();

        assertEquals("Recurso não encontrado.", response);
    }


    // TODO: Como transferir Json para o controller?
//    @Test
//    void testCreateConta() throws Exception {
//        Pessoa pessoa = new Pessoa("Victor", "12345", LocalDate.of(2000, 1, 1));
//
//        String response = mvc.perform(get(baseUri)
//                        .param("modalidade", ModalidadeConta.CC.toString())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(pessoa.toString()))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        assertEquals(1, repository.findAll().size());
//    }

    @Test
    void testCreateContaSemPessoa() throws Exception {
        mvc.perform(post(baseUri).param("modalidade", ModalidadeConta.CC.toString()))
                .andExpect(status().isOk());

        assertEquals(1, repository.findAll().size());
    }

    @Test
    void testDeleteConta() throws Exception {
        Conta conta = criarConta(BigDecimal.TEN);
        String uri = baseUri + "/" + conta.getNumeroConta();

        mvc.perform(delete(uri)).andExpect(status().isAccepted());

        assertThrows(NullPointerException.class, () -> obtemContaDoBanco(conta));
    }

    // TODO: Erro ao deletar conta inexistente
//    @Test
//    void testDeleteContaInexistente() throws Exception {
//        Conta conta = criarConta(BigDecimal.TEN);
//        String uri = baseUri + "/" + numeroContaInexistente;
//
//        String response = mvc.perform(delete(uri)).andExpect(status().isNotFound())
//                .andReturn().getResponse().getContentAsString();
//
//        assertEquals("Recurso não encontrado.", response);
//        assertEquals(conta, obtemContaDoBanco(conta));
//    }
}