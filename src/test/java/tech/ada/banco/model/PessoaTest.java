package tech.ada.banco.model;

import org.junit.jupiter.api.Test;
import tech.ada.banco.exceptions.ValorInvalidoException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PessoaTest {

    @Test
    void testMenorDeIdade() {
        LocalDate dataNascimento = LocalDate.now().minusYears(17);

        assertThrows(ValorInvalidoException.class, () -> new Pessoa("João", "123.456.789-00", dataNascimento));
    }

    @Test
    void testSetMenorDeIdade() {
        LocalDate dataNascimento = LocalDate.now().minusYears(18);
        LocalDate dataNascimentoFinal = LocalDate.now().minusYears(17);
        Pessoa pessoa = new Pessoa("João", "123.456.789-00", dataNascimento);

        assertThrows(ValorInvalidoException.class, () -> pessoa.setDataNascimento(dataNascimentoFinal));
    }

    @Test
    void testMaiorDeIdade() {
        LocalDate dataNascimento = LocalDate.now().minusYears(18);
        Pessoa pessoa = new Pessoa("João", "123.456.789-00", dataNascimento);

        assertEquals(dataNascimento, pessoa.getDataNascimento());
    }

    @Test
    void testSetMaiorDeIdade() {
        LocalDate dataNascimento = LocalDate.now().minusYears(18);
        LocalDate dataNascimentoFinal = LocalDate.now().minusYears(19);
        Pessoa pessoa = new Pessoa("João", "123.456.789-00", dataNascimento);

        pessoa.setDataNascimento(dataNascimentoFinal);

        assertEquals(dataNascimentoFinal, pessoa.getDataNascimento());
    }
}