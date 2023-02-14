package tech.ada.banco.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import tech.ada.banco.model.Pessoa;
import tech.ada.banco.utils.Uri;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PessoaControllerTest extends BasePessoaControllerTest {
    private final Uri uri = new Uri("/pessoas");
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void testGetPessoas() throws Exception {
        final Pessoa pessoa1 = criarPessoa("Victor");
        final Pessoa pessoa2 = criarPessoa("Hugo");

        final String response = mvc.perform(get(uri.base())).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final List<Pessoa> pessoaResposta = List.of(mapper.readValue(response, Pessoa[].class));

        assertTrue(pessoaResposta.contains(pessoa1));
        assertTrue(pessoaResposta.contains(pessoa2));
        assertEquals(pessoa1, obtemPessoaDoBanco(pessoa1));
        assertEquals(pessoa2, obtemPessoaDoBanco(pessoa2));
    }

    @Test
    void testGetPessoa() throws Exception {
        final Pessoa pessoa = criarPessoa("Victor");

        final String response = mvc.perform(get(uri.criar(pessoa.getId())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final Pessoa pessoaResposta = mapper.readValue(response, Pessoa.class);

        assertEquals(pessoa, pessoaResposta);
        assertEquals(pessoa, obtemPessoaDoBanco(pessoa));
    }


    @Test
    void testCreatePessoa() throws Exception {
        final Pessoa pessoa = new Pessoa("Victor", "12345", LocalDate.of(2000, 1, 1));

        final String response = mvc.perform(post(uri.base())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(pessoa)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final Pessoa pessoaResposta = mapper.readValue(response, Pessoa.class);

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

    @Test
    void testAtualizaPessoa() throws Exception {
        final Pessoa pessoa = criarPessoa("Victor");

        Pessoa pessoa1 = new Pessoa("Hugo", pessoa.getCpf(), pessoa.getDataNascimento());
        pessoa1.setId(pessoa.getId());

        final String response = mvc.perform(put(uri.base())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(pessoa1)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final Pessoa pessoaResposta = mapper.readValue(response, Pessoa.class);

        assertEquals(pessoa, pessoaResposta);
    }

    @Test
    void testAtualizaPessoaInexistente() throws Exception {
        Pessoa pessoa = criarPessoa("Victor");

        Pessoa pessoa1 = new Pessoa("Hugo", pessoa.getCpf(), pessoa.getDataNascimento());

        assertIdPessoaInexistente();

        final String response = mvc.perform(put(uri.base())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(pessoa1)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals("Recurso não encontrado.", response);
    }

}