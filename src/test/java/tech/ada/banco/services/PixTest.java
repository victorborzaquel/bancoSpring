package tech.ada.banco.services;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tech.ada.banco.model.Conta;
import tech.ada.banco.model.ModalidadeConta;
import tech.ada.banco.repository.ContaRepository;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PixTest {

    private final ContaRepository repository = Mockito.mock(ContaRepository.class);
    private final Pix pix = new Pix(repository);

    @Test
    void testSaqueProblemaDeBancoDeDados() {
        Conta conta = new Conta(ModalidadeConta.CC, null);
        conta.deposito(BigDecimal.TEN);
        when(repository.findContaByNumeroConta(10)).thenThrow(RuntimeException.class);
        assertEquals(BigDecimal.valueOf(10), conta.getSaldo(), "O saldo inicial da conta deve ser alterado para 10");

        try {
            // TODO
            pix.executar(1, 2, BigDecimal.ONE);
            fail("A conta deveria não ter sido encontrada. Por problema de conexão de banco de dados");
        } catch (RuntimeException e) {

        }

        verify(repository, times(0)).save(any());
        assertEquals(BigDecimal.valueOf(10), conta.getSaldo(), "O saldo da conta não pode ter sido alterado.");
    }

    @Test
    void testOrigemInexistente() {

    }

    @Test
    void testDestinoInexistente() {

    }

    @Test
    void testValorMenorQueZero() {

    }

    @Test
    void testArredondamentoParaCima() {

    }

    @Test
    void testArredondamentoParaBaixo() {

    }

    @Test
    void testValorComCasaDecimal() {

    }

    @Test
    void testOrigemComSaldo() {

    }

    @Test
    void testOrigemSemSaldo() {

    }

    @Test
    void testDestinoSemSaldo() {

    }

    @Test
    void testDestinoComSaldo() {

    }
}