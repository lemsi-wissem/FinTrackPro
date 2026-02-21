package com.fintrack.web.mapper;

import com.fintrack.domain.model.User;
import com.fintrack.web.dto.response.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserWebMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                user.isVerified(),
                user.getCreatedAt()
        );
    }
}
