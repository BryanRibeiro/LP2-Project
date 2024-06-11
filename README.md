## Aluno: Bryan Romero Ribeiro
LP-System é um sistema para registro e execução de problemas de programação, desenvolvido como parte de um projeto acadêmico.

# Pré-requisitos
Antes de iniciar, certifique-se de ter instalado em sua máquina:
- [Java Development Kit (JDK) 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [Apache Maven](https://maven.apache.org/download.cgi)
- [Docker](https://docs.docker.com/desktop/install/windows-install/)

# Configuração do Projeto

1. Clone o repositório:
```bash
git clone https://github.com/BryanRibeiro/LP2-Project.git
```

2. Abra o projeto em sua IDE (Recomendo o IntelliJ IDEA).
3. Configure o arquivo `src/main/resources/application.properties` com as credenciais do banco de dados MySQL conforme abaixo:
```bash
quarkus.datasource.db-kind=mysql
quarkus.datasource.username=root
quarkus.datasource.password=aluno
quarkus.datasource.jdbc.url=jdbc:mysql://localhost:3307/problemlp2
quarkus.hibernate-orm.database.generation=update
```
   
4. Execute o comando Maven para construir o projeto:
```bash
mvn clean install
```

5. Inicie a aplicação Quarkus:
```bash
mvn quarkus:dev
```

# Configuração do Banco de Dados

1. Certifique-se de ter o [Docker](https://docs.docker.com/desktop/install/windows-install/) instalado e em execução.
2. No terminal, navegue até a pasta do projeto.
3. Na raiz do projeto, execute o comando a seguir para criar as imagens do contêiner do MySQL:

```bash
docker-compose up --build
```
Isso criará um contêiner do MySQL com as configurações especificadas no arquivo docker-compose.yml.

Esses comandos são usados para acessar o contêiner do MySQL e interagir com o banco de dados:

4. Permite acessar o shell do contêiner MySQL.
```bash
docker exec -it mysql bash
```
5. Em seguida, o comando abaixo é usado para acessar o banco de dados problemlp2 com o usuário root e a senha aluno.
```bash
mysql -uroot -paluno problemlp2
```

# Swagger UI

Acesse a documentação do projeto através da interface do Swagger UI, que permite explorar os endpoints disponíveis, ver os parâmetros e as respostas de cada endpoint, bem como testar cada endpoint diretamente na interface.

Link:
http://localhost:8080/q/swagger-ui

![SwaggerImage](https://github.com/BryanRibeiro/LP2-Project/assets/70216549/596fd9ee-d233-42a4-bdcd-2820e2e6359d)

# Endpoints Disponíveis

Após iniciar o projeto, você poderá acessar os endpoints através do seguinte link: http://localhost:8080/

### Requisição POST (Postman)
- `POST /activity`: Registra um problema na base de dados.
- `POST /tc`: Registra um teste case para o problema.
- `POST /activity/solution`: Submete uma solução para um problema registrado.

### Requisição GET
- `GET /activity`: Retorna todos os problemas registrados na base de dados.
- `GET /tc`: Retorna todos os test cases cadastrados.
- `GET /activity/solution`: Retorna todas as soluções submetidas.

Outras requisições GET especificas: 
- `GET /activity/{problemCode}`: Retorna um problema específico do banco com base no problemCode fornecido.
- `GET /tc/{problem_id}`: Retorna um test case específico cadastrado no sistema com base no ID fornecido.
- `GET /activity/solution/{id}`: Retorna uma solução submetida específica do sistema com base no ID fornecido.

### Requisição DELETE (Postman)
- `DELETE /activity/{problemCode}`: Remove um problema específico do banco com base no problemCode fornecido.
- `DELETE /tc/{testCaseId}`: Remove um test case específico cadastrado no sistema com base no ID fornecido.
- `DELETE /activity/solution/{solutionId}`: Remove uma solução submetida específica do sistema com base no ID fornecido.





