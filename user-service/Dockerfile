FROM openjdk:11

ADD ./build/libs/user-service-1.0-SNAPSHOT.jar /app/
CMD ["java", "-jar", "-Dspring.profiles.active=docker", "/app/user-service-1.0-SNAPSHOT.jar"]

#TODO: HEALTHCHECK

EXPOSE 8084