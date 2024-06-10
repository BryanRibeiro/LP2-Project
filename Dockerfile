# Usar uma imagem base do Maven para construir o projeto
FROM maven:3.8.6-openjdk-17 AS build

# Configurar o diretório de trabalho
WORKDIR /app

# Copiar os arquivos de configuração do Maven
COPY pom.xml .
COPY src ./src

# Compilar o projeto
RUN mvn clean package

# Usar uma imagem base do OpenJDK para rodar o projeto
FROM adoptopenjdk/openjdk17:jre-17.0.2_8-alpine

# Configurar o diretório de trabalho
WORKDIR /app

# Copiar o JAR construído para a imagem
COPY --from=build /app/target/*.jar app.jar

# Expor a porta que a aplicação irá rodar
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
