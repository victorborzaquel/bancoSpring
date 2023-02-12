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

class PixTest extends BaseContaTest {

    private final Pix pix = new Pix(repository);

    @Test
    void testSaqueProblemaDeBancoDeDados() {
        final BigDecimal VALOR_INICIAL_ORIGEM = BigDecimal.valueOf(7);
        final BigDecimal VALOR_INICIAL_DESTINO = BigDecimal.valueOf(3);
        final BigDecimal VALOR_PIX = BigDecimal.valueOf(1);
        final BigDecimal VALOR_FINAL_ORIGEM = BigDecimal.valueOf(7);
        final BigDecimal VALOR_FINAL_DESTINO = BigDecimal.valueOf(3);

        final Conta origem = criarConta(VALOR_INICIAL_ORIGEM, numeroContaOrigem);
        final Conta destino = criarConta(VALOR_INICIAL_DESTINO, numeroContaDestino);

        when(repository.findContaByNumeroConta(numeroContaOrigem)).thenThrow(new RuntimeException("Problema de conexão com o banco de dados"));

        assertThrows(RuntimeException.class, () -> pix.executar(numeroContaOrigem, numeroContaDestino, VALOR_PIX), "A conta deveria não ter sido encontrada. Por problema de conexão de banco de dados");

        verify(repository, times(0)).save(any());
        assertEquals(VALOR_FINAL_ORIGEM, origem.getSaldo(), "O saldo da conta não pode ter sido alterado.");
        assertEquals(VALOR_FINAL_DESTINO, destino.getSaldo(), "O saldo da conta não pode ter sido alterado.");
    }

    @Test
    void testOrigemInexistente() {
        final BigDecimal VALOR_INICIAL_ORIGEM = BigDecimal.valueOf(7);
        final BigDecimal VALOR_INICIAL_DESTINO = BigDecimal.valueOf(3);
        final BigDecimal VALOR_PIX = BigDecimal.valueOf(1);
        final BigDecimal VALOR_FINAL_ORIGEM = BigDecimal.valueOf(7);
        final BigDecimal VALOR_FINAL_DESTINO = BigDecimal.valueOf(3);

        final Conta origem = criarConta(VALOR_INICIAL_ORIGEM, numeroContaOrigem);
        final Conta destino = criarConta(VALOR_INICIAL_DESTINO, numeroContaDestino);

        assertThrows(ResourceNotFoundException.class, () -> pix.executar(numeroContaInexistente, numeroContaDestino, VALOR_PIX), "A conta deveria não ter sido encontrada.");

        verify(repository, times(0)).save(any());
        assertEquals(VALOR_FINAL_ORIGEM, origem.getSaldo(), "O saldo da conta não pode ter sido alterado.");
        assertEquals(VALOR_FINAL_DESTINO, destino.getSaldo(), "O saldo da conta não pode ter sido alterado.");
    }

    @Test
    void testDestinoInexistente() {
        final BigDecimal VALOR_INICIAL_ORIGEM = BigDecimal.valueOf(7);
        final BigDecimal VALOR_INICIAL_DESTINO = BigDecimal.valueOf(3);
        final BigDecimal VALOR_PIX = BigDecimal.valueOf(1);
        final BigDecimal VALOR_FINAL_ORIGEM = BigDecimal.valueOf(7);
        final BigDecimal VALOR_FINAL_DESTINO = BigDecimal.valueOf(3);

        final Conta origem = criarConta(VALOR_INICIAL_ORIGEM, numeroContaOrigem);
        final Conta destino = criarConta(VALOR_INICIAL_DESTINO, numeroContaDestino);

        assertThrows(ResourceNotFoundException.class, () -> pix.executar(numeroContaOrigem, numeroContaInexistente, VALOR_PIX), "A conta deveria não ter sido encontrada.");

        verify(repository, times(0)).save(any());
        assertEquals(VALOR_FINAL_ORIGEM, origem.getSaldo(), "O saldo da conta não pode ter sido alterado.");
        assertEquals(VALOR_FINAL_DESTINO, destino.getSaldo(), "O saldo da conta não pode ter sido alterado.");
    }

    @Test
    void testValorMenorQueZero() {
        final BigDecimal VALOR_INICIAL_ORIGEM = BigDecimal.valueOf(7);
        final BigDecimal VALOR_INICIAL_DESTINO = BigDecimal.valueOf(3);
        final BigDecimal VALOR_PIX = BigDecimal.valueOf(-1);
        final BigDecimal VALOR_FINAL_ORIGEM = BigDecimal.valueOf(7);
        final BigDecimal VALOR_FINAL_DESTINO = BigDecimal.valueOf(3);

        final Conta origem = criarConta(VALOR_INICIAL_ORIGEM, numeroContaOrigem);
        final Conta destino = criarConta(VALOR_INICIAL_DESTINO, numeroContaDestino);

        assertThrows(IllegalArgumentException.class, () -> pix.executar(numeroContaOrigem, numeroContaDestino, VALOR_PIX));

        verify(repository, times(0)).save(any());
        assertEquals(VALOR_FINAL_ORIGEM, origem.getSaldo(), "O saldo da conta não pode ter sido alterado.");
        assertEquals(VALOR_FINAL_DESTINO, destino.getSaldo(), "O saldo da conta não pode ter sido alterado.");
    }

    // TODO: Não está arrumando o saldo
//    @Test
//    void testArredondamentoParaCima() {
//        final BigDecimal VALOR_INICIAL_ORIGEM = BigDecimal.valueOf(7);
//        final BigDecimal VALOR_INICIAL_DESTINO = BigDecimal.valueOf(3);
//        final BigDecimal VALOR_PIX = BigDecimal.valueOf(1.005);
//        final BigDecimal VALOR_FINAL_ORIGEM = BigDecimal.valueOf(5.99);
//        final BigDecimal VALOR_FINAL_DESTINO = BigDecimal.valueOf(4.01);
//
//        final Conta origem = criarConta(VALOR_INICIAL_ORIGEM, numeroContaOrigem);
//        final Conta destino = criarConta(VALOR_INICIAL_DESTINO, numeroContaDestino);
//
//        BigDecimal saldo = pix.executar(numeroContaOrigem, numeroContaDestino, VALOR_PIX);
//
//        verify(repository, times(2)).save(any());
//        assertEquals(VALOR_FINAL_ORIGEM, origem.getSaldo());
//        assertEquals(VALOR_FINAL_ORIGEM, saldo);
//        assertEquals(VALOR_FINAL_DESTINO, destino.getSaldo());
//    }
//
//    @Test
//    void testArredondamentoParaBaixo() {
//        final BigDecimal VALOR_INICIAL_ORIGEM = BigDecimal.valueOf(7);
//        final BigDecimal VALOR_INICIAL_DESTINO = BigDecimal.valueOf(3);
//        final BigDecimal VALOR_PIX = BigDecimal.valueOf(1.004);
//        final BigDecimal VALOR_FINAL_ORIGEM = BigDecimal.valueOf(6);
//        final BigDecimal VALOR_FINAL_DESTINO = BigDecimal.valueOf(4);
//
//        final Conta origem = criarConta(VALOR_INICIAL_ORIGEM, numeroContaOrigem);
//        final Conta destino = criarConta(VALOR_INICIAL_DESTINO, numeroContaDestino);
//
//        BigDecimal saldo = pix.executar(numeroContaOrigem, numeroContaDestino, VALOR_PIX);
//
//        verify(repository, times(2)).save(any());
//        assertEquals(VALOR_FINAL_ORIGEM, origem.getSaldo());
//        assertEquals(VALOR_FINAL_ORIGEM, saldo);
//        assertEquals(VALOR_FINAL_DESTINO, destino.getSaldo());
//    }

    @Test
    void testValorComCasaDecimal() {
        final BigDecimal VALOR_INICIAL_ORIGEM = BigDecimal.valueOf(7);
        final BigDecimal VALOR_INICIAL_DESTINO = BigDecimal.valueOf(3);
        final BigDecimal VALOR_PIX = BigDecimal.valueOf(1.15);
        final BigDecimal VALOR_FINAL_ORIGEM = BigDecimal.valueOf(5.85);
        final BigDecimal VALOR_FINAL_DESTINO = BigDecimal.valueOf(4.15);

        final Conta origem = criarConta(VALOR_INICIAL_ORIGEM, numeroContaOrigem);
        final Conta destino = criarConta(VALOR_INICIAL_DESTINO, numeroContaDestino);

        BigDecimal saldo = pix.executar(numeroContaOrigem, numeroContaDestino, VALOR_PIX);

        verify(repository, times(2)).save(any());
        assertEquals(VALOR_FINAL_ORIGEM, origem.getSaldo());
        assertEquals(VALOR_FINAL_ORIGEM, saldo);
        assertEquals(VALOR_FINAL_DESTINO, destino.getSaldo());
    }

    @Test
    void testOrigemSemSaldo() {
        final BigDecimal VALOR_INICIAL_ORIGEM = BigDecimal.valueOf(0);
        final BigDecimal VALOR_INICIAL_DESTINO = BigDecimal.valueOf(3);
        final BigDecimal VALOR_PIX = BigDecimal.valueOf(5);
        final BigDecimal VALOR_FINAL_ORIGEM = BigDecimal.valueOf(0);
        final BigDecimal VALOR_FINAL_DESTINO = BigDecimal.valueOf(3);

        final Conta origem = criarConta(VALOR_INICIAL_ORIGEM, numeroContaOrigem);
        final Conta destino = criarConta(VALOR_INICIAL_DESTINO, numeroContaDestino);

        assertThrows(SaldoInsuficienteException.class, () -> pix.executar(numeroContaOrigem, numeroContaDestino, VALOR_PIX));

        verify(repository, times(0)).save(any());
        assertEquals(VALOR_FINAL_ORIGEM, origem.getSaldo());
        assertEquals(VALOR_FINAL_DESTINO, destino.getSaldo());
    }

    @Test
    void testDestinoSemSaldo() {
        final BigDecimal VALOR_INICIAL_ORIGEM = BigDecimal.valueOf(7);
        final BigDecimal VALOR_INICIAL_DESTINO = BigDecimal.valueOf(0);
        final BigDecimal VALOR_PIX = BigDecimal.valueOf(5);
        final BigDecimal VALOR_FINAL_ORIGEM = BigDecimal.valueOf(2);
        final BigDecimal VALOR_FINAL_DESTINO = BigDecimal.valueOf(5);

        final Conta origem = criarConta(VALOR_INICIAL_ORIGEM, numeroContaOrigem);
        final Conta destino = criarConta(VALOR_INICIAL_DESTINO, numeroContaDestino);

        BigDecimal saldo = pix.executar(numeroContaOrigem, numeroContaDestino, VALOR_PIX);

        verify(repository, times(2)).save(any());
        assertEquals(VALOR_FINAL_ORIGEM, origem.getSaldo());
        assertEquals(VALOR_FINAL_ORIGEM, saldo);
        assertEquals(VALOR_FINAL_DESTINO, destino.getSaldo());
    }
}