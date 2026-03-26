package br.ufca.sgco.model;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class Paciente {
    private String nome;
    private String cpf;
    private Date dataNascimento;
    private String contato;
    
    private Anamnese anamnese;
    

    private List<Procedimento> procedimentos;

    public Paciente(String nome, String cpf, Date dataNascimento, String contato) {
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.contato = contato;
        this.procedimentos = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public Anamnese getAnamnese() {
        return anamnese;
    }

    public void setAnamnese(Anamnese anamnese) {
        this.anamnese = anamnese;
    }

    public void adicionarProcedimento(Procedimento procedimento) {
        this.procedimentos.add(procedimento);
    }
    
    public List<Procedimento> listarProcedimentos() {
        return procedimentos;
    }

    @Override
    public String toString() {
        return "Paciente(" + nome + ", CPF: " + cpf + ")";
    }
}
