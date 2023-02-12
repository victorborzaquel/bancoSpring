package tech.ada.banco.services;

import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

    protected ContaRepository repository = Mockito.mock(ContaRepository.class);
    protected int numeroContaInexistente = 9999;
    protected int numeroContaOrigem = 1;
    protected int numeroContaDestino = 2;

    protected Conta criarConta(BigDecimal valor, int numeroConta) {
        Conta conta = new Conta(ModalidadeConta.CP, null);
        conta.deposito(valor);
        when(repository.findContaByNumeroConta(numeroConta)).thenReturn(Optional.of(conta));
        assertEquals(valor, conta.getSaldo(), "O saldo inicial da conta deve ser alterado para " + valor);
        return conta;
    }
}
