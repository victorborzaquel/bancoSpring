package tech.ada.banco.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import tech.ada.banco.model.Pessoa;
import tech.ada.banco.utils.Uri;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PessoaControllerTest extends BasePessoaControllerTest {
    private final Uri uri = new Uri("/pessoas");
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

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
    void test() throws Exception {
        final Pessoa pessoa = new Pessoa("Victor", "12345", LocalDate.of(2000, 1, 1));

        final String response = mvc.perform(post(uri.base())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(pessoa)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final Pessoa pessoaResposta = mapper.readValue(response, Pessoa.class);
        System.out.println(response);
    }

    @Test
    void testGetPessoaInexistente() throws Exception {
        criarPessoa("Victor");

        assertIdPessoaInexistente();

        final String response = mvc.perform(get(uri.criar(idPessoaInexistente)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getErrorMessage();

        assertEquals("Recurso não encontrado.", response);
    }
        // TODO: Como transferir Json para o controller.

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