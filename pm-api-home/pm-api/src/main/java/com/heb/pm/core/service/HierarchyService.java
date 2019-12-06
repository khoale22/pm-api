package com.heb.pm.core.service;

import com.heb.pm.dao.core.entity.*;
import com.heb.pm.core.model.*;
import com.heb.pm.core.repository.PhDepartmentRepository;
import com.heb.pm.core.repository.PhItemClassRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Contains business logic related to the merchant hierarchy.
 *
 * @author d116773
 * @since 1.0.0
 */
@Service
public class HierarchyService {

	private static final Logger logger = LoggerFactory.getLogger(HierarchyService.class);

	public static final String LEVEL_ALL = "ALL";
	public static final String LEVEL_DEPARTMENT = "DEPARTMENT";
	public static final String LEVEL_SUB_DEPARTMENT = "SUB_DEPARTMENT";
	public static final String LEVEL_CLASS = "CLASS";
	public static final String LEVEL_COMMODITY = "COMMODITY";

	private static final String SUB_DEPARTMENT_NAME_FORMAT = "%s%s";

	@Autowired
	private transient PhDepartmentRepository phDepartmentRepository;

	@Autowired
	private transient PhItemClassRepository phItemClassRepository;

	/**
	 * Returns a graph of the merchant hierarchy.
	 *
	 * @param toLevel The level at which to stop returning data.
	 * @return A graph of the merchant hierarchy.
	 */
	public List<Department> getFullHierarchy(String toLevel) {

		return this.phDepartmentRepository.findAll().stream()
				.map((d) -> this.phDepartmentToPopulatedDepartment(d, toLevel)).collect(Collectors.toList());
	}

	/**
	 * Converts a PhDepartment entity to a Department model object without the lower levels of the hierarchy.
	 *
	 * @param phDepartment The PhDepartment to convert.
	 * @return The converted Department.
	 */
	private Department phDepartmentToSkinnyDepartment(PhDepartment phDepartment) {
		return Department.of().setDepartmentId(phDepartment.getKey().getDepartment())
				.setDescription(phDepartment.getName());
	}

	/**
	 * Converts a PhDepartment entity to a Department model including the lower levels of the hierarchy.
	 *
	 * @param phDepartment The PhDepartment to convert.
	 * @param toLevel The level at which to stop pulling the hierarchy.
	 * @return The converted Department.
	 */
	private Department phDepartmentToPopulatedDepartment(PhDepartment phDepartment, String toLevel) {

		if (Objects.isNull(phDepartment)) {
			return null;
		}

		Department d = this.phDepartmentToSkinnyDepartment(phDepartment);
		if (!LEVEL_DEPARTMENT.equals(toLevel)) {
			d.setSubDepartments(phDepartment.getSubDepartments().stream()
					.map((s) -> this.phSubDepartmentToSubDepartment(s, toLevel)).collect(Collectors.toList()));
		}
		return d;
	}

	/**
	 * Converts a PhSubDepartment entity to a SubDepartment model object without the lower levels of the hierarchy.
	 *
	 * @param phSubDepartment The PhSubDepartment to convert.
	 * @return The converted SubDepartment.
	 */
	private SubDepartment phSubDepartmentToSkinnySubDepartment(PhSubDepartment phSubDepartment) {

		return SubDepartment.of()
				.setSubDepartmentId(String.format(SUB_DEPARTMENT_NAME_FORMAT, phSubDepartment.getKey().getDepartment(),
						phSubDepartment.getKey().getSubDepartment())).setDescription(phSubDepartment.getName());
	}

	/**
	 * Converts a PhSubDepartment entity to a Department model including the lower levels of the hierarchy.
	 *
	 * @param phSubDepartment The PhSubDepartment to convert.
	 * @param toLevel The level at which to stop pulling the hierarchy.
	 * @return The converted SubDepartment.
	 */
	private SubDepartment phSubDepartmentToSubDepartment(PhSubDepartment phSubDepartment, String toLevel) {

		if (Objects.isNull(phSubDepartment)) {
			return null;
		}

		SubDepartment subDepartment = this.phSubDepartmentToSkinnySubDepartment(phSubDepartment);

		if (!LEVEL_SUB_DEPARTMENT.equals(toLevel)) {
			try {
				Long departmentId = Long.valueOf(phSubDepartment.getKey().getDepartment());
				List<ItemClass> classes =
						this.phItemClassRepository.findByDepartmentIdAndSubDepartmentId(departmentId, phSubDepartment.getKey().getSubDepartment())
								.stream().map((c) -> this.phItemClassToItemClass(c, toLevel)).collect(Collectors.toList());
				subDepartment.setClasses(classes);
			} catch (NumberFormatException e) {
				logger.trace(String.format("Department %s cannot be converted to a number.", phSubDepartment.getKey().getDepartment()));
			}
		}

		return subDepartment;
	}

	/**
	 * Converts a PhItemClass entity to a ItemClass model object without the lower levels of the hierarchy.
	 *
	 * @param phItemClass The PhItemClass to convert.
	 * @return The converted ItemClass.
	 */
	private ItemClass phItemClassToSkinnyItemClass(PhItemClass phItemClass) {

		return ItemClass.of().setItemClassId(phItemClass.getItemClassCode())
				.setDescription(phItemClass.getItemClassDescription());
	}

	/**
	 * Converts a PhItemClass entity to a ItemClass model including the lower levels of the hierarchy.
	 *
	 * @param phItemClass The PhItemClass to convert.
	 * @param toLevel The level at which to stop pulling the hierarchy.
	 * @return The converted ItemClass.
	 */
	private ItemClass phItemClassToItemClass(PhItemClass phItemClass, String toLevel) {

		if (Objects.isNull(phItemClass)) {
			return null;
		}

		ItemClass itemClass = this.phItemClassToSkinnyItemClass(phItemClass);

		if (!LEVEL_CLASS.equals(toLevel)) {
			itemClass.setCommodities(phItemClass.getCommodityList().stream()
					.map((c) -> this.phCommodityToCommodity(c, toLevel)).collect(Collectors.toList()));
		}
		return itemClass;
	}

	/**
	 * Converts a PhCommodity entity to a Commodity model optionally including the lower levels of the hierarchy.
	 *
	 * @param phCommodity The PhCommodity to convert.
	 * @param toLevel The level at which to stop pulling the hierarchy.
	 * @return The converted Commodity.
	 */
	private Commodity phCommodityToCommodity(PhCommodity phCommodity, String toLevel) {

		Commodity c =  Commodity.of().setCommodityId(phCommodity.getKey().getCommodityCode())
				.setDescription(phCommodity.getName()).setBdm(this.phBdmToBdm(phCommodity.getBdm()));
		if (!LEVEL_COMMODITY.equals(toLevel)) {
			c.setSubCommodities(phCommodity.getSubCommodities().stream()
					.map(this::phSubCommodityToSubCommodity).collect(Collectors.toList()));
		}
		return c;
	}

	/**
	 * Converts a PhSubCommodity entity to a SubCommodity model.
	 *
	 * @param phSubCommodity The PhSubCommodity to convert.
	 * @return The converted SubCommodity.
	 */
	private SubCommodity phSubCommodityToSubCommodity(PhSubCommodity phSubCommodity) {

		return SubCommodity.of().setSubCommodityId(phSubCommodity.getKey().getSubCommodityCode())
				.setDescription(phSubCommodity.getName())
				.setCategory(this.phCategoryToCategory(phSubCommodity.getCategory()));
	}

	/**
	 * Converts a PhCategory entity to a Category model.
	 *
	 * @param phCategory The PhCategory to convert.
	 * @return The converted Category.
	 */
	private Category phCategoryToCategory(PhCategory phCategory) {

		if (Objects.isNull(phCategory)) {
			return null;
		}
		
		Category c = Category.of().setCategoryId(phCategory.getProductCategoryId());
		if (Objects.nonNull(phCategory.getProductCategoryName())) {
			c.setDescription(phCategory.getProductCategoryName().trim());
		}

		return c;
	}

	/**
	 * Converts a PhBdm entity to a Bdm model.
	 *
	 * @param phBdm The PhBdm to convert.
	 * @return The converted Bdm.
	 */
	private Bdm phBdmToBdm(PhBdm phBdm) {
		if (Objects.isNull(phBdm)) {
			return null;
		}
		return Bdm.of().setBdmId(phBdm.getBdmCode()).setFirstName(phBdm.getFirstName())
				.setLastName(phBdm.getLastName()).setFullName(phBdm.getFullName()).setOnePassId(phBdm.getUserId());
	}
}
