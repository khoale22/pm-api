create sequence EMD.TRX_TRACKING_SEQ
	minvalue 0
;

create sequence EMD.PS_WORK_RQST_SEQ
	minvalue 0
;

create sequence EMD.PS_ITEM_MASTER_SEQ
	minvalue 0
;

create sequence EMD.PS_PRODUCT_MASTER_SEQ
	minvalue 0
;

create table EMD.VENDOR_LOCATION
(
	LOC_NBR NUMBER default 0 not null,
	LOC_TYP_CD CHAR(2 char) default ' ' not null,
	CONNECTING_LOC_TYP CHAR(2 char),
	CONNECTING_LOC_NBR NUMBER,
	VEND_STRTGY_CD CHAR(2 char) default ' ' not null,
	VEND_STRTGY_DT DATE default sysdate not null,
	VEND_CLS_CD CHAR(5 char) default ' ' not null,
	WHSE_CD CHAR(2 char) default ' ' not null,
	CNTL_NBR NUMBER default 0 not null,
	VEND_GRP_NBR NUMBER default 0 not null,
	DEA_NBR CHAR(9 char) default ' ' not null,
	DUNNS_NBR NUMBER default 0 not null,
	TAX_ID CHAR(12 char) default ' ' not null,
	VEND_CHGD_IND CHAR(1 char) default ' ' not null,
	PLP_IND CHAR(1 char) default ' ' not null,
	CNSDTD_VEND_SW CHAR(1 char) default ' ' not null,
	BKHL_SW CHAR(1 char) default ' ' not null,
	FRT_BIL_SW CHAR(1 char) default ' ' not null,
	PREPD_SW CHAR(1 char) default ' ' not null,
	PREPD_AND_ADD_SW CHAR(1 char) default ' ' not null,
	RAIL_SW CHAR(1 char) default ' ' not null,
	TRUK_SW CHAR(1 char) default ' ' not null,
	WATER_SW CHAR(1 char) default ' ' not null,
	AIR_SW CHAR(1 char) default ' ' not null,
	HOW_TO_SHP_DT DATE default sysdate not null,
	CARR_CMT CHAR(50 char) default ' ' not null,
	VEND_NTE CHAR(50 char) default ' ' not null,
	VEND_REMRK_SW CHAR(1 char) default ' ' not null,
	LST_RCPT_DT DATE default sysdate not null,
	LST_OPEN_PO_NBR NUMBER default 0 not null,
	LST_OPEN_PO_STAT CHAR(1 char) default ' ' not null,
	OPEN_PO_CNT NUMBER default 0 not null,
	LEAD_TM_AVG_WKS NUMBER(7,4) default 0 not null,
	LEAD_TM_STD_WKS NUMBER(7,4) default 0 not null,
	LEAD_TM_VAR NUMBER(7,4) default 0 not null,
	ORD_INRVL_WKS NUMBER(7,4) default 0 not null,
	ORD_SEQ_NBR NUMBER default 0 not null,
	BKOR_ALLWD_SW CHAR(1 char) default ' ' not null,
	CURR_BRKT_NBR NUMBER default 0 not null,
	MAX_ORD_QTY NUMBER default 0 not null,
	MAX_ORD_QTY_TYP CHAR(1 char) default ' ' not null,
	PO_RCMD_SW CHAR(1 char) default ' ' not null,
	PO_PRNT_MNFST_SW CHAR(1 char) default ' ' not null,
	FWD_BUY_APPRD_SW CHAR(1 char) default ' ' not null,
	TRGT_SVC_LVL_PCT NUMBER(3) default 0 not null,
	FIRM_DTNG_SW CHAR(1 char) default ' ' not null,
	FIX_REV_SUN_SW CHAR(1 char) default ' ' not null,
	FIX_REV_MON_SW CHAR(1 char) default ' ' not null,
	FIX_REV_TUES_SW CHAR(1 char) default ' ' not null,
	FIX_REV_WEDS_SW CHAR(1 char) default ' ' not null,
	FIX_REV_THURS_SW CHAR(1 char) default ' ' not null,
	FIX_REV_FRI_SW CHAR(1 char) default ' ' not null,
	FIX_REV_SAT_SW CHAR(1 char) default ' ' not null,
	PROD_LIABILITY_TXT CHAR(30 char) default ' ' not null,
	RCMD_DTL_SW CHAR(1 char) default ' ' not null,
	ACTV_CONTR_SW CHAR(1 char) default ' ' not null,
	ACTV_CONTR_DT DATE default sysdate not null,
	AR_DTA_CHG_SW CHAR(1 char) default ' ' not null,
	FLAT_FRT NUMBER(11,4) default 0 not null,
	BAS_WHSE_DISC_CD CHAR(1 char) default ' ' not null,
	WHSE_DISC_DT DATE default sysdate not null,
	PREPD_ACCT_NBR CHAR(30 char) default ' ' not null,
	BIL_SHRTED_DEAL_SW CHAR(1 char) default ' ' not null,
	BASE_TRMS_TYP_CD CHAR(1 char) default ' ' not null,
	TERMS_DT DATE default sysdate not null,
	TERMS_EX_DT DATE default sysdate not null,
	TERMS_DAYS NUMBER default 0 not null,
	TERMS_EX_DAYS NUMBER default 0 not null,
	TERMS_INVC_IND CHAR(1 char) default ' ' not null,
	TERMS_NET_DAYS NUMBER default 0 not null,
	TERMS_PCT NUMBER(7,4) default 0 not null,
	TERMS_EX_PCT NUMBER(7,4) default 0 not null,
	TERMS_PICKUP_IND CHAR(1 char) default ' ' not null,
	TERMS_RECV_IND CHAR(1 char) default ' ' not null,
	FRT_REVD_SW CHAR(1 char) default ' ' not null,
	FRT_REV_DT DATE default sysdate not null,
	CURR_PCKUP_PNT_ID NUMBER default 0 not null,
	WHSE_DISC_PCT_1 NUMBER(7,4) default 0 not null,
	WHSE_DISC_PCT_2 NUMBER(7,4) default 0 not null,
	WHSE_DISC_PCT_3 NUMBER(7,4) default 0 not null,
	WHSE_DISC_PCT_4 NUMBER(7,4) default 0 not null,
	SUP_STRTGY_CD CHAR(1 char) default ' ' not null,

	FCST_METH_TYP CHAR(1 char) default ' ' not null,

	EDI_USC_TYP_CD CHAR(2 char) default ' ' not null,

	EDI_USC_TYP_CD_2 CHAR(2 char) default ' ' not null,

	BKHL_TYP_CD CHAR(2 char) default ' ' not null,

	PRIM_FRT_BIL_TYP CHAR(1 char) default ' ' not null,

	SEC_FRT_BIL_TYP CHAR(1 char) default ' ' not null,

	FRT_LVL_TYP_CD CHAR(1 char) default ' ' not null,

	PO_PRNT_OPT_CD CHAR(1 char) default ' ' not null,

	PO_PRNT_OPT_PK CHAR(1 char) default ' ' not null,

	LD_BLDING_LIM_CD CHAR(1 char) default ' ' not null,

	BLBK_TYP_CD CHAR(1 char) default ' ' not null,

	PRIM_GRP_CD_1 CHAR(10 char) default ' ' not null,
	PRIM_GRP_CD_2 CHAR(10 char) default ' ' not null,
	SECY_GRP_CD_1 CHAR(10 char) default ' ' not null,
	SECY_GRP_CD_2 CHAR(10 char) default ' ' not null,
	PRIM_CLS_NBR_1 NUMBER default 0 not null,
	PRIM_CLS_NBR_2 NUMBER default 0 not null,
	SECY_CLS_NBR_1 NUMBER default 0 not null,
	SECY_CLS_NBR_2 NUMBER default 0 not null,
	VEND_STAT_CD CHAR(5 char) default ' ' not null,

	IMPRT_SW CHAR(1 char) default ' ' not null,
	SRC_CD CHAR(2 char) default ' ' not null,
	OFFIC_CD CHAR(3 char) default ' ' not null,
	PYMT_METH_CD CHAR(2 char) default ' ' not null,
	PORT_APPR_DT DATE default sysdate not null,
	PORT_APPR_SEASN_TX CHAR(20 char) default ' ' not null,
	PORT_APPR_PRTNR_NM CHAR(30 char) default ' ' not null,
	ISA_VER_TXT CHAR(20 char) default ' ' not null,
	ISA_SIGNED_DT DATE default sysdate not null,
	ISA_SIGNED_BY_NM CHAR(30 char) default ' ' not null,
	ISA_SGNTR_TITL_NM CHAR(30 char) default ' ' not null,
	PO_FRT_MGMT_CD CHAR(1 char) default ' ' not null,
	WHSE_SEL_METH_CD CHAR(1 char) default ' ' not null,
	STR_LVL_PLNNG_CD CHAR(5 char) default ' ' not null,
	TRUSTED_VEND_SW CHAR(1 char) default 'N' not null,
	REAL_WHSE_NBR NUMBER,
	REAL_WHSE_TYP_CD CHAR(2 char),
	CRE8_TS TIMESTAMP(6) default to_date('1600-01-01 00:00:00','YYYY-MM-DD HH24:MI:SS') not null,
	CRE8_UID CHAR(20 char) default 'INITIAL LOAD' not null,
	LST_UPDT_TS TIMESTAMP(6) default to_date('1600-01-01 00:00:00','YYYY-MM-DD HH24:MI:SS') not null,
	LST_UPDT_UID CHAR(20 char) default 'INITIAL LOAD' not null,
	VEND_PO_CRUD_CD CHAR(5 char) default ' ' not null,
	MIX_ITM_CLS_SW CHAR(1 char) default ' ' not null
)
;


create table EMD.TRX_TRACKING
(
	TRX_TRKG_ID NUMBER not null
		constraint PK_TRX_TRACKING
			primary key,
	TRX_SRC_CD CHAR(5 char) not null,
	CRE8_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	CRE8_USR_ID CHAR(8 char) default ' ' not null,
	USR_ROLE_CD CHAR(5 char) default ' ' not null,
	IC_CNTL_NBR NUMBER(9),
	GRP_CNTL_NBR NUMBER(9),
	TRX_CNTL_NBR NUMBER(9),
	FILE_NM VARCHAR2(200 char),
	FILE_DES VARCHAR2(300 char),
	TRX_STAT_CD CHAR(5 char)
)
;

create table EMD.PS_WORK_RQST
(
	PS_WORK_ID NUMBER not null

			primary key,
	INTNT_ID NUMBER default 0 not null,

	CRE8_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	CRE8_USR_ID CHAR(20 char) default ' ' not null,
	LST_UPDT_TS DATE default SYSDATE not null,
	PD_SETUP_STAT_CD CHAR(5 char) default ' ' not null,

	STAT_CHG_RSN_ID NUMBER,

	SRC_SYSTEM_ID NUMBER default 0 not null,

	RDY_TO_ACTVD_SW CHAR(1 char) default ' ' not null,
	CAND_UPDT_UID CHAR(8 char),
	VEND_PHN_AREA_CD CHAR(3 char),
	VEND_PHN_NBR CHAR(7 char),
	VEND_PHN_EXTN CHAR(8 char),
	VEND_EMAIL_ADR CHAR(80 char),
	CAND_UPDTD_FRST_NM CHAR(30 char),
	CAND_UPDTD_LST_NM CHAR(30 char),
	TRX_TRKG_ID NUMBER,

	PROD_ID NUMBER,
	ITM_KEY_TYP_CD CHAR(5 char),
	ITM_ID NUMBER(17),
	WHSE_LOC_NBR NUMBER,
	VEND_LOC_NBR NUMBER,
	VEND_LOC_TYP_CD CHAR(2 char),
	STR_LOC_NBR NUMBER,
	SCN_CD_ID NUMBER(17),
	DELGTED_BY_USR_ID CHAR(20 char),
	DELGTED_TS TIMESTAMP(6)
)
;

create table EMD.PS_ITEM_MASTER
(
	PS_ITM_ID NUMBER not null
		constraint PK_PS_ITEM_MASTER
			primary key,
	ITM_ID NUMBER(17),
	ITEM_DES CHAR(30 char),
	SHRT_ITM_DES CHAR(20 char),
	ITEM_SIZE_TXT CHAR(12 char),
	ITM_SZ_QTY NUMBER(9,2),
	ITM_SZ_UOM_CD CHAR(5 char),

	SRC_CD CHAR(1 char),

	ADDED_DT DATE,
	ADDED_USR_ID CHAR(8 char) default ' ' not null,
	DSCON_TRX_SW CHAR(1 char),
	DSCON_DT DATE,
	DSCON_USR_ID CHAR(8 char),
	DELETE_DT DATE,
	SPLR_CS_LIF_DD NUMBER,
	ON_RCPT_LIF_DD NUMBER,
	WHSE_REACTION_DD NUMBER,
	GUARN_TO_STR_DD NUMBER,
	DT_CNTRLD_ITM_SW CHAR(1 char) default ' ' not null,
	SPCL_TAX_CD CHAR(5 char),

	PD_OMI_COM_CD NUMBER(5),
	PD_OMI_SUB_COM_CD NUMBER(5),
	PD_OMI_COM_CLS_CD NUMBER(3),
	IMS_COM_CD NUMBER(5),
	IMS_SUB_COM_CD CHAR(1 char),
	ORD_CTLG_NBR NUMBER(5),

	ITM_MDSE_TYP_CD CHAR(1 char),

	CASE_UPC NUMBER(18),
	ORDERING_UPC NUMBER(18),
	USDA_NBR NUMBER,
	MEX_AUTH_CD CHAR(1 char),
	MEX_BRDR_AUTH_CD CHAR(1 char),
	MAX_SHIP_QTY NUMBER,
	ABC_AUTH_CD CHAR(1 char),
	ABC_ITM_CAT_NO NUMBER,
	NEW_ITM_SW CHAR(1 char) default ' ' not null,
	REPACK_SW CHAR(1 char) default ' ' not null,
	CRIT_ITM_IND CHAR(1 char) default ' ' not null,
	NEV_OUT_SW CHAR(1 char) default ' ' not null,
	CTCH_WT_SW CHAR(1 char) default ' ' not null,
	LOW_VEL_SW CHAR(1 char) default ' ' not null,
	ONE_TOUCH_TYP_CD CHAR(2 char),

	PLUS_ITM_TYP_CD CHAR(1 char),

	SHPR_ITM_SW CHAR(1 char) default ' ' not null,
	SRS_HNDLG_CD CHAR(1 char),
	CROSS_DOCK_ITM_SW CHAR(1 char) default ' ' not null,
	RPLAC_ORD_QTY_SW CHAR(1 char) default ' ' not null,
	LTR_OF_CR_SW CHAR(1 char) default ' ' not null,
	VAR_WT_SW CHAR(1 char) default ' ' not null,
	FWDBY_APPR_SW CHAR(1 char) default ' ' not null,
	CATTLE_SW CHAR(1 char) default ' ' not null,
	HEB_ITM_PK NUMBER,
	RETL_SALS_PK NUMBER,
	DIM_CK_IND CHAR(1 char),
	AVG_WHLSL_CST NUMBER(11,4),
	DSD_ITM_SW CHAR(1 char) default ' ' not null,
	UPC_MAP_SW CHAR(1 char) default ' ' not null,
	DEPOSIT_SW CHAR(1 char) default ' ' not null,
	DEPT_ID_1 NUMBER,
	SUB_DEPT_ID_1 CHAR(1 char),
	DEPT_MDSE_TYP_1 CHAR(1 char),
	PSS_DEPT_1 NUMBER,
	DEPT_ID_2 NUMBER,
	SUB_DEPT_ID_2 CHAR(1 char),
	DEPT_MDSE_TYP_2 CHAR(1 char),
	PSS_DEPT_2 NUMBER,
	DEPT_ID_3 NUMBER,
	SUB_DEPT_ID_3 CHAR(1 char),
	DEPT_MDSE_TYP_3 CHAR(1 char),
	PSS_DEPT_3 NUMBER,
	DEPT_ID_4 NUMBER,
	SUB_DEPT_ID_4 CHAR(1 char),
	DEPT_MDSE_TYP_4 CHAR(1 char),
	PSS_DEPT_4 NUMBER,
	GROSS_WT NUMBER(9,3),
	UNSTAMPED_TBCO_SW CHAR(1 char) default ' ' not null,
	LST_UPDT_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	LST_UPDT_USR_ID CHAR(8 char) default ' ' not null,
	INBND_SPEC_DD NUMBER,
	PS_WORK_ID NUMBER default 0 not null,

	IN_STR_DT DATE,
	WHSE_FLSH_DT DATE,
	SELL_YY NUMBER,
	CRTN_MRKNG_TXT CHAR(30 char),
	PRODN_MIN_ORD_QTY NUMBER(7),
	PRODN_MIN_ORD_DES CHAR(20 char),
	SEASN_TXT CHAR(20 char),
	CNTAN_SZ_CD CHAR(7 char),
	INCO_TRM_CD CHAR(3 char),
	PCKUP_PNT_TXT CHAR(20 char),
	HTS_NBR NUMBER(10),
	CNTRY_OF_ORIG_NM CHAR(30 char),
	DUTY_RT_PCT NUMBER(9,4),
	DUTY_CNFRM_TXT CHAR(20 char),
	FRT_CNFRM_TXT CHAR(20 char),
	AGENT_COMSN_PCT NUMBER(9,4),
	COLOR_DES CHAR(50 char),
	PROR_DT DATE,
	DC_CHNL_TYP_CD CHAR(5 char),
	ITM_KEY_TYP_CD CHAR(5 char),

	MAX_SHLF_LIFE_DD NUMBER,
	MRT_SW CHAR(1 char) default 'N' not null,
	MSTR_PK_QTY NUMBER,
	NEW_DATA_SW CHAR(1 char) default 'N' not null,
	SHP_CS_LN NUMBER(7,2),
	SHP_CS_WD NUMBER(7,2),
	SHP_CS_HT NUMBER(7,2),
	SHP_CU NUMBER(9,3),
	SHP_NEST_CU NUMBER(9,3),
	SHP_NEST_MAX_QTY NUMBER,
	SHP_WT NUMBER(9,3),
	SHP_INR_PK NUMBER(9,3),
	VEND_LN NUMBER(7,2),
	VEND_WD NUMBER(7,2),
	VEND_HT NUMBER(7,2),
	VEND_WT NUMBER(7,2),
	VEND_CU NUMBER(9,4),
	VEND_NEST_CU NUMBER(9,3),
	VEND_NEST_MAX NUMBER,
	VEND_PK_QTY NUMBER,
	CS_UNT_FACTR_1 NUMBER(9,4),
	PRCH_STAT_CD CHAR(5 char),

	DSPLY_RDY_PAL_SW CHAR(1 char) default 'N' not null,
	STD_SUBST_LOGIC_SW CHAR(1 char),
	PROD_FCNG_NBR NUMBER,
	PROD_ROW_DEEP_NBR NUMBER,
	PROD_ROW_HI_NBR NUMBER,
	NBR_OF_ORINT_NBR NUMBER(3),
	SRS_AFF_TYP_CD CHAR(1 char)
)
;

create table EMD.PS_PRODUCT_MASTER
(
	PS_PROD_ID NUMBER  not null
		constraint PK_PS_PRODUCT_MASTER
			primary key,
	PROD_ID NUMBER,
	PROD_ENG_DES CHAR(30 char),
	PROD_SPNSH_DES CHAR(30 char),
	PROD_TYP_CD CHAR(5 char),
	PD_OMI_COM_CLS_CD NUMBER(3),
	PD_OMI_COM_CD NUMBER(5),
	PD_OMI_SUB_COM_CD NUMBER(5),
	IMS_COM_CD NUMBER(5),
	IMS_SUB_COM_CD CHAR(1 char),
	GRMGN_PCT NUMBER(7,4),
	SALS_RSTR_CD CHAR(5 char),

	BDM_CD CHAR(5 char),
	LBL_LANG_TXT CHAR(10 char),
	RETL_GRP_NBR NUMBER(5),
	STR_DEPT_NBR NUMBER(5),
	STR_SUB_DEPT_ID CHAR(5 char),
	LST_UPDT_USR_ID CHAR(8 char) default ' ' not null,
	LST_UPDT_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	RETL_LINK_CD NUMBER(7),
	DPOS_SCN_TYP_CD CHAR(5 char),

	DPOS_SCN_CD_ID NUMBER(17),
	PKG_TXT CHAR(30 char) default ' ' not null,
	QTY_RSTR_SW CHAR(1 char) default ' ' not null,
	SPOIL_ALLOW_AMT NUMBER,
	WT_SW CHAR(1 char) default ' ' not null,
	RETL_X4_QTY NUMBER,
	RETL_PRC_AMT NUMBER(9,2),
	MARTIX_MGN_CD CHAR(5 char),
	AVC_SW CHAR(1 char) default ' ' not null,
	PRCH_TM_RSTR_CD CHAR(5 char),
	STK_SW CHAR(1 char) default ' ' not null,
	SPEC_SHEET_ATT_SW CHAR(1 char) default ' ' not null,
	CEIL_AMT NUMBER,
	CEIL_FOR_AMT NUMBER(11,4),
	CENT_OFF_AMT NUMBER(11,4),
	NDC_ID CHAR(12 char),
	AHSFCC_ID CHAR(20 char),
	AWP_EFF_DT DATE,
	AWP_IND_CD CHAR(5 char),
	BRND_EQ_NM CHAR(20 char),
	CRDNAL_AVLTY_STAT CHAR(5 char),
	CRDNAL_STAT_CD CHAR(5 char),
	CRDNAL_ST_DSCON_DT DATE,
	PS_WORK_ID NUMBER default 0 not null,

	DRUG_SCH_TYP_CD CHAR(5 char),

	TBCO_PROD_TYP_CD CHAR(5 char),

	UNSTAMPED_SW CHAR(1 char) default ' ' not null,
	CIG_TAX_AMT NUMBER(9,4),
	PREMRK_PRC_AMT NUMBER(9,2),
	MRK_PRC_IN_STR_SW CHAR(1 char) default ' ' not null,
	COLOR_CD CHAR(10 char),
	FD_STMP_SW CHAR(1 char) default ' ' not null,
	SALS_TAX_SW CHAR(1 char) default ' ' not null,
	MEDICAID_CD CHAR(1 char),
	SESNL_SW CHAR(1 char) default ' ' not null,
	PROD_TAG_DES CHAR(30 char),
	NBR_OF_TAGS NUMBER,
	TAG_EFF_DT DATE,
	RETL_UNT_LN NUMBER(7,2),
	RETL_UNT_WD NUMBER(7,2),
	RETL_UNT_HT NUMBER(7,2),
	RETL_UNT_WT NUMBER(9,4),
	MAX_SHLF_LIFE_DD NUMBER,
	TBCO_PROD_SW CHAR(1 char) default ' ' not null,
	RX_PROD_SW CHAR(1 char) default ' ' not null,
	ALCOHOL_PROD_SW CHAR(1 char) default ' ' not null,
	ALCOHOL_PCT_AMT NUMBER(6,3),
	GNRC_PROD_SW CHAR(1 char) default ' ' not null,
	PRIV_LBL_SW CHAR(1 char) default ' ' not null,
	MAC_EAC_SW CHAR(1 char) default ' ' not null,
	SCANABLE_ITM_SW CHAR(1 char) default ' ' not null,
	FSA_CD CHAR(5 char),
	PSE_TYPE_CD CHAR(1 char),
	GRAMS_OF_PSE NUMBER(9,4),
	AGE_RSTR_QTY NUMBER,
	SCALE_SW CHAR(1 char) default ' ' not null,
	HEALTHY_LIVING_SW CHAR(1 char) default ' ' not null,
	CLRNC_DT DATE,
	DSD_DELETED_SW CHAR(1 char) default ' ' not null,
	ACTVT_FLAG CHAR(1 char) default ' ' not null,
	OVRD_WEEKS NUMBER(3),
	FAM_1_CD NUMBER(5),
	FAM_2_CD NUMBER(5),
	DSD_DEPT_SW CHAR(1 char) default ' ' not null,
	BRND_GRP_CD CHAR(5 char),
	BRND_SGRP_CD CHAR(5 char),
	BRND_TYP_CD CHAR(5 char),
	DRG_FACT_PAN_SW CHAR(1 char) default ' ' not null,
	SESNLY_YY NUMBER(5),
	EAS_SW CHAR(1 char) default ' ' not null,
	INGRD_SW CHAR(1 char) default ' ' not null,
	WIC_SW CHAR(1 char) default ' ' not null,
	SELF_MFG_SW CHAR(1 char) default ' ' not null,
	PREMARKED_PRC_SW CHAR(1 char) default ' ' not null,
	SHLF_TAG_SZ_CD CHAR(2 char),

	MAINT_FUNCTION CHAR(1 char),
	EFFECTIVE_DATE DATE,
	STRIP_FLAG CHAR(1 char) default ' ' not null,
	TARE_SERV_COUNTER NUMBER(4,3),
	TARE_PREPACK NUMBER(4,3),
	SHELF_LIFE NUMBER(3),
	EAT_BY_DAYS NUMBER(3),
	FREEZE_BY_DAYS NUMBER(3),
	INGR_STATEMENT_NUM NUMBER(7),
	PRODUCT_DESC_LINE1 VARCHAR2(50 char),
	PRODUCT_DESC_LINE2 VARCHAR2(50 char),
	PD_NTRNT_STMT_NO NUMBER(7),
	SPANISH_DESC_LINE1 VARCHAR2(50 char),
	SPANISH_DESC_LINE2 VARCHAR2(50 char),
	FRC_TARE_SW CHAR(1 char) default ' ' not null,
	TAG_TYP_CD CHAR(5 char),

	PROD_CAT_ID NUMBER,
	PROD_BRND_ID NUMBER,
	PD_HILITE_PRNT_CD NUMBER(5),

	PD_SAFE_HAND_CD NUMBER(5),

	SL_LBL_FRMAT_2_CD NUMBER(5),

	SL_LBL_FRMAT_1_CD NUMBER(5),

	PRPRC_FOR_NBR NUMBER default 1 not null,
	PRIM_PS_ITM_ID NUMBER,

	PRIM_SCN_CD NUMBER(17),
	AVG_WHLSL_RX_CST NUMBER(11,4),
	CRIT_ITM_SW CHAR(1 char) default 'N' not null,
	DIRECT_RX_CST NUMBER(11,4),
	NEW_DATA_SW CHAR(1 char) default 'N' not null,
	PRPRC_OFF_PCT NUMBER(5,2),
	PSS_DEPT NUMBER,
	SESNLY_ID NUMBER,
	SCAN_DES CHAR(12 char),
	SUGG_RETL_X4_QTY NUMBER,
	SUGG_RETL_PRC_AMT NUMBER(9,2),
	LABR_CAT_CD CHAR(2 char),
	SALS_RSTR_AGE_CD CHAR(5 char),

	RETL_LINK_SCN_CD NUMBER(17),
	DRUG_NM_CD CHAR(5 char),

	COPIED_FRM_PROD_ID NUMBER,
	ALT_COM_BDM_CD CHAR(5 char),
	GRADE_NBR NUMBER(3),
	NET_WT NUMBER(9,4),
	PROD_MODL_TXT CHAR(20 char),
	INBND_SPCFN_DD NUMBER(5),
	REACT_DD NUMBER(5),
	GUARN_TO_STR_DD NUMBER(5),
	SND_DD_TO_WMS_SW CHAR(1 char),
	CD_DATED_ITM_CD CHAR(1 char) default ' ' not null,
	MKT_GNDR_CD CHAR(1 char),
	BKRY_PROD_SW CHAR(1 char),
	WINE_PROD_SW CHAR(1 char),
	PROD_CNTNT_ID NUMBER,
	COLOR_OPT_SW CHAR(1 char),
	MIN_INVEN_THRH_CNT NUMBER,
	DECO_PKG_SW CHAR(1 char),
	FLNG_SW CHAR(1 char),
	BKRY_SHAPE_CD CHAR(5 char),
	BKRY_THM_CD CHAR(5 char),
	VARTL_ID NUMBER,
	WINEMKR_ID NUMBER,
	WINE_RGN_ID NUMBER,
	WINE_VINTAGE_YY NUMBER,
	IN_STR_PRODN_SW CHAR(1 char),
	GIFT_WRP_ELIG_SW CHAR(1 char),
	HEB_GUARN_TYP_CD CHAR(5 char),
	SOLD_SEPLY_SW CHAR(1 char),
	MAP_REQ_SW CHAR(1 char),
	MAP_AMT NUMBER(9,2),
	MAX_CUST_ORD_QTY NUMBER,
	MIN_CUST_ORD_QTY NUMBER,
	VERTEX_TAX_CAT_CD CHAR(40 char),
	OPEN_PKG_SHLF_DD NUMBER default 0 not null,
	TAX_QUAL_CD CHAR(10 char),
	PRODUCT_DESC_LINE3 CHAR(50 char) default ' ' not null,
	PRODUCT_DESC_LINE4 CHAR(50 char) default ' ' not null,
	SPANISH_DESC_LINE3 CHAR(50 char) default ' ' not null,
	SPANISH_DESC_LINE4 CHAR(50 char) default ' ' not null,
	IM_PRC_REQ_FLAG CHAR(1 char) default ' ' not null,
	CSUME_ACHOL_STR_SW CHAR(1 char) default 'N' not null,
	SHOW_CLRS_SW CHAR(1 char) default 'N' not null,
	CUST_ORD_INCRM_QTY NUMBER(5,2) default 0 not null,
	MIN_UNT_SZ_WT NUMBER(9,4) default 0 not null,
	MAX_UNT_SZ_WT NUMBER(9,4) default 0 not null
)
;

create table EMD.PS_ITM_SCN_CD
(
	SCN_CD_ID NUMBER(17) default 0 not null,
	PS_ITM_ID NUMBER default 0 not null,

	SCN_TYP_CD CHAR(5 char) default ' ' not null,

	PRIM_SW CHAR(1 char) default 'Y' not null,
	RETL_PACK_QTY NUMBER,
	NEW_DATA_SW CHAR(1 char) default ' ' not null,
	constraint PK_PS_ITM_SCN_CD
		primary key (SCN_CD_ID, PS_ITM_ID)
)
;

create table EMD.PS_VEND_LOC_ITM
(
	VEND_LOC_TYP_CD CHAR(2 char) default ' ' not null,
	VEND_LOC_NBR NUMBER default 0 not null,
	PS_ITM_ID NUMBER default 0 not null,

	VEND_ITM_ID CHAR(20 char),
	VEND_PAL_TIE NUMBER,
	VEND_PAL_TIER NUMBER,
	VEND_PAL_QTY NUMBER,
	VEND_PAL_SZ CHAR(1 char),
	VEND_LIST_CST NUMBER(11,4),
	INV_SEQ_NBR NUMBER,
	ORD_QTY_RSTR_CD CHAR(5 char),

	SCA_CD CHAR(5 char),

	LST_UPDT_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	LST_UPDT_USR_ID CHAR(8 char) default ' ' not null,
	DIR_CST_AMT NUMBER(11,4),
	CD_DT_CD CHAR(1 char),
	MIN_QTY NUMBER,
	MIN_TYP_TXT CHAR(20 char),
	FRST_SHP_DT DATE,
	SAMP_PROVIDE_SW CHAR(18 char) default ' ' not null,
	GUARNT_SALE_SW CHAR(1 char) default ' ' not null,
	CNTRY_ORIG_ID NUMBER,

	SESNLY_ID NUMBER,

	CST_OWN_ID NUMBER,
	T2T_ID NUMBER,

	CNTAN_SZ_CD CHAR(7 char),
	COLOR_DES CHAR(50 char),
	DUTY_RT_PCT NUMBER(9,4),
	FRT_CNFRM_TXT CHAR(20 char),
	IN_STR_DT DATE,
	INCO_TRM_CD CHAR(3 char),
	NEW_DATA_SW CHAR(1 char) default 'N' not null,
	PCKUP_PNT_TXT CHAR(20 char),
	PROR_DT DATE,
	SESNLY_YY NUMBER(5),
	HTS_NBR NUMBER(10),
	HTS2_NBR NUMBER(10),
	HTS3_NBR NUMBER(10),
	AGENT_COMSN_PCT NUMBER(5,2),
	WHSE_FLSH_DT DATE,
	CRTN_MRKNG_TXT CHAR(30 char),
	DEAL_ITM_IND CHAR(1 char),
	DUTY_CNFRM_TXT CHAR(20 char),
	AUTHD_SW CHAR(1 char) default 'N' not null,
	EXPCT_WKLY_MVT NUMBER,
	IMPORT_SW CHAR(1 char) default 'N' not null,
	CST_LNK_ID NUMBER,
	CST_ITM_ID NUMBER(17) default 0,
	CST_ITM_KEY_TYP_CD CHAR(5 char),
	STR_DEPT_NBR CHAR(5 char),
	STR_SUB_DEPT_ID CHAR(5 char),
	ORD_QTY_TYP_CD CHAR(5 char),

	PSS_DEPT_1 NUMBER,
	SELL_YY NUMBER,
	SEASN_TXT VARCHAR2(20 char),
	DUTY_INFO_TXT CHAR(20 char),
	CST_LNK_SCN_CD NUMBER(17),
	constraint PK_PS_VEND_LOC_ITM
		primary key (VEND_LOC_TYP_CD, VEND_LOC_NBR, PS_ITM_ID)

)
;

create table EMD.PS_WHSE_LOC_ITM
(
	PS_ITM_ID NUMBER default 0 not null,

	WHSE_LOC_NBR NUMBER default 0 not null,
	WHSE_LOC_TYP_CD CHAR(2 char) default ' ' not null,
	SHP_FROM_LOC_NBR NUMBER,
	SHP_FROM_TYP_CD CHAR(2 char),
	SCA_CD CHAR(5 char),

	SPLR_STAT_USR_ID CHAR(8 char),
	SPLR_STAT_TS TIMESTAMP(6),
	SPLR_ITM_RSN_CD CHAR(5 char),

	PRCH_STAT_USR_ID CHAR(8 char),
	PRCH_STAT_TS TIMESTAMP(6),
	DSCON_TRX_SW CHAR(1 char) default ' ' not null,
	DSCON_DT DATE,
	DSCON_USR_ID CHAR(8 char),
	DSCON_CMT CHAR(50 char),
	NEW_ITM_RSN_CD CHAR(1 char),
	PO_CMT CHAR(30 char),
	CST_LNK_TS TIMESTAMP(6),
	CST_LNK_USR_ID CHAR(8 char),
	USE_CST_LNK_SW CHAR(1 char),
	CST_LNK_ID NUMBER,
	MFG_ID CHAR(20 char) default ' ' not null,
	AVG_WT NUMBER(9,3),
	PAL_TI NUMBER,
	PAL_TIER NUMBER,
	PAL_QTY NUMBER,
	SRS_SCOPE_IND CHAR(1 char),
	VAR_WT_SW CHAR(1 char) default ' ' not null,
	CTCH_WT_SW CHAR(1 char) default ' ' not null,
	CMPTR_SETUP_IND CHAR(1 char),
	ITM_REMARK_SW CHAR(1 char) default ' ' not null,
	DALY_HIST_SW CHAR(1 char) default ' ' not null,
	TRNSFR_AVAIL_SW CHAR(1 char) default ' ' not null,
	PO_RCMD_SW CHAR(1 char) default ' ' not null,
	ORD_INRVL_DD NUMBER,
	ORD_QTY_TYP_CD CHAR(5 char),

	ORD_PNT_QTY NUMBER,
	SAFE_STCK_QTY NUMBER,
	FCST_TYP_CD CHAR(5 char),

	SESNL_IND_CD CHAR(5 char),

	VEND_TRGT_SVCLV NUMBER(5,2),
	WHSE_TRGT_SVCLV NUMBER(5,2),
	CS_UNT_FACTR_1 NUMBER(9,4),
	CS_UNT_FACTR_2 NUMBER(9,4),
	CS_MVT_PRWK NUMBER,
	CS_MVT_DESEAS_PRWK NUMBER,
	MAD_MVT_QTY NUMBER(9,3),
	FCST_GRP CHAR(10 char),
	CURR_PKSLT CHAR(10 char),
	PAL_STK_LIM_QTY NUMBER,
	BULK_PCK_QTY NUMBER,
	PAL_SZ_CD CHAR(5 char),

	FLR_STK_IND CHAR(1 char),
	MAND_FLW_THRG_SW CHAR(1 char) default ' ' not null,
	FLW_THRGH_TYP_CD CHAR(1 char),

	PRIOR_AVG_CST NUMBER(11,4),
	CURR_AVG_CST NUMBER(11,4),
	BIL_CST NUMBER(11,4),
	SUGG_BILL_CST NUMBER(11,4),
	MVT_PCT_CD CHAR(2 char),
	BILLABLE_QTY NUMBER,
	DAMAGED_QTY NUMBER,
	DSTRB_RES_QTY NUMBER,
	TOT_ON_HAND_QTY NUMBER,
	OFSIT_QTY NUMBER,
	ON_HLD_QTY NUMBER,
	PLUG_QTY NUMBER,
	CYCL_STCK_QTY NUMBER,
	TURN_INVEN_QTY NUMBER,
	PROMO_INVEN_QTY NUMBER,
	FWDBY_INVEN_QTY NUMBER,
	DEAL_ITM_IND CHAR(1 char),
	MAND_FLW_THRU_SW CHAR(1 char) default ' ' not null,
	FLW_TYP_CD CHAR(3 char),
	LST_UPDT_DT DATE default SYSDATE not null,
	LST_UPDT_USR_ID CHAR(8 char) default ' ' not null,
	LST_UPDT_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	ADDED_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	ADDED_USR_ID CHAR(8 char) default ' ' not null,
	NEW_DATA_SW CHAR(1 char) default 'N' not null,
	PRCH_STAT_CD CHAR(5 char),

	constraint PK_PS_WHSE_LOC_ITM
		primary key (WHSE_LOC_TYP_CD, WHSE_LOC_NBR, PS_ITM_ID)

)
;

create table EMD.PS_ITM_WHSE_CMTS
(
	WHSE_LOC_NBR NUMBER default 0 not null,
	WHSE_LOC_TYP_CD CHAR(2 char) default ' ' not null,
	ITM_CMT_TYP_CD CHAR(5 char) default ' ' not null,

	ITM_WHSE_CMT_NBR NUMBER default 0 not null,
	PS_ITM_ID NUMBER default 0 not null,
	ITM_WHSE_CMT_TXT CHAR(80 char) default ' ' not null,
	constraint PK_PS_ITM_WHSE_CMTS
		primary key (WHSE_LOC_NBR, WHSE_LOC_TYP_CD, ITM_CMT_TYP_CD, ITM_WHSE_CMT_NBR, PS_ITM_ID)
)
;

create table EMD.PS_ITM_WHSE_VEND
(
	VEND_LOC_TYP_CD CHAR(2 char) default ' ' not null,
	VEND_LOC_NBR NUMBER default 0 not null,
	WHSE_LOC_TYP_CD CHAR(2 char) default ' ' not null,
	WHSE_LOC_NBR NUMBER default 0 not null,
	PS_ITM_ID NUMBER default 0 not null,
	PRIM_VEND_SW CHAR(1 char) default ' ' not null,
	FRT_EXCP_SW CHAR(1 char) default ' ' not null,
	TERMS_EXCP_SW CHAR(1 char) default ' ' not null,
	ITM_UP_DN_CST NUMBER(11,4),
	UP_DN_CST_CMT CHAR(30 char),
	UP_DN_POS_OR_NEG CHAR(1 char),
	FRT_FREE_SW CHAR(1 char) default ' ' not null,
	TOT_ALLOW_AMT NUMBER(11,4),
	MFG_SRP_AMT NUMBER(11,4),
	SELL_AMT NUMBER(11,4),
	FRT_BIL_AMT NUMBER(11,4),
	BKHL_AMT NUMBER(11,4),
	PPADD_EXCP_SW CHAR(1 char) default ' ' not null,
	FRT_ALLOW_AMT NUMBER(11,4),
	LST_UPDT_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	LST_UPDT_USR_ID CHAR(8 char) default ' ' not null
)
;

create table EMD.PS_CANDIDATE_STAT
(
	PS_WORK_ID NUMBER default 0 not null,

	PD_SETUP_STAT_CD CHAR(5 char) default ' ' not null,

	LST_UPDT_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	UPDT_USR_ID CHAR(8 char) default ' ' not null,
	STAT_CHG_RSN_ID NUMBER,

	CMT_TXT CHAR(100 char),
	constraint PK_PS_CANDIDATE_STAT
		primary key (PS_WORK_ID, PD_SETUP_STAT_CD, LST_UPDT_TS)
)
;

create table EMD.PS_PROD_DESC_TXT
(
	PS_PROD_ID NUMBER not null,
	LANG_TYP_CD CHAR(5 char) not null,
	DES_TYP_CD CHAR(5 char) not null,
	PROD_DES VARCHAR2(3000 char),
	LST_UPDT_USR_ID CHAR(8 char) default ' ' not null,
	LST_UPDT_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	constraint PK_PS_PROD_DESC_TXT
		primary key (PS_PROD_ID, LANG_TYP_CD, DES_TYP_CD)
)
;

create table EMD.PS_PROD_SCN_CODES
(
	PS_PROD_ID NUMBER default 0 not null,
	SCN_CD_SEQ_NBR NUMBER default 0 not null,
	SCN_CD_ID NUMBER(17),
	SCN_TYP_CD CHAR(5 char),

	BNS_SCN_CD_SW CHAR(1 char) default ' ' not null,
	LST_UPDT_USR_ID CHAR(8 char) default ' ' not null,
	LST_UPDT_TS TIMESTAMP(6) default SYSTIMESTAMP not null,
	INGRD_STMT_TXT CHAR(50 char),
	SCL_TARE_WT NUMBER(2,1),
	TST_SCN_OVRD_SW CHAR(1 char) default ' ' not null,
	TST_SCN_INPUT_ID NUMBER(17),
	RETL_UNT_SELL_SZ_2 NUMBER(7,2),
	RETL_UNT_SELL_SZ_1 NUMBER(7,2),
	RETL_SELL_SZ_CD1 CHAR(2 char),

	RETL_SELL_SZ_CD2 CHAR(2 char),

	PROD_SUB_BRND_ID NUMBER,
	NEW_DATA_SW CHAR(1 char) default 'N' not null,
	PS_NTRNT_STMT_NO NUMBER,
	RETL_UNT_HT NUMBER(7,2),
	RETL_UNT_LN NUMBER(7,2),
	RETL_UNT_WD NUMBER(7,2),
	RETL_UNT_WT NUMBER(9,4),
	SAMPLE_PROVD_SW CHAR(1 char) default 'N' not null,
	TAG_SZ_DES CHAR(6 char),
	WIC_SW CHAR(1 char),
	WIC_APL_ID NUMBER(17),
	PSE_GRAMS_WT NUMBER(9,4),
	LEB_SW CHAR(1 char),
	IMG_AVAIL_DT DATE,
	constraint PK_PS_PROD_SCN_CODES
		primary key (PS_PROD_ID, SCN_CD_SEQ_NBR)
)
;

