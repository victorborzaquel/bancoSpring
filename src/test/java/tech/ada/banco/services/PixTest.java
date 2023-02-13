package tech.ada.banco.services;

import org.junit.jupiter.api.Test;
import tech.ada.banco.exceptions.ResourceNotFoundException;
import tech.ada.banco.exceptions.SaldoInsuficienteException;
import tech.ada.banco.exceptions.ValorInvalidoException;
import tech.ada.banco.model.Conta;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static tech.ada.banco.utils.Format.format;

class PixTest extends BaseContaTest {

    private final Pix pix = new Pix(repository);

    @Test
    void testSaqueProblemaDeBancoDeDados() {
        final BigDecimal VALOR_PIX = BigDecimal.valueOf(1);

        final Conta origem = criarContaOrigem(7);
        final Conta destino = criarContaDestino(3);

        when(repository.findContaByNumeroConta(numeroContaOrigem))
                .thenThrow(new RuntimeException("Problema de conexão com o banco de dados"));

        assertThrows(RuntimeException.class, () -> {
            pix.executar(numeroContaOrigem, numeroContaDestino, VALOR_PIX);
        }, "A conta deveria não ter sido encontrada. Por problema de conexão de banco de dados");

        verify(repository, times(0)).save(any());
        assertEquals(format(7), origem.getSaldo(), "O saldo da conta não pode ter sido alterado.");
        assertEquals(format(3), destino.getSaldo(), "O saldo da conta não pode ter sido alterado.");
    }

    @Test
    void testOrigemInexistente() {
        final BigDecimal VALOR_PIX = BigDecimal.valueOf(1);

        final Conta origem = criarContaOrigem(7);
        final Conta destino = criarContaDestino(3);

        assertThrows(ResourceNotFoundException.class, () -> {
            pix.executar(numeroContaInexistente, numeroContaDestino, VALOR_PIX);
        }, "A conta deveria não ter sido encontrada.");

        verify(repository, times(0)).save(any());
        assertEquals(format(7), origem.getSaldo(), "O saldo da conta não pode ter sido alterado.");
        assertEquals(format(3), destino.getSaldo(), "O saldo da conta não pode ter sido alterado.");
    }

    @Test
    void testDestinoInexistente() {
        final BigDecimal VALOR_PIX = BigDecimal.valueOf(1);

        final Conta origem = criarContaOrigem(7);
        final Conta destino = criarContaDestino(3);

        assertThrows(ResourceNotFoundException.class, () -> {
            pix.executar(numeroContaOrigem, numeroContaInexistente, VALOR_PIX);
        }, "A conta deveria não ter sido encontrada.");

        verify(repository, times(0)).save(any());
        assertEquals(format(7), origem.getSaldo(), "O saldo da conta não pode ter sido alterado.");
        assertEquals(format(3), destino.getSaldo(), "O saldo da conta não pode ter sido alterado.");
    }

    @Test
    void testValorMenorQueZero() {
        final BigDecimal VALOR_PIX = BigDecimal.valueOf(-1);

        final Conta origem = criarContaOrigem(7);
        final Conta destino = criarContaDestino(3);

        assertThrows(ValorInvalidoException.class, () -> pix.executar(numeroContaOrigem, numeroContaDestino, VALOR_PIX));

        verify(repository, times(0)).save(any());
        assertEquals(format(7), origem.getSaldo(), "O saldo da conta não pode ter sido alterado.");
        assertEquals(format(3), destino.getSaldo(), "O saldo da conta não pode ter sido alterado.");
    }

    @Test
    void testArredondamentoParaCima() {
        final BigDecimal VALOR_PIX = BigDecimal.valueOf(1.005);

        final Conta origem = criarContaOrigem(7);
        final Conta destino = criarContaDestino(3);

        final BigDecimal saldo = pix.executar(numeroContaOrigem, numeroContaDestino, VALOR_PIX);

        verify(repository, times(2)).save(any());
        assertEquals(format(5.99), origem.getSaldo());
        assertEquals(format(5.99), saldo);
        assertEquals(format(4.01), destino.getSaldo());
    }

    @Test
    void testArredondamentoParaBaixo() {
        final BigDecimal VALOR_PIX = BigDecimal.valueOf(1.004);

        final Conta origem = criarContaOrigem(7);
        final Conta destino = criarContaDestino(3);

        final BigDecimal saldo = pix.executar(numeroContaOrigem, numeroContaDestino, VALOR_PIX);

        verify(repository, times(2)).save(any());
        assertEquals(format(6), origem.getSaldo());
        assertEquals(format(6), saldo);
        assertEquals(format(4), destino.getSaldo());
    }

    @Test
    void testValorComCasaDecimal() {
        final BigDecimal VALOR_PIX = BigDecimal.valueOf(1.15);

        final Conta origem = criarContaOrigem(7);
        final Conta destino = criarContaDestino(3);

        final BigDecimal saldo = pix.executar(numeroContaOrigem, numeroContaDestino, VALOR_PIX);

        verify(repository, times(2)).save(any());
        assertEquals(format(5.85), origem.getSaldo());
        assertEquals(format(5.85), saldo);
        assertEquals(format(4.15), destino.getSaldo());
    }

    @Test
    void testOrigemSemSaldo() {
        final BigDecimal VALOR_PIX = BigDecimal.valueOf(5);

        final Conta origem = criarContaOrigem(0);
        final Conta destino = criarContaDestino(3);

        assertThrows(SaldoInsuficienteException.class, () -> {
            pix.executar(numeroContaOrigem, numeroContaDestino, VALOR_PIX);
        });

        verify(repository, times(0)).save(any());
        assertEquals(format(0), origem.getSaldo());
        assertEquals(format(3), destino.getSaldo());
    }

    @Test
    void testDestinoSemSaldo() {
        final BigDecimal VALOR_PIX = BigDecimal.valueOf(5);

        final Conta origem = criarContaOrigem(7);
        final Conta destino = criarContaDestino(0);

        final BigDecimal saldo = pix.executar(numeroContaOrigem, numeroContaDestino, VALOR_PIX);

        verify(repository, times(2)).save(any());
        assertEquals(format(2), origem.getSaldo());
        assertEquals(format(2), saldo);
        assertEquals(format(5), destino.getSaldo());
    }
}