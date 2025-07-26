FROM ubuntu:latest AS build

#Atualiza os pacotes e instala o java 17
RUN apt-get update

RUN apt-get install openjdk-17-jdk -y 

#Argumento para usar no build
ARG DB_USER
ARG DB_PASSWORD
ARG DB_HOST
#Copia o projeto para dentro da imagem do ubuntu
COPY . . 

#Instala o maven e gera o .jar do projeto
RUN apt-get install maven -y
RUN mvn clean install

FROM openjdk:17-jdk-slim

#Expõe a porta 8080
EXPOSE 8080

#Copia o .jar do projeto
COPY --from=build /target/finansys-backend-0.0.1-SNAPSHOT.jar app.jar

#Executa o .jar do projeto
ENTRYPOINT [ "java", "-jar", "app.jar" ]