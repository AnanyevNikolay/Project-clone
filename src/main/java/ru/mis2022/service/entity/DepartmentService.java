package ru.mis2022.service.entity;

import ru.mis2022.models.entity.Department;


public interface DepartmentService {

    Department save(Department department);

    boolean isExistById(Long departmentId);

    void deleteAll();
}
