package br.ufca.sgco.model;

import java.util.Date;


public abstract class Documento {
    protected Date data;
    protected Paciente paciente;

    public Documento(Date data, Paciente paciente) {
        this.data = data;
        this.paciente = paciente;
    }

    public final void gerarPDF() {
        System.out.println("Iniciando geração de PDF para " + paciente.getNome() + "...");
        String conteudo = gerarConteudo();
        salvarArquivoPDF(conteudo);
        System.out.println("PDF gerado com sucesso.");
    }

    protected abstract String gerarConteudo();

    private void salvarArquivoPDF(String conteudo) {
        br.ufca.sgco.service.PdfService.salvarPDF(conteudo, this.getClass().getSimpleName() + "_" + paciente.getNome() + ".pdf");
    }

    public Date getData() { return data; }
    public void setData(Date data) { this.data = data; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }
}
