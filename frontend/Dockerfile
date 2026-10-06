FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml .
COPY src ./src
RUN mvn -q package

# Tomcat 10.1 = Jakarta EE 10 (Servlets 6 + JSP 3.1), igual que en NetBeans
FROM tomcat:10.1-jdk21-temurin
RUN rm -rf /usr/local/tomcat/webapps/*

# Como ROOT.war la app queda en http://localhost:8081/ (sin /sixt-frontend)
COPY --from=build /workspace/target/sixt-frontend.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
