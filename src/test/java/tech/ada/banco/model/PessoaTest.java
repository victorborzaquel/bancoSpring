package tech.ada.banco.model;

import org.junit.jupiter.api.Test;
import tech.ada.banco.exceptions.ValorInvalidoException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PessoaTest {

    @Test
    void testMenorDeIdade() {
        final LocalDate dataNascimento = LocalDate.now().minusYears(17);

        assertThrows(ValorInvalidoException.class, () -> new Pessoa("João", "123.456.789-00", dataNascimento));
    }

    @Test
    void testSetMenorDeIdade() {
        final LocalDate dataNascimento = LocalDate.now().minusYears(18);
        final LocalDate dataNascimentoFinal = LocalDate.now().minusYears(17);
        final Pessoa pessoa = new Pessoa("João", "123.456.789-00", dataNascimento);

        assertThrows(ValorInvalidoException.class, () -> pessoa.setDataNascimento(dataNascimentoFinal));
    }

    @Test
    void testMaiorDeIdade() {
        final LocalDate dataNascimento = LocalDate.now().minusYears(18);
        final Pessoa pessoa = new Pessoa("João", "123.456.789-00", dataNascimento);

        assertEquals(dataNascimento, pessoa.getDataNascimento());
    }

    @Test
    void testSetMaiorDeIdade() {
        final LocalDate dataNascimento = LocalDate.now().minusYears(18);
        final LocalDate dataNascimentoFinal = LocalDate.now().minusYears(19);
        final Pessoa pessoa = new Pessoa("João", "123.456.789-00", dataNascimento);

        pessoa.setDataNascimento(dataNascimentoFinal);

        assertEquals(dataNascimentoFinal, pessoa.getDataNascimento());
    }
}