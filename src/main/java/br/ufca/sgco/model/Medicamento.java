package br.ufca.sgco.model;

public class Medicamento {
    private String nome;

    public Medicamento(String nome) {
        this.nome = nome;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    @Override
    public String toString() {
        return nome;
    }
}
