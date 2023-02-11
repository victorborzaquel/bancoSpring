package tech.ada.banco.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import tech.ada.banco.model.Conta;
import tech.ada.banco.model.ModalidadeConta;
import tech.ada.banco.repository.ContaRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class BaseContaTest {

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ContaRepository repository;

    protected int numeroContaInexistente = 9999;

    protected Conta criarConta(double valor, int numeroConta) {
        Conta conta = new Conta(ModalidadeConta.CC, null);
        conta.deposito(BigDecimal.valueOf(valor));
        when(repository.findContaByNumeroConta(numeroConta)).thenReturn(Optional.of(conta));
        assertEquals(BigDecimal.valueOf(valor), conta.getSaldo(),
                "O saldo inicial da conta deve ser alterado para " + valor);
        return conta;
    }

    protected Conta obtemContaDoBanco(Conta contaBase) {
        return repository.findContaByNumeroConta(contaBase.getNumeroConta())
                .orElseThrow(NullPointerException::new);
    }

}
