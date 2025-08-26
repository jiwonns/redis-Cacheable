package org.zerock.prac.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.zerock.prac.entity.User;
import org.zerock.prac.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 캐시 조회 → 없으면 DB 조회 후 Redis 저장
    @Cacheable(value = "users", key = "#uid")
    public User getUserById(Long uid) {
        System.out.println("DB 접근");
        return userRepository.findById(uid).orElseThrow();
    }

    // 저장 + 캐시 업데이트
    @CachePut(value = "users", key = "#user.uid")
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // 3) 삭제: DB 삭제 + 캐시 제거
    @CacheEvict(value = "users", key = "#uid")
    public void deleteUser(Long uid) {
        userRepository.deleteById(uid);
    }
}
