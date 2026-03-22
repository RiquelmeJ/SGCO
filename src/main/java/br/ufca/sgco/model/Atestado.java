package br.ufca.sgco.model;

import java.util.Date;

public class Atestado extends Documento {
    private int diasRepouso;
    private String motivo;

    public Atestado(Date data, Paciente paciente, int diasRepouso, String motivo) {
        super(data, paciente);
        this.diasRepouso = diasRepouso;
        this.motivo = motivo;
    }

    @Override
    protected String gerarConteudo() {
        StringBuilder sb = new StringBuilder();
        sb.append("ATESTADO ODONTOLÓGICO\n\n");
        sb.append("Atesto para os devidos fins que o paciente ").append(paciente.getNome());
        sb.append(" (CPF: ").append(paciente.getCpf()).append(") foi atendido nesta data (");
        sb.append(data.toString()).append(").\n");
        sb.append("Motivo do atendimento: ").append(motivo).append(".\n");
        sb.append("Necessita de ").append(diasRepouso).append(" dia(s) de repouso.\n");
        return sb.toString();
    }

    public int getDiasRepouso() { return diasRepouso; }
    public void setDiasRepouso(int diasRepouso) { this.diasRepouso = diasRepouso; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
