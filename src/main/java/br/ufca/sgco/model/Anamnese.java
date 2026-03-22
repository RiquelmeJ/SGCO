package br.ufca.sgco.model; 

public class Anamnese {
    private String historico;
    private String alergias;
    private String observacoes;

    public Anamnese() {}

    public Anamnese(String historico, String alergias, String observacoes) {
        this.historico = historico;
        this.alergias = alergias;
        this.observacoes = observacoes;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public String getAlergias() {
        return alergias;
    }

    public void setAlergias(String alergias) {
        this.alergias = alergias;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    @Override
    public String toString() {
        return "Anamnese(Histórico: " + historico + ", Alergias: " + alergias + ", Observações: " + observacoes + ")";
    }
}

