package org.zerock.prac.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.zerock.prac.entity.User;
import org.zerock.prac.repository.UserRepository;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserServiceManual {
    private static final String KEY_PREFIX = "users:";
    private final UserRepository userRepository;
    private final RedisTemplate<String, User> userRedisTemplate;

    public User getUserById(Long uid) {
        String key = KEY_PREFIX + uid;

        // 1) 캐시 조회
        User cached = userRedisTemplate.opsForValue().get(key);
        if (cached != null) return cached;

        // 2) DB 조회
        User user = userRepository.findById(uid).orElseThrow();

        // 3) 캐시에 저장 (TTL 10분)
        userRedisTemplate.opsForValue().set(key, user, 10, TimeUnit.MINUTES);
        return user;
    }

    public User saveUser(User user) {
        User saved = userRepository.save(user);
        String key = KEY_PREFIX + saved.getUid();
        // DB 정합성 보장 후 캐시 갱신
        userRedisTemplate.opsForValue().set(key, saved, 10, TimeUnit.MINUTES);
        return saved;
    }

    public void deleteUser(Long uid) {
        userRepository.deleteById(uid);
        userRedisTemplate.delete(KEY_PREFIX + uid);
    }
}
