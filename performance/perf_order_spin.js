import http from 'k6/http';
import { check } from 'k6';
import { Counter, Trend } from 'k6/metrics';

const orderSuccess = new Counter('order_success');
const orderStockOut = new Counter('order_stock_out');
const orderServerError = new Counter('order_server_error');
const orderSuccessDuration = new Trend('order_success_duration');

/**
 * Redisson 스핀락 기반 주문 부하 테스트 (성능 비교용)
 *
 * 비교 대상:
 * - perf_order.js      → POST /api/v1/order       (Lua 원자 스크립트)
 * - perf_order_lock.js → POST /api/v1/order/lock  (Redisson PubSub 분산락)
 * - perf_order_spin.js → POST /api/v1/order/spin  (Redisson 스핀락)
 *
 * PubSub vs Spin 차이:
 * - PubSub: 락 해제 시 Redis pub/sub 알림으로 대기 스레드 깨움 (event-driven)
 * - Spin:   락 획득 실패 시 exponential backoff로 polling 재시도 (busy-wait)
 */
export let options = {
    scenarios: {
        order_spin_load: {
            executor: 'constant-arrival-rate',
            rate: 100,
            timeUnit: '1s',
            duration: '90s',
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
    let res = http.post(`${BASE_URL}/order/spin`, payload, { headers });

    if (res.status === 200) {
        orderSuccess.add(1);
        orderSuccessDuration.add(res.timings.duration);
    } else if (res.status === 500) {
        orderStockOut.add(1);
    } else {
        orderServerError.add(1);
    }

    check(res, {
        'Order response is valid (200 or stock-out)': (r) => r.status === 200 || r.status === 500,
        'Success response time < 500ms': (r) => r.status !== 200 || r.timings.duration < 500,
    });
}
