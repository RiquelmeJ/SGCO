package br.ufca.sgco.model;

public class Posologia {
    private String descricao;
    private String duracao;

    public Posologia(String descricao, String duracao) {
        this.descricao = descricao;
        this.duracao = duracao;
    }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getDuracao() { return duracao; }
    public void setDuracao(String duracao) { this.duracao = duracao; }
}
