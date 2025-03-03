import http from 'k6/http';
import { sleep, check } from 'k6';
import { Trend } from 'k6/metrics';

// ✅ 성공/실패 개수를 기록하는 Trend 메트릭 생성
let successCount = new Trend('success_count');
let failureCount = new Trend('failure_count');

export let options = {
    scenarios: {
        order_test: {
            executor: 'per-vu-iterations', // ✅ VU마다 지정된 횟수만큼 실행
            vus: 12, // ✅ 2개의 VU만 사용 (즉, 정확히 2개의 요청이 보장됨)
            iterations: 1, // ✅ 각 VU가 1번만 실행
            maxDuration: '1s', // ✅ 실행 제한 시간 (최대 1초 동안 실행)

            // executor: 'constant-arrival-rate',
            // rate: 2, // ✅ 초당 10개의 요청으로 변경
            // timeUnit: '1s', // 요청 간격 (1초 기준)
            // duration: '1s', // 10초 동안 실행
            // preAllocatedVUs: 2, // 사전 할당된 VUs (최소값으로 줄임)
            // maxVUs: 2, // 최대 VUs
        },
    },
};

const BASE_URL = 'http://localhost:8080';

export default function () {
    // 주문 API 호출
    let payload = JSON.stringify({
        userId: Math.floor(Math.random() * 5) + 1, // 1~5 랜덤 사용자 ID
        details: [
            {
                productId: 2, // 고정 상품 ID
                quantity: 1,
                price: 1000,
            }
        ]
    });

    let headers = { 'Content-Type': 'application/json' };

    let res = http.post(`${BASE_URL}/order`, payload, { headers });

    let isSuccess = res.status === 200;

    // ✅ 성공/실패 개수 카운트
    if (isSuccess) {
        successCount.add(1);
    } else {
        failureCount.add(1);
        console.error(`❌ 실패 응답: 상태 코드 ${res.status}, 응답 내용: ${res.body}`);
    }

    check(res, {
        '✅ 성공 응답 (200)': (r) => isSuccess,
        '❌ 실패 응답 (200이 아닌 코드)': (r) => !isSuccess,
    });

    sleep(0.1); // 요청 간 짧은 간격
}
