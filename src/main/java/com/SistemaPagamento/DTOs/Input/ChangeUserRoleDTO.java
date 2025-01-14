package com.SistemaPagamento.DTOs.Input;

import com.SistemaPagamento.Domain.User.UserRoles;

public record ChangeUserRoleDTO(
        UserRoles NewRole,
        Long UserId
) {
}
