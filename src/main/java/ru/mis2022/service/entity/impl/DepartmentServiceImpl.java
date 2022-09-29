package ru.mis2022.service.entity.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mis2022.models.entity.Department;
import ru.mis2022.repositories.DepartmentRepository;
import ru.mis2022.service.entity.DepartmentService;


@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    public Department save(Department department) {
        return departmentRepository.save(department);
    }

    @Override
    public boolean isExistById(Long departmentId) {
        return departmentRepository.existsById(departmentId);
    }

    @Override
    public void deleteAll() {
        departmentRepository.deleteAll();
    }

    @Override
    public Department findDepartmentById(Long id) {
        return departmentRepository.findDepartmentById(id);
    }
}
