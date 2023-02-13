package tech.ada.banco.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tech.ada.banco.exceptions.SaldoInsuficienteException;
import tech.ada.banco.exceptions.ValorInvalidoException;

import java.math.BigDecimal;

import static tech.ada.banco.utils.Format.format;

@Entity
@Table(name = "CONTA")
@Getter
@Setter
public class Conta {

    @Id
    @SequenceGenerator(name = "contaSequenceGenerator", sequenceName = "CONTA_SQ", initialValue = 10000)
    @GeneratedValue(generator = "contaSequenceGenerator", strategy = GenerationType.SEQUENCE)
    @Column(updatable = false)
    private int numeroConta;

    @Column(name = "TIPO")
    @Enumerated(EnumType.STRING)
    private ModalidadeConta tipo;

    @Column(name = "SALDO")
    protected BigDecimal saldo;

    @Column(name = "AGENCIA")
    private final String agencia;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "PESSOA_ID", referencedColumnName = "ID")
    private Pessoa titular;

    public Conta(ModalidadeConta tipo, Pessoa titular) {
        this();
        this.tipo = tipo;
        this.titular = titular;
    }

    protected Conta() {
        agencia = "0001";
        saldo = format(0);
    }

    public ModalidadeConta getTipo() {
        return tipo;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void deposito(BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValorInvalidoException();
        }

        saldo = saldo.add(format(valor));
    }

    public void saque(BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValorInvalidoException();
        }

        if (valor.compareTo(saldo) > 0) {
            throw new SaldoInsuficienteException();
        } else {
            saldo = saldo.subtract(format(valor));
        }
    }

    public int getNumeroConta() {
        return numeroConta;
    }

}
