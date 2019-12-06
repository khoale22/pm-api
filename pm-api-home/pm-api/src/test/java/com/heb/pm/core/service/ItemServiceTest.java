package com.heb.pm.core.service;

import com.heb.pm.dao.core.entity.*;
import com.heb.pm.core.model.ContainedUpc;
import com.heb.pm.core.model.Item;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * Tests ItemService.
 *
 * @author d116773
 * @since 1.0.0
 */
public class ItemServiceTest {

	private UpcService upcService;
	private ItemService itemService;
	private ProductService productService;

	@Before
	public void setup() {
		this.upcService = new UpcService();
		this.itemService = new ItemService();
		this.productService = new ProductService();

		this.itemService.setUpcService(this.upcService);
		this.upcService.setProductService(this.productService);
	}

	@Test
	public void itemMasterToSkinnyItem_handlesNull() {

		Assert.assertNull(this.itemService.itemMasterToSkinnyItem(null));
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void itemMasterToSkinnyItem_handlesEmptyItemCode() {

		ItemMaster itemMaster = new ItemMaster();
		this.itemService.itemMasterToSkinnyItem(itemMaster);
	}

	@Test
	public void itemMasterToSkinnyItem_handlesItemCodeOnly() {

		ItemMaster itemMaster = this.getItemCodeOnly();
		Item item = this.itemService.itemMasterToSkinnyItem(itemMaster);

		Assert.assertEquals(Long.valueOf(12345L), item.getItemCode());
		Assert.assertNull(item.getOrderingUpc());
		Assert.assertNull(item.getDescription());
	}

	@Test
	public void itemMasterToSkinnyItem_handlesItemCodeOrderingUpcAndDescription() {

		ItemMaster itemMaster = this.getSkinnyItem();

		Item item = this.itemService.itemMasterToSkinnyItem(itemMaster);
		Assert.assertEquals(Long.valueOf(12345L), item.getItemCode());
		Assert.assertEquals(Long.valueOf(9023942398L), item.getOrderingUpc());
		Assert.assertEquals("TEST DESCRIPTION", item.getDescription());
	}


	// isFakeMrt

	@Test
	public void isFakeMrt_handlesOpenStock() {

		ItemMaster itemMaster = this.getOpenStockItem();

		Assert.assertFalse(this.itemService.isFakeMrt(itemMaster));
	}

	@Test
	public void isFakeMrt_handlesMrt() {

		ItemMaster itemMaster = this.getMrt();

		Assert.assertFalse(this.itemService.isFakeMrt(itemMaster));
	}

	@Test
	public void isFakeMrt_handlesTwoInnerMrt() {

		ItemMaster itemMaster = this.getTwoInnerMrt();

		Assert.assertFalse(this.itemService.isFakeMrt(itemMaster));
	}

	@Test
	public void isFakeMrt_handlesFakeMrt() {

		ItemMaster itemMaster = this.getFakeMrt();

		Assert.assertTrue(this.itemService.isFakeMrt(itemMaster));
	}

	@Test
	public void isFakeMrt_handlesAltPack() {

		ItemMaster itemMaster = this.getAltPack();

		Assert.assertFalse(this.itemService.isFakeMrt(itemMaster));
	}


	// isMrt

	@Test
	public void isMrt_handlesOpenStock() {

		ItemMaster itemMaster = this.getOpenStockItem();

		Assert.assertFalse(this.itemService.isMrt(itemMaster));
	}

	@Test
	public void isMrt_handlesMrt() {

		ItemMaster itemMaster = this.getMrt();

		Assert.assertTrue(this.itemService.isMrt(itemMaster));
	}

	@Test
	public void isMrt_handlesTwoInnerMrt() {

		ItemMaster itemMaster = this.getTwoInnerMrt();

		Assert.assertTrue(this.itemService.isMrt(itemMaster));
	}

	@Test
	public void isMrt_handlesFakeMrt() {

		ItemMaster itemMaster = this.getFakeMrt();

		Assert.assertFalse(this.itemService.isMrt(itemMaster));
	}

	@Test
	public void isMrt_handlesAltPack() {

		ItemMaster itemMaster = this.getAltPack();

		Assert.assertFalse(this.itemService.isMrt(itemMaster));
	}

	// isOpenStock

	@Test
	public void isOpenStock_handlesOpenStock() {

		ItemMaster itemMaster = this.getOpenStockItem();

		Assert.assertTrue(this.itemService.isOpenStock(itemMaster));
	}

	@Test
	public void isOpenStock_handlesMrt() {

		ItemMaster itemMaster = this.getMrt();

		Assert.assertFalse(this.itemService.isOpenStock(itemMaster));
	}

	@Test
	public void isOpenStock_handlesTwoInnerMrt() {

		ItemMaster itemMaster = this.getTwoInnerMrt();

		Assert.assertFalse(this.itemService.isOpenStock(itemMaster));
	}

	@Test
	public void isOpenStock_handlesFakeMrt() {

		ItemMaster itemMaster = this.getFakeMrt();

		Assert.assertFalse(this.itemService.isOpenStock(itemMaster));
	}

	@Test
	public void isOpenStock_handlesAltPack() {

		ItemMaster itemMaster = this.getAltPack();

		Assert.assertFalse(this.itemService.isOpenStock(itemMaster));
	}


	// isAltPack

	@Test
	public void isAltPack_handlesOpenStock() {

		ItemMaster itemMaster = this.getOpenStockItem();

		Assert.assertFalse(this.itemService.isAltPack(itemMaster));
	}

	@Test
	public void isAltPack_handlesMrt() {

		ItemMaster itemMaster = this.getMrt();

		Assert.assertFalse(this.itemService.isAltPack(itemMaster));
	}

	@Test
	public void isAltPack_handlesTwoInnerMrt() {

		ItemMaster itemMaster = this.getTwoInnerMrt();

		Assert.assertFalse(this.itemService.isAltPack(itemMaster));
	}

	@Test
	public void isAltPack_handlesFakeMrt() {

		ItemMaster itemMaster = this.getFakeMrt();

		Assert.assertFalse(this.itemService.isAltPack(itemMaster));
	}

	@Test
	public void isAltPackf_handlesAltPack() {

		ItemMaster itemMaster = this.getAltPack();

		Assert.assertTrue(this.itemService.isAltPack(itemMaster));
	}


	// getContainedUpc

	@Test
	public void getContainedUpc_handlesOpenStock() {

		ItemMaster itemMaster = this.getOpenStockItem();
		ContainedUpc containedUpc = this.itemService.getContainedUpc(itemMaster);

		Assert.assertEquals(Long.valueOf(243952498L), containedUpc.getUpc().getScanCodeId());
		Assert.assertEquals(Long.valueOf(50L), containedUpc.getPack());
	}

	@Test
	public void getContainedUpc_handlesAltPack() {

		ItemMaster itemMaster = this.getAltPack();
		ContainedUpc containedUpc = this.itemService.getContainedUpc(itemMaster);

		Assert.assertEquals(Long.valueOf(4849873453L), containedUpc.getUpc().getScanCodeId());
		Assert.assertEquals(Long.valueOf(50L), containedUpc.getPack());
	}

	@Test
	public void getContainedUpc_handlesMrt() {

		ItemMaster itemMaster = this.getMrt();
		Assert.assertNull(this.itemService.getContainedUpc(itemMaster));
	}


	@Test
	public void getContainedUpc_handlesFakeMrt() {

		ItemMaster itemMaster = this.getFakeMrt();
		ContainedUpc containedUpc = this.itemService.getContainedUpc(itemMaster);

		Assert.assertEquals(Long.valueOf(35409485344L), containedUpc.getUpc().getScanCodeId());
		Assert.assertEquals(Long.valueOf(34L), containedUpc.getPack());
	}

	// getInnerUpcs

	@Test
	public void getInnerUpcs_handlesOpenStock() {

		ItemMaster itemMaster = this.getOpenStockItem();
		Assert.assertNull(this.itemService.getInnerUpcs(itemMaster));
	}

	@Test
	public void getInnerUpcs_handlesAltPack() {

		ItemMaster itemMaster = this.getAltPack();
		Assert.assertNull(this.itemService.getInnerUpcs(itemMaster));
	}

	@Test
	public void getInnerUpcs_handlesMrt() {

		ItemMaster itemMaster = this.getMrt();
		Assert.assertNull(this.itemService.getContainedUpc(itemMaster));

		List<ContainedUpc> containedUpcs = this.itemService.getInnerUpcs(itemMaster);
		Assert.assertEquals(3, containedUpcs.size());

		Assert.assertEquals(Long.valueOf(10L), containedUpcs.get(0).getUpc().getScanCodeId());
		Assert.assertEquals(Long.valueOf(10L), containedUpcs.get(0).getPack());

		Assert.assertEquals(Long.valueOf(11L), containedUpcs.get(1).getUpc().getScanCodeId());
		Assert.assertEquals(Long.valueOf(11L), containedUpcs.get(1).getPack());

		Assert.assertEquals(Long.valueOf(3L), containedUpcs.get(2).getUpc().getScanCodeId());
		Assert.assertEquals(Long.valueOf(12L), containedUpcs.get(2).getPack());
	}


	@Test
	public void getInnerUpcs_handlesFakeMrt() {

		ItemMaster itemMaster = this.getFakeMrt();
		Assert.assertNull(this.itemService.getInnerUpcs(itemMaster));
	}

	// itemMasterToItem

	@Test
	public void itemMasterToItem_handlesProblemItems() {

		ItemMaster itemMaster = this.getSkinnyItem();
		Assert.assertNotNull(this.itemService.itemMasterToItem(itemMaster));

		itemMaster.setPrimaryUpc(new PrimaryUpc());
		Assert.assertNotNull(this.itemService.itemMasterToItem(itemMaster));
	}

	@Test
	public void itemMasterToItem_handlesNull() {

		Assert.assertNull(this.itemService.itemMasterToItem(null));
	}

	@Test
	public void itemMasterToItem_handlesOpenStock() {

		ItemMaster itemMaster = this.getOpenStockItem();
		Item item = this.itemService.itemMasterToItem(itemMaster);

		Assert.assertEquals(Long.valueOf(12345L), item.getItemCode());
		Assert.assertEquals("TEST DESCRIPTION", item.getDescription());
		Assert.assertEquals(Long.valueOf(9023942398L), item.getOrderingUpc());
		Assert.assertEquals(Long.valueOf(99), item.getCommodity());
		Assert.assertEquals(Long.valueOf(88), item.getSubCommodity());
		Assert.assertEquals(Long.valueOf(243952498), item.getContainedUpc().getUpc().getScanCodeId());
		Assert.assertEquals(Long.valueOf(50), item.getContainedUpc().getPack());
		Assert.assertNull(item.getInnerUpcs());
		Assert.assertFalse(item.getMrt());
		Assert.assertFalse(item.getAltPack());
	}

	@Test
	public void itemMasterToItem_handlesAltPack() {

		ItemMaster itemMaster = this.getAltPack();
		Item item = this.itemService.itemMasterToItem(itemMaster);

		Assert.assertEquals(Long.valueOf(12345L), item.getItemCode());
		Assert.assertEquals("TEST DESCRIPTION", item.getDescription());
		Assert.assertEquals(Long.valueOf(9023942398L), item.getOrderingUpc());
		Assert.assertEquals(Long.valueOf(99), item.getCommodity());
		Assert.assertEquals(Long.valueOf(88), item.getSubCommodity());
		Assert.assertEquals(Long.valueOf(4849873453L), item.getContainedUpc().getUpc().getScanCodeId());
		Assert.assertEquals(Long.valueOf(50), item.getContainedUpc().getPack());
		Assert.assertNull(item.getInnerUpcs());
		Assert.assertFalse(item.getMrt());
		Assert.assertTrue(item.getAltPack());
	}

	@Test
	public void itemMasterToItem_handlesFakeMrt() {

		ItemMaster itemMaster = this.getFakeMrt();

		Item item = this.itemService.itemMasterToItem(itemMaster);

		Assert.assertEquals(Long.valueOf(12345L), item.getItemCode());
		Assert.assertEquals("TEST DESCRIPTION", item.getDescription());
		Assert.assertEquals(Long.valueOf(9023942398L), item.getOrderingUpc());
		Assert.assertEquals(Long.valueOf(99), item.getCommodity());
		Assert.assertEquals(Long.valueOf(88), item.getSubCommodity());
		Assert.assertEquals(Long.valueOf(35409485344L), item.getContainedUpc().getUpc().getScanCodeId());
		Assert.assertEquals(Long.valueOf(34), item.getContainedUpc().getPack());
		Assert.assertNull(item.getInnerUpcs());
		Assert.assertFalse(item.getMrt());
		Assert.assertTrue(item.getAltPack());
	}

	@Test
	public void itemMasterToItem_handlesMrt() {

		ItemMaster itemMaster = this.getMrt();
		Item item = this.itemService.itemMasterToItem(itemMaster);

		Assert.assertEquals(Long.valueOf(12345L), item.getItemCode());
		Assert.assertEquals("TEST DESCRIPTION", item.getDescription());
		Assert.assertEquals(Long.valueOf(9023942398L), item.getOrderingUpc());
		Assert.assertEquals(Long.valueOf(99), item.getCommodity());
		Assert.assertEquals(Long.valueOf(88), item.getSubCommodity());
		Assert.assertNull(item.getContainedUpc());
		Assert.assertNotNull(item.getInnerUpcs());
		Assert.assertEquals(3, item.getInnerUpcs().size());
		Assert.assertTrue(item.getMrt());
		Assert.assertFalse(item.getAltPack());
	}

	// Functions that return objects to test with.

	private ItemMaster getItemCodeOnly() {
		ItemMaster itemMaster = new ItemMaster();
		itemMaster.setKey(new ItemMasterKey().setItemId(12345L).setItemKeyTypeCode("ITMCD"));
		return itemMaster;
	}

	private ItemMaster getSkinnyItem() {
		return this.getItemCodeOnly().setOrderingUpc(9023942398L)
				.setDescription("TEST DESCRIPTION");
	}

	private ItemMaster getFullItem() {
		ItemMaster itemMaster = this.getSkinnyItem();
		itemMaster.setPack(50L);
		return itemMaster;
	}

	private PrimaryUpc getPrimaryUpc(long upc, long itemCode) {
		SellingUnit sellingUnit = new SellingUnit().setUpc(upc).setProductMaster(new ProductMaster().setProdId(3434L).setDescription("TEST PRODUCT"));

		AssociatedUpc associatedUpc = new AssociatedUpc();
		associatedUpc.setPdUpcNo(upc).setUpc(upc).setSellingUnit(sellingUnit);

		ItemMaster itemMaster = this.getOpenStockItem();
		LinkedList<ItemMaster> itemMasters = new LinkedList<>();
		itemMasters.add(itemMaster);
		PrimaryUpc primaryUpc = new PrimaryUpc().setUpc(upc).setItemCode(itemCode)
				.setAssociateUpcs(List.of(associatedUpc))
				.setSellingUnit(sellingUnit)
				.setItemMasters(itemMasters);
		associatedUpc.setPrimaryUpc(primaryUpc);
		return primaryUpc;
	}

	private ItemMaster getOpenStockItem() {

		ItemMaster itemMaster = this.getFullItem();

		SellingUnit sellingUnit = new SellingUnit().setUpc(243952498L).setProductMaster(new ProductMaster().setProdId(3434L).setDescription("TEST PRODUCT"));

		AssociatedUpc associatedUpc = new AssociatedUpc();
		associatedUpc.setPdUpcNo(243952498L).setUpc(243952498L).setSellingUnit(sellingUnit);

		PrimaryUpc primaryUpc = new PrimaryUpc().setUpc(243952498L).setItemCode(itemMaster.getKey().getItemId())
				.setAssociateUpcs(List.of(associatedUpc))
				.setSellingUnit(sellingUnit);
		associatedUpc.setPrimaryUpc(primaryUpc);
		return itemMaster.setPrimaryUpc(primaryUpc).setCommodityCode(99L).setSubCommodityCode(88L);
	}

	private ItemMaster getTwoInnerMrt() {

		ItemMaster itemMaster = this.getOpenStockItem().setPack(1L);

		List<Shipper> shipperList = new LinkedList<>();
		for (int i = 0; i < 2; i++) {
			shipperList.add(
					new Shipper().setShipperQuantity((long) (i + 10))
							.setSellingUnit(new SellingUnit().setUpc((long) i + 10))
							.setKey(new ShipperKey().setUpc(itemMaster.getPrimaryUpc().getUpc()).setShipperUpc((long) i))
					.setPrimaryUpc(this.getPrimaryUpc((long) i, (long) i))
					.setInnerPrimaryUpc(this.getPrimaryUpc((long) i + 10, (long) i + 10))
			);
		}
		itemMaster.getPrimaryUpc().setShipper(shipperList);
		return itemMaster;
	}

	private ItemMaster getMrt() {
		ItemMaster itemMaster = this.getTwoInnerMrt();
		itemMaster.getPrimaryUpc().getShipper().add(new Shipper().setShipperQuantity(12L)
				.setSellingUnit(new SellingUnit().setUpc(3L))
				.setKey(new ShipperKey().setUpc(itemMaster.getPrimaryUpc().getUpc()).setShipperUpc(2L))
				.setPrimaryUpc(this.getPrimaryUpc(2L, 2L))
				.setInnerPrimaryUpc(this.getPrimaryUpc(3L, 3L)));
		return itemMaster;
	}

	private ItemMaster getFakeMrt() {

		ItemMaster itemMaster = this.getTwoInnerMrt();
		itemMaster.getPrimaryUpc().getShipper().get(0).setShipperQuantity(0L).getPrimaryUpc().setUpc(Shipper.FAKE_MRT_UPC);
		itemMaster.getPrimaryUpc().getShipper().get(0).setShipperQuantity(0L).getPrimaryUpc().getAssociateUpcs().get(0).setUpc(Shipper.FAKE_MRT_UPC);
		itemMaster.getPrimaryUpc().getShipper().get(0).getKey().setShipperUpc(Shipper.FAKE_MRT_UPC);
		itemMaster.getPrimaryUpc().getShipper().get(1).setShipperQuantity(34L).setSellingUnit(new SellingUnit().setUpc(35409485344L)
				.setProductMaster(new ProductMaster().setProdId(453443L).setDescription("TEST FAKE MRT PRODUCT")));
		itemMaster.getPrimaryUpc().getShipper().get(1).getInnerPrimaryUpc().setUpc(35409485344L);
		return itemMaster;
	}

	private ItemMaster getAltPack() {

		ItemMaster itemMaster = this.getOpenStockItem();

		List<Shipper> shipperList = new LinkedList<>();
		itemMaster.getPrimaryUpc().setShipper(shipperList);
		itemMaster.getPrimaryUpc().getShipper().add(
				new Shipper().setKey(new ShipperKey().setUpc(itemMaster.getPrimaryUpc().getUpc())
						.setShipperUpc(4849873453L))
						.setPrimaryUpc(this.getPrimaryUpc(itemMaster.getPrimaryUpc().getUpc(), itemMaster.getKey().getItemId()))
						.setInnerPrimaryUpc(this.getPrimaryUpc(4849873453L, 12324L))
						.setSellingUnit(new SellingUnit().setUpc(4849873453L)
						.setProductMaster(new ProductMaster().setProdId(43534L).setDescription("TEST ALT PACK PRODUCT"))));

		return itemMaster;
	}
}
