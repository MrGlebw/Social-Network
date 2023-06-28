package com.gleb.mapper;

import com.gleb.data.User.User;
import com.gleb.dto.UserDto;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;


@Mapper(componentModel = "spring")
@Component
public interface UserMapper {

        UserDto map(User user);

        @InheritInverseConfiguration
        User map(UserDto dto);

}
