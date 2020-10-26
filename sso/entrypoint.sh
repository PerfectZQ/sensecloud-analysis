#!/usr/bin/env sh

exec java -jar app.jar --server.port=$PORT --spring.profiles.active=$RUN_ENV