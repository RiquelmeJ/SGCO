package br.ufca.sgco.model;

import br.ufca.sgco.strategy.PagamentoStrategy;
import java.util.Date;

public class Procedimento {
    private Date data;
    private String tipo;
    private String observacoes;
    private String formaPagamento;
    private double valor;
    private PagamentoStrategy pagamentoStrategy;

    public Procedimento(Date data, String tipo, String observacoes, String formaPagamento, double valor) {
        this.data = data;
        this.tipo = tipo;
        this.observacoes = observacoes;
        this.formaPagamento = formaPagamento;
        this.valor = valor;
    }

    public void processarPagamento(PagamentoStrategy estrategia) {
        this.pagamentoStrategy = estrategia;
        this.pagamentoStrategy.processarPagamento(this.valor);
        this.formaPagamento = strategyToFormaPagamento(estrategia);
    }
    
    private String strategyToFormaPagamento(PagamentoStrategy estrategia) {
        if(estrategia.getClass().getSimpleName().contains("Dinheiro")) return "Dinheiro";
        if(estrategia.getClass().getSimpleName().contains("Cartao")) return "Cartão";
        return "Outros";
    }

    public Date getData() { return data; }
    public void setData(Date data) { this.data = data; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }
    
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    @Override
    public String toString() {
        return "Procedimento(" + tipo + " em " + data + ", Valor: R$" + valor + ", Pagamento: " + formaPagamento + ")";
    }
}
