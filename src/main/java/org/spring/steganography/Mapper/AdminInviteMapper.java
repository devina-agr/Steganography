package org.spring.steganography.Mapper;

import org.spring.steganography.DTO.UserDTO.AdminInviteResponse;
import org.spring.steganography.Model.AdminInvite;

public class AdminInviteMapper {

    public static AdminInviteResponse toResponse(AdminInvite adminInvite){
        return new AdminInviteResponse(
                adminInvite.getEmail(),
                adminInvite.getInvitedBy(),
                adminInvite.getInviteToken(),
                adminInvite.getExpiresAt(),
                adminInvite.isUsed(),
                adminInvite.getSentAt()
        );
    }

}
