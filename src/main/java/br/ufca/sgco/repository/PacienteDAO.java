package br.ufca.sgco.repository;

import br.ufca.sgco.model.Anamnese;
import br.ufca.sgco.model.Paciente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PacienteDAO {
    private Connection conn;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public PacienteDAO() {
        conn = DatabaseConnector.getInstance().getConnection();
    }

    public void salvar(Paciente p) {
        String sql = "INSERT INTO pacientes (nome, cpf, dataNascimento, contato, historico, alergias, observacoes) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getCpf());
            stmt.setString(3, p.getDataNascimento() != null ? sdf.format(p.getDataNascimento()) : null);
            stmt.setString(4, p.getContato());
            
            if (p.getAnamnese() != null) {
                stmt.setString(5, p.getAnamnese().getHistorico());
                stmt.setString(6, p.getAnamnese().getAlergias());
                stmt.setString(7, p.getAnamnese().getObservacoes());
            } else {
                stmt.setString(5, null);
                stmt.setString(6, null);
                stmt.setString(7, null);
            }
            
            stmt.executeUpdate();
            System.out.println("Paciente " + p.getNome() + " salvo com sucesso.");
        } catch (SQLException e) {
            System.err.println("Erro ao salvar paciente: " + e.getMessage());
        }
    }

    public List<Paciente> listarTodos() {
        List<Paciente> pacientes = new ArrayList<>();
        String sql = "SELECT * FROM pacientes";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Date dt = null;
                try {
                    String dtStr = rs.getString("dataNascimento");
                    if (dtStr != null) dt = sdf.parse(dtStr);
                } catch (Exception e) {}
                
                Paciente p = new Paciente(
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        dt,
                        rs.getString("contato")
                );
                
                String hist = rs.getString("historico");
                String aler = rs.getString("alergias");
                String obs = rs.getString("observacoes");
                
                if (hist != null || aler != null || obs != null) {
                    Anamnese anamnese = new Anamnese(hist, aler, obs);
                    p.setAnamnese(anamnese);
                }
                
                pacientes.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar pacientes: " + e.getMessage());
        }
        return pacientes;
    }

    public Paciente buscarPorCpf(String cpf) {
        String sql = "SELECT * FROM pacientes WHERE cpf = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Date dt = null;
                    try {
                        String dtStr = rs.getString("dataNascimento");
                        if (dtStr != null) dt = sdf.parse(dtStr);
                    } catch (Exception e) {}
                    
                    Paciente p = new Paciente(
                            rs.getString("nome"),
                            rs.getString("cpf"),
                            dt,
                            rs.getString("contato")
                    );
                    
                    Anamnese anamnese = new Anamnese(
                            rs.getString("historico"),
                            rs.getString("alergias"),
                            rs.getString("observacoes")
                    );
                    p.setAnamnese(anamnese);
                    return p;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar paciente: " + e.getMessage());
        }
        return null;
    }

    public void atualizar(Paciente p) {
        String sql = "UPDATE pacientes SET nome = ?, dataNascimento = ?, contato = ?, historico = ?, alergias = ?, observacoes = ? WHERE cpf = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, p.getNome());
            stmt.setString(2, p.getDataNascimento() != null ? sdf.format(p.getDataNascimento()) : null);
            stmt.setString(3, p.getContato());
            
            if (p.getAnamnese() != null) {
                stmt.setString(4, p.getAnamnese().getHistorico());
                stmt.setString(5, p.getAnamnese().getAlergias());
                stmt.setString(6, p.getAnamnese().getObservacoes());
            } else {
                stmt.setString(4, null);
                stmt.setString(5, null);
                stmt.setString(6, null);
            }
            stmt.setString(7, p.getCpf());
            
            stmt.executeUpdate();
            System.out.println("Paciente " + p.getNome() + " atualizado com sucesso.");
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar paciente: " + e.getMessage());
        }
    }

    public boolean excluir(String cpf) {
        String sql = "DELETE FROM pacientes WHERE cpf = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Paciente com CPF " + cpf + " excluído com sucesso.");
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir paciente: " + e.getMessage());
            return false;
        }
    }
}
