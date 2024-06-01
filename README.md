# LP2 Project
LP-System é um sistema para registro e execução de problemas de programação, desenvolvido como parte de um projeto acadêmico.

## Pré-requisitos
Antes de iniciar, certifique-se de ter instalado em sua máquina:
- [Java Development Kit (JDK) 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [Apache Maven](https://maven.apache.org/download.cgi)
- [MySQL](https://www.mysql.com/downloads/)

## Configuração do Banco de Dados
1. Crie um banco de dados MySQL chamado `lp_system`.
2. No arquivo `src/main/resources/application.properties`, configure o acesso ao banco de dados conforme abaixo:

```bash
quarkus.datasource.db-kind=mysql
quarkus.datasource.username=seu_usuario_mysql
quarkus.datasource.password=sua_senha_mysql
quarkus.datasource.jdbc.url=jdbc:mysql://localhost:3306/lp_system
quarkus.hibernate-orm.database.generation=update
```

# Passos para rodar o projeto:

1. Clone o repositório do projeto: `git clone https://github.com/BryanRibeiro/LP2-Project.git`
2. Abra o projeto em sua IDE favorita.
3. Certifique-se de ter o MySQL instalado e em execução.
4. Configure o arquivo `application.properties` com as credenciais do banco de dados MySQL.
5. Execute o comando Maven para construir o projeto: `mvn clean install`.
6. Inicie a aplicação Quarkus: `mvn quarkus:dev`.
7. Acesse os endpoints da API conforme necessário.

## Endpoints Disponíveis
- `POST /activity`: Registra um problema na base de dados.
- `POST /tc`: Registra um teste case para o problema.
- `POST /activity/solution`: Submete uma solução para um problema registrado.

# Exemplos de Requisição com Postman:

## Cadastrar um problema (json):
**Método:** POST  
**URL:** `http://localhost:8080/activity`  
**Headers:**  
- Content-Type: application/json  
**Body:**
```json
{
  "filename": "pradinho",
  "problem": "A",
  "lps": "python"
}
```

## Cadastrar um caso de teste (json):
**Método:** POST  
**URL:** `http://localhost:8080/tc`  
**Headers:**  
- Content-Type: application/json  
**Body:**
```json
{
  "problemCode": "A",
  "inputFile": "<arquivo_de_entrada>",
  "expectedOutputFile": "<arquivo_resultado_esperado>"
}
```

## Cadastrar uma solução (json):
**Método:** POST  
**URL:** `http://localhost:8080/activity/solution`  
**Headers:**  
- Content-Type: application/json  
**Body:**
```json
{
  "author": "autor_da_solucao",
  "filename": "nome_do_arquivo.py",
  "problemCode": "A",
  "sourceCode": "código_fonte_da_solucao"
}
```






