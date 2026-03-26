package br.ufca.sgco.view;

import br.ufca.sgco.facade.SistemaFacade;
import br.ufca.sgco.model.HistoricoProcedimentos;
import br.ufca.sgco.model.Medicamento;
import br.ufca.sgco.model.Paciente;
import br.ufca.sgco.model.Receituario;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static SistemaFacade facade = new SistemaFacade();
    private static Scanner scanner = new Scanner(System.in);
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public static void main(String[] args) {
        System.out.println("Bem-vindo ao Sistema de Gestão para Consultórios Odontológicos (SGCO)");
        boolean rodando = true;

        while (rodando) {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1. Cadastrar Paciente");
            System.out.println("2. Ver Pacientes");
            System.out.println("3. Registrar Procedimento");
            System.out.println("4. Histórico de Procedimentos (Acervo)");
            System.out.println("5. Gerar Atestado Rápido");
            System.out.println("6. Gerar Encaminhamento para Especialista");
            System.out.println("7. Prescrição Inteligente (Gerar Receituário)");
            System.out.println("8. Sair");
            System.out.print("Escolha uma opção: ");

            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1":
                    cadastrarPaciente();
                    break;
                case "2":
                    listarPacientes();
                    break;
                case "3":
                    registrarProcedimento();
                    break;
                case "4":
                    historicoProcedimentos();
                    break;
                case "5":
                    gerarAtestado();
                    break;
                case "6":
                    gerarEncaminhamento();
                    break;
                case "7":
                    gerarReceituario();
                    break;
                case "8":
                    rodando = false;
                    System.out.println("Saindo do sistema. Até logo!");
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    private static void cadastrarPaciente() {
        System.out.println("\n--- Cadastro de Paciente ---");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        System.out.print("Data de Nascimento (DD/MM/YYYY) ou enter para pular: ");
        String dataStr = scanner.nextLine();
        Date dtNasc = null;
        if (!dataStr.isEmpty()) {
            try {
                dtNasc = sdf.parse(dataStr);
            } catch (ParseException e) {
                System.out.println("Data em formato inválido. Pulando.");
            }
        }
        System.out.print("Contato: ");
        String contato = scanner.nextLine();

        System.out.print("Deseja preencher Anamnese/Histórico Médico? (S/N): ");
        String resp = scanner.nextLine();
        String historico = null, alergias = null, obs = null;

        if (resp.equalsIgnoreCase("S")) {
            System.out.print("Breve histórico clínico: ");
            historico = scanner.nextLine();
            System.out.print("Alergias conhecidas: ");
            alergias = scanner.nextLine();
            System.out.print("Observações gerais: ");
            obs = scanner.nextLine();
        }

        facade.cadastrarPaciente(nome, cpf, dtNasc, contato, historico, alergias, obs);
        System.out.println("Paciente cadastrado.");
    }

    private static void listarPacientes() {
        System.out.println("\n--- Lista de Pacientes ---");
        List<Paciente> pacientes = facade.listarPacientes();
        if (pacientes.isEmpty()) {
            System.out.println("Nenhum paciente cadastrado.");
            return;
        }
        for (Paciente p : pacientes) {
            System.out.println("- " + p.getNome() + " (CPF: " + p.getCpf() + ")");
            if(p.getAnamnese() != null) {
                System.out.println("  Anamnese: Alergias (" + p.getAnamnese().getAlergias() + ")");
            }
        }
    }

    private static void registrarProcedimento() {
        System.out.println("\n--- Registrar Procedimento ---");
        System.out.print("CPF do Paciente: ");
        String cpf = scanner.nextLine();
        Paciente p = facade.buscarPacientePorCpf(cpf);
        if (p == null) {
            System.out.println("Paciente não encontrado!");
            return;
        }

        System.out.print("Tipo do procedimento (ex: Limpeza, Restauração): ");
        String tipo = scanner.nextLine();
        System.out.print("Observações: ");
        String obs = scanner.nextLine();
        System.out.print("Valor cobrado (R$): ");
        double valor = 0;
        try {
            valor = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido.");
            return;
        }

        System.out.print("Forma de Pagamento (1-Dinheiro, 2-Cartão): ");
        int forma = 1;
        try {
            forma = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {}

        facade.registrarProcedimento(cpf, tipo, obs, valor, forma, new Date(), "Agendado");
    }

    private static void historicoProcedimentos() {
        System.out.println("\n--- Acervo de Procedimentos do Paciente ---");
        System.out.print("CPF do Paciente: ");
        String cpf = scanner.nextLine();
        HistoricoProcedimentos hist = facade.obterHistoricoProcedimentos(cpf);
        hist.exibirHistorico();
    }

    private static void gerarAtestado() {
        System.out.println("\n--- Emitir Atestado Rápido ---");
        System.out.print("CPF do Paciente: ");
        String cpf = scanner.nextLine();
        System.out.print("Motivo (ex: Repouso pós-cirúrgico, Consulta de praxe): ");
        String motivo = scanner.nextLine();
        System.out.print("Dias de repouso necessários: ");
        int dias = 0;
        try {
            dias = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {}

        facade.gerarAtestado(cpf, dias, motivo);
    }

    private static void gerarEncaminhamento() {
        System.out.println("\n--- Emitir Encaminhamento ---");
        System.out.print("CPF do Paciente: ");
        String cpf = scanner.nextLine();
        System.out.print("Especialidade destino (ex: Ortodontista, Radiologista): ");
        String esp = scanner.nextLine();
        System.out.print("Motivo do encaminhamento: ");
        String mov = scanner.nextLine();

        facade.gerarEncaminhamento(cpf, esp, mov);
    }

    private static void gerarReceituario() {
        System.out.println("\n--- Prescrição Inteligente ---");
        System.out.print("CPF do Paciente: ");
        String cpf = scanner.nextLine();
        
        Receituario receita = facade.iniciarReceituario(cpf);
        if (receita == null) {
            System.out.println("Paciente não encontrado.");
            return;
        }
        
        System.out.println("Medicamentos pré-definidos disponíveis:");
        List<Medicamento> meds = facade.listarMedicamentosPreDefinidos();
        for (Medicamento m : meds) {
            System.out.println("- " + m.getNome());
        }
        
        boolean prescrevendo = true;
        while (prescrevendo) {
            System.out.print("\nNome do Medicamento (ou 'Sair' para finalizar recebimento): ");
            String med = scanner.nextLine();
            
            if (med.equalsIgnoreCase("Sair") || med.isEmpty()) {
                prescrevendo = false;
                break;
            }
            
            System.out.print("Posologia (ex: 1 comprimido de 8/8h): ");
            String poso = scanner.nextLine();
            System.out.print("Duração do tratamento (ex: 5 dias): ");
            String dur = scanner.nextLine();
            
            facade.adicionarMedicamentoNoReceituario(receita, med, poso, dur);
            System.out.println("Medicamento adicionado à receita.");
        }
        
        // Finaliza gerando PDF
        facade.finalizarReceituario(receita);
    }
}
