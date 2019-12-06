package com.heb.pm.core.service;

import com.heb.pm.core.model.Upc;
import com.heb.pm.dao.core.entity.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * Tests UpcService.
 *
 * @author d116773
 * @since 1.0.0
 */
public class UpcServiceTest {

	private UpcService upcService;
	private ProductService productService;
	private ItemService itemService;

	@Before
	public void setup() {

		this.productService = new ProductService();
		this.upcService = new UpcService();

		this.itemService = new ItemService();

		this.upcService.setProductService(this.productService);
		this.upcService.setItemService(this.itemService);
	}

	// sellingUnitToSkinnyUpc

	@Test
	public void primaryUpcToSkinnyUpc_handlesNull() {

		Assert.assertNull(this.upcService.primaryUpcToSkinnyUpc(null));
	}

	@Test
	public void primaryUpcToSkinnyUpc_handlesEmptyProductMaster() {

		SellingUnit sellingUnit = new SellingUnit();
		sellingUnit.setUpc(35344435L);
		sellingUnit.setTagSizeDescription("TAG!");

		PrimaryUpc primaryUpc = new PrimaryUpc();
		primaryUpc.setUpc(sellingUnit.getUpc());
		primaryUpc.setSellingUnit(sellingUnit);

		Upc upc = this.upcService.primaryUpcToSkinnyUpc(primaryUpc);
		Assert.assertEquals(Long.valueOf(35344435), upc.getScanCodeId());
		Assert.assertEquals("TAG!", upc.getSize());
	}

	@Test
	public void primaryUpcToSkinnyUpc_handlesGoodData() {

		SellingUnit sellingUnit = new SellingUnit();
		sellingUnit.setUpc(35344435L).setProductMaster(new ProductMaster().setProdId(23423L).setDescription("TEST PRODUCT"));
		sellingUnit.setTagSizeDescription("TAG4");

		RetailLink retailLink = new RetailLink();
		retailLink.setRetailLinkCd(2344849L);

		PrimaryUpc primaryUpc = new PrimaryUpc();
		primaryUpc.setUpc(sellingUnit.getUpc());
		primaryUpc.setSellingUnit(sellingUnit);
		primaryUpc.setRetailLink(retailLink);

		Upc upc = this.upcService.primaryUpcToSkinnyUpc(primaryUpc);
		Assert.assertEquals(Long.valueOf(35344435), upc.getScanCodeId());
		Assert.assertNotNull(upc.getProduct());
		Assert.assertEquals(Long.valueOf(2344849L), upc.getRetailLink());
		Assert.assertEquals("TAG4", upc.getSize());
	}

	// associateUpcToUpc

	@Test
	public void associateUpcToUpc_handlesNull() {

		Assert.assertNull(this.upcService.associateUpcToUpc(null));
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void associateUpcToUpc_handlesNoPdUpc() {

		this.upcService.associateUpcToUpc(new AssociatedUpc());
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void associateUpcToUpc_handlesNoProdScnCodes() {

		this.upcService.associateUpcToUpc(new AssociatedUpc().setPrimaryUpc(new PrimaryUpc()));
	}

	@Test
	public void associateUpcToUpc_doesNotChokeOnMissingData() {

		// This is the minimum required data.
		AssociatedUpc associatedUpc = new AssociatedUpc();
		associatedUpc.setPrimaryUpc(
				new PrimaryUpc()
						.setSellingUnit(new SellingUnit()).setAssociateUpcs(List.of()).setUpc(4534L)
		);

		Assert.assertNotNull(this.upcService.associateUpcToUpc(associatedUpc));
	}

	@Test
	public void associateUpcToUpc_handlesRegularUpc() {

		AssociatedUpc associatedUpc = new AssociatedUpc();

		SellingUnit sellingUnit = SellingUnit.of(34543453433L, "test").setPrimaryScanCode(true).setProductId(234566L)
				.setTagSizeDescription("1 EACH")
				.setProductMaster(new ProductMaster().setProdId(234566L).setDescription("TEST PRODUCT"));

		PrimaryUpc primaryUpc = new PrimaryUpc();
		primaryUpc.setUpc(34543453433L).setItemCode(345L).setSellingUnit(sellingUnit);
		primaryUpc.setAssociateUpcs(new LinkedList<>()).getAssociateUpcs().add(associatedUpc);
		primaryUpc.setItemMasters(new LinkedList<>()).getItemMasters().add(
				new ItemMaster().setPrimaryUpc(primaryUpc).setOrderingUpc(34543453433L)
						.setCommodityCode(99L).setSubCommodityCode(88L).setPack(15L).setDescription("TEST ITEM")
						.setKey(ItemMasterKey.of(ItemMasterKey.WAREHOUSE, 345L)));

		associatedUpc.setPrimaryUpc(primaryUpc).setPdUpcNo(34543453433L)
				.setSellingUnit(sellingUnit).setUpc(34543453433L);

		Upc upc = this.upcService.associateUpcToUpc(associatedUpc);

		Assert.assertEquals(Long.valueOf(34543453433L), upc.getScanCodeId());
		Assert.assertNotNull(upc.getItem());
		Assert.assertEquals("1 EACH", upc.getSize());
		Assert.assertEquals(Long.valueOf(34543453433L), upc.getSearchedUpc());
		Assert.assertEquals("ACTIVE", upc.getStatus());

		Assert.assertNull(upc.getAssociatedUpcs());

		Assert.assertNotNull(upc.getItem());
		Assert.assertNotNull(upc.getProduct());
		Assert.assertNull(upc.getRetailLink());
		Assert.assertNull(upc.getRetailPrice());
		Assert.assertNull(upc.getWeightSw());
		Assert.assertNull(upc.getXFor());
	}

	@Test
	public void associateUpcToUpc_handlesAssociatedUpcSearchByAssociate() {

		AssociatedUpc primaryAssociate = new AssociatedUpc();
		AssociatedUpc associatedAssociate = new AssociatedUpc();

		ProductMaster product = new ProductMaster().setProdId(234566L).setDescription("TEST PRODUCT");

		SellingUnit primarySellingUnit = SellingUnit.of(111L, "test").setPrimaryScanCode(true).setProductId(234566L)
				.setTagSizeDescription("1 EACH P")
				.setProductMaster(product);
		primaryAssociate.setUpc(111L).setSellingUnit(primarySellingUnit).setPdUpcNo(111L);

		SellingUnit associatedSellingUnit = new SellingUnit().setUpc(222L).setPrimaryScanCode(false).setProductId(234566L)
				.setTagSizeDescription("1 EACH A")
				.setProductMaster(product);
		associatedAssociate.setUpc(222L).setSellingUnit(associatedSellingUnit).setPdUpcNo(111L);

		PrimaryUpc primaryUpc = new PrimaryUpc();
		primaryUpc.setUpc(primarySellingUnit.getUpc()).setItemCode(345L).setSellingUnit(primarySellingUnit);
		primaryAssociate.setPrimaryUpc(primaryUpc);
		associatedAssociate.setPrimaryUpc(primaryUpc);

		primaryUpc.setAssociateUpcs(new LinkedList<>()).getAssociateUpcs().add(primaryAssociate);
		primaryUpc.getAssociateUpcs().add(associatedAssociate);

		primaryUpc.setItemMasters(new LinkedList<>()).getItemMasters().add(
				new ItemMaster().setPrimaryUpc(primaryUpc).setOrderingUpc(111L)
						.setCommodityCode(99L).setSubCommodityCode(88L).setPack(15L).setDescription("TEST ITEM")
						.setKey(new ItemMasterKey().setItemId(345L).setItemKeyTypeCode(ItemMasterKey.WAREHOUSE)));

		Upc upc = this.upcService.associateUpcToUpc(associatedAssociate);

		Assert.assertEquals(Long.valueOf(111L), upc.getScanCodeId());
		Assert.assertNotNull(upc.getItem());
		Assert.assertEquals("1 EACH P", upc.getSize());
		Assert.assertEquals(Long.valueOf(222L), upc.getSearchedUpc());
		Assert.assertEquals("ACTIVE", upc.getStatus());

		Assert.assertNotNull(upc.getAssociatedUpcs());
		Assert.assertEquals(1, upc.getAssociatedUpcs().size());
		Assert.assertEquals(Long.valueOf(222L), upc.getAssociatedUpcs().get(0).getScanCodeId());

		Assert.assertNotNull(upc.getItem());
		Assert.assertNotNull(upc.getProduct());
		Assert.assertNull(upc.getRetailLink());
		Assert.assertNull(upc.getRetailPrice());
		Assert.assertNull(upc.getWeightSw());
		Assert.assertNull(upc.getXFor());
	}

}
