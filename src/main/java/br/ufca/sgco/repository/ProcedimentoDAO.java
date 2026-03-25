package br.ufca.sgco.repository;

import br.ufca.sgco.model.Procedimento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcedimentoDAO {
    private Connection conn;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public ProcedimentoDAO() {
        conn = DatabaseConnector.getInstance().getConnection();
    }

    public void salvar(Procedimento p, String cpfPaciente) {
        String sqlBuscaPacienteId = "SELECT id FROM pacientes WHERE cpf = ?";
        String sqlInsert = "INSERT INTO procedimentos (paciente_id, data, tipo, observacoes, formaPagamento, valor, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmtBusca = conn.prepareStatement(sqlBuscaPacienteId)) {
            stmtBusca.setString(1, cpfPaciente);
            ResultSet rs = stmtBusca.executeQuery();
            if (rs.next()) {
                int pacienteId = rs.getInt("id");
                try (PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert)) {
                    stmtInsert.setInt(1, pacienteId);
                    stmtInsert.setString(2, p.getData() != null ? sdf.format(p.getData()) : null);
                    stmtInsert.setString(3, p.getTipo());
                    stmtInsert.setString(4, p.getObservacoes());
                    stmtInsert.setString(5, p.getFormaPagamento());
                    stmtInsert.setDouble(6, p.getValor());
                    stmtInsert.setString(7, p.getStatus() != null ? p.getStatus() : "Agendado");
                    stmtInsert.executeUpdate();
                    System.out.println("Procedimento saved successfully.");
                }
            } else {
                System.err.println("Paciente não encontrado para associar o procedimento.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar procedimento: " + e.getMessage());
        }
    }

    public List<Procedimento> listarPorPacienteCpf(String cpfPaciente) {
        List<Procedimento> procedimentos = new ArrayList<>();
        String sql = "SELECT p.* FROM procedimentos p JOIN pacientes pa ON p.paciente_id = pa.id WHERE pa.cpf = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpfPaciente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Date dt = null;
                    try {
                        String dtStr = rs.getString("data");
                        if (dtStr != null) dt = sdf.parse(dtStr);
                    } catch (Exception e) {}
                    
                    Procedimento p = new Procedimento(
                            dt,
                            rs.getString("tipo"),
                            rs.getString("observacoes"),
                            rs.getString("formaPagamento"),
                            rs.getDouble("valor"),
                            rs.getString("status")
                    );
                    procedimentos.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar procedimentos: " + e.getMessage());
        }
        return procedimentos;
    }

    public List<Procedimento> listarTodos() {
        List<Procedimento> procedimentos = new ArrayList<>();
        String sql = "SELECT * FROM procedimentos ORDER BY data DESC LIMIT 20";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Date dt = null;
                    try {
                        String dtStr = rs.getString("data");
                        if (dtStr != null) dt = sdf.parse(dtStr);
                    } catch (Exception e) {}
                    Procedimento p = new Procedimento(dt, rs.getString("tipo"), rs.getString("observacoes"),
                            rs.getString("formaPagamento"), rs.getDouble("valor"), rs.getString("status"));
                    procedimentos.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar todos os procedimentos: " + e.getMessage());
        }
        return procedimentos;
    }

    public List<Map<String, Object>> listarTodosComPacientes() {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT p.*, pa.nome as paciente_nome, pa.cpf as paciente_cpf " +
                     "FROM procedimentos p " +
                     "JOIN pacientes pa ON p.paciente_id = pa.id " +
                     "ORDER BY p.data DESC LIMIT 20";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", rs.getInt("id"));
                    map.put("tipo", rs.getString("tipo"));
                    map.put("data", rs.getString("data"));
                    map.put("valor", rs.getDouble("valor"));
                    map.put("status", rs.getString("status"));
                    map.put("formaPagamento", rs.getString("formaPagamento"));
                    map.put("pacienteNome", rs.getString("paciente_nome"));
                    map.put("pacienteCpf", rs.getString("paciente_cpf"));
                    result.add(map);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar todos os procedimentos com pacientes: " + e.getMessage());
        }
        return result;
    }

    public void atualizarStatus(int id, String novoStatus) {
        String sql = "UPDATE procedimentos SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, novoStatus);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar status do procedimento: " + e.getMessage());
        }
    }
}
