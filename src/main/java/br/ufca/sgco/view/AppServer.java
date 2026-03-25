package br.ufca.sgco.view;

import br.ufca.sgco.facade.SistemaFacade;
import br.ufca.sgco.model.Paciente;
import br.ufca.sgco.model.Anamnese;
import br.ufca.sgco.model.Receituario;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class AppServer {
    private static SistemaFacade facade = new SistemaFacade();
    private static Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    public static void main(String[] args) {
        port(4567);

        String staticFilesPath = findFrontendPath();
        if (staticFilesPath != null) {
            externalStaticFileLocation(staticFilesPath);
        }

        path("/api", () -> {
            
            // --- DASHBOARD & STATS ---
            get("/dashboard/stats", (req, res) -> {
                res.type("application/json");
                Map<String, Object> stats = new HashMap<>();
                List<Paciente> pacientes = facade.listarPacientes();
                List<Map<String, Object>> procedimentos = facade.listarProcedimentosDashboard();
                
                stats.put("totalPacientes", pacientes.size());
                stats.put("totalProcedimentos", procedimentos.size());
                
                // For "consultas hoje", we might need to parse the dates from the maps
                long hojeCount = 0;
                String hojeStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                for (Map<String, Object> p : procedimentos) {
                    String pDate = (String) p.get("data");
                    if (pDate != null && pDate.startsWith(hojeStr)) {
                        hojeCount++;
                    }
                }
                stats.put("consultasHoje", hojeCount);
                
                return gson.toJson(stats);
            });

            // --- PACIENTES ---
            get("/pacientes", (req, res) -> {
                res.type("application/json");
                return gson.toJson(facade.listarPacientes());
            });

            post("/pacientes", (req, res) -> {
                res.type("application/json");
                JsonObject json = gson.fromJson(req.body(), JsonObject.class);
                facade.cadastrarPaciente(
                    json.get("nome").getAsString(),
                    json.get("cpf").getAsString(),
                    json.has("dataNascimento") ? java.sql.Date.valueOf(json.get("dataNascimento").getAsString()) : null,
                    json.has("contato") ? json.get("contato").getAsString() : "",
                    json.has("historico") ? json.get("historico").getAsString() : null,
                    json.has("alergias") ? json.get("alergias").getAsString() : null,
                    json.has("obs") ? json.get("obs").getAsString() : null
                );
                return "{\"status\": \"success\"}";
            });

            put("/pacientes", (req, res) -> {
                res.type("application/json");
                JsonObject json = gson.fromJson(req.body(), JsonObject.class);
                String birthStr = json.has("dataNascimento") ? json.get("dataNascimento").getAsString() : null;
                Date dataNascimento = null;
                if (birthStr != null && !birthStr.isEmpty()) {
                    try {
                        dataNascimento = new SimpleDateFormat("yyyy-MM-dd").parse(birthStr);
                    } catch (Exception e) {}
                }

                Paciente p = new Paciente(
                    json.get("nome").getAsString(),
                    json.get("cpf").getAsString(),
                    dataNascimento,
                    json.get("contato").getAsString()
                );
                p.setAnamnese(new Anamnese(
                    json.get("historico").getAsString(),
                    json.get("alergias").getAsString(),
                    json.get("observacoes").getAsString()
                ));
                facade.atualizarPaciente(p);
                return "{\"status\": \"success\"}";
            });

            delete("/pacientes/:cpf", (req, res) -> {
                res.type("application/json");
                String cpf = req.params(":cpf");
                boolean success = facade.excluirPaciente(cpf);
                if (success) {
                    return "{\"status\": \"success\"}";
                } else {
                    res.status(400);
                    return "{\"status\": \"error\", \"message\": \"Não foi possível excluir o paciente. Verifique se existem procedimentos vinculados.\"}";
                }
            });

            // --- PROCEDIMENTOS ---
            get("/procedimentos", (req, res) -> {
                res.type("application/json");
                return gson.toJson(facade.listarProcedimentosDashboard());
            });

            post("/procedimentos", (req, res) -> {
                res.type("application/json");
                JsonObject json = gson.fromJson(req.body(), JsonObject.class);
                
                String dateStr = json.has("data") ? json.get("data").getAsString() : null;
                Date data = null;
                if (dateStr != null && !dateStr.isEmpty()) {
                    try {
                        data = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
                    } catch (Exception e) {}
                }

                facade.registrarProcedimento(
                    json.get("cpf").getAsString(),
                    json.get("tipo").getAsString(),
                    json.get("observacoes").getAsString(),
                    json.get("valor").getAsDouble(),
                    json.get("formaPagamento").getAsInt(),
                    data,
                    json.has("status") ? json.get("status").getAsString() : "Agendado"
                );
                return "{\"status\": \"success\"}";
            });

            post("/procedimentos/update-status", (req, res) -> {
                res.type("application/json");
                JsonObject json = gson.fromJson(req.body(), JsonObject.class);
                int id = json.get("id").getAsInt();
                String status = json.get("status").getAsString();
                facade.atualizarStatusProcedimento(id, status);
                return "{\"status\": \"success\"}";
            });

            // --- DOCUMENTOS ---
            get("/documentos", (req, res) -> {
                res.type("application/json");
                return gson.toJson(facade.listarDocumentosRecentes());
            });

            post("/documentos/atestado", (req, res) -> {
                res.type("application/json");
                JsonObject json = gson.fromJson(req.body(), JsonObject.class);
                facade.gerarAtestado(
                    json.get("cpf").getAsString(),
                    json.get("dias").getAsInt(),
                    json.get("motivo").getAsString()
                );
                return "{\"status\": \"success\", \"message\": \"Atestado gerado com sucesso\"}";
            });

            post("/documentos/encaminhamento", (req, res) -> {
                res.type("application/json");
                JsonObject json = gson.fromJson(req.body(), JsonObject.class);
                facade.gerarEncaminhamento(
                    json.get("cpf").getAsString(),
                    json.get("especialidade").getAsString(),
                    json.get("motivo").getAsString()
                );
                return "{\"status\": \"success\", \"message\": \"Encaminhamento gerado com sucesso\"}";
            });

            post("/documentos/receituario", (req, res) -> {
                res.type("application/json");
                JsonObject json = gson.fromJson(req.body(), JsonObject.class);
                String cpf = json.get("cpf").getAsString();
                Receituario receita = facade.iniciarReceituario(cpf);
                
                if (receita != null && json.has("itens")) {
                    JsonArray itens = json.getAsJsonArray("itens");
                    for (int i = 0; i < itens.size(); i++) {
                        JsonObject item = itens.get(i).getAsJsonObject();
                        facade.adicionarMedicamentoNoReceituario(
                            receita,
                            item.get("medicamento").getAsString(),
                            item.get("posologia").getAsString(),
                            item.get("duracao").getAsString()
                        );
                    }
                    facade.finalizarReceituario(receita);
                    return "{\"status\": \"success\", \"message\": \"Receituário gerado\"}";
                }
                return "{\"status\": \"error\", \"message\": \"Falha ao gerar receita\"}";
            });
        });

        after((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET,POST");
            response.header("Access-Control-Allow-Headers", "Content-Type");
        });

        System.out.println("Servidor SGCO rodando em: http://localhost:4567");
    }

    private static String findFrontendPath() {
        String userDir = System.getProperty("user.dir");
        File currentDirFrontend = new File(userDir, "frontend");
        if (currentDirFrontend.exists()) return currentDirFrontend.getAbsolutePath();
        File parentDirFrontend = new File(new File(userDir).getParent(), "frontend");
        if (parentDirFrontend.exists()) return parentDirFrontend.getAbsolutePath();
        String absoluteFallback = "f:\\semestre final\\adp\\SGCO\\frontend";
        File fallbackDir = new File(absoluteFallback);
        if (fallbackDir.exists()) return fallbackDir.getAbsolutePath();
        return null;
    }
}
