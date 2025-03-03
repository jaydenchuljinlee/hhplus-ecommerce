#!/bin/bash

# 스크립트 실행 시 첫 번째 인자로 파일 이름 받기
SCRIPT_NAME=$1

# 현재 디렉토리에서 K6 실행
if [ -z "$SCRIPT_NAME" ]; then
  echo "❌ 오류: 실행할 K6 스크립트 파일명을 입력하세요."
  echo "사용법: ./run-k6.sh script.js"
  exit 1
fi

# 실행할 스크립트가 존재하는지 확인
if [ ! -f "$SCRIPT_NAME" ]; then
  echo "❌ 오류: 파일 '$SCRIPT_NAME'을 찾을 수 없습니다."
  exit 1
fi

echo "🚀 K6 스크립트 실행 중: $SCRIPT_NAME"
k6 run "$SCRIPT_NAME"
