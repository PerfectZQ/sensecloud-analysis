#!/usr/bin/env sh

JAAS=/app/jaas-prod.conf
exec java -jar app.jar --server.port=$PORT --spring.profiles.active=$RUN_ENV