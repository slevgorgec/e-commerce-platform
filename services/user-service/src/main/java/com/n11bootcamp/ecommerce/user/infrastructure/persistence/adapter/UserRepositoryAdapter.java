package com.n11bootcamp.ecommerce.user.infrastructure.persistence.adapter;

import com.n11bootcamp.ecommerce.user.application.port.out.UserRepositoryPort;
import com.n11bootcamp.ecommerce.user.domain.model.User;
import com.n11bootcamp.ecommerce.user.domain.model.UserRole;
import com.n11bootcamp.ecommerce.user.infrastructure.persistence.entity.UserEntity;
import com.n11bootcamp.ecommerce.user.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository jpaRepository;

    @Override
    public User save(User user) {
        return toModel(jpaRepository.save(toEntity(user)));
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toModel);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toModel);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    private UserEntity toEntity(User user) {
        var entity = new UserEntity();
        entity.setId(user.id());
        entity.setEmail(user.email());
        entity.setPasswordHash(user.passwordHash());
        entity.setFirstName(user.firstName());
        entity.setLastName(user.lastName());
        entity.setRole(user.role().name());
        entity.setCreatedAt(user.createdAt());
        entity.setUpdatedAt(user.updatedAt());
        return entity;
    }

    private User toModel(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getFirstName(),
                entity.getLastName(),
                UserRole.valueOf(entity.getRole()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
