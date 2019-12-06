create table EMD.NUTRIENT_MASTER
(
	NTRNT_MST_ID NUMBER not null
		constraint PK_NUTRIENT_MASTER
			primary key,
	NTRNT_NM CHAR(150 char) default ' ' not null,
	STD_NTRNT_SW CHAR(1 char) default ' ' not null,
	XTRNL_ID VARCHAR2(40 char),
	NTRNT_TXT VARCHAR2(500 char),
	SRC_SYSTEM_ID NUMBER,
	SEQ_NBR NUMBER,
	NTRNT_TYP_CD CHAR(5 char) default ' ' not null
)
;


create table EMD.NUTRIENT
(
	SCN_CD_ID NUMBER(17) not null,
	NTRNT_MST_ID NUMBER not null,
	VAL_PREPRD_TYP_CD NUMBER(3) not null,
	SRC_SYSTEM_ID NUMBER default 7 not null,
	NTRNT_QTY NUMBER(9,4) default 0 not null,
	SRVNG_SZ_UOM_CD CHAR(5 char) default ' ' not null,
	DALY_VAL_SRVNG_PCT NUMBER(11,4),
	DCLR_ON_LBL_SW CHAR(1 char) default ' ' not null,
	NTRNT_MEASR_TXT CHAR(20 char) default ' ' not null,
	CRE8_ID CHAR(20 char) default ' ' not null,
	CRE8_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	LST_UPDT_UID CHAR(20 char) default ' ' not null,
	LST_UPDT_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	constraint PK_NUTRIENT
		primary key (SCN_CD_ID, NTRNT_MST_ID, VAL_PREPRD_TYP_CD, SRC_SYSTEM_ID)
)
;

create table EMD.SRVNG_SZ_UOM
(
	SRVNG_SZ_UOM_CD CHAR(5 char) not null
		constraint PK_SRVNG_SZ_UOM
			primary key,
	SRVNG_SZ_UOM_ABB CHAR(6 char) default ' ' not null,
	SRVNG_SZ_UOM_DES CHAR(50 char) default ' ' not null,
	SRC_SYSTEM_ID NUMBER,
	XTRNL_ID VARCHAR2(40 char) default ' ' not null
)
;



create table EMD.MST_DTA_EXTN_ATTR
(
	ATTR_ID NUMBER not null,
	KEY_ID NUMBER(18) not null,
	ITM_PROD_KEY_CD CHAR(5 char) not null,
	SEQ_NBR NUMBER(5) not null,
	DTA_SRC_SYS NUMBER not null,
	ATTR_CD_ID NUMBER,
	ATTR_VAL_TXT VARCHAR2(10000 char),
	ATTR_VAL_NBR NUMBER(18,4),
	ATTR_VAL_DT DATE,
	ATTR_VAL_TS TIMESTAMP(6),
	CRE8_UID CHAR(20 char) default ' ' not null,
	CRE8_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	LST_UPDT_UID CHAR(20 char) default ' ' not null,
	LST_UPDT_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	PRIM_SRC_SYS_ID NUMBER,
	constraint PK_MST_DTA_EXTN_ATTR
		primary key (ATTR_ID, KEY_ID, ITM_PROD_KEY_CD, SEQ_NBR, DTA_SRC_SYS)
)
;

create table EMD.PROD_PK_VARIATION
(
	SCN_CD_ID NUMBER(17) not null,
	SEQ_NBR NUMBER(5) not null,
	SRC_SYSTEM_ID NUMBER default 7 not null,
	PROD_VAL_DES CHAR(255 char) default ' ' not null,
	SRVNG_SZ_QTY NUMBER(9,4) default 0 not null,
	SRVNG_SZ_UOM_CD CHAR(5 char) default ' ' not null,
	MIN_SPCR_QTY NUMBER(9,4) default 0 not null,
	MAX_SPCR_QTY NUMBER(9,4) default 0 not null,
	PREPRD_PROD_SW CHAR(1 char) default ' ' not null,
	CRE8_ID CHAR(20 char) default ' ' not null,
	CRE8_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	LST_UPDT_UID CHAR(20 char) default ' ' not null,
	LST_UPDT_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	HSHLD_SRVNG_SZ_TXT CHAR(70 char) default ' ' not null,
	PROD_PAN_TYP_CD CHAR(5 char) default ' ' not null,
	SPCR_TXT CHAR(50 char) default ' ' not null,
	NTRNT_PAN_NBR NUMBER(5),
	CLRS_QTY NUMBER,
	constraint PK_PROD_PK_VARIATION
		primary key (SCN_CD_ID, SEQ_NBR, SRC_SYSTEM_ID)
)
;



create table EMD.SRC_SYSTEM
(
	SRC_SYSTEM_ID NUMBER default 0 not null
		constraint PK_SRC_SYSTEM
			primary key,
	SRC_SYSTEM_ABB CHAR(5 char) default ' ' not null,
	SRC_SYSTEM_DES CHAR(50 char) default ' ' not null
)
;


