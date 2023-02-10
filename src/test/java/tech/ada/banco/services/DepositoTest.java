package tech.ada.banco.services;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tech.ada.banco.exceptions.ResourceNotFoundException;
import tech.ada.banco.model.Conta;
import tech.ada.banco.model.ModalidadeConta;
import tech.ada.banco.repository.ContaRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DepositoTest {
    private final ContaRepository repository = Mockito.mock(ContaRepository.class);
    private final Deposito deposito = new Deposito(repository);

    @Test
    void testDepositoContaNaoEncontrada() {
        Conta conta = new Conta(ModalidadeConta.CC, null);
        conta.deposito(BigDecimal.valueOf(7));
        when(repository.findContaByNumeroConta(1)).thenReturn(Optional.of(conta));
        assertEquals(BigDecimal.valueOf(7), conta.getSaldo());

        assertThrows(ResourceNotFoundException.class, () -> deposito.executar(1, BigDecimal.ONE), "A conta não foi encontrada.");
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
}