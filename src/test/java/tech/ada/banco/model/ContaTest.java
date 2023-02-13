package tech.ada.banco.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.ada.banco.exceptions.SaldoInsuficienteException;
import tech.ada.banco.exceptions.ValorInvalidoException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContaTest {
    private final Pessoa pessoa = new Pessoa("João", "123.456.789-00", LocalDate.of(1990, 1, 1));
    private Conta conta;

    @BeforeEach
    void setUp() {
        conta = new Conta(ModalidadeConta.CC, pessoa);
    }

    @Test
    void testDeposito() {
        BigDecimal saldo = BigDecimal.valueOf(10).setScale(2, RoundingMode.HALF_UP);
        conta.deposito(BigDecimal.TEN);

        assertEquals(saldo, conta.getSaldo());
    }

    @Test
    void testDepositoValorMenorQueZero() {
        BigDecimal saldo = BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP);
        BigDecimal valor = BigDecimal.valueOf(-1);

        assertThrows(ValorInvalidoException.class, () -> conta.deposito(valor));
        assertEquals(saldo, conta.getSaldo());
    }

    @Test
    void testSaque() {
        BigDecimal saldo = BigDecimal.valueOf(4).setScale(2, RoundingMode.HALF_UP);
        conta.deposito(BigDecimal.valueOf(7));
        conta.saque(BigDecimal.valueOf(3));

        assertEquals(saldo, conta.getSaldo());
    }

    @Test
    void testSaqueValorMenorQueZero() {
        BigDecimal saldo = BigDecimal.valueOf(7).setScale(2, RoundingMode.HALF_UP);
        conta.deposito(BigDecimal.valueOf(7));
        BigDecimal valor = BigDecimal.valueOf(-1);

        assertThrows(ValorInvalidoException.class, () -> conta.saque(valor));
        assertEquals(saldo, conta.getSaldo());
    }

    @Test
    void testSaqueSaldoInsuficiente() {
        BigDecimal saldo = BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_UP);
        BigDecimal valor = BigDecimal.valueOf(3);

        assertThrows(SaldoInsuficienteException.class, () -> conta.saque(valor));
        assertEquals(saldo, conta.getSaldo());
    }
}