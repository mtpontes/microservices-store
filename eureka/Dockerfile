# Use a imagem base do JDK 11 ou 17
FROM openjdk:17-slim

# Defina o diretório de trabalho no container
WORKDIR /app

# Copie o JAR construído para o diretório de trabalho do container
COPY target/eureka-server.jar eureka-server.jar

# Expõe a porta do Eureka Server
EXPOSE 9091

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "eureka-server.jar"]
