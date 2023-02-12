package tech.ada.banco.services;

import org.junit.jupiter.api.Test;
import tech.ada.banco.exceptions.ResourceNotFoundException;
import tech.ada.banco.exceptions.SaldoInsuficienteException;
import tech.ada.banco.model.Conta;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SaqueTest extends BaseContaTest {
    private final Saque saque = new Saque(repository);

    @Test
    void testSaqueParcial() {
        final BigDecimal VALOR_INICIAL = BigDecimal.valueOf(10);
        final BigDecimal VALOR_SAQUE = BigDecimal.valueOf(1);
        final BigDecimal VALOR_FINAL = BigDecimal.valueOf(9).setScale(2, RoundingMode.HALF_UP);

        final Conta conta = criarConta(VALOR_INICIAL, numeroContaOrigem);

        BigDecimal saldo = saque.executar(numeroContaOrigem, VALOR_SAQUE);

        verify(repository, times(1)).save(conta);
        assertEquals(VALOR_FINAL, saldo, "O valor de retorno da função tem que ser 9. Saldo anterior vale 10 e o valor de saque é 1");
        assertEquals(BigDecimal.valueOf(9).setScale(2, RoundingMode.HALF_UP), conta.getSaldo());
    }

    @Test
    void testSaqueContaNaoEncontrada() {
        final BigDecimal VALOR_INICIAL = BigDecimal.valueOf(10);
        final BigDecimal VALOR_SAQUE = BigDecimal.valueOf(1);
        final BigDecimal VALOR_FINAL = BigDecimal.valueOf(10);

        final Conta conta = criarConta(VALOR_INICIAL, numeroContaOrigem);

        verify(repository, times(0)).save(any());
        assertThrows(ResourceNotFoundException.class, () -> saque.executar(numeroContaInexistente, VALOR_SAQUE), "A conta deveria não ter sido encontrada.");
        assertEquals(VALOR_FINAL, conta.getSaldo(), "O saldo da conta não pode ter sido alterado.");
    }

    @Test
    void testSaqueProblemaDeBancoDeDados() {
        final BigDecimal VALOR_INICIAL = BigDecimal.valueOf(10);
        final BigDecimal VALOR_SAQUE = BigDecimal.valueOf(1);
        final BigDecimal VALOR_FINAL = BigDecimal.valueOf(10);

        final Conta conta = criarConta(VALOR_INICIAL, numeroContaOrigem);

        when(repository.findContaByNumeroConta(numeroContaOrigem)).thenThrow(new RuntimeException("Problema de conexão com o banco de dados"));

        verify(repository, times(0)).save(any());
        assertThrows(RuntimeException.class, () -> saque.executar(numeroContaInexistente, VALOR_SAQUE), "A conta deveria não ter sido encontrada. Por problema de conexão de banco de dados");
        assertEquals(VALOR_FINAL, conta.getSaldo(), "O saldo da conta não pode ter sido alterado.");
    }

    @Test
    void testSaqueMaiorSaldo() {
        final BigDecimal VALOR_INICIAL = BigDecimal.valueOf(5);
        final BigDecimal VALOR_SAQUE = BigDecimal.valueOf(6);
        final BigDecimal VALOR_FINAL = BigDecimal.valueOf(5).setScale(2, RoundingMode.HALF_UP);

        final Conta conta = criarConta(VALOR_INICIAL, numeroContaOrigem);

        verify(repository, times(0)).save(any());
        assertThrows(SaldoInsuficienteException.class, () -> saque.executar(numeroContaOrigem, VALOR_SAQUE));
        assertEquals(VALOR_FINAL, conta.getSaldo().setScale(2, RoundingMode.HALF_UP), "O saldo da conta não se alterou");
    }

    @Test
    void testSaqueArredondamentoParaBaixo() {
        final BigDecimal VALOR_INICIAL = BigDecimal.valueOf(10);
        final BigDecimal VALOR_SAQUE = BigDecimal.valueOf(2.134956789);
        final BigDecimal VALOR_FINAL = BigDecimal.valueOf(7.87).setScale(2, RoundingMode.HALF_UP);

        final Conta conta = criarConta(VALOR_INICIAL, numeroContaOrigem);

        final BigDecimal saldo = saque.executar(numeroContaOrigem, VALOR_SAQUE);

        verify(repository, times(1)).save(conta);
        assertEquals(VALOR_FINAL, saldo, "O valor subtraído foi arredondado para 2.13");
        assertEquals(VALOR_FINAL, conta.getSaldo());
    }

    @Test
    void testSaqueArredondamentoParaCima() {
        final BigDecimal VALOR_INICIAL = BigDecimal.valueOf(10);
        final BigDecimal VALOR_SAQUE = BigDecimal.valueOf(2.135456789);
        final BigDecimal VALOR_FINAL = BigDecimal.valueOf(7.86).setScale(2, RoundingMode.HALF_UP);

        final Conta conta = criarConta(VALOR_INICIAL, numeroContaOrigem);

        final BigDecimal saldo = saque.executar(numeroContaOrigem, VALOR_SAQUE);

        verify(repository, times(1)).save(conta);
        assertEquals(VALOR_FINAL, saldo, "O valor subtraído foi arredondado para 2.14");
        assertEquals(VALOR_FINAL, conta.getSaldo());
    }
}