package com.example.barojava.repository;

import com.example.barojava.entity.User;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> store = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1L);

    @Override
    public User save(User user) {

        Long id = idGenerator.getAndIncrement();
        User savedUser = User.builder()
                .id(id)
                .username(user.getUsername())
                .password(user.getPassword())
                .nickname(user.getNickname())
                .roles(user.getRoles())
                .build();

        store.put(id, savedUser);
        return savedUser;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return store.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public boolean existsByUsername(String username) {
        return store.values().stream()
                .anyMatch(user -> user.getUsername().equals(username));
    }

    @Override
    public void clear() {
        store.clear();
        idGenerator.set(1L);
    }
}
