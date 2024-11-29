#!/bin/bash
docker-compose down

./gradlew clean build -Dspring.profiles.active=dev --no-daemon

docker-compose up -d

k6 run ./performance/perf-product-db.js