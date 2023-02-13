package tech.ada.banco.utils;

import tech.ada.banco.model.Conta;

public class Uri {
    private final String baseUri;
    public Uri(String baseUri) {
        this.baseUri = baseUri;
    }

    public String criar(int numeroConta) {
        return baseUri + "/" + numeroConta;
    }

    public String criar(Conta conta) {
        return criar(conta.getNumeroConta());
    }

    public String base() {
        return baseUri;
    }
}
