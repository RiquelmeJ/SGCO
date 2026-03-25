package br.ufca.sgco.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentoDAO {
    private Connection conn;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public DocumentoDAO() {
        conn = DatabaseConnector.getInstance().getConnection();
    }

    public void salvar(String cpfPaciente, String tipo, String referencia) {
        String sqlBuscaPacienteId = "SELECT id FROM pacientes WHERE cpf = ?";
        String sqlInsert = "INSERT INTO documentos (paciente_id, tipo, data, referencia) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmtBusca = conn.prepareStatement(sqlBuscaPacienteId)) {
            stmtBusca.setString(1, cpfPaciente);
            ResultSet rs = stmtBusca.executeQuery();
            if (rs.next()) {
                int pacienteId = rs.getInt("id");
                try (PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert)) {
                    stmtInsert.setInt(1, pacienteId);
                    stmtInsert.setString(2, tipo);
                    stmtInsert.setString(3, sdf.format(new java.util.Date()));
                    stmtInsert.setString(4, referencia);
                    stmtInsert.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar histórico de documento: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> listarRecentes() {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT d.*, p.nome as paciente_nome, p.cpf as paciente_cpf " +
                     "FROM documentos d " +
                     "JOIN pacientes p ON d.paciente_id = p.id " +
                     "ORDER BY d.data DESC LIMIT 15";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", rs.getInt("id"));
                    map.put("pacienteNome", rs.getString("paciente_nome"));
                    map.put("pacienteCpf", rs.getString("paciente_cpf"));
                    map.put("tipo", rs.getString("tipo"));
                    map.put("data", rs.getString("data"));
                    map.put("referencia", rs.getString("referencia"));
                    result.add(map);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar documentos recentes: " + e.getMessage());
        }
        return result;
    }
}
