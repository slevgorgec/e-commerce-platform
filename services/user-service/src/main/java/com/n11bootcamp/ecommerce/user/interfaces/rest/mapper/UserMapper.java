package com.n11bootcamp.ecommerce.user.interfaces.rest.mapper;

import com.n11bootcamp.ecommerce.user.application.dto.RegisterCommand;
import com.n11bootcamp.ecommerce.user.application.dto.UpdateProfileCommand;
import com.n11bootcamp.ecommerce.user.domain.model.User;
import com.n11bootcamp.ecommerce.user.interfaces.rest.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    RegisterCommand toCommand(RegisterRequest request);

    UpdateProfileCommand toCommand(UpdateProfileRequest request);

    @Mapping(target = "role", expression = "java(user.role().name())")
    UserProfileResponse toResponse(User user);

    default AuthResponse toAuthResponse(com.n11bootcamp.ecommerce.user.application.dto.AuthTokensDto dto) {
        return AuthResponse.of(dto.accessToken(), dto.refreshToken(), dto.accessTokenExpiresIn());
    }
}
