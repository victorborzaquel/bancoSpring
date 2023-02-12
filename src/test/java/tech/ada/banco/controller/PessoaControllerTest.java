package tech.ada.banco.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PessoaControllerTest extends BasePessoaControllerTest {
    private final String baseUri = "/pessoas";

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    // TODO: Como fazer o toString de uma pessoa?
//    @Test
//    void testGetPessoas() throws Exception {
//        criarPessoa("João");
//
//        String response = mvc.perform(get(baseUri)).andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        assertEquals(1, repository.findAll().size());
//        String expected = "[{\"id\":100,\"dataNascimento\":\"2000-01-01\",\"cpf\":\"123.456.789.00\",\"telefone\":null,\"nome\":\"JoÃ£o\"}]";
//        assertEquals(expected, response);
//    }
//
//    @Test
//    void testGetPessoa() throws Exception {
//        Pessoa pessoa = criarPessoa("João");
//        assertEquals(1, repository.findAll().size());
//        String uri = baseUri + "/" + pessoa.getId();
//
//        String response = mvc.perform(get(uri)).andExpect(status().isOk())
//                .andReturn().getResponse().getContentAsString();
//
//        String expected = "{\"id\":100,\"dataNascimento\":\"2000-01-01\",\"cpf\":\"123.456.789.00\",\"telefone\":null,\"nome\":\"JoÃ£o\"}";
//        assertEquals(expected, response);
//    }

    @Test
    void testGetPessoaInexistente() throws Exception {
        criarPessoa("Victor");
        assertEquals(1, repository.findAll().size());
        String uri = baseUri + "/" + idPessoaInexistente;

        String response = mvc.perform(get(uri)).andExpect(status().isNotFound())
                .andReturn().getResponse().getErrorMessage();

        assertEquals("Recurso não encontrado.", response);
    }
        // TODO: Implementar testes de atualização e criação de pessoa.

//    @Test
//    void testAtualizaPessoa() throws Exception {
//        Pessoa pessoa = criarPessoa("João");
//        assertEquals(1, repository.findAll().size());
//        String uri = baseUri + "/" + pessoa.getId();
//
//        String response = mvc.perform(put(baseUri).contentType()).andExpect(status().isAccepted())
//                .andReturn().getResponse().getContentAsString();
//
//        assertEquals("Recurso não encontrado.", response);
//    }
//
//    @Test
//    void testAtualizaPessoaInexistente() throws Exception {
//        Pessoa pessoa = new Pessoa("João", "123.456.789.00", LocalDate.of(2000, 1, 1));
//        assertEquals(1, repository.findAll().size());
//        String uri = baseUri + "/" + pessoa.getId();
//
//        String response = mvc.perform(put(baseUri).contentType()).andExpect(status().isAccepted())
//                .andReturn().getResponse().getContentAsString();
//
//        assertEquals("Recurso não encontrado.", response);
//    }
//
//    @Test
//    void testCreatePessoa() throws Exception {
//
//    }
}