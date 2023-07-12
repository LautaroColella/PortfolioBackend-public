FROM amazoncorretto:17-alpine-jdk

WORKDIR /app

COPY target/portfolio-0.0.2.jar /app/app.jar

ENV DB_URL=${DB_URL}
ENV DB_USER=${DB_USER}
ENV DB_PASS=${DB_PASS}
ENV CORS_URL=${CORS_URL}

ENTRYPOINT ["java", "-jar", "app.jar"]
