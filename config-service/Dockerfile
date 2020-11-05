FROM openjdk:11

ADD ./build/libs/config-service-1.0-SNAPSHOT.jar /app/
CMD ["java", "-jar", "/app/config-service-1.0-SNAPSHOT.jar"]

#TODO: Think
#HEALTHCHECK --interval=30s --timeout=30s CMD curl -f http://localhost:8081/actuator/health || exit 1

EXPOSE 8081