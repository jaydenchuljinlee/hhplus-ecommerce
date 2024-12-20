import http from 'k6/http';
import { sleep, check } from 'k6';

export let options = {
    stages: [
        { duration: '30s', target: 500 },  // 1분 동안 500명의 VU로 증가
    ],
};

const BASE_URL = 'http://localhost:8080';

export default function () {
    // DB 조회 엔드포인트
    let dbRes = http.get(`${BASE_URL}/product/db?productId=1`);
    check(dbRes, {
        'DB status is 200': (r) => r.status === 200,
        'DB response time < 500ms': (r) => r.timings.duration < 500,
    });

    sleep(1); // 1초 간격으로 반복
}
