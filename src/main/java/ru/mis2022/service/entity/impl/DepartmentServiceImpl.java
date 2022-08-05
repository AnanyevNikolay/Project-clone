package ru.mis2022.service.entity.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mis2022.models.entity.Department;
import ru.mis2022.models.entity.MedicalOrganization;
import ru.mis2022.repositories.DepartmentRepository;
import ru.mis2022.service.entity.DepartmentService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    public Department persist(Department department) {
        return departmentRepository.save(department);
    }

    @Override
    public List<Department> findAllByMedicalOrganization_Id(Long id) {
        return departmentRepository.findAllByMedicalOrganization_Id(id);
    }

    @Override
    public Department existById(Long id) {
        return departmentRepository.existById(id);
    }


}

