package br.ufca.sgco.repository;

import br.ufca.sgco.model.Medicamento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MedicamentoDAO {
    private Connection conn;

    public MedicamentoDAO() {
        conn = DatabaseConnector.getInstance().getConnection();
        inicializarBase();
    }
    
    // Injeta medicamentos base para Prescrição Inteligente se a tabela estiver vazia
    private void inicializarBase() {
        if (listarTodos().isEmpty()) {
            salvar(new Medicamento("Amoxicilina 500mg"));
            salvar(new Medicamento("Ibuprofeno 400mg"));
            salvar(new Medicamento("Dipirona 500mg"));
            salvar(new Medicamento("Paracetamol 750mg"));
        }
    }

    public void salvar(Medicamento m) {
        String sql = "INSERT OR IGNORE INTO medicamentos (nome) VALUES (?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, m.getNome());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao salvar medicamento: " + e.getMessage());
        }
    }

    public List<Medicamento> listarTodos() {
        List<Medicamento> medicamentos = new ArrayList<>();
        String sql = "SELECT nome FROM medicamentos";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                medicamentos.add(new Medicamento(rs.getString("nome")));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar medicamentos: " + e.getMessage());
        }
        return medicamentos;
    }

    public Medicamento buscarPorNome(String nome) {
        String sql = "SELECT nome FROM medicamentos WHERE nome LIKE ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + nome + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Medicamento(rs.getString("nome"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar medicamento: " + e.getMessage());
        }
        return null;
    }
}
