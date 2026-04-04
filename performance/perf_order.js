import http from 'k6/http';
import { check } from 'k6';

export let options = {
    scenarios: {
        order_stress: {
            executor: 'ramping-vus',
            startVUs: 10,
            stages: [
                { duration: '30s', target: 100 },
                { duration: '30s', target: 200 },
                { duration: '30s', target: 500 },
            ],
        },
    },
};

const BASE_URL = 'http://localhost:8080/api/v1';

export default function () {
    // 주문 API 호출
    let payload = JSON.stringify({
        userId: Math.floor(Math.random() * 5) + 1, // 1~5 랜덤 사용자 ID
        details: [
            {
                productId: 1, // 고정 상품 ID
                quantity: 1,
                price: 50000,
            }
        ]
    });

    let headers = { 'Content-Type': 'application/json' };

    let res = http.post(`${BASE_URL}/order`, payload, { headers });
    check(res, {
        'Order status is 200': (r) => r.status === 200,
        'Order response time < 500ms': (r) => r.timings.duration < 500,
    });

}
