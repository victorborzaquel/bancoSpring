package tech.ada.banco.services;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tech.ada.banco.controller.BaseContaControllerTest;
import tech.ada.banco.exceptions.ResourceNotFoundException;
import tech.ada.banco.model.Conta;
import tech.ada.banco.model.ModalidadeConta;
import tech.ada.banco.repository.ContaRepository;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DepositoTest extends BaseContaControllerTest {
    private final ContaRepository repository = Mockito.mock(ContaRepository.class);
    private final Deposito deposito = new Deposito(repository);

    @Test
    void testSaqueProblemaDeBancoDeDados() {
        Conta conta = new Conta(ModalidadeConta.CC, null);
        conta.deposito(BigDecimal.TEN);
        when(repository.findContaByNumeroConta(10)).thenThrow(RuntimeException.class);
        assertEquals(BigDecimal.valueOf(10), conta.getSaldo(), "O saldo inicial da conta deve ser alterado para 10");

        try {
            // TODO
            deposito.executar(1, BigDecimal.ONE);
            fail("A conta deveria não ter sido encontrada. Por problema de conexão de banco de dados");
        } catch (RuntimeException e) {

        }

        verify(repository, times(0)).save(any());
        assertEquals(BigDecimal.valueOf(10), conta.getSaldo(), "O saldo da conta não pode ter sido alterado.");
    }

    @Test
    void testDepositoContaNaoEncontrada() {
        Conta conta = criarConta(BigDecimal.valueOf(7));

        assertThrows(ResourceNotFoundException.class, () -> deposito.executar(numeroContaInexistente, BigDecimal.ONE), "A conta não foi encontrada.");
        verify(repository, times(0)).save(any());
        assertEquals(BigDecimal.valueOf(7), conta.getSaldo());
    }

    @Test
    void testProblemaBancoDeDados() {

    }

    @Test
    void testDepositoNegativo() {

    }

    @Test
    void testDepositoNormal() {

    }

    @Test
    void testDepositoCentavos() {

    }

    @Test
    void testArredondamentoParaCima() {

    }

    @Test
    void testArredondamentoParaBaixo() {

    }
}