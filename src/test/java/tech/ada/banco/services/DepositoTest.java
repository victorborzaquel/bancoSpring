package tech.ada.banco.services;

import org.junit.jupiter.api.Test;
import tech.ada.banco.exceptions.ResourceNotFoundException;
import tech.ada.banco.exceptions.ValorInvalidoException;
import tech.ada.banco.model.Conta;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static tech.ada.banco.utils.Format.format;

class DepositoTest extends BaseContaTest {
    private final Deposito deposito = new Deposito(repository);

    @Test
    void testProblemaDeBancoDeDados() {
        final BigDecimal VALOR_DEPOSITO = BigDecimal.valueOf(1);

        final Conta conta = criarConta(7, numeroContaOrigem);

        when(repository.save(any())).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> {
            deposito.executar(numeroContaDestino, VALOR_DEPOSITO);
        }, "O saque não pode ser realizado. Por problema de conexão de banco de dados");

        verify(repository, times(0)).save(any());
        assertEquals(format(7), conta.getSaldo(), "O saldo da conta não pode ter sido alterado.");
    }

    @Test
    void testContaNaoEncontrada() {
        final BigDecimal VALOR_DEPOSITO = BigDecimal.valueOf(1);

        final Conta conta = criarConta(7, numeroContaOrigem);

        assertThrows(ResourceNotFoundException.class, () -> {
            deposito.executar(numeroContaInexistente, VALOR_DEPOSITO);
        }, "O deposito não pode ser realizado. Por problema de conexão de banco de dados");

        verify(repository, times(0)).save(any());
        assertEquals(format(7), conta.getSaldo(), "O saldo da conta não pode ter sido alterado.");
    }

    @Test
    void testDepositoNegativo() {
        final BigDecimal VALOR_DEPOSITO = BigDecimal.valueOf(-1);

        final Conta destino = criarConta(7, numeroContaDestino);

        assertThrows(ValorInvalidoException.class, () -> {
            deposito.executar(numeroContaDestino, VALOR_DEPOSITO);
        }, "O deposito não pode ser realizado. Valor inválido.");

        verify(repository, times(0)).save(any());
        assertEquals(format(7), destino.getSaldo(), "O saldo da conta não pode ter sido alterado.");
    }

    @Test
    void testDepositoValorInteiro() {
        final BigDecimal VALOR_DEPOSITO = BigDecimal.valueOf(1);

        final Conta destino = criarConta(7, numeroContaDestino);

        final BigDecimal saldo = deposito.executar(numeroContaDestino, VALOR_DEPOSITO);

        verify(repository, times(1)).save(any());
        assertEquals(format(8), saldo);
        assertEquals(format(8), destino.getSaldo());
    }

    @Test
    void testDepositoCentavos() {
        final BigDecimal VALOR_DEPOSITO = BigDecimal.valueOf(0.15);

        final Conta destino = criarConta(7.85, numeroContaDestino);

        final BigDecimal saldo = deposito.executar(numeroContaDestino, VALOR_DEPOSITO);

        verify(repository, times(1)).save(any());
        assertEquals(format(8), saldo);
        assertEquals(format(8), destino.getSaldo());
    }

    @Test
    void testArredondamentoParaCima() {
        final BigDecimal VALOR_DEPOSITO = BigDecimal.valueOf(0.255);

        final Conta destino = criarConta(7, numeroContaDestino);

        final BigDecimal saldo = deposito.executar(numeroContaDestino, VALOR_DEPOSITO);

        verify(repository, times(1)).save(any());
        assertEquals(format(7.26), saldo);
        assertEquals(format(7.26), destino.getSaldo());
    }

    @Test
    void testArredondamentoParaBaixo() {
        final BigDecimal VALOR_DEPOSITO = BigDecimal.valueOf(0.254);

        final Conta destino = criarConta(7, numeroContaDestino);

        final BigDecimal saldo = deposito.executar(numeroContaDestino, VALOR_DEPOSITO);

        verify(repository, times(1)).save(any());
        assertEquals(format(7.25), saldo);
        assertEquals(format(7.25), destino.getSaldo());
    }
}