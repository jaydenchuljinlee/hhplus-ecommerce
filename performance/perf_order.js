import http from 'k6/http';
import { check } from 'k6';
import { Counter, Trend } from 'k6/metrics';

// 커스텀 메트릭
const orderSuccess = new Counter('order_success');
const orderStockOut = new Counter('order_stock_out');
const orderServerError = new Counter('order_server_error');
const orderSuccessDuration = new Trend('order_success_duration');

/**
 * 초당 부하 성능 테스트
 *
 * 데이터 기준:
 * - 상품 1 (가방): 재고 10,000개 (productId=1)
 * - 사용자 1~5: 잔액 각 10,000,000원
 * - 상품 가격: 50,000원
 *
 * 초당 100건 × 90초 = 최대 9,000건 (재고 10,000건 이내)
 */
export let options = {
    scenarios: {
        order_load: {
            executor: 'constant-arrival-rate',
            rate: 100,
            timeUnit: '1s',
            duration: '60s',
            preAllocatedVUs: 100,
            maxVUs: 300,
        },
    },
    thresholds: {
        'order_success_duration': ['p(95)<500'],
        'order_server_error': ['count<10'],
    },
};

const BASE_URL = 'http://localhost:8080/api/v1';

export default function () {
    let payload = JSON.stringify({
        userId: Math.floor(Math.random() * 5) + 1,
        details: [
            {
                productId: 1,
                quantity: 1,
                price: 50000,
            }
        ]
    });

    let headers = { 'Content-Type': 'application/json' };
    let res = http.post(`${BASE_URL}/order`, payload, { headers });

    if (res.status === 200) {
        orderSuccess.add(1);
        orderSuccessDuration.add(res.timings.duration);
    } else if (res.status === 500) {
        // 재고 부족 / 잔액 부족 (RepositoryException → 500)
        orderStockOut.add(1);
    } else {
        orderServerError.add(1);
    }

    check(res, {
        'Order response is valid (200 or stock-out)': (r) => r.status === 200 || r.status === 500,
        'Success response time < 500ms': (r) => r.status !== 200 || r.timings.duration < 500,
    });
}
