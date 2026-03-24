package br.ufca.sgco.model;

import java.util.ArrayList;
import java.util.List;

// Padrão Composite: Agrupa vários procedimentos em um único registro/histórico
public class HistoricoProcedimentos {
    private List<Procedimento> procedimentos;

    public HistoricoProcedimentos() {
        this.procedimentos = new ArrayList<>();
    }

    public void adicionarProcedimento(Procedimento proc) {
        procedimentos.add(proc);
    }

    public void removerProcedimento(Procedimento proc) {
        procedimentos.remove(proc);
    }
    
    public List<Procedimento> getProcedimentos() {
        return procedimentos;
    }

    public double calcularTotalGasto() {
        double total = 0;
        for (Procedimento p : procedimentos) {
            total += p.getValor();
        }
        return total;
    }

    public void exibirHistorico() {
        System.out.println("=== HISTÓRICO DE PROCEDIMENTOS ===");
        if (procedimentos.isEmpty()) {
            System.out.println("Nenhum procedimento registrado.");
        } else {
            for (Procedimento p : procedimentos) {
                System.out.println("- " + p.toString());
            }
            System.out.println("Total Anotado: R$ " + String.format("%.2f", calcularTotalGasto()));
        }
    }
}
