# LP2 Project
LP-System é um sistema para registro e execução de problemas de programação, desenvolvido como parte de um projeto acadêmico.

## Pré-requisitos
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

# Endpoints Disponíveis
- `POST /activity`: Registra um problema na base de dados.
- `POST /tc`: Registra um teste case para o problema.
- `POST /activity/solution`: Submete uma solução para um problema registrado.






