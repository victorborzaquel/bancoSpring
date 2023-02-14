package tech.ada.banco.services;

import org.junit.jupiter.api.Test;
import tech.ada.banco.exceptions.ResourceNotFoundException;
import tech.ada.banco.exceptions.SaldoInsuficienteException;
import tech.ada.banco.model.Conta;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static tech.ada.banco.utils.Format.format;

class SaqueTest extends BaseContaTest {
    private final Saque saque = new Saque(repository);

    @Test
    void testSaqueParcial() {
        final BigDecimal VALOR_SAQUE = BigDecimal.valueOf(1);

        final Conta conta = criarContaOrigem(10);

        final BigDecimal saldo = saque.executar(numeroContaOrigem, VALOR_SAQUE);

        verify(repository, times(1)).save(conta);
        assertEquals(format(9), saldo, "O valor de retorno da função tem que ser 9. Saldo anterior vale 10 e o valor de saque é 1");
        assertEquals(format(9), conta.getSaldo());
    }

    @Test
    void testSaqueContaNaoEncontrada() {
        final BigDecimal VALOR_SAQUE = BigDecimal.valueOf(1);

        final Conta conta = criarContaOrigem(10);

        assertThrows(ResourceNotFoundException.class, () -> {
            saque.executar(numeroContaInexistente, VALOR_SAQUE);
        }, "A conta deveria não ter sido encontrada.");

        verify(repository, times(0)).save(any());
        assertEquals(format(10), conta.getSaldo(), "O saldo da conta não pode ter sido alterado.");
    }

    @Test
    void testSaqueProblemaDeBancoDeDados() {
        final BigDecimal VALOR_SAQUE = BigDecimal.valueOf(1);

        final Conta conta = criarContaOrigem(10);

        when(repository.findContaByNumeroConta(numeroContaOrigem))
                .thenThrow(new RuntimeException("Problema de conexão com o banco de dados"));

        assertThrows(RuntimeException.class, () -> {
            saque.executar(numeroContaInexistente, VALOR_SAQUE);
        }, "A conta deveria não ter sido encontrada. Por problema de conexão de banco de dados");

        verify(repository, times(0)).save(any());
        assertEquals(format(10), conta.getSaldo(), "O saldo da conta não pode ter sido alterado.");
    }

    @Test
    void testSaqueMaiorSaldo() {
        final BigDecimal VALOR_SAQUE = BigDecimal.valueOf(6);

        final Conta conta = criarContaOrigem(5);

        assertThrows(SaldoInsuficienteException.class, () -> saque.executar(numeroContaOrigem, VALOR_SAQUE));

        verify(repository, times(0)).save(any());
        assertEquals(format(5), conta.getSaldo(), "O saldo da conta não se alterou");
    }

    @Test
    void testSaqueArredondamentoParaBaixo() {
        final BigDecimal VALOR_SAQUE = BigDecimal.valueOf(2.134956789);

        final Conta conta = criarContaOrigem(10);

        final BigDecimal saldo = saque.executar(numeroContaOrigem, VALOR_SAQUE);

        verify(repository, times(1)).save(conta);
        assertEquals(format(7.87), saldo, "O valor subtraído foi arredondado para 2.13");
        assertEquals(format(7.87), conta.getSaldo());
    }

    @Test
    void testSaqueArredondamentoParaCima() {
        final BigDecimal VALOR_SAQUE = BigDecimal.valueOf(2.135456789);

        final Conta conta = criarContaOrigem(10);

        final BigDecimal saldo = saque.executar(numeroContaOrigem, VALOR_SAQUE);

        verify(repository, times(1)).save(conta);
        assertEquals(format(7.86), saldo, "O valor subtraído foi arredondado para 2.14");
        assertEquals(format(7.86), conta.getSaldo());
    }
}