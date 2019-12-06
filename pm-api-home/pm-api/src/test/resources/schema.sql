CREATE SCHEMA EMD;

RUNSCRIPT FROM 'classpath:product-hierarchy-schema.sql';

RUNSCRIPT FROM 'classpath:arbaf-schema.sql';

RUNSCRIPT FROM 'classpath:ps-schema.sql';

RUNSCRIPT FROM 'classpath:dictionary-schema.sql';

RUNSCRIPT FROM 'classpath:ecommerce-schema.sql';

RUNSCRIPT FROM 'classpath:scale-schema.sql';

RUNSCRIPT FROM 'classpath:product-schema.sql';

RUNSCRIPT FROM 'classpath:nlea2016-schema.sql';

-----------------------------------------
-- LOCATION
-----------------------------------------
create table EMD.LOCATION
(
	LOC_TYP_CD CHAR(2 char) default ' ' not null,
-- 		constraint LOCATION_FK1
-- 			references EMD.LOCATION_TYPE,
	LOC_NBR NUMBER default 0 not null,
	LOC_NM CHAR(30 char) default ' ' not null,
	LOC_ABB CHAR(6 char) default ' ' not null,
	LGL_LOC_NAM CHAR(30 char) default ' ' not null,
	PRIM_CONTACT_NM CHAR(50 char) default ' ' not null,
	PRIM_ADR_1 CHAR(30 char) default ' ' not null,
	PRIM_ADR_2 CHAR(30 char) default ' ' not null,
	PRIM_ADR_3 CHAR(30 char) default ' ' not null,
	PRIM_ADR_4 CHAR(30 char) default ' ' not null,
	PRIM_CITY CHAR(30 char) default ' ' not null,
	PRIM_CITY_ID NUMBER default 0 not null,
	PRIM_STATE_CD CHAR(2 char) default ' ' not null,
	PRIM_ZIP5_CD NUMBER(5) default 0 not null,
	PRIM_ZIP4_CD NUMBER(4) default 0 not null,
	PRIM_PHN_CNTRY_CD NUMBER default 0 not null,
	PRIM_AREA_CD NUMBER(3) default 0 not null,
	PRIM_PHONE_NBR NUMBER(7) default 0 not null,
	PRIM_CNTRY_NM CHAR(30 char) default ' ' not null,
	PRIM_CNTRY_ABB CHAR(6 char) default ' ' not null,
	SEC_LOC_NM CHAR(30 char) default ' ' not null,
	SEC_CONTACT_NM CHAR(30 char) default ' ' not null,
	SEC_ADR_1 CHAR(30 char) default ' ' not null,
	SEC_ADR_2 CHAR(30 char) default ' ' not null,
	SEC_ADR_3 CHAR(30 char) default ' ' not null,
	SEC_ADR_4 CHAR(30 char) default ' ' not null,
	SEC_CITY CHAR(30 char) default ' ' not null,
	SEC_STATE_CD CHAR(2 char) default ' ' not null,
	SEC_ZIP5_CD NUMBER(5) default 0 not null,
	SEC_ZIP4_CD NUMBER(4) default 0 not null,
	SEC_PHN_CNTRY_CD NUMBER default 0 not null,
	SEC_AREA_CD NUMBER(3) default 0 not null,
	SEC_PHONE_NBR NUMBER(7) default 0 not null,
	SEC_CNTRY_NM CHAR(30 char) default ' ' not null,
	SEC_CNTRY_ABB CHAR(6 char) default ' ' not null,
	MAIL_TO_LOC_NM CHAR(30 char) default ' ' not null,
	MAIL_TO_CNTCT_NM CHAR(30 char) default ' ' not null,
	MAIL_TO_ADR_1 CHAR(30 char) default ' ' not null,
	MAIL_TO_ADR_2 CHAR(30 char) default ' ' not null,
	MAIL_TO_ADR_3 CHAR(30 char) default ' ' not null,
	MAIL_TO_ADR_4 CHAR(30 char) default ' ' not null,
	MAIL_TO_CITY CHAR(30 char) default ' ' not null,
	MAIL_TO_STATE_CD CHAR(2 char) default ' ' not null,
	MAIL_TO_ZIP5_CD NUMBER(5) default 0 not null,
	MAIL_TO_ZIP4_CD NUMBER(4) default 0 not null,
	MAIL_PHN_CNTRY_CD NUMBER default 0 not null,
	MAIL_TO_AREA_CD NUMBER(3) default 0 not null,
	MAIL_TO_PHONE_NBR NUMBER(7) default 0 not null,
	MAIL_TO_CNTRY_NM CHAR(30 char) default ' ' not null,
	MAIL_TO_CNTRY_AB CHAR(6 char) default ' ' not null,
	CURR_FAX_ID NUMBER default 0 not null,
	ADDED_DT DATE default sysdate not null,
	DELETE_DT DATE default sysdate not null,
	OPENED_DT DATE default sysdate not null,
	CLOSED_DT DATE default sysdate not null,
	INACTIVE_SW CHAR(1 char) default ' ' not null,
	INACTIVE_DT DATE default sysdate not null,
	AP_NBR NUMBER,
	AP_TYP_CD CHAR(2 char),
	LST_UPDT_TS TIMESTAMP(6) default systimestamp not null,
	LST_UPDT_USR_ID CHAR(8 char) default ' ' not null,
	PRIM_EMAIL_ID CHAR(50 char) default ' ' not null,
	SECY_EMAIL_ID CHAR(50 char) default ' ' not null,
	MAIL_TO_EMAIL_ID CHAR(50 char) default ' ' not null,
	FAC_ID NUMBER default 0 not null,
	ORG_ID NUMBER default 0 not null,
	B2B_PRIM_RTNG_ID CHAR(30 char) default ' ' not null,
	PRIM_CNTY_TXT CHAR(30 char) default ' ' not null,
	SECY_CNTY_TXT CHAR(30 char) default ' ' not null,
	MAIL_TO_CNTY_TXT CHAR(30 char) default ' ' not null,
	DIR_SHP_LOC_SW CHAR(1 char) default 'N' not null,
	LOC_ORD_PROCNG_DD NUMBER(5,2) default 0 not null,
	ORD_PROCNG_CTOF_TM TIMESTAMP(0) default to_date('1700-01-01 00:00:00','YYYY-MM-DD HH24:MI:SS') not null,
	SCH_SHP_DD_TXT CHAR(7 char) default ' ' not null,
	FILLER1_TM TIMESTAMP(0) not null,
	FILLER2_TM TIMESTAMP(0) not null,
	ORD_LEAD_TM_DD NUMBER(5,2) default 0 not null,
	ORD_BUFFER_TM_DD NUMBER(5,2) default 0 not null,
	MAP_PLACE_ID VARCHAR2(128 char),
	constraint PK_LOCATION
		primary key (LOC_TYP_CD, LOC_NBR)
-- 	constraint LOCATION_FK2
-- 		foreign key (AP_NBR, AP_TYP_CD) references EMD.AP_LOCATION
-- 			on delete set null
)
;

create unique index EMD.RMDDLOZ0
	on EMD.LOCATION (LOC_NM, LOC_NBR, LOC_TYP_CD)
;

create unique index EMD.RMDDLOC0
	on EMD.LOCATION (LOC_NBR, LOC_TYP_CD)
;

create unique index EMD.RMDDLOZ1
	on EMD.LOCATION (AP_NBR, LOC_NBR, LOC_TYP_CD)
;



-----------------------------------------
-- PD_UPC
-----------------------------------------
create table EMD.PD_UPC
(
	PD_UPC_NO NUMBER(13) not null
		constraint PK_PD_UPC
			primary key,
	PD_ITEM_NO NUMBER(7) not null,
	PD_RETAIL_GROUP_NO NUMBER(3) not null,
-- 		constraint PD_UPC_FK1
-- 			references EMD.PD_RETAIL_GROUP,
	LST_UPDT_UID CHAR(20 char) default ' ' not null
)
;

create unique index EMD.PD_UPC_AK1
	on EMD.PD_UPC (PD_ITEM_NO, PD_UPC_NO, PD_RETAIL_GROUP_NO)
;

create unique index EMD.PD_UPC_AK2
	on EMD.PD_UPC (PD_UPC_NO, PD_ITEM_NO, PD_RETAIL_GROUP_NO)
;


-----------------------------------------
-- PD_UPC_LINK
-----------------------------------------
create table EMD.PD_UPC_LINK
(
	PD_UPC_NO NUMBER(13) not null
		constraint PK_PD_UPC_LINK
			primary key
	,
	PD_LINK_CD NUMBER(7) not null,
	PD_SEQUENCE_NO NUMBER(5) not null,
	PD_CENTS_OFF_AMT NUMBER(7,2) not null
)
;

create unique index EMD.PD_UPC_LINK_AK1
	on EMD.PD_UPC_LINK (PD_LINK_CD, PD_UPC_NO)
;

-----------------------------------------
-- PD_ASSOCIATED_UPC
-----------------------------------------
create table EMD.PD_ASSOCIATED_UPC
(
	PD_ASSOC_UPC_NO NUMBER(13) not null
		constraint PK_PD_ASSOCIATED_UPC
			primary key,
	PD_UPC_NO NUMBER(13) not null
		constraint PD_ASSOCIATED_UPC_FK1
			references EMD.PD_UPC,
	LST_UPDT_UID CHAR(20 char) default ' ' not null
)
;

create unique index EMD.PD_ASSOCIATED_UPC_AK1
	on EMD.PD_ASSOCIATED_UPC (PD_UPC_NO, PD_ASSOC_UPC_NO)
;

-----------------------------------------
-- PRODUCT_MASTER
-----------------------------------------
create table EMD.PRODUCT_MASTER
(
	PROD_ID NUMBER default 0 not null
		constraint PK_PRODUCT_MASTER
			primary key,
	PROD_ENG_DES CHAR(30 char) default ' ' not null,
	PROD_SPNSH_DES CHAR(30 char) default ' ' not null,
	PROD_TYP_CD CHAR(5 char) default ' ' not null,
-- 		constraint PRODUCT_MASTER_FK3
-- 			references EMD.PRODUCT_TYP_CD,
	PD_OMI_COM_CLS_CD NUMBER(3) default 0 not null,
	PD_OMI_COM_CD NUMBER(5) default 0 not null,
	PD_OMI_SUB_COM_CD NUMBER(5) default 0 not null,
	IMS_COM_CD NUMBER(5) default 0 not null,
	IMS_SUB_COM_CD CHAR(1 char) default ' ' not null,
	GRMGN_PCT NUMBER(7,4) default 0 not null,
	SALS_RSTR_CD CHAR(5 char) default ' ' not null,
-- 		constraint PRODUCT_MASTER_FK4
-- 			references EMD.SALS_RSTR_CODE,
	BDM_CD CHAR(5 char) default ' ' not null,
	LBL_LANG_TXT CHAR(10 char) default ' ' not null,
	RETL_GRP_NBR NUMBER(5) default 0 not null,
	STR_DEPT_NBR CHAR(5 char),
	STR_SUB_DEPT_ID CHAR(5 char),
	RETL_LINK_CD NUMBER(7) default 0 not null,
	PSS_DEPT_1 NUMBER default 0 not null,
	OB_SUB_BRND_ID NUMBER default 0 not null,
	PROD_BRND_ID NUMBER default 0 not null,
-- 		constraint PRODUCT_MASTER_FK1
-- 			references EMD.PROD_BRND,
	FRST_SCN_DT DATE default to_date('1600-01-01','YYYY-MM-DD') not null,
	LST_SCN_DT DATE default to_date('1600-01-01','YYYY-MM-DD') not null,
	GBB_SW CHAR(1 char) default 'N' not null,
	KVIL_SW CHAR(1 char) default 'N' not null,
	NBB_SW CHAR(1 char) default 'N' not null,
	OPP_SW CHAR(1 char) default 'N' not null,
	LEB_SW CHAR(1 char) default 'N' not null,
	SENS_SW CHAR(1 char) default 'N' not null,
	MGN_SW CHAR(1 char) default 'N' not null,
	TAG_ITM_ID NUMBER(17) default 0 not null,
	TAG_ITM_KEY_TYP_CD CHAR(5 char) default ' ' not null,
	PROD_SZ_TXT CHAR(12 char) default ' ' not null,
	RC_IM_QTY_REQ_FLAG CHAR(1 char) default ' ' not null,
	RC_IM_PRC_REQ_FLAG CHAR(1 char) default ' ' not null,
	PROD_PRIM_SCN_ID NUMBER(17) default 0 not null,
	CRE8_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	CRE8_UID CHAR(8 char) default ' ' not null,
	LST_UPDT_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	LST_UPDT_UID CHAR(8 char) default ' ' not null,
	LST_SYS_UPDT_ID NUMBER default 0 not null,
-- 		constraint PRODUCT_MASTER_FK6
-- 			references EMD.SRC_SYSTEM,
	MKT_CD CHAR(5 char) default ' ' not null,
	LST_PUB_TS TIMESTAMP(6),
	LST_PUB_UID CHAR(8 char),
	SUBSCR_PROD_SW CHAR(1 char) default 'N' not null,
	PROD_TEMP_CNTL_CD CHAR(5 char),
-- 		constraint PRODUCT_MASTER_FK2
-- 			references EMD.PROD_TEMP_CNTL,
	GIFT_MSG_REQ_SW CHAR(1 char) default 'N' not null,
	SUBSCR_STRT_DT DATE default to_date('1600-01-01','YYYY-MM-DD') not null,
	SUBSCR_END_DT DATE default to_date('1600-01-01','YYYY-MM-DD') not null,
	TAX_QUAL_CD CHAR(10 char),
	SHOW_CLRS_SW CHAR(1 char) default 'N' not null
-- 	constraint PRODUCT_MASTER_FK5
-- 		foreign key (STR_DEPT_NBR, STR_SUB_DEPT_ID) references EMD.STR_DEPT
)
;

create unique index EMD.PRODUCT_MASTER_AK1
	on EMD.PRODUCT_MASTER (PROD_ENG_DES, PROD_ID)
;

create unique index EMD.PRODUCT_MASTER_AK2
	on EMD.PRODUCT_MASTER (BDM_CD, PROD_ID)
;

create unique index EMD.PRODUCT_MASTER_AK3
	on EMD.PRODUCT_MASTER (PD_OMI_COM_CLS_CD, PD_OMI_COM_CD, PD_OMI_SUB_COM_CD, PROD_ID)
;

create unique index EMD.PRODUCT_MASTER_AK4
	on EMD.PRODUCT_MASTER (RETL_LINK_CD, PROD_ID)
;

create index EMD.PRODUCT_MASTER_IE1
	on EMD.PRODUCT_MASTER (STR_DEPT_NBR, STR_SUB_DEPT_ID, PROD_ID)
;

create index EMD.PRODUCT_MASTER_IE2
	on EMD.PRODUCT_MASTER (CRE8_TS, PROD_ID)
;

-----------------------------------------
-- CST_OWN
-----------------------------------------
create table EMD.CST_OWN
(
	CST_OWN_ID NUMBER default 0 not null
		constraint PK_CST_OWN
			primary key,
	CST_OWN_ABB CHAR(6 char) default ' ' not null,
	CST_OWN_NM CHAR(30 char) default ' ' not null,
	T2T_ID NUMBER default 0 not null
-- 		constraint CST_OWN_FK1
-- 			references EMD.T2T
)
;

-----------------------------------------
-- CNTRY_CD
-----------------------------------------
create table EMD.CNTRY_CD
(
	CNTRY_ID NUMBER default 0 not null
		constraint PK_CNTRY_CD
			primary key,
	CNTRY_ABB CHAR(6 char) default ' ' not null,
	CNTRY_NM CHAR(30 char) default ' ' not null,
	CNTRY_ISO_A3_COD CHAR(3 char) default ' ' not null,
	CNTRY_ISO_N3_CD NUMBER(3) default 0 not null
)
;

-----------------------------------------
-- DISCO_RSN_CD
-----------------------------------------
create table EMD.DISCO_RSN_CD
(
	DSCON_RSN_CD CHAR(5 char) default ' ' not null
		constraint PK_DISCO_RSN_CD
			primary key,
	DSCON_RSN_ABB CHAR(6 char) default ' ' not null,
	DSCON_RSN_DES CHAR(30 char) default ' ' not null
)
;

-----------------------------------------
-- ITM_TYP_CODES
-----------------------------------------
create table EMD.ITM_TYP_CODES
(
	ITM_TYP_CD CHAR(5 char) default ' ' not null
		constraint PK_ITM_TYP_CODES
			primary key,
	ITM_TYP_ABB CHAR(6 char) default ' ' not null,
	ITM_TYP_DES CHAR(30 char) default ' ' not null
)
;

-----------------------------------------
-- ONE_TOUCH_TYP
-----------------------------------------
create table EMD.ONE_TOUCH_TYP
(
	ONE_TOUCH_TYP_CD CHAR(2 char) default ' ' not null
		constraint PK_ONE_TOUCH_TYP
			primary key,
	ONE_TOUCH_TYP_ABB CHAR(6 char) default ' ' not null,
	ONE_TOUCH_TYP_DES CHAR(30 char) default ' ' not null
)
;



-----------------------------------------
-- ITEM_MASTER
-----------------------------------------
create table EMD.ITEM_MASTER
(
	ITM_KEY_TYP_CD CHAR(5 char) default ' ' not null,
-- 		constraint ITEM_MASTER_FK1
-- 			references EMD.ITM_KEY_TYP,
	ITM_ID NUMBER(17) default 0 not null,
	ITEM_DES CHAR(30 char) default ' ' not null,
	SHRT_ITM_DES CHAR(20 char) default ' ' not null,
	ITEM_SIZE_TXT CHAR(12 char) default ' ' not null,
	ITM_SZ_QTY NUMBER(9,2) default 0 not null,
	ITM_SZ_UOM_CD CHAR(5 char) default ' ' not null,
-- 		constraint ITEM_MASTER_FK5
-- 			references EMD.ITM_SZ_UOM_CODE,
	SRC_CD CHAR(1 char) default ' ' not null,
-- 		constraint ITEM_MASTER_FK3
-- 			references EMD.ITEM_SOURCE,
	ADDED_DT DATE default SYSDATE not null,
	ADDED_USR_ID CHAR(8 char) default ' ' not null,
	DSCON_TRX_SW CHAR(1 char) default ' ' not null,
	DSCON_DT DATE default SYSDATE not null,
	DSCON_RSN_CD CHAR(5 char) default ' ' not null
		constraint ITEM_MASTER_FK2
			references EMD.DISCO_RSN_CD,
	DSCON_USR_ID CHAR(8 char) default ' ' not null,
	DELETE_DT DATE default SYSDATE not null,
	SPLR_CS_LIF_DD NUMBER default 0 not null,
	ON_RCPT_LIF_DD NUMBER default 0 not null,
	WHSE_REACTION_DD NUMBER default 0 not null,
	GUARN_TO_STR_DD NUMBER default 0 not null,
	DT_CNTRLD_ITM_SW CHAR(1 char) default ' ' not null,
	SPCL_TAX_CD CHAR(5 char) default ' ' not null,
-- 		constraint ITEM_MASTER_FK9
-- 			references EMD.SPCL_TAX_CODES,
	PD_OMI_COM_CD NUMBER(5) default 0 not null,
	PD_OMI_SUB_COM_CD NUMBER(5) default 0 not null,
	PD_OMI_COM_CLS_CD NUMBER(3) default 0 not null,
	IMS_COM_CD NUMBER(5) default 0 not null,
	IMS_SUB_COM_CD CHAR(1 char) default ' ' not null,
	ITM_TYP_CD CHAR(5 char) default ' ' not null
		constraint ITEM_MASTER_FK6
			references EMD.ITM_TYP_CODES,
	ORD_CTLG_NBR NUMBER(5) default 0 not null,
	ITM_MDSE_TYP_CD CHAR(1 char) default ' ' not null,
-- 		constraint ITEM_MASTER_FK4
-- 			references EMD.ITM_MDSE_TYP_CD,
	CASE_UPC NUMBER(18) default 0 not null,
	ORDERING_UPC NUMBER(18) default 0 not null,
	USDA_NBR NUMBER default 0 not null,
	MEX_AUTH_CD CHAR(1 char) default ' ' not null,
	MEX_BRDR_AUTH_CD CHAR(1 char) default ' ' not null,
	MAX_SHIP_QTY NUMBER default 0 not null,
	ABC_AUTH_CD CHAR(1 char) default ' ' not null,
	ABC_ITM_CAT_NO NUMBER default 0 not null,
	NEW_ITM_SW CHAR(1 char) default ' ' not null,
	REPACK_SW CHAR(1 char) default ' ' not null,
	CRIT_ITM_IND CHAR(1 char) default ' ' not null,
	NEV_OUT_SW CHAR(1 char) default ' ' not null,
	CTCH_WT_SW CHAR(1 char) default ' ' not null,
	LOW_VEL_SW CHAR(1 char) default ' ' not null,
	ONE_TOUCH_TYP_CD CHAR(2 char) default ' ' not null
		constraint ITEM_MASTER_FK7
			references EMD.ONE_TOUCH_TYP,
	PLUS_ITM_TYP_CD CHAR(1 char) default ' ' not null,
-- 		constraint ITEM_MASTER_FK8
-- 			references EMD.PLUS_ITM_TYP,
	SHPR_ITM_SW CHAR(1 char) default ' ' not null,
	SRS_HNDLG_CD CHAR(1 char) default ' ' not null,
	CROSS_DOCK_ITM_SW CHAR(1 char) default ' ' not null,
	RPLAC_ORD_QTY_SW CHAR(1 char) default ' ' not null,
	LTR_OF_CR_SW CHAR(1 char) default ' ' not null,
	VAR_WT_SW CHAR(1 char) default ' ' not null,
	FWDBY_APPR_SW CHAR(1 char) default ' ' not null,
	CATTLE_SW CHAR(1 char) default ' ' not null,
	HEB_ITM_PK NUMBER default 0 not null,
	RETL_SALS_PK NUMBER default 0 not null,
	DIM_CK_IND CHAR(1 char) default ' ' not null,
	AVG_WHLSL_CST NUMBER(11,4) default 0 not null,
	DSD_ITM_SW CHAR(1 char) default ' ' not null,
	UPC_MAP_SW CHAR(1 char) default ' ' not null,
	DEPOSIT_SW CHAR(1 char) default ' ' not null,
	LST_UPDT_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	LST_UPDT_USR_ID CHAR(8 char) default ' ' not null,
	DEPT_ID_1 NUMBER default 0 not null,
	SUB_DEPT_ID_1 CHAR(1 char) default ' ' not null,
	DEPT_MDSE_TYP_1 CHAR(1 char) default ' ' not null,
	PSS_DEPT_1 NUMBER default 0 not null,
	DEPT_ID_2 NUMBER default 0 not null,
	SUB_DEPT_ID_2 CHAR(1 char) default ' ' not null,
	DEPT_MDSE_TYP_2 CHAR(1 char) default ' ' not null,
	PSS_DEPT_2 NUMBER default 0 not null,
	DEPT_ID_3 NUMBER default 0 not null,
	SUB_DEPT_ID_3 CHAR(1 char) default ' ' not null,
	DEPT_MDSE_TYP_3 CHAR(1 char) default ' ' not null,
	PSS_DEPT_3 NUMBER default 0 not null,
	DEPT_ID_4 NUMBER default 0 not null,
	SUB_DEPT_ID_4 CHAR(1 char) default ' ' not null,
	DEPT_MDSE_TYP_4 CHAR(1 char) default ' ' not null,
	PSS_DEPT_4 NUMBER default 0 not null,
	GROSS_WT NUMBER(9,3) default 0 not null,
	UNSTAMPED_TBCO_SW CHAR(1 char) default ' ' not null,
	MRT_SW CHAR(1 char) default 'N' not null,
	DC_ID NUMBER default 0 not null,
	TBCO_TAX_SZ_UOM_CD CHAR(1 char) default ' ' not null,
	TBCO_TAX_SZ_QTY NUMBER(11,4) default 0 not null,
	DSPLY_RDY_PAL_SW CHAR(1 char) default 'N' not null,
	JV_ITM_AUTH_CD CHAR(1 char) default ' ' not null,
	SRS_AFF_TYP_CD CHAR(1 char) default ' ' not null,
	STD_SUBST_LOGIC_SW CHAR(1 char) default 'Y' not null,
	ITM_SZ_SPNSH_TXT CHAR(12 char) default ' ' not null,
	PROD_FCNG_NBR NUMBER default 0 not null,
	PROD_ROW_DEEP_NBR NUMBER default 0 not null,
	PROD_ROW_HI_NBR NUMBER default 0 not null,
	NBR_OF_ORINT_NBR NUMBER(3) default 0 not null,
	ITM_EXCSV_SW CHAR(1 char) default 'N' not null,
	LST_SYS_UPDT_ID NUMBER default 0 not null,
-- 		constraint ITEM_MASTER_FK10
-- 			references EMD.SRC_SYSTEM,
	ASSRTED_ITM_SW CHAR(1 char) default 'N' not null,
	VARIANT_CD CHAR(5 char) default ' ' not null,
	SW_CD CHAR(5 char) default ' ' not null,
	constraint PK_ITEM_MASTER
		primary key (ITM_KEY_TYP_CD, ITM_ID)
)
;

create unique index EMD.ITEM_MASTER_AK1
	on EMD.ITEM_MASTER (PD_OMI_COM_CLS_CD, PD_OMI_COM_CD, PD_OMI_SUB_COM_CD, ITM_ID, ITM_KEY_TYP_CD)
;

create unique index EMD.ITEM_MASTER_AK2
	on EMD.ITEM_MASTER (ORDERING_UPC, ITM_ID, ITM_KEY_TYP_CD)
;

create unique index EMD.ITEM_MASTER_AK3
	on EMD.ITEM_MASTER (ITM_ID, ITM_KEY_TYP_CD)
;

create index EMD.ITEM_MASTER_IE1
	on EMD.ITEM_MASTER (CASE_UPC)
;

create index EMD.ITEM_MASTER_IE2
	on EMD.ITEM_MASTER (ADDED_DT, ITM_ID, ITM_KEY_TYP_CD)
;

-----------------------------------------
-- ITEM_MASTER
-----------------------------------------
create table EMD.PROD_ITEM
(
	ITM_KEY_TYP_CD CHAR(5 char) default ' ' not null,
	ITM_ID NUMBER(17) default 0 not null,
	PROD_ID NUMBER default 0 not null
		constraint PROD_ITEM_FK2
			references EMD.PRODUCT_MASTER,
	RETL_PACK_QTY NUMBER default 0 not null,
	CRE8_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	CRE8_UID CHAR(8 char) default ' ' not null,
	constraint PK_PROD_ITEM
		primary key (ITM_KEY_TYP_CD, ITM_ID, PROD_ID),
	constraint PROD_ITEM_FK1
		foreign key (ITM_KEY_TYP_CD, ITM_ID) references EMD.ITEM_MASTER
)
;

create unique index EMD.PROD_ITEM_AK1
	on EMD.PROD_ITEM (PROD_ID, ITM_ID, ITM_KEY_TYP_CD)
;


-----------------------------------------
-- SCA
-----------------------------------------
create table EMD.SCA
(
	SCA_CD CHAR(5 char) default ' ' not null
		constraint PK_SCA
			primary key,
	SCA_FRST_NM CHAR(20 char) default ' ' not null,
	SCA_LST_NM CHAR(20 char) default ' ' not null,
	SCA_FULL_NM CHAR(30 char) default ' ' not null,
	OMI_SCA_NBR NUMBER(3) default 0 not null,
	SCA_IMS_CD CHAR(2 char) default ' ' not null,
	ACTV_SW CHAR(1 char) default ' ' not null,
	DIRECTOR_ID NUMBER default 0 not null,
-- 		constraint SCA_FK1
-- 			references EMD.DIRECTOR,
	PRIM_FUNC_CD_1 CHAR(10 char) default ' ' not null,
	SECY_FUNC_CD_2 CHAR(10 char) default ' ' not null,
	PRIM_GRP_NBR_1 NUMBER default 0 not null,
	SECY_GRP_NBR_2 NUMBER default 0 not null,
	LST_UPDT_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	LST_UPDT_USR_ID CHAR(8 char) default ' ' not null,
	SCA_EMAIL_ID CHAR(50 char) default ' ' not null,
	USR_ID CHAR(20 char) default ' ' not null
)
;

create index EMD.SCA_AK0
	on EMD.SCA (SCA_IMS_CD, SCA_CD)
;

-----------------------------------------
-- PROD_SCN_CODES
-----------------------------------------
create table EMD.PROD_SCN_CODES
(
	SCN_CD_ID NUMBER(17) default 0 not null
		constraint PK_PROD_SCN_CODES
			primary key,
	PROD_ID NUMBER default 0 not null
		constraint PROD_SCN_CODES_FK1
			references EMD.PRODUCT_MASTER,
	SCN_TYP_CD CHAR(5 char) default ' ' not null,
-- 		constraint PROD_SCN_CODES_FK4
-- 			references EMD.SCN_TYP_CD,
	PRIM_SCN_CD_SW CHAR(1 char) default ' ' not null,
	BNS_SCN_CD_SW CHAR(1 char) default ' ' not null,
	SCN_CD_CMT CHAR(30 char) default ' ' not null,
	RETL_UNT_LN NUMBER(7,2) default 0 not null,
	RETL_UNT_WD NUMBER(7,2) default 0 not null,
	RETL_UNT_HT NUMBER(7,2) default 0 not null,
	RETL_UNT_WT NUMBER(9,4) default 0 not null,
	RETL_SELL_SZ_CD_1 CHAR(2 char) default ' ' not null,
-- 		constraint PROD_SCN_CODES_FK5
-- 			references EMD.RTL_SELL_UNITS,
	RETL_UNT_SELL_SZ_1 NUMBER(7,2) default 0 not null,
	RETL_SELL_SZ_CD_2 CHAR(2 char) default ' ' not null,
-- 		constraint PROD_SCN_CODES_FK6
-- 			references EMD.RTL_SELL_UNITS,
	RETL_UNT_SELL_SZ_2 NUMBER(7,2) default 0 not null,
	SAMP_PROVD_SW CHAR(1 char) default 'N' not null,
	PRPRC_OFF_PCT NUMBER(9,4) default 0 not null,
	PROD_SUB_BRND_ID NUMBER default 0 not null,
-- 		constraint PROD_SCN_CODES_FK3
-- 			references EMD.PROD_SUB_BRND,
	FRST_SCN_DT DATE default to_date('1600-01-01','YYYY-MM-DD') not null,
	LST_SCN_DT DATE default to_date('1600-01-01','YYYY-MM-DD') not null,
	CONSM_UNT_ID NUMBER default 0 not null,
	TAG_ITM_ID NUMBER(17) default 0 not null,
	TAG_ITM_KEY_TYP_CD CHAR(5 char) default ' ' not null,
	TAG_SZ_DES CHAR(6 char) default ' ' not null,
	WIC_SW CHAR(1 char) default 'N' not null,
	LEB_SW CHAR(1 char) default 'N' not null,
	WIC_APL_ID NUMBER(17) default 0 not null,
	FAM_3_CD NUMBER(5) default 0 not null,
	FAM_4_CD NUMBER(5) default 0 not null,
	DSD_DELD_SW CHAR(1 char) default ' ' not null,
	DSD_DEPT_OVRD_SW CHAR(1 char) default ' ' not null,
	UPC_ACTV_SW CHAR(1 char) default ' ' not null,
	SCALE_SW CHAR(1 char) default ' ' not null,
	WIC_UPDT_USR_ID CHAR(8 char) default ' ' not null,
	WIC_LST_UPDT_TS TIMESTAMP(6) default to_date('1600-01-01 00:00:00','YYYY-MM-DD HH24:MI:SS') not null,
	LEB_UPDT_USR_ID CHAR(8 char) default ' ' not null,
	LEB_LST_UPDT_TS TIMESTAMP(6) default to_date('1600-01-01 00:00:00','YYYY-MM-DD HH24:MI:SS') not null,
	CRE8_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	CRE8_UID CHAR(8 char) default ' ' not null,
	LST_UPDT_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	LST_UPDT_UID CHAR(8 char) default ' ' not null,
	LST_SYS_UPDT_ID NUMBER default 0 not null,
-- 		constraint PROD_SCN_CODES_FK2
-- 			references EMD.SRC_SYSTEM,
	PSE_GRAMS_WT NUMBER(9,4) default 0 not null,
	TST_SCN_PRFMD_SW CHAR(1 char) default 'N' not null,
	DSCON_DT DATE default to_date('1600-01-01','YYYY-MM-DD') not null,
	PROC_SCN_MAINT_SW CHAR(1 char) default 'Y' not null
)
;

create unique index EMD.PROD_SCN_CODES_AK1
	on EMD.PROD_SCN_CODES (PROD_ID, SCN_CD_ID)
;

-----------------------------------------
-- PD_SHIPPER
-----------------------------------------
create table EMD.PD_SHIPPER
(
	PD_SHPR_UPC_NO NUMBER(13) not null
		constraint PD_SHIPPER_FK2
			references EMD.PD_ASSOCIATED_UPC,
	PD_UPC_NO NUMBER(13) not null
		constraint PD_SHIPPER_FK1
			references EMD.PD_ASSOCIATED_UPC,
	PD_SHPR_QTY NUMBER(5) not null,
	PD_SHPR_TYP_CD CHAR(1 char) not null,
	LST_UPDT_UID CHAR(20 char) default ' ' not null,
	constraint PK_PD_SHIPPER
		primary key (PD_UPC_NO, PD_SHPR_UPC_NO)
)
;

-----------------------------------------
-- WHSE_LOC_ITM
-----------------------------------------
create table EMD.WHSE_LOC_ITM
(
	ITM_KEY_TYP_CD CHAR(5 char) default ' ' not null,
	ITM_ID NUMBER(17) default 0 not null,
	WHSE_LOC_NBR NUMBER default 0 not null,
	WHSE_LOC_TYP_CD CHAR(2 char) default ' ' not null,
	SHP_FROM_TYP_CD CHAR(2 char) default ' ' not null,
	SHP_FROM_LOC_NBR NUMBER default 0 not null,
	SCA_CD CHAR(5 char) default ' ' not null
		constraint WHSE_LOC_ITM_FK12
			references EMD.SCA,
	SPLR_ITM_STATUS_CD CHAR(5 char) default ' ' not null,
-- 		constraint WHSE_LOC_ITM_FK15
-- 			references EMD.SPLR_ITM_STAT_CD,
	SPLR_STAT_USR_ID CHAR(8 char) default ' ' not null,
	SPLR_STAT_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	SPLR_ITM_RSN_CD CHAR(5 char) default ' ' not null,
-- 		constraint WHSE_LOC_ITM_FK14
-- 			references EMD.SPLR_ITM_RSN_CD,
	PRCH_STAT_CD CHAR(5 char) default ' ' not null,
-- 		constraint WHSE_LOC_ITM_FK11
-- 			references EMD.PURCHASE_STAT_CD,
	PRCH_STAT_USR_ID CHAR(8 char) default ' ' not null,
	PRCH_STAT_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	DSCON_TRX_SW CHAR(1 char) default ' ' not null,
	DSCON_DT DATE default SYSDATE not null,
	DSCON_RSN_CD CHAR(5 char) default ' ' not null
		constraint WHSE_LOC_ITM_FK5
			references EMD.DISCO_RSN_CD,
	DSCON_USR_ID CHAR(8 char) default ' ' not null,
	DSCON_CMT CHAR(50 char) default ' ' not null,
	NEW_ITM_RSN_CD CHAR(1 char) default ' ' not null,
	PO_CMT CHAR(30 char) default ' ' not null,
	CST_LNK_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	CST_LNK_USR_ID CHAR(8 char) default ' ' not null,
	USE_CST_LNK_SW CHAR(1 char) default ' ' not null,
	CST_LNK_ID NUMBER default 0 not null,
-- 		constraint MDWIMDCL
-- 			references EMD.CST_LINKS,
	MFG_ID CHAR(20 char) default ' ' not null,
	SHP_CS_LN NUMBER(7,2) default 0 not null,
	SHP_CS_WD NUMBER(7,2) default 0 not null,
	SHP_CS_HT NUMBER(7,2) default 0 not null,
	SHP_CU NUMBER(9,3) default 0 not null,
	SHP_NEST_CU NUMBER(9,3) default 0 not null,
	SHP_NEST_MAX_QTY NUMBER default 0 not null,
	SHP_WT NUMBER(9,3) default 0 not null,
	AVG_WT NUMBER(9,3) default 0 not null,
	PAL_TI NUMBER default 0 not null,
	PAL_TIER NUMBER default 0 not null,
	PAL_QTY NUMBER default 0 not null,
	SHP_INR_PK NUMBER(5) default 0 not null,
	LST_UPDT_DT DATE default SYSDATE not null,
	LST_UPDT_USR_ID CHAR(8 char) default ' ' not null,
	LST_UPDT_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	SRS_SCOPE_IND CHAR(1 char) default ' ' not null,
	VAR_WT_SW CHAR(1 char) default ' ' not null,
	CTCH_WT_SW CHAR(1 char) default ' ' not null,
	CMPTR_SETUP_IND CHAR(1 char) default ' ' not null,
	ITM_REMARK_SW CHAR(1 char) default ' ' not null,
	DALY_HIST_SW CHAR(1 char) default ' ' not null,
	TRNSFR_AVAIL_SW CHAR(1 char) default ' ' not null,
	PO_RCMD_SW CHAR(1 char) default ' ' not null,
	ORD_INRVL_DD NUMBER default 0 not null,
	ORD_QTY_TYP_CD CHAR(5 char) default ' ' not null,
-- 		constraint WHSE_LOC_ITM_FK9
-- 			references EMD.ORD_QTY_TYP_CD,
	ORD_PNT_QTY NUMBER default 0 not null,
	SAFE_STCK_QTY NUMBER default 0 not null,
	FCST_TYP_CD CHAR(5 char) default ' ' not null,
-- 		constraint WHSE_LOC_ITM_FK6
-- 			references EMD.FCST_TYP_CD,
	SESNL_IND_CD CHAR(5 char) default ' ' not null,
-- 		constraint WHSE_LOC_ITM_FK13
-- 			references EMD.SESNL_CD,
	VEND_TRGT_SVCLV NUMBER(5,2) default 0 not null,
	WHSE_TRGT_SVCLV NUMBER(5,2) default 0 not null,
	CS_UNT_FACTR_1 NUMBER(9,4) default 0 not null,
	CS_UNT_FACTR_2 NUMBER(9,4) default 0 not null,
	CS_MVT_PRWK NUMBER default 0 not null,
	CS_MVT_DESEAS_PRWK NUMBER default 0 not null,
	MAD_MVT_QTY NUMBER(9,3) default 0 not null,
	EXPCT_WKLY_MVT NUMBER default 0 not null,
	FCST_GRP CHAR(10 char) default ' ' not null,
	CURR_PKSLT CHAR(10 char) default ' ' not null,
	PAL_STK_LIM_QTY NUMBER default 0 not null,
	BULK_PCK_QTY NUMBER default 0 not null,
	PAL_SZ_CD CHAR(5 char) default ' ' not null,
-- 		constraint WHSE_LOC_ITM_FK10
-- 			references EMD.PAL_SZ,
	FLR_STK_IND CHAR(1 char) default ' ' not null,
	MAND_FLW_THRG_SW CHAR(1 char) default ' ' not null,
	FLW_THRGH_TYP_CD CHAR(1 char) default ' ' not null,
-- 		constraint WHSE_LOC_ITM_FK7
-- 			references EMD.FLW_THRU_TYP_CD,
	PRIOR_AVG_CST NUMBER(11,4) default 0 not null,
	CURR_AVG_CST NUMBER(11,4) default 0 not null,
	BIL_CST NUMBER(11,4) default 0 not null,
	SUGG_BILL_CST NUMBER(11,4) default 0 not null,
	MVT_PCT_CD CHAR(2 char) default ' ' not null,
	BILLABLE_QTY NUMBER default 0 not null,
	DAMAGED_QTY NUMBER default 0 not null,
	DSTRB_RES_QTY NUMBER default 0 not null,
	TOT_ON_HAND_QTY NUMBER default 0 not null,
	OFSIT_QTY NUMBER default 0 not null,
	ON_HLD_QTY NUMBER default 0 not null,
	PLUG_QTY NUMBER default 0 not null,
	CYCL_STCK_QTY NUMBER default 0 not null,
	TURN_INVEN_QTY NUMBER default 0 not null,
	PROMO_INVEN_QTY NUMBER default 0 not null,
	FWDBY_INVEN_QTY NUMBER default 0 not null,
	DEAL_ITM_IND CHAR(1 char) default ' ' not null,
	MAND_FLW_THRU_SW CHAR(1 char) default ' ' not null,
	FLW_TYP_CD CHAR(3 char) default ' ' not null,
-- 		constraint WHSE_LOC_ITM_FK8
-- 			references EMD.FLW_TYP,
	ADDED_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	ADDED_USR_ID CHAR(8 char) default ' ' not null,
	GNRC_OVRD_CLS NUMBER(3) default 0 not null,
	MAX_SHP_WHSE_QTY NUMBER default 99999 not null,
	OVRD_HEB_SHP_PK_QTY NUMBER(5) default 0 not null,
	REPL_SYS_CD CHAR(5 char) default ' ' not null,
	CONVEY_SW CHAR(1 char) default 'N' not null,
	BINABLE_SW CHAR(1 char) default 'N' not null,
	MAX_SHP_TO_EDC_QTY NUMBER default 0 not null,
	PO_EVNT_RES_QTY NUMBER default 0 not null,
	ADHOC_RES_QTY NUMBER default 0 not null,
	constraint PK_WHSE_LOC_ITM
		primary key (ITM_KEY_TYP_CD, ITM_ID, WHSE_LOC_TYP_CD, WHSE_LOC_NBR),
	constraint WHSE_LOC_ITM_FK1
		foreign key (ITM_KEY_TYP_CD, ITM_ID) references EMD.ITEM_MASTER
-- 	constraint WHSE_LOC_ITM_FK2
-- 		foreign key (WHSE_LOC_TYP_CD, WHSE_LOC_NBR) references EMD.LOCATION,
-- 	constraint WHSE_LOC_ITM_FK3
-- 		foreign key (SHP_FROM_TYP_CD, SHP_FROM_LOC_NBR) references EMD.LOCATION
)
;

create index EMD.WHSE_LOC_ITM_AK0
	on EMD.WHSE_LOC_ITM (SHP_FROM_TYP_CD, SHP_FROM_LOC_NBR)
;

create unique index EMD.WHSE_LOC_ITM_AK3
	on EMD.WHSE_LOC_ITM (WHSE_LOC_TYP_CD, WHSE_LOC_NBR, ITM_KEY_TYP_CD, ITM_ID)
;

create unique index EMD.WHSE_LOC_ITM_AK2
	on EMD.WHSE_LOC_ITM (ITM_ID, ITM_KEY_TYP_CD, WHSE_LOC_TYP_CD, WHSE_LOC_NBR)
;

create unique index EMD.WHSE_LOC_ITM_AK1
	on EMD.WHSE_LOC_ITM (CST_LNK_ID, ITM_ID, ITM_KEY_TYP_CD, WHSE_LOC_TYP_CD, WHSE_LOC_NBR)
;

-----------------------------------------
-- WHSE_LOC_ITM
-----------------------------------------
create table EMD.CST_PAST_PRES_FUTR
(
	ITM_PROD_ID NUMBER(17) default 0 not null,
	ITM_PROD_KEY_CD CHAR(5 char) default ' ' not null,
-- 		constraint CST_PAST_PRES_FUTR_FK5
-- 			references EMD.ITM_PROD_KEY_CODE,
	VEND_LOC_TYP_CD CHAR(2 char) default ' ' not null,
	VEND_LOC_NBR NUMBER default 0 not null,
	DEAL_LOC_TYP_CD CHAR(5 char) default ' ' not null,
	DEAL_LOC_KEY CHAR(30 char) default ' ' not null,
	CST_TYP_CD CHAR(5 char) default ' ' not null,
	SEQ_NBR NUMBER(15) default 0 not null,
	DEAL_UNT_BAS_CD CHAR(5 char) default ' ' not null,
	CST_AMT NUMBER(11,4) default 0 not null,
	CST_EFF_DT DATE default SYSDATE not null,
	CST_EFF_TM TIMESTAMP(0) default to_date('1700-01-01 00:00:00','YYYY-MM-DD HH24:MI:SS') not null,
	CST_EXPRN_DT DATE default SYSDATE not null,
	CST_EXPRN_TM TIMESTAMP(0) default to_date('1700-01-01 00:00:00','YYYY-MM-DD HH24:MI:SS') not null,
	RETL_EFF_DT DATE default SYSDATE not null,
	RETL_EXPR_DT DATE default SYSDATE not null,
	REV_RETL_SW CHAR(1 char) default ' ' not null,
	OFFER_ID NUMBER default 0 not null,
	DEAL_HDR_NBR NUMBER default 0 not null,
	CONTRACT_NBR NUMBER default 0 not null,
	CRE8_UID CHAR(20 char) default ' ' not null,
	CRE8_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	LST_UPDT_UID CHAR(20 char) default ' ' not null,
	LST_UPDT_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	DEAL_AMT_BAS_CD CHAR(5 char) default 'AMT' not null,
	CST_CHG_STAT_CD CHAR(5 char) default ' ' not null,
	CST_CHG_STAT_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	RC_BFS_SEQ_NBR NUMBER default 0 not null,
	RC_BFS_JLN_DT NUMBER(7) default 0 not null,
	CST_CHG_RSN_CD CHAR(1 char) default ' ' not null,
	constraint PK_CST_PAST_PRES_FUTR
		primary key (ITM_PROD_ID, ITM_PROD_KEY_CD, VEND_LOC_TYP_CD, VEND_LOC_NBR, DEAL_LOC_TYP_CD, DEAL_LOC_KEY, CST_TYP_CD, SEQ_NBR)
)
;

create unique index EMD.CST_PAST_PRES_FUTR_AK2
	on EMD.CST_PAST_PRES_FUTR (CST_EFF_DT, VEND_LOC_NBR, VEND_LOC_TYP_CD, DEAL_LOC_KEY, DEAL_LOC_TYP_CD, ITM_PROD_ID, ITM_PROD_KEY_CD, CST_TYP_CD, SEQ_NBR, CST_EXPRN_DT)
;

create unique index EMD.CST_PAST_PRES_FUTR_AK1
	on EMD.CST_PAST_PRES_FUTR (CST_EXPRN_DT, VEND_LOC_NBR, VEND_LOC_TYP_CD, DEAL_LOC_KEY, DEAL_LOC_TYP_CD, ITM_PROD_ID, ITM_PROD_KEY_CD, CST_TYP_CD, SEQ_NBR, CST_EFF_DT)
;

create unique index EMD.CST_PAST_PRES_FUTR_AK3
	on EMD.CST_PAST_PRES_FUTR (DEAL_LOC_KEY, DEAL_LOC_TYP_CD, CST_EFF_DT, ITM_PROD_ID, ITM_PROD_KEY_CD, VEND_LOC_NBR, VEND_LOC_TYP_CD, CST_TYP_CD, SEQ_NBR, CST_EXPRN_DT)
;

create unique index EMD.CST_PAST_PRES_FUTR_AK4
	on EMD.CST_PAST_PRES_FUTR (DEAL_LOC_TYP_CD, DEAL_LOC_KEY, ITM_PROD_ID, ITM_PROD_KEY_CD, VEND_LOC_NBR, VEND_LOC_TYP_CD, CST_TYP_CD, SEQ_NBR)
;

create unique index EMD.CST_PAST_PRES_FUTR_AK5
	on EMD.CST_PAST_PRES_FUTR (VEND_LOC_NBR, VEND_LOC_TYP_CD, DEAL_LOC_TYP_CD, DEAL_LOC_KEY, ITM_PROD_ID, ITM_PROD_KEY_CD, CST_TYP_CD, SEQ_NBR)
;

-----------------------------------------
-- VEND_LOC_ITM
-----------------------------------------
create table EMD.VEND_LOC_ITM
(
	ITM_KEY_TYP_CD CHAR(5 char) default ' ' not null,
	ITM_ID NUMBER(17) default 0 not null,
	VEND_LOC_TYP_CD CHAR(2 char) default ' ' not null,
	VEND_LOC_NBR NUMBER default 0 not null,
	VEND_ITM_ID CHAR(20 char) default ' ' not null,
	VEND_LN NUMBER(7,2) default 0 not null,
	VEND_WD NUMBER(7,2) default 0 not null,
	VEND_HT NUMBER(7,2) default 0 not null,
	VEND_WT NUMBER(7,2) default 0 not null,
	VEND_CU NUMBER(9,4) default 0 not null,
	VEND_NEST_CU NUMBER(9,3) default 0 not null,
	VEND_NEST_MAX NUMBER default 0 not null,
	VEND_PAL_TIE NUMBER default 0 not null,
	VEND_PAL_TIER NUMBER default 0 not null,
	VEND_PAL_QTY NUMBER default 0 not null,
	VEND_PAL_SZ CHAR(1 char) default ' ' not null,
	VEND_LIST_CST NUMBER(11,4) default 0 not null,
	VEND_PK_QTY NUMBER default 0 not null,
	INV_SEQ_NBR NUMBER default 0 not null,
	ORD_QTY_RSTR_CD CHAR(5 char) default ' ' not null,
-- 		constraint VEND_LOC_ITM_FK3
-- 			references EMD.VEND_ORD_RSTR,
	SCA_CD CHAR(5 char) default ' ' not null
		constraint VEND_LOC_ITM_FK7
			references EMD.SCA,
	CNTRY_OF_ORIG_ID NUMBER default 0 not null,
-- 		constraint VEND_LOC_ITM_FK4
-- 			references EMD.CNTRY_CD,
	CST_OWN_ID NUMBER default 0 not null,
-- 		constraint VEND_LOC_ITM_FK5
-- 			references EMD.CST_OWN,
	CST_LNK_ID NUMBER default 0 not null,
-- 		constraint MDVIMDCL
-- 			references EMD.CST_LINKS,
	DC_ID NUMBER default 0 not null,
	LST_UPDT_UID CHAR(20 char) default ' ' not null,
	ORD_PROCNG_CTOF_TM TIMESTAMP(0) default to_date('1700-01-01 12:00:01','YYYY-MM-DD HH24:MI:SS') not null,
	ORD_LEAD_TM TIMESTAMP(0) default to_date('1700-01-01 12:00:01','YYYY-MM-DD HH24:MI:SS') not null,
	constraint PK_VEND_LOC_ITM
		primary key (ITM_KEY_TYP_CD, ITM_ID, VEND_LOC_TYP_CD, VEND_LOC_NBR),
	constraint VEND_LOC_ITM_FK1
		foreign key (ITM_KEY_TYP_CD, ITM_ID) references EMD.ITEM_MASTER,
	constraint VEND_LOC_ITM_FK2
		foreign key (VEND_LOC_TYP_CD, VEND_LOC_NBR) references EMD.LOCATION
)
;

create index EMD.VEND_LOC_ITM_AK4
	on EMD.VEND_LOC_ITM (CST_LNK_ID)
;

create unique index EMD.VEND_LOC_ITM_AK3
	on EMD.VEND_LOC_ITM (VEND_LOC_TYP_CD, VEND_LOC_NBR, ITM_KEY_TYP_CD, ITM_ID)
;

create unique index EMD.VEND_LOC_ITM_AK2
	on EMD.VEND_LOC_ITM (VEND_LOC_NBR, VEND_LOC_TYP_CD, ITM_ID, ITM_KEY_TYP_CD)
;

create unique index EMD.VEND_LOC_ITM_AK1
	on EMD.VEND_LOC_ITM (ITM_ID, ITM_KEY_TYP_CD, VEND_LOC_TYP_CD, VEND_LOC_NBR)
;

-----------------------------------------
-- ITM_WHSE_VEND
-----------------------------------------
create table EMD.ITM_WHSE_VEND
(
	ITM_KEY_TYP_CD CHAR(5 char) default ' ' not null,
	ITM_ID NUMBER(17) default 0 not null,
	VEND_LOC_TYP_CD CHAR(2 char) default ' ' not null,
	WHSE_LOC_TYP_CD CHAR(2 char) default ' ' not null,
	VEND_LOC_NBR NUMBER default 0 not null,
	WHSE_LOC_NBR NUMBER default 0 not null,
	PRIM_VEND_SW CHAR(1 char) default ' ' not null,
	FRT_EXCP_SW CHAR(1 char) default ' ' not null,
	TERMS_EXCP_SW CHAR(1 char) default ' ' not null,
	ITM_UP_DN_CST NUMBER(11,4) default 0 not null,
	UP_DN_CST_CMT CHAR(30 char) default ' ' not null,
	UP_DN_POS_OR_NEG CHAR(1 char) default ' ' not null,
	FRT_FREE_SW CHAR(1 char) default ' ' not null,
	TOT_ALLOW_AMT NUMBER(11,4) default 0 not null,
	MFG_SRP_AMT NUMBER(11,4) default 0 not null,
	SELL_AMT NUMBER(11,4) default 0 not null,
	FRT_BIL_AMT NUMBER(11,4) default 0 not null,
	BKHL_AMT NUMBER(11,4) default 0 not null,
	PPADD_EXCP_SW CHAR(1 char) default ' ' not null,
	FRT_ALLOW_AMT NUMBER(11,4) default 0 not null,
	constraint PK_ITM_WHSE_VEND
		primary key (ITM_KEY_TYP_CD, ITM_ID, VEND_LOC_TYP_CD, VEND_LOC_NBR, WHSE_LOC_TYP_CD, WHSE_LOC_NBR),
	constraint ITM_WHSE_VEND_FK1
		foreign key (ITM_KEY_TYP_CD, ITM_ID, VEND_LOC_TYP_CD, VEND_LOC_NBR) references EMD.VEND_LOC_ITM,
	constraint ITM_WHSE_VEND_FK2
		foreign key (ITM_KEY_TYP_CD, ITM_ID, WHSE_LOC_TYP_CD, WHSE_LOC_NBR) references EMD.WHSE_LOC_ITM
)
;

create unique index EMD.ITM_WHSE_VEND_AK1
	on EMD.ITM_WHSE_VEND (ITM_ID, ITM_KEY_TYP_CD, VEND_LOC_TYP_CD, VEND_LOC_NBR, WHSE_LOC_TYP_CD, WHSE_LOC_NBR)
;

create unique index EMD.ITM_WHSE_VEND_AK2
	on EMD.ITM_WHSE_VEND (VEND_LOC_TYP_CD, VEND_LOC_NBR, ITM_KEY_TYP_CD, ITM_ID, WHSE_LOC_TYP_CD, WHSE_LOC_NBR)
;

create unique index EMD.ITM_WHSE_VEND_AK3
	on EMD.ITM_WHSE_VEND (WHSE_LOC_TYP_CD, WHSE_LOC_NBR, ITM_KEY_TYP_CD, ITM_ID, VEND_LOC_TYP_CD, VEND_LOC_NBR)
;

create unique index EMD.ITM_WHSE_VEND_AK4
	on EMD.ITM_WHSE_VEND (ITM_KEY_TYP_CD, ITM_ID, WHSE_LOC_TYP_CD, WHSE_LOC_NBR, VEND_LOC_TYP_CD, VEND_LOC_NBR)
;

-----------------------------------------
-- PS_CAND_RQST
-----------------------------------------
CREATE TABLE EMD.PS_CAND_RQST
(
    RQSTOR_UNIQ_ID       VARCHAR2(40 CHAR) NOT NULL,
    PD_SETUP_STAT_CD     CHAR(5 CHAR) NOT NULL,
    TRX_TRKG_ID          INTEGER NOT NULL,
    RESP_URI             VARCHAR2(255 CHAR) NULL,
    ERR_TXT              VARCHAR2(4000 CHAR) NULL,
    CRE8_ID              CHAR(20 CHAR) NOT NULL,
    CRE8_TS              TIMESTAMP(6) NOT NULL,
    CAND_PROD_JSON       VARCHAR2(32767 CHAR) NULL
);

CREATE UNIQUE INDEX EMD.PK_PS_CAND_RQST ON EMD.PS_CAND_RQST
    (RQSTOR_UNIQ_ID ASC);

ALTER TABLE EMD.PS_CAND_RQST
    ADD CONSTRAINT  PK_PS_CAND_RQST PRIMARY KEY (RQSTOR_UNIQ_ID);


-----------------------------------------
-- PD_REGULAR_PRICE
-----------------------------------------
create table EMD.PD_REGULAR_PRICE
(
	PD_UPC_NO NUMBER(13) default 0 not null,
	PD_ZONE_NO NUMBER(5) default 0 not null,
	PD_PRICE_EFF_DT DATE default SYSDATE not null,
	PD_RETAIL_PRC NUMBER(7,2) default 0 not null,
	PD_XFOR_QTY NUMBER(3) default 0 not null,
	PD_WEIGHT_SW CHAR(1 char) default ' ' not null,
	PD_CHANGE_TYPE_CD CHAR(1 char) default ' ' not null,
	PD_TAG_CHG_TYPE_CD CHAR(1 char) default ' ' not null,
	PD_AD_SW CHAR(1 char) default ' ' not null,
	PD_EXCEPTION_SW CHAR(1 char) default ' ' not null,
	constraint PK_PD_REGULAR_PRICE
		primary key (PD_UPC_NO, PD_ZONE_NO, PD_PRICE_EFF_DT)
)
;

