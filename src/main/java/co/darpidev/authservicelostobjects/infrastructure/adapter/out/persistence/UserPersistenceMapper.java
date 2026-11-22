package co.darpidev.authservicelostobjects.infrastructure.adapter.out.persistence;

import co.darpidev.authservicelostobjects.domain.model.Email;
import co.darpidev.authservicelostobjects.domain.model.Role;
import co.darpidev.authservicelostobjects.domain.model.User;
import co.darpidev.authservicelostobjects.domain.model.UserStatus;

/**
 * Data Mapper: aísla la conversión entre el modelo de dominio y el modelo JPA.
 * Si la base de datos contiene un email o rol inválido es corrupción de datos (fallo de
 * infraestructura), por eso aquí sí se permite la excepción de {@code Result#getValue}.
 */
final class UserPersistenceMapper {

    private UserPersistenceMapper() {
    }

    static User toDomain(UserJpaEntity entity) {
        return User.reconstitute(
                entity.getUserId(),
                entity.getName(),
                Email.create(entity.getEmail()).getValue(),
                entity.getPasswordHash(),
                UserStatus.valueOf(entity.getStatus()),
                entity.getRegistrationDate(),
                entity.getCampusId(),
                entity.getUniversityId(),
                Role.create(entity.getRole()).getValue());
    }

    static UserJpaEntity toEntity(User user) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.setUserId(user.getId());
        entity.setName(user.getName());
        entity.setEmail(user.getEmail().value());
        entity.setPasswordHash(user.getPasswordHash());
        entity.setStatus(user.getStatus().name());
        entity.setRegistrationDate(user.getRegistrationDate());
        entity.setCampusId(user.getCampusId());
        entity.setUniversityId(user.getUniversityId());
        entity.setRole(user.getRole().value());
        return entity;
    }
}
