package br.ufca.sgco.strategy;

public class PagamentoDinheiro implements PagamentoStrategy {
    @Override
    public void processarPagamento(double valor) {
        System.out.println("Pagamento de R$ " + String.format("%.2f", valor) + " processado em dinheiro.");
    }
}
