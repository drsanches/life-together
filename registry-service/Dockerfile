FROM openjdk:11

ADD ./build/libs/registry-service-1.0-SNAPSHOT.jar /app/
CMD ["java", "-jar", "-Dspring.profiles.active=docker", "/app/registry-service-1.0-SNAPSHOT.jar"]

#TODO: Think
#HEALTHCHECK --interval=30s --timeout=30s CMD curl -f http://localhost:8761/actuator/health || exit 1

EXPOSE 8761