FROM openjdk:11

ADD ./build/libs/debts-service-1.0-SNAPSHOT.jar /app/
CMD ["java", "-jar", "-Dspring.profiles.active=docker", "/app/debts-service-1.0-SNAPSHOT.jar"]

#TODO: HEALTHCHECK

EXPOSE 8086