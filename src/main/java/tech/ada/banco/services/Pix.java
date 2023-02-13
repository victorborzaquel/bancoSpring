package tech.ada.banco.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.ada.banco.exceptions.ResourceNotFoundException;
import tech.ada.banco.exceptions.ValorInvalidoException;
import tech.ada.banco.model.Conta;
import tech.ada.banco.repository.ContaRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Slf4j
public class Pix {

    private final ContaRepository repository;

    public Pix(ContaRepository repository) {
        this.repository = repository;
    }

    public BigDecimal executar(int contaOrigem, int contaDestino, BigDecimal valor) {
        valor = valor.setScale(2, RoundingMode.HALF_UP);

        Conta origem = repository.findContaByNumeroConta(contaOrigem).orElseThrow(ResourceNotFoundException::new);
        Conta destino = repository.findContaByNumeroConta(contaDestino).orElseThrow(ResourceNotFoundException::new);

        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValorInvalidoException();
        }

        origem.saque(valor);
        repository.save(origem);
        destino.deposito(valor);
        repository.save(destino);
        log.info("Operação realizada com sucesso.");
        return origem.getSaldo();
    }

}
