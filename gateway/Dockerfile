FROM openjdk:11

ADD ./build/libs/gateway-1.0-SNAPSHOT.jar /app/
CMD ["java", "-jar", "-Dspring.profiles.active=docker", "/app/gateway-1.0-SNAPSHOT.jar"]

#TODO: HEALTHCHECK

EXPOSE 4000