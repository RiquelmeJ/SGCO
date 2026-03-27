# Sistema de Gestão para Consultórios Odontológicos (SGCO)

### Visão Geral

O SGCO é uma plataforma de apoio a odontólogos e demais profissionais envolvidos em ambientes de consultórios. 
Desenvolvido em Java, o sistema foca no gerenciamento de pacientes, acervo de procedimentos (linha do tempo de serviços odontológicos efetuados), e geração automática de PDFs reais de Receituários, Atestados e Encaminhamentos.

### Padrões de Projeto (Design Patterns)
Os 6 padrões exigidos foram estritamente aplicados na arquitetura:
1. **Singleton**: `br.ufca.sgco.repository.DatabaseConnector` garante única conexão com o SQLite.
2. **Factory Method**: `br.ufca.sgco.factory.DocumentoFactory` lida com a instanciação dos tipos de Documentos (Atestado, Encaminhamento, Receituário) baseado no input.
3. **Facade**: `br.ufca.sgco.facade.SistemaFacade` simplifica o acesso aos DAOs de banco de dados para a View CLI.
4. **Composite**: `br.ufca.sgco.model.HistoricoProcedimentos` agrupa vários Procedimentos para exibição do acervo do paciente num único nodo.
5. **Strategy**: `br.ufca.sgco.strategy.PagamentoStrategy` para definir táticas diferentes de pagamento na hora do registro do Procedimento (Dinheiro ou Cartão).
6. **Template Method**: A classe abstrata `br.ufca.sgco.model.Documento` define a estrutura da geração e salvamento em PDF através de `gerarPDF()`, delegando os detalhes dinâmicos à implementação `gerarConteudo()` de cada subclasse de documento.

### Histórias de Usuário Implementadas
- Cadastro de Pacientes 
- Linha do Tempo do Paciente 
- Registro de Histórico Medicamentoso 
- Repositório de Receituários 
- Acervo de Procedimentos 
- Gestão de Templates
- Emissão de Atestado Rápido
- Prescrição Inteligente (Receituário)
- Encaminhamento para Especialistas 
- Acervo de Documentos 

### Como executar

#### 1. Versão Web (Interface Gráfica) - Recomendado
Execute a classe `br.ufca.sgco.view.AppServer`. O sistema iniciará um servidor local e servirá a interface moderna.
Acesse pelo navegador: **http://localhost:4567**

Depois disso, vá na pasta de frontend e execute `npm run dev`

Via terminal (Maven):
```bash
mvn compile exec:java -Dexec.mainClass="br.ufca.sgco.view.AppServer"
```

#### 2. Versão CLI (Terminal)
Execute a classe `br.ufca.sgco.view.Main` para utilizar a CLI interativa original.

Via terminal (Maven):
```bash
mvn compile exec:java -Dexec.mainClass="br.ufca.sgco.view.Main"
```

### Autores
[Davi Santos Alexandrino](https://github.com/davilxn), [Francisco Guilherme Cesario Alcantara](https://github.com/frerp), [Leonardo Pereira Silva](https://github.com/leopsdev), [Pedro Henrique Bezerra Simeão](https://github.com/hnnrik), [Raissa Karoliny da Silva Rodrigues](https://github.com/RaissaKarolliny) e [Riquelme Jatay Ribeiro Scarcela Bezerra](https://github.com/RiquelmeJ).
