package br.ufca.sgco.facade;

import br.ufca.sgco.factory.DocumentoFactory;
import br.ufca.sgco.model.*;
import br.ufca.sgco.repository.MedicamentoDAO;
import br.ufca.sgco.repository.PacienteDAO;
import br.ufca.sgco.repository.ProcedimentoDAO;
import br.ufca.sgco.strategy.PagamentoCartao;
import br.ufca.sgco.strategy.PagamentoDinheiro;

import java.util.Date;
import java.util.List;

public class SistemaFacade {
    private PacienteDAO pacienteDAO;
    private ProcedimentoDAO procedimentoDAO;
    private MedicamentoDAO medicamentoDAO;
    private DocumentoFactory documentoFactory;

    public SistemaFacade() {
        this.pacienteDAO = new PacienteDAO();
        this.procedimentoDAO = new ProcedimentoDAO();
        this.medicamentoDAO = new MedicamentoDAO();
        this.documentoFactory = new DocumentoFactory();
    }

    // 1. Cadastro de Pacientes
    public void cadastrarPaciente(String nome, String cpf, Date dataNascimento, String contato, String historico, String alergias, String obsAnamnese) {
        Paciente p = new Paciente(nome, cpf, dataNascimento, contato);
        if (historico != null || alergias != null || obsAnamnese != null) {
            p.setAnamnese(new Anamnese(historico, alergias, obsAnamnese));
        }
        pacienteDAO.salvar(p);
    }
    
    public Paciente buscarPacientePorCpf(String cpf) {
        return pacienteDAO.buscarPorCpf(cpf);
    }
    
    public List<Paciente> listarPacientes() {
        return pacienteDAO.listarTodos();
    }

    // 2. Registrar Procedimento (Linha do Tempo)
    public void registrarProcedimento(String cpfPaciente, String tipo, String observacoes, double valor, int tipoPagamento) {
        Paciente paciente = buscarPacientePorCpf(cpfPaciente);
        if (paciente == null) {
            System.out.println("Paciente não encontrado!");
            return;
        }
        
        Procedimento proc = new Procedimento(new Date(), tipo, observacoes, "", valor);
        if (tipoPagamento == 1) {
            proc.processarPagamento(new PagamentoDinheiro());
        } else {
            proc.processarPagamento(new PagamentoCartao());
        }
        
        procedimentoDAO.salvar(proc, cpfPaciente);
    }

    // 3. Acervo de Procedimentos (Composite)
    public HistoricoProcedimentos obterHistoricoProcedimentos(String cpfPaciente) {
        List<Procedimento> list = procedimentoDAO.listarPorPacienteCpf(cpfPaciente);
        HistoricoProcedimentos historico = new HistoricoProcedimentos();
        for (Procedimento p : list) {
            historico.adicionarProcedimento(p);
        }
        return historico;
    }

    // 4. Emissão de Atestado Rápido
    public void gerarAtestado(String cpfPaciente, int diasRepouso, String motivo) {
        Paciente p = buscarPacientePorCpf(cpfPaciente);
        if (p == null) return;
        Documento doc = documentoFactory.criarDocumento("atestado", p, diasRepouso, motivo);
        doc.gerarPDF();
    }

    // 5. Encaminhamento para Especialistas
    public void gerarEncaminhamento(String cpfPaciente, String especialidade, String motivo) {
        Paciente p = buscarPacientePorCpf(cpfPaciente);
        if (p == null) return;
        Documento doc = documentoFactory.criarDocumento("encaminhamento", p, especialidade, motivo);
        doc.gerarPDF();
    }

    // 6. Prescrição Inteligente (Receituário)
    public List<Medicamento> listarMedicamentosPreDefinidos() {
        return medicamentoDAO.listarTodos();
    }
    
    public void adicionarNovoMedicamento(String nome) {
        medicamentoDAO.salvar(new Medicamento(nome));
    }
    
    public Receituario iniciarReceituario(String cpfPaciente) {
        Paciente p = buscarPacientePorCpf(cpfPaciente);
        if (p == null) return null;
        return (Receituario) documentoFactory.criarDocumento("receituario", p);
    }
    
    public void adicionarMedicamentoNoReceituario(Receituario receita, String nomeMedicamento, String descPosologia, String duracao) {
        Medicamento med = medicamentoDAO.buscarPorNome(nomeMedicamento);
        if (med == null) {
            System.out.println("Medicamento não encontrado na base, cadastrando um novo e adicionando.");
            med = new Medicamento(nomeMedicamento);
            medicamentoDAO.salvar(med);
        }
        Posologia posologia = new Posologia(descPosologia, duracao);
        receita.adicionarItem(new ItemReceituario(med, posologia));
    }
    
    public void finalizarReceituario(Receituario receita) {
        if (receita != null) {
            receita.gerarPDF();
        }
    }
}
