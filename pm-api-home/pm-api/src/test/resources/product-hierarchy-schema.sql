create table EMD.BDM
(
	BDM_CD CHAR(5 char) default ' ' not null
		constraint PK_BDM
			primary key,
	BDM_FRST_NM CHAR(20 char) default ' ' not null,
	DBM_LST_NM CHAR(20 char) default ' ' not null,
	BDM_FULL_NM CHAR(30 char) default ' ' not null,
	BDM_OMI_NBR NUMBER(3) default 0 not null,
	BDM_IMS_CD CHAR(2 char) default ' ' not null,
	ACTV_SW CHAR(1 char) default ' ' not null,
	DIRECTOR_ID NUMBER default 0 not null,
-- 		constraint BDM_FK1
-- 			references EMD.DIRECTOR,
	PRIM_FUNC_CD_1 CHAR(10 char) default ' ' not null,
	PRIM_FUNC_CD_2 CHAR(10 char) default ' ' not null,
	PRIM_GRP_NBR_1 NUMBER default 0 not null,
	PRIM_GRP_NBR_2 NUMBER default 0 not null,
	LST_UPDT_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	LST_UPDT_USR_ID CHAR(8 char) default ' ' not null,
	BDM_EMAIL_ID CHAR(50 char) default ' ' not null,
	USR_ID CHAR(20 char) default ' ' not null,
	GL_ACCT_NBR CHAR(30 char) default ' ' not null
)
;

create table EMD.STR_DEPT
(
	STR_DEPT_NBR CHAR(5 char) default ' ' not null,
	STR_SUB_DEPT_ID CHAR(5 char) default ' ' not null,
	DEPT_NM CHAR(30 char) default ' ' not null,
	DEPT_ABB CHAR(6 char) default ' ' not null,
	REPT_GRP_CD NUMBER default 0 not null,
	GRPRFT_LO_PCT NUMBER(7,4) default 0 not null,
	GRPRFT_HI_PCT NUMBER(7,4) default 0 not null,
	SHRNK_LO_PCT NUMBER(7,4) default 0 not null,
	SHRNK_HI_PCT NUMBER(7,4) default 0 not null,
	LST_UPDT_USR_ID CHAR(8 char) default ' ' not null,
	LST_UPDT_TS TIMESTAMP(6) default systimestamp not null,
	ORG_ID NUMBER default 0 not null,
	constraint PK_STR_DEPT
		primary key (STR_DEPT_NBR, STR_SUB_DEPT_ID)
)
;

create table EMD.ITM_CLS
(
	ITEM_CLS_CODE NUMBER(3) default 0 not null
		constraint PK_ITM_CLS
			primary key,
	ITM_CLS_DES CHAR(30 char) default ' ' not null,
	GNRC_CLS NUMBER(3) default 0 not null,
	SCN_DEPT NUMBER(5) default 0 not null,
	DTA_CHKR_DEPT NUMBER(5) default 0 not null,
	SCN_MAINT_GRP NUMBER(3) default 0 not null,
	MIX_MAT_GRP NUMBER(3) default 0 not null,
	BIL_AT_AD_SW CHAR(1 char) default ' ' not null,
	DEPT_ID NUMBER(5) default 0 not null,
	SUB_DEPT_ID CHAR(3 char) default ' ' not null,
	BIL_CST_ELIG_SW CHAR(1 char) default ' ' not null,
	CLS_DIRECTOR_ID NUMBER(3) default 0 not null,
	CLS_VAR_AMT NUMBER(7,2) default 0 not null,
	MERCH_TYP_CD CHAR(1 char) default ' ' not null,
-- 		constraint ITM_CLS_FK1
-- 			references EMD.MERCH_TYP_CD,
	BEG_AD_LEAD_TM_DD NUMBER(3) default 0 not null,
	END_AD_LEAD_TM_DD NUMBER(3) default 0 not null,
	AD_PRC_CHG_PCT NUMBER(5,2) default 0 not null,
	PRC_BUL_CD CHAR(5 char) default ' ' not null,
	PRC_CHG_PCT NUMBER(5) default 0 not null,
	CLS_LO_GRMGN NUMBER(7,3) default 0 not null,
	CLS_HI_GRMGN NUMBER(7,3) default 0 not null,
	ACTV_SW CHAR(1 char) default ' ' not null,
	DFLT_SALS_RSTR_CD CHAR(5 char) default 'N' not null,
-- 		constraint ITM_CLS_FK3
-- 			references EMD.SALS_RSTR_CODE,
	PROD_TEMP_CNTL_CD CHAR(5 char)
-- 		constraint ITM_CLS_FK2
-- 			references EMD.PROD_TEMP_CNTL
)
;

create table EMD.PD_CLASS_COMMODITY
(
	PD_OMI_COM_CLS_CD NUMBER(3) not null,
	PD_OMI_COM_CD NUMBER(5) not null,
	PD_OMI_COM_DES CHAR(30 char) not null,
	PD_OMI_DIR_CD NUMBER(3) not null,
	PD_APPR_QA_CD CHAR(1 char) not null,
	PD_NIELS_CD NUMBER(5) not null,
	PC_CLS_COM_ACTV_CD CHAR(1 char) not null,
	PD_PSS_DEPT_NO NUMBER(3) not null,
	LOG_COUNTER NUMBER(15) not null,
	BDM_CD CHAR(5 char) default ' ' not null,
	IMS_COM_CD NUMBER(3) default 0 not null,
	ALLOW_MULT_VEND_SW CHAR(1 char) default 'Y' not null,
	ENFORCE_PACK_SZ_SW CHAR(1 char) default 'Y' not null,
	DFLT_SALS_RSTR_CD CHAR(5 char) default 'N' not null,
	ECOMM_BUS_MGR_ID CHAR(20 char),
	DFLT_SUBSCR_PD_SW CHAR(1 char) default 'N' not null,
	BDA_UID CHAR(20 char) default ' ' not null,
	DFLT_TMPLT_ID CHAR(20 char),
-- 		constraint PD_CLASS_COMMODITY_FK1
-- 			references EMD.USR_INRFC_TMPLT,
	MAX_CUST_ORD_QTY NUMBER default 0 not null,
	CRE8_ID CHAR(20 char) default ' ' not null,
	CRE8_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	LST_UPDT_UID CHAR(20 char) default ' ' not null,
	LST_UPDT_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	constraint PK_PD_CLASS_COMMODITY
		primary key (PD_OMI_COM_CLS_CD, PD_OMI_COM_CD)
)
;

create table EMD.PROD_CAT
(
	PROD_CAT_ID NUMBER default 0 not null
		constraint PK_PROD_CAT
			primary key,
	PROD_CAT_ABB CHAR(6 char) default ' ' not null,
	PROD_CAT_NM CHAR(50 char) default ' ' not null,
	MKT_CONSM_EVNT_CD CHAR(5 char) default ' ' not null,
-- 		constraint PROD_CAT_FK2
-- 			references EMD.MKT_CONSM_EVNT_TYP,
	PROD_CAT_ROLE_CD CHAR(5 char) default ' ' not null,
-- 		constraint PROD_CAT_FK1
-- 			references EMD.PROD_CAT_ROLE,
	CRE8_ID CHAR(20 char) default ' ' not null,
	CRE8_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	LST_UPDT_UID CHAR(20 char) default ' ' not null,
	LST_UPDT_TS TIMESTAMP(6) default SYSTIMESTAMP not null
)
;


create unique index EMD.PD_CLASS_COMMODITY_AK1
	on EMD.PD_CLASS_COMMODITY (PD_OMI_COM_CD, PD_OMI_COM_CLS_CD);

	create table EMD.PD_CLS_COM_SUB_COM
(
	PD_OMI_COM_CLS_CD NUMBER(3) not null,
	PD_OMI_COM_CD NUMBER(5) not null,
	PD_OMI_SUB_COM_CD NUMBER(5) not null,
	PD_ITEM_CLASS_CD NUMBER(3) not null,
	PD_COM_CD NUMBER(3) not null,
	PD_SUB_COM_CD CHAR(1 char) not null,
	PD_OMI_COM_DES CHAR(30 char) not null,
	PC_SUB_COM_ACTV_CD CHAR(1 char) not null,
	RL_SUB_COM_HGM_PCT NUMBER(5,1) not null,
	RL_SUB_COM_LGM_PCT NUMBER(5,1) not null,
	PD_LABR_CAT_CD CHAR(2 char) not null,
	PD_FD_STAMP_CD CHAR(1 char) not null,
	PD_CRG_TAX_CD CHAR(1 char) not null,
	LOG_COUNTER NUMBER(15) not null,
	DFLT_RETL_SELL_CD CHAR(2 char) default ' ' not null,
-- 		constraint PD_CLS_COM_SUB_COM_FK3
-- 			references EMD.RTL_SELL_UNITS,
	PROD_CAT_ID NUMBER default 0 not null
		constraint PD_CLS_COM_SUB_COM_FK2
			references EMD.PROD_CAT,
	DF_MAX_SHLF_LIF_DD NUMBER default 0 not null,
	DF_INBND_SPCFN_DD NUMBER(5) default 0 not null,
	DF_REACT_DD NUMBER(5) default 0 not null,
	DF_GUARN_TO_STR_DD NUMBER(5) default 0 not null,
	DF_GIFT_MSG_REQ_SW CHAR(1 char) default 'N' not null,
	VERTEX_TAX_CAT_CD CHAR(40 char) default ' ' not null,
	CRE8_ID CHAR(20 char) default ' ' not null,
	CRE8_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	LST_UPDT_UID CHAR(20 char) default ' ' not null,
	LST_UPDT_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	VERTEX_NON_TAX_CD CHAR(40 char) default ' ' not null,
	constraint PK_PD_CLS_COM_SUB_COM
		primary key (PD_OMI_COM_CLS_CD, PD_OMI_COM_CD, PD_OMI_SUB_COM_CD),
	constraint PD_CLS_COM_SUB_COM_FK1
		foreign key (PD_OMI_COM_CLS_CD, PD_OMI_COM_CD) references EMD.PD_CLASS_COMMODITY
)
;

create unique index EMD.PD_CLS_COM_SUB_COM_AK1
	on EMD.PD_CLS_COM_SUB_COM (PD_ITEM_CLASS_CD, PD_COM_CD, PD_SUB_COM_CD)
;

create unique index EMD.PD_CLS_COM_SUB_COM_AK2
	on EMD.PD_CLS_COM_SUB_COM (PD_OMI_COM_CD, PD_SUB_COM_CD, PD_OMI_COM_CLS_CD, PD_OMI_SUB_COM_CD)
;

create unique index EMD.PD_CLS_COM_SUB_COM_AK3
	on EMD.PD_CLS_COM_SUB_COM (PD_OMI_COM_CLS_CD, PD_COM_CD, PD_SUB_COM_CD, PD_OMI_COM_CD, PD_OMI_SUB_COM_CD)
;

create unique index EMD.PD_CLS_COM_SUB_COM_AK4
	on EMD.PD_CLS_COM_SUB_COM (DFLT_RETL_SELL_CD, PD_OMI_COM_CLS_CD, PD_OMI_COM_CD, PD_OMI_SUB_COM_CD)
;



