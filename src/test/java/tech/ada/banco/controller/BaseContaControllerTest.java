package tech.ada.banco.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import tech.ada.banco.model.Conta;
import tech.ada.banco.model.ModalidadeConta;
import tech.ada.banco.repository.ContaRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class BaseContaControllerTest {

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ContaRepository repository;

    protected int numeroContaInexistente = 9999;

    protected Conta criarConta(BigDecimal saldo) {
        Conta conta = repository.save(new Conta(ModalidadeConta.CC, null));
        conta.deposito(saldo);
        conta = repository.save(conta);
        assertEquals(saldo.setScale(2, RoundingMode.HALF_UP), conta.getSaldo());
        return conta;
    }

    protected Conta criarConta(int saldo) {
        return criarConta(BigDecimal.valueOf(saldo));
    }

    protected Conta criarConta(double saldo) {
        return criarConta(BigDecimal.valueOf(saldo));
    }

    protected Conta obtemContaDoBanco(Conta conta) {
        return repository.findContaByNumeroConta(conta.getNumeroConta())
                .orElseThrow(NullPointerException::new);
    }

    protected void assertNumeroContaInexistente() {
        assertTrue(repository.findContaByNumeroConta(numeroContaInexistente).isEmpty());
    }
}
