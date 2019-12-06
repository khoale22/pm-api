package com.heb.pm.core.repository;

import com.heb.pm.dao.core.entity.PhItemClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for PhItemClass entities.
 *
 * @author d116773
 * @since 1.0.0
 */
@Repository
public interface PhItemClassRepository extends JpaRepository<PhItemClass, Long> {

	/**
	 * Finds a list of PhItemClasses for a given department and sub-department.
	 *
	 * @param departmentId The ID of the department to look for.
	 * @param subDepartmentId The ID of the sub-department to look for.
	 * @return A list of PhItemClasses tied to that department and sub-department.
	 */
	List<PhItemClass> findByDepartmentIdAndSubDepartmentId(Long departmentId, String subDepartmentId);
}
