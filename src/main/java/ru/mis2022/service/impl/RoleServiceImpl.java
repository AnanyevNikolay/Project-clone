package ru.mis2022.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mis2022.models.entity.Role;
import ru.mis2022.repositories.RoleRepository;
import ru.mis2022.service.abstr.RoleService;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public void persist(Role role) {
        roleRepository.save(role);
    }
}
