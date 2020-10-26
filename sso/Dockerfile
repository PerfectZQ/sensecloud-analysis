FROM registry.sensetime.com/workplatform/openjdk:latest
WORKDIR /app

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone
ADD ./target/*.jar app.jar
ADD ./entrypoint.sh /app
RUN chmod +x /app/entrypoint.sh

ENTRYPOINT ["./entrypoint.sh"]