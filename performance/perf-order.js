import http from 'k6/http';
import { sleep, check } from 'k6';

export let options = {
    scenarios: {
        order_test: {
            executor: 'constant-arrival-rate',
            rate: 100, // 초당 50개의 요청
            timeUnit: '1s', // 요청 간격
            duration: '1m', // 10초 동안 실행
            preAllocatedVUs: 200, // 사전 할당된 VUs
            maxVUs: 500, // 최대 VUs
        },
    },
};

const BASE_URL = 'http://localhost:8080';

export default function () {
    // 주문 API 호출
    let payload = JSON.stringify({
        userId: Math.floor(Math.random() * 5) + 1, // 1~5 랜덤 사용자 ID
        productId: 1, // 고정 상품 ID
        quantity: 1,
        price: 1000,
    });

    let headers = { 'Content-Type': 'application/json' };

    let res = http.post(`${BASE_URL}/order`, payload, { headers });
    check(res, {
        'Order status is 200': (r) => r.status === 200,
        'Order response time < 500ms': (r) => r.timings.duration < 500,
    });

    sleep(0.1); // 요청 간 짧은 간격
}
