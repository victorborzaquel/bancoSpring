package tech.ada.banco.controller;

import org.junit.jupiter.api.Test;
import tech.ada.banco.model.Conta;
import tech.ada.banco.model.Pessoa;
import tech.ada.banco.utils.Uri;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ContaControllerTest extends BaseContaControllerTest {

    private final Uri uri = new Uri("/contas");

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
        assertNumeroContaInexistente();

        final String response = mvc.perform(get(uri.criar(numeroContaInexistente)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getErrorMessage();

        assertEquals("Recurso não encontrado.", response);
    }

    @Test
    void test() {
        Pessoa pessoa = new Pessoa("Victor", "12345", LocalDate.of(2000, 1, 1));

//        String response = mvc.perform(post(uri));
    }

    //     TODO: Como transferir Json para o controller?
//    @Test
//    void testCreateConta() throws Exception {
//        Pessoa pessoa = new Pessoa("Victor", "12345", LocalDate.of(2000, 1, 1));
//
//        String response = mvc.perform(post(uri)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(pessoa.toString()))
//                .andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        System.out.println(response);
//        assertEquals(1, repository.findAll().size());
//    }

    // TODO: Como verificar se o objeto foi criado no banco?
//    @Test
//    void testCreateContaSemPessoa() throws Exception {
//        mvc.perform(post(uri.base()).param("modalidade", ModalidadeConta.CC.toString()))
//                .andExpect(status().isOk());
//
//        assertEquals(1, repository.findAll().size());
//    }

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