#!/usr/bin/env sh

JAAS=/app/jaas-prod.conf
exec java -jar app.jar --spring.profiles.active=$RUN_ENV