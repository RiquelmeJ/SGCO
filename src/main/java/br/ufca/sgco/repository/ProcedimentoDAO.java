package br.ufca.sgco.repository;

import br.ufca.sgco.model.Procedimento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProcedimentoDAO {
    private Connection conn;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public ProcedimentoDAO() {
        conn = DatabaseConnector.getInstance().getConnection();
    }

    public void salvar(Procedimento p, String cpfPaciente) {
        String sqlBuscaPacienteId = "SELECT id FROM pacientes WHERE cpf = ?";
        String sqlInsert = "INSERT INTO procedimentos (paciente_id, data, tipo, observacoes, formaPagamento, valor) VALUES (?, ?, ?, ?, ?, ?)";
        
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
                    stmtInsert.executeUpdate();
                    System.out.println("Procedimento salvo com sucesso.");
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
                            rs.getDouble("valor")
                    );
                    procedimentos.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar procedimentos: " + e.getMessage());
        }
        return procedimentos;
    }
}
