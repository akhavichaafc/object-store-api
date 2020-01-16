FROM openjdk:8-jre-slim
RUN useradd -s /bin/bash user
USER user
COPY --chown=644 target/object-store.api-*.jar /object-store-api.jar
EXPOSE 8080
ENTRYPOINT ["java","-XX:+UnlockExperimentalVMOptions","-XX:+UseCGroupMemoryLimitForHeap","-jar","/object-store-api.jar"]
