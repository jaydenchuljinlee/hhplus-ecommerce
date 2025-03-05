import http from 'k6/http';
import { sleep, check } from 'k6';

export let options = {
    stages: [
        { duration: '30s', target: 100 }, // 30초 동안 100명의 사용자
        { duration: '1m', target: 500 },  // 1분 동안 500명의 사용자
        { duration: '30s', target: 0 },   // 30초 동안 사용자 종료
    ],
};

const BASE_URL = 'http://localhost:8080';

export default function () {
    // DB 조회 엔드포인트
    let dbRes = http.get(`${BASE_URL}/product/db?productId=1`);
    check(dbRes, {
        'DB status is 200': (r) => r.status === 200,
        'DB response time < 400ms': (r) => r.timings.duration < 400,
    });

    sleep(1); // 1초 간격으로 반복
}