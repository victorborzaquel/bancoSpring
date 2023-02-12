package tech.ada.banco.services;

import org.junit.jupiter.api.Test;
import tech.ada.banco.exceptions.ResourceNotFoundException;
import tech.ada.banco.exceptions.ValorInvalidoException;
import tech.ada.banco.model.Conta;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DepositoTest extends BaseContaTest {
    private final Deposito deposito = new Deposito(repository);

    @Test
    void testProblemaDeBancoDeDados() {
        final BigDecimal VALOR_INICIAL = BigDecimal.valueOf(7);
        final BigDecimal VALOR_DEPOSITO = BigDecimal.valueOf(1);
        final BigDecimal VALOR_FINAL = BigDecimal.valueOf(7);

        Conta conta = criarConta(VALOR_INICIAL, numeroContaOrigem);

        when(repository.save(any())).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> deposito.executar(numeroContaDestino, VALOR_DEPOSITO), "O saque não pode ser realizado. Por problema de conexão de banco de dados");

        verify(repository, times(0)).save(any());
        assertEquals(VALOR_FINAL, conta.getSaldo(), "O saldo da conta não pode ter sido alterado.");
    }

    @Test
    void testContaNaoEncontrada() {
        final BigDecimal VALOR_INICIAL = BigDecimal.valueOf(7);
        final BigDecimal VALOR_DEPOSITO = BigDecimal.valueOf(1);
        final BigDecimal VALOR_FINAL = BigDecimal.valueOf(7);

        Conta conta = criarConta(VALOR_INICIAL, numeroContaOrigem);

        assertThrows(ResourceNotFoundException.class, () -> deposito.executar(numeroContaInexistente, VALOR_DEPOSITO), "O deposito não pode ser realizado. Por problema de conexão de banco de dados");

        verify(repository, times(0)).save(any());
        assertEquals(VALOR_FINAL, conta.getSaldo(), "O saldo da conta não pode ter sido alterado.");
    }

    @Test
    void testDepositoNegativo() {
        final BigDecimal VALOR_INICIAL = BigDecimal.valueOf(7);
        final BigDecimal VALOR_DEPOSITO = BigDecimal.valueOf(-1);
        final BigDecimal VALOR_FINAL = BigDecimal.valueOf(7);

        Conta destino = criarConta(VALOR_INICIAL, numeroContaDestino);

        assertThrows(ValorInvalidoException.class, () -> deposito.executar(numeroContaDestino, VALOR_DEPOSITO), "O deposito não pode ser realizado. Valor inválido.");

        verify(repository, times(0)).save(any());
        assertEquals(VALOR_FINAL, destino.getSaldo(), "O saldo da conta não pode ter sido alterado.");
    }

    @Test
    void testDepositoValorInteiro() {
        final BigDecimal VALOR_INICIAL = BigDecimal.valueOf(7);
        final BigDecimal VALOR_DEPOSITO = BigDecimal.valueOf(1);
        final BigDecimal VALOR_FINAL = BigDecimal.valueOf(8).setScale(2, RoundingMode.HALF_UP);

        Conta destino = criarConta(VALOR_INICIAL, numeroContaDestino);

        BigDecimal saldo = deposito.executar(numeroContaDestino, VALOR_DEPOSITO);

        verify(repository, times(1)).save(any());
        assertEquals(VALOR_FINAL, saldo);
        assertEquals(VALOR_FINAL, destino.getSaldo());
    }

    @Test
    void testDepositoCentavos() {
        final BigDecimal VALOR_INICIAL = BigDecimal.valueOf(7.85);
        final BigDecimal VALOR_DEPOSITO = BigDecimal.valueOf(0.15);
        final BigDecimal VALOR_FINAL = BigDecimal.valueOf(8).setScale(2, RoundingMode.HALF_UP);

        Conta destino = criarConta(VALOR_INICIAL, numeroContaDestino);

        BigDecimal saldo = deposito.executar(numeroContaDestino, VALOR_DEPOSITO);

        verify(repository, times(1)).save(any());
        assertEquals(VALOR_FINAL, saldo);
        assertEquals(VALOR_FINAL, destino.getSaldo());
    }

    @Test
    void testArredondamentoParaCima() {
        final BigDecimal VALOR_INICIAL = BigDecimal.valueOf(7);
        final BigDecimal VALOR_DEPOSITO = BigDecimal.valueOf(0.255);
        final BigDecimal VALOR_FINAL = BigDecimal.valueOf(7.26);

        Conta destino = criarConta(VALOR_INICIAL, numeroContaDestino);

        BigDecimal saldo = deposito.executar(numeroContaDestino, VALOR_DEPOSITO);

        verify(repository, times(1)).save(any());
        assertEquals(VALOR_FINAL, saldo);
        assertEquals(VALOR_FINAL, destino.getSaldo());
    }

    @Test
    void testArredondamentoParaBaixo() {
        final BigDecimal VALOR_INICIAL = BigDecimal.valueOf(7);
        final BigDecimal VALOR_DEPOSITO = BigDecimal.valueOf(0.254);
        final BigDecimal VALOR_FINAL = BigDecimal.valueOf(7.25);

        Conta destino = criarConta(VALOR_INICIAL, numeroContaDestino);

        BigDecimal saldo = deposito.executar(numeroContaDestino, VALOR_DEPOSITO);

        verify(repository, times(1)).save(any());
        assertEquals(VALOR_FINAL, saldo);
        assertEquals(VALOR_FINAL, destino.getSaldo());
    }
}