FROM registry.sensetime.com/workplatform/openjdk:latest
WORKDIR /app

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone
ADD ./web/target/*.jar app.jar
ADD ./entrypoint.sh /app
ADD ./.kube /app
ENV KUBECONFIG=/app/.kube/config
RUN chmod +x /app/entrypoint.sh

ENTRYPOINT ["./entrypoint.sh"]