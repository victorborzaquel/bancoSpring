package tech.ada.banco.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import tech.ada.banco.model.Pessoa;
import tech.ada.banco.repository.PessoaRepository;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class BasePessoaControllerTest {

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected PessoaRepository repository;

    protected int idPessoaInexistente = 9999;

    protected Pessoa criarPessoa(String nome) {
        return repository.save(new Pessoa(nome, "123.456.789.00",  LocalDate.of(2000, 1, 1)));
    }
}
