package org.zerock.prac.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.prac.entity.User;
import org.zerock.prac.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class RedisCacheController {
    private final UserService userService;

    // 캐시 조회: 캐시에 있으면 바로 반환, 없으면 DB 조회 후 캐시에 저장
    @GetMapping("/{uid}")
    public ResponseEntity<User> getUser(@PathVariable Long uid) {
        return ResponseEntity.ok(userService.getUserById(uid));
    }

    // 저장/수정: DB 반영 후 캐시 갱신
    @PostMapping
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.saveUser(user));
    }

    // (선택) 삭제: DB 삭제 + 캐시 제거 (Service에서 @CacheEvict 사용 시)
    @DeleteMapping("/{uid}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long uid) {
        userService.deleteUser(uid);
        return ResponseEntity.noContent().build();
    }

}
