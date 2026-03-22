package br.ufca.sgco.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Receituario extends Documento {
    private List<ItemReceituario> itens;

    public Receituario(Date data, Paciente paciente) {
        super(data, paciente);
        this.itens = new ArrayList<>();
    }
    
    public void adicionarItem(ItemReceituario item) {
        this.itens.add(item);
    }

    @Override
    protected String gerarConteudo() {
        StringBuilder sb = new StringBuilder();
        sb.append("RECEITUÁRIO ODONTOLÓGICO\n\n");
        sb.append("Paciente: ").append(paciente.getNome()).append("\n");
        sb.append("Data: ").append(data.toString()).append("\n\n");
        sb.append("Receita:\n");
        
        for (int i = 0; i < itens.size(); i++) {
            ItemReceituario item = itens.get(i);
            sb.append(i + 1).append(". ").append(item.getMedicamento().getNome()).append("\n");
            sb.append("   Uso: ").append(item.getPosologia().getDescricao());
            sb.append(" por ").append(item.getPosologia().getDuracao()).append("\n");
        }
        return sb.toString();
    }

    public List<ItemReceituario> getItens() { return itens; }
    public void setItens(List<ItemReceituario> itens) { this.itens = itens; }
}
