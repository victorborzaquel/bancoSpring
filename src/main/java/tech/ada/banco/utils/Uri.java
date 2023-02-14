package tech.ada.banco.utils;

import tech.ada.banco.model.Conta;
import tech.ada.banco.model.Pessoa;

public class Uri {
    private final String baseUri;
    public Uri(String baseUri) {
        this.baseUri = baseUri;
    }

    public String criar(int numeroConta) {
        return baseUri + "/" + numeroConta;
    }
    public String criar(long numeroConta) {
        return baseUri + "/" + numeroConta;
    }

    public String criar(Conta conta) {
        return criar(conta.getNumeroConta());
    }

    public String criar(Pessoa pessoa) {
        return criar(pessoa.getId());
    }

    public String base() {
        return baseUri;
    }

    @Override
    public String toString() {
        return baseUri;
    }
}
