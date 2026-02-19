package org.spring.steganography.Mapper;

import org.spring.steganography.Model.User;

import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {

    public static Set<String> mapRoles(User user){
        return user.getRole()
                .stream()
                .map(Enum::name)
                .collect(Collectors.toSet());
    }

}
