package tech.ada.banco.services;

import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import tech.ada.banco.model.Conta;
import tech.ada.banco.model.ModalidadeConta;
import tech.ada.banco.repository.ContaRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        assertEquals(valor.setScale(2, RoundingMode.HALF_UP), conta.getSaldo(), "O saldo inicial da conta deve ser alterado para " + valor);
        return conta;
    }

    protected Conta criarConta(double valor, int numeroConta) {
        return criarConta(BigDecimal.valueOf(valor), numeroConta);
    }

    protected Conta criarConta(int valor, int numeroConta) {
        return criarConta(BigDecimal.valueOf(valor), numeroConta);
    }

    protected Conta criarContaOrigem(double valor) {
        return criarConta(BigDecimal.valueOf(valor), numeroContaOrigem);
    }

    protected Conta criarContaOrigem(int valor) {
        return criarConta(BigDecimal.valueOf(valor), numeroContaOrigem);
    }

    protected Conta criarContaDestino(double valor) {
        return criarConta(BigDecimal.valueOf(valor), numeroContaDestino);
    }

    protected Conta criarContaDestino(int valor) {
        return criarConta(BigDecimal.valueOf(valor), numeroContaDestino);
    }
}
