package br.ufca.sgco.model;

public class ItemReceituario {
    private Medicamento medicamento;
    private Posologia posologia;

    public ItemReceituario(Medicamento medicamento, Posologia posologia) {
        this.medicamento = medicamento;
        this.posologia = posologia;
    }

    public Medicamento getMedicamento() { return medicamento; }
    public void setMedicamento(Medicamento medicamento) { this.medicamento = medicamento; }

    public Posologia getPosologia() { return posologia; }
    public void setPosologia(Posologia posologia) { this.posologia = posologia; }
}
