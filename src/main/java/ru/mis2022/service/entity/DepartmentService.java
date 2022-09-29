package ru.mis2022.service.entity;

import ru.mis2022.models.entity.Department;


public interface DepartmentService {

    Department save(Department department);

    Department findDepartmentById(Long id);

    boolean isExistById(Long departmentId);

    void deleteAll();
}
