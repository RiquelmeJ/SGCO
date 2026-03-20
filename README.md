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
Importe este projeto no Eclipse, IntelliJ ou VSCode. Por usar arquitetura Maven, as dependências do `sqlite-jdbc` e do `Apache PDFBox` serão carregadas do `pom.xml` e automaticamente geridas.
Execute o `br.ufca.sgco.view.Main` para utilizar a CLI interativa. O banco `sgco.db` será gerado automaticamente.

Se você tem o Maven instalado localmente no terminal, basta rodar:
```bash
mvn compile exec:java -Dexec.mainClass="br.ufca.sgco.view.Main"
```

### Autores
[Davi Santos Alexandrino](https://github.com/davilxn), [Francisco Guilherme Cesario Alcantara](https://github.com/frerp), [Leonardo Pereira Silva](https://github.com/leopsdev), [Pedro Henrique Bezerra Simeão](https://github.com/hnnrik), [Raissa Karoliny da Silva Rodrigues](https://github.com/RaissaKarolliny) e [Riquelme Jatay Ribeiro Scarcela Bezerra](https://github.com/RiquelmeJ).
