package com.heb.pm.hierarchy.service;

import com.heb.pm.hierarchy.entity.Attribute;
import com.heb.pm.hierarchy.repository.AttributeRepository;
import com.heb.pm.hierarchy.repository.AttributeRepositoryWithCount;
import com.heb.pm.util.endpoint.PageableResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Holds business logic related to category specific attributes.
 */
@Service
public class AttributeService {

    @Autowired
	private final transient AttributeRepository repository;
    @Autowired
	private final transient AttributeRepositoryWithCount repositoryWithCount;

	/**
	 * Constructs a new AttrbuteService.
	 *
	 * @param repository The AttributeRepository to use to search for attributes.
	 * @param repositoryWithCount The AttributeRepositoryWithCont to search for attributes when counts are needed.
	 */
	public AttributeService(AttributeRepository repository, AttributeRepositoryWithCount repositoryWithCount) {
		this.repository = repository;
		this.repositoryWithCount = repositoryWithCount;
	}

	/**
	 * Finds a page of attributes given a category id (of selected hierarchy level), page, page size, and include
	 * count.
	 *
	 * @param page Page to search for.
	 * @param pageSize Page size to use.
	 * @param includeCount Whether count needs to be returned or not.
	 * @param categoryId ID of the selected hierarchy level.
	 * @return Requested page of attributes.
	 */
    @Transactional(readOnly = true)
    public PageableResult findPageByCategoryId(
			Integer page, Integer pageSize, Boolean includeCount, String categoryId) {


		PageRequest request = PageRequest.of(page, pageSize, Attribute.getDefaultSort());
		if (includeCount) {
			return findPageByCategoryIdWithCount(categoryId, request);
		} else {
			return findPageByCategoryIdWithoutCount(categoryId, request);
		}
    }

	/**
	 * Finds a page of attributes given a category id (of selected hierarchy level), and page information, including
	 * counts.
	 *
	 * @param categoryId ID of the selected hierarchy level.
	 * @param request Page request information.
	 * @return Requested page of attributes.
	 */
	private PageableResult findPageByCategoryIdWithCount(String categoryId, PageRequest request) {
		Page<Attribute> data = this.repositoryWithCount
				.findByPropertyOfCategoryCategoryId(categoryId, request);
		return PageableResult.of(request.getPageNumber(), data.getTotalPages(),
				data.getTotalElements(), data.getContent());
	}

	/**
	 * Finds a page of attributes given a category id (of selected hierarchy level), and page information, not
	 * including counts.
	 *
	 * @param categoryId ID of the selected hierarchy level.
	 * @param request Page request information.
	 * @return Requested page of attributes.
	 */
	private PageableResult findPageByCategoryIdWithoutCount(String categoryId, PageRequest request) {
		Slice<Attribute> data = this.repository
				.findByPropertyOfCategoryCategoryId(categoryId, request);
		return PageableResult.of(request.getPageNumber(), data);
	}
}
