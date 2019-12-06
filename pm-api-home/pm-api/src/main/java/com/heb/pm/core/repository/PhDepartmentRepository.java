package com.heb.pm.core.repository;

import com.heb.pm.dao.core.entity.PhDepartment;
import com.heb.pm.dao.core.entity.PhSubDepartmentKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository to access financial departments.
 *
 * @author d116773
 * @since 1.0.0
 */
@Repository
public interface PhDepartmentRepository extends JpaRepository<PhDepartment, PhSubDepartmentKey> {
}
