package br.ufca.sgco.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnector {
    private static DatabaseConnector instance;
    private Connection connection;
    private final String url = "jdbc:sqlite:sgco.db";

    private DatabaseConnector() {
        try {
            connection = DriverManager.getConnection(url);
            createTables();
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }

    public static DatabaseConnector getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnector.class) {
                if (instance == null) {
                    instance = new DatabaseConnector();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
    
    private void createTables() {
        String sqlPacientes = "CREATE TABLE IF NOT EXISTS pacientes ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " nome TEXT NOT NULL,"
                + " cpf TEXT NOT NULL UNIQUE,"
                + " dataNascimento TEXT,"
                + " contato TEXT,"
                + " historico TEXT,"
                + " alergias TEXT,"
                + " observacoes TEXT"
                + ");";
                
        String sqlProcedimentos = "CREATE TABLE IF NOT EXISTS procedimentos ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " paciente_id INTEGER,"
                + " data TEXT,"
                + " tipo TEXT,"
                + " observacoes TEXT,"
                + " formaPagamento TEXT,"
                + " valor REAL,"
                + " status TEXT DEFAULT 'Agendado',"
                + " FOREIGN KEY(paciente_id) REFERENCES pacientes(id)"
                + ");";
                
        String sqlMedicamentos = "CREATE TABLE IF NOT EXISTS medicamentos ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " nome TEXT NOT NULL UNIQUE"
                + ");";
                
        String sqlDocumentos = "CREATE TABLE IF NOT EXISTS documentos ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " paciente_id INTEGER,"
                + " tipo TEXT,"
                + " data TEXT,"
                + " referencia TEXT,"
                + " FOREIGN KEY(paciente_id) REFERENCES pacientes(id)"
                + ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sqlPacientes);
            stmt.execute(sqlProcedimentos);
            stmt.execute(sqlMedicamentos);
            stmt.execute(sqlDocumentos);
            
            // Migrações simples para versões existentes do banco
            try {
                stmt.execute("ALTER TABLE procedimentos ADD COLUMN status TEXT DEFAULT 'Agendado'");
            } catch (SQLException e1) {
                // Coluna já existe, ignora
            }
            try {
                stmt.execute("ALTER TABLE documentos ADD COLUMN referencia TEXT");
            } catch (SQLException e2) {
                // Coluna já existe, ignora
            }
        } catch (SQLException e) {
            System.err.println("Erro ao criar tabelas: " + e.getMessage());
        }
    }
}
