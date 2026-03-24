package br.ufca.sgco.factory;

import br.ufca.sgco.model.Atestado;
import br.ufca.sgco.model.Documento;
import br.ufca.sgco.model.Encaminhamento;
import br.ufca.sgco.model.Paciente;
import br.ufca.sgco.model.Receituario;

import java.util.Date;

public class DocumentoFactory {
    
    public Documento criarDocumento(String tipo, Paciente paciente, Object... args) {
        if (tipo == null) {
            return null;
        }
        
        Date dataAtual = new Date();
        
        switch (tipo.toLowerCase()) {
            case "atestado":
                if (args.length >= 2 && args[0] instanceof Integer && args[1] instanceof String) {
                    return new Atestado(dataAtual, paciente, (Integer) args[0], (String) args[1]);
                }
                break;
            case "encaminhamento":
                if (args.length >= 2 && args[0] instanceof String && args[1] instanceof String) {
                    return new Encaminhamento(dataAtual, paciente, (String) args[0], (String) args[1]);
                }
                break;
            case "receituario":
                return new Receituario(dataAtual, paciente);
            default:
                throw new IllegalArgumentException("Tipo de documento desconhecido: " + tipo);
        }
        throw new IllegalArgumentException("Argumentos inválidos para a criação do documento: " + tipo);
    }
}
