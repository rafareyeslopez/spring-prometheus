FROM openjdk:13-alpine
VOLUME /tmp
ADD target/spring-batch-mysql-0.0.1-SNAPSHOT.jar spring-batch-mysql.jar
EXPOSE 8080
CMD ["java","-Djava.security.egd=file:/dev/./urandom","-jar","spring-batch-mysql.jar"]
