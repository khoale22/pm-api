package com.heb.pm.core.repository;

import com.heb.pm.dao.core.entity.RegularZoneRetail;
import com.heb.pm.dao.core.entity.ZoneRetailKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repository for the RegularZoneRetail class.
 *
 * @author d116773
 * @since 1.3.0
 */
public interface RegularZoneRetailRepository extends JpaRepository<RegularZoneRetail, ZoneRetailKey> {

	/**
	 * Returns current retail regular zone retail for a given UPC and zone. This does not account for associate UPCs,
	 * so the caller should send a primary UPC.
	 *
	 * @param upc The UPC to look up retail for.
	 * @param retailZone The zone to look up retail for.
	 * @return Retail if it exists and empty if not.
	 */
	@Query(value = "select pd_upc_no, pd_zone_no, pd_price_eff_dt, pd_retail_prc, pd_xfor_qty, " +
			"			pd_weight_sw, pd_change_type_cd, pd_tag_chg_type_cd from ( " +
			"				select pd_upc_no, pd_zone_no, pd_price_eff_dt, pd_retail_prc, pd_xfor_qty, pd_weight_sw, pd_change_type_cd, pd_tag_chg_type_cd, " +
			"					max(pd_price_eff_dt) over (partition by pd_upc_no, pd_zone_no) med " +
			"				from emd.pd_regular_price " +
			"				where pd_upc_no = :upc and pd_zone_no = :retailZone and pd_price_eff_dt <= trunc(sysdate)" +
			"		) where pd_price_eff_dt = med ",
			nativeQuery = true)
	Optional<RegularZoneRetail> findCurrentByUpcAndZone(@Param("upc") Long upc, @Param("retailZone") Long retailZone);


}
