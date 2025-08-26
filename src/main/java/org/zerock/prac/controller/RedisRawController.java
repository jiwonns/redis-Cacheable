package org.zerock.prac.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.prac.entity.User;
import org.zerock.prac.service.UserService;

import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/redis")
public class RedisRawController {

    // 문자열 KV 전용 (Config에서 StringRedisSerializer 설정)
    private final RedisTemplate<String, String> stringRedisTemplate;

    // 단순 KV 저장
    @PostMapping("/kv")
    public ResponseEntity<Void> putKv() {
        ValueOperations<String, String> vop = stringRedisTemplate.opsForValue();
        vop.set("yellow", "banana");
        vop.set("red", "apple");
        vop.set("green", "watermelon");
        return new ResponseEntity<>(HttpStatus.CREATED);
        // GET /redis/kv/yellow -> "banana"
    }

    // 단순 KV 조회
    @GetMapping("/kv/{key}")
    public ResponseEntity<String> getKv(@PathVariable String key) {
        return ResponseEntity.ok(stringRedisTemplate.opsForValue().get(key));
    }

    // (예시) TTL 있는 값 저장
    @PostMapping("/kv/{key}")
    public ResponseEntity<Void> putKvWithTtl(@PathVariable String key, @RequestBody String value) {
        stringRedisTemplate.opsForValue().set(key, value, 600, TimeUnit.SECONDS);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    // (예시) 분산락 시도
    @PostMapping("/lock/{resourceId}")
    public ResponseEntity<String> acquireLock(@PathVariable String resourceId) {
        String lockKey = "lock:" + resourceId;
        Boolean ok = stringRedisTemplate.opsForValue()
                .setIfAbsent(lockKey, "locked", 10, TimeUnit.SECONDS); // 10초 TTL
        return ok != null && ok
                ? ResponseEntity.ok("LOCKED")
                : ResponseEntity.status(423).body("ALREADY_LOCKED"); // 423 Locked
    }

    // 락 해제
    @DeleteMapping("/lock/{resourceId}")
    public ResponseEntity<Void> releaseLock(@PathVariable String resourceId) {
        stringRedisTemplate.delete("lock:" + resourceId);
        return ResponseEntity.noContent().build();
    }
}
