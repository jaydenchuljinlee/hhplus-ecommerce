# 문서 정리 계획

## 현재 문제점
- 진입점 부재: 처음 온 사람이 어디서부터 읽어야 할지 모름
- 분류 기준 불명확: 하위 폴더가 주제별로 흩어져 있고, 루트에도 문서가 섞여 있음
- 중복/겹침: concurrency/, lock/, perf/, mornitoring/ 등 관련 주제가 분산
- 프로젝트 진행 기록과 기술 문서 혼재

## 목표 폴더 구조
```
docs/
├── README.md              ← 문서 허브 (신규)
├── architecture/          ← 설계 문서 통합
│   ├── ERD.md
│   ├── SEQUANCE_DIAGRAM.md
│   ├── API_SPEC.md
│   └── event.md
├── concurrency/           ← 동시성 관련 통합
│   ├── order.md
│   ├── payment.md
│   └── LOCK_REPORT.md
├── performance/           ← 성능 관련 통합
│   ├── cache.md
│   ├── index.md
│   ├── load_test.md
│   └── perf.md
├── infra/                 ← 인프라 관련 통합
│   ├── kafka.md
│   └── monitoring.md
├── planning/              ← 프로젝트 계획/회고
│   ├── MILESTONE.md
│   ├── COMMERCE_IMPROVEMENT_PLAN.md
│   ├── TECHNICAL_IMPROVEMENTS.md
│   └── CHAPTER_REVIEW.md
├── http/                  ← 기존 유지
└── postman/               ← 기존 유지
```

## 작업 체크리스트

### Step 1. `docs/README.md` 문서 허브 생성
- [x] 모든 문서의 목적과 링크를 한 곳에 정리
- [x] "처음 읽을 문서" / "주제별 심화" 섹션 구분
- [x] 추천 읽기 순서 제공

### Step 2. 폴더 구조 재분류
- [x] `docs/architecture/` 생성 및 ERD, 시퀀스 다이어그램, API 스펙, 이벤트 문서 이동
- [x] `docs/lock/LOCK_REPORT.md` → `docs/concurrency/` 로 이동
- [x] `docs/performance/` 생성 및 cache, index, load_test, perf 문서 이동
- [x] `docs/infra/` 생성 및 kafka, monitoring 문서 이동
- [x] `docs/planning/` 생성 및 MILESTONE, 개선계획, 기술개선, 회고 문서 이동
- [x] 이미지 파일을 각 문서와 같은 폴더로 이동
- [x] 문서 내 이미지 경로 일괄 수정

### Step 3. 루트 `README.md` 개선
- [x] "주요 작업 내용" 항목에 docs 문서 링크 추가
- [x] PR 링크와 문서를 함께 참조할 수 있도록 수정

### Step 4. 내용 최신화 점검
- [x] `COMMERCE_IMPROVEMENT_PLAN.md` 완료 상태가 코드와 일치하는지 확인 (모두 ✅ 완료로 정확함)
- [x] `API_SPEC.md`가 현재 컨트롤러와 일치하는지 비교
- [x] 오래된 정보나 더 이상 유효하지 않은 내용 업데이트

### Step 5. 이미지 정리
- [x] 각 폴더 내 `images/` 서브폴더로 이미지 분리
- [x] 문서 내 이미지 경로 최종 수정
