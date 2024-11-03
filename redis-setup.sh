#!/bin/bash

# redis:latest 이미지 ID 찾기
REDIS_IMAGE_ID=$(docker images -q redis:latest)

# 이미지가 존재하는지 확인하여 삭제
if [ -n "$REDIS_IMAGE_ID" ]; then
  echo "redis:latest 이미지($REDIS_IMAGE_ID)를 삭제합니다..."

  # 실행 중인 Redis 컨테이너 중지 및 삭제
  echo "실행 중인 Redis 컨테이너를 중지하고 삭제합니다..."
  docker ps -a --filter ancestor=redis:latest --format "{{.ID}}" | xargs -r docker stop
  docker ps -a --filter ancestor=redis:latest --format "{{.ID}}" | xargs -r docker rm

  # redis:latest 이미지 삭제
  docker rmi "$REDIS_IMAGE_ID"

  echo "redis:latest 이미지 및 관련 컨테이너가 성공적으로 삭제되었습니다."
else
  echo "redis:latest 이미지가 존재하지 않습니다. 삭제할 이미지가 없습니다."
fi

docker-compose up -d