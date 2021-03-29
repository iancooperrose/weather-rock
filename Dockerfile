FROM adoptopenjdk/openjdk11:ubi
RUN mkdir /opt/app
COPY build/libs/weather-rock.jar /opt/app/
CMD ["java", "-jar", "/opt/app/weather-rock.jar"]