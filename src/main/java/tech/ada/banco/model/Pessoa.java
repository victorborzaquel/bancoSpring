package tech.ada.banco.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tech.ada.banco.exceptions.ValorInvalidoException;

import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name = "PESSOA")
public class Pessoa {

    @Id
    @SequenceGenerator(name = "pessoaSequenceGenerator", sequenceName = "PESSOA_SQ", initialValue = 100)
    @GeneratedValue(generator = "pessoaSequenceGenerator", strategy = GenerationType.SEQUENCE)
    @Column(updatable = false)
    private Long id;

    @Column(name = "DATA_NASCIMENTO")
    private LocalDate dataNascimento;

    @Column(name = "CPF")
    private String cpf;

    @Column(name = "TELEFONE")
    private String telefone;

    @Column(name = "NOME")
    private String nome;

    public Pessoa(String nome, String cpf, LocalDate dataNascimento) {
        setDataNascimento(dataNascimento);
        this.cpf = cpf;
        this.nome = nome;
    }

    protected Pessoa() {

    }

    public String toString() {
        return "Nome: " + nome + " telefone: " + telefone + " e cpf: " + cpf;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        if (dataNascimento.plusYears(18).isAfter(LocalDate.now())) {
            throw new ValorInvalidoException();
        } else {
            this.dataNascimento = dataNascimento;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pessoa pessoa = (Pessoa) o;

        return id.equals(pessoa.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
