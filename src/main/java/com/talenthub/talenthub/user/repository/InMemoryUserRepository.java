package com.talenthub.talenthub.user.repository;

import com.talenthub.talenthub.user.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final ConcurrentHashMap<String, User> byEmail = new ConcurrentHashMap<>();
    private final AtomicLong ids = new AtomicLong();

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(ids.incrementAndGet());
        }
        byEmail.put(user.getEmail(), user);
        return user;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(byEmail.get(email.trim().toLowerCase()));
    }

    @Override
    public boolean existsByEmail(String email) {
        return byEmail.containsKey(email.trim().toLowerCase());
    }
}