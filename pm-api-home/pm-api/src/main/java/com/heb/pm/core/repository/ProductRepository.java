package com.heb.pm.core.repository;

import com.heb.pm.dao.core.entity.ProductMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for functions against the product_master table.
 */
@Repository
public interface ProductRepository extends JpaRepository<ProductMaster, Long> {
}
