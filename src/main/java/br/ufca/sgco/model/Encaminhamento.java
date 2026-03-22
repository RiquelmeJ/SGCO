package br.ufca.sgco.model;

import java.util.Date;

public class Encaminhamento extends Documento {
    private String especialidade;
    private String motivoEncaminhamento;

    public Encaminhamento(Date data, Paciente paciente, String especialidade, String motivoEncaminhamento) {
        super(data, paciente);
        this.especialidade = especialidade;
        this.motivoEncaminhamento = motivoEncaminhamento;
    }

    @Override
    protected String gerarConteudo() {
        StringBuilder sb = new StringBuilder();
        sb.append("ENCAMINHAMENTO MÉDICO/ODONTOLÓGICO\n\n");
        sb.append("Paciente: ").append(paciente.getNome()).append("\n");
        sb.append("CPF: ").append(paciente.getCpf()).append("\n");
        if (paciente.getAnamnese() != null) {
            sb.append("\nResumo Clínico:\n");
            sb.append(paciente.getAnamnese().getHistorico()).append("\n");
            sb.append("Alergias: ").append(paciente.getAnamnese().getAlergias()).append("\n");
        }
        sb.append("\nEncaminho o paciente ao especialista: ").append(especialidade).append("\n");
        sb.append("Motivo: ").append(motivoEncaminhamento).append("\n");
        sb.append("Data: ").append(data.toString()).append("\n");
        return sb.toString();
    }

    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }

    public String getMotivoEncaminhamento() { return motivoEncaminhamento; }
    public void setMotivoEncaminhamento(String motivoEncaminhamento) { this.motivoEncaminhamento = motivoEncaminhamento; }
}
