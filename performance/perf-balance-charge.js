import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
    stages: [
        { duration: '1s', target: 1000 }, // 1분 동안 500명의 사용자 도달
    ],
};

export default function () {
    const url = 'http://localhost:8080/balance/charge';

    const payload = JSON.stringify({
        userId: 1,
        amount: Math.floor(Math.random() * 10) + 1, // 1~10 사이 랜덤 값
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    http.patch(url, payload, params);

    sleep(1); // optional, 실제 환경에서 요청 간격을 두기 위해 추가 가능
}
