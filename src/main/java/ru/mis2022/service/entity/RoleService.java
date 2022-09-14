package ru.mis2022.service.entity;

import ru.mis2022.models.entity.Role;

public interface RoleService {

    Role save(Role role);

    Role findByName(String name);

    void deleteAll();
}
