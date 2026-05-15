# 1-р шат: Build хийх (Maven болон JDK 17)
# openjdk-ийн оронд eclipse-temurin ашиглана
FROM eclipse-temurin:17-jdk AS build
COPY . .
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# 2-р шат: Ажиллуулах
FROM eclipse-temurin:17-jre
COPY --from=build /target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
