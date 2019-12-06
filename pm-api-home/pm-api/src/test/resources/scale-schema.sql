create table EMD.SL_INGREDIENT
(
	INGREDIENT_CODE CHAR(5 char) not null
		constraint PK_SL_INGREDIENT
			primary key,
	SOI_FLAG CHAR(1 char) not null,
	MAINT_FUNCTION CHAR(1 char) not null,
	PD_INGRD_EXT_DES CHAR(20 char) not null,
	PD_INGRD_CAT_CD NUMBER(3) not null,
	INGREDIENT_DESC VARCHAR2(50 char) default ' ' not null,
	CRE8_TS TIMESTAMP(6),
	CRE8_UID CHAR(20 char),
	LST_UPDT_TS TIMESTAMP(6),
	LST_UPDT_UID CHAR(20 char)
)
;

create table EMD.SL_INGRD_STMT_HDR
(
	PD_INGRD_STMT_NO NUMBER(7) not null
		constraint PK_SL_INGRD_STMT_HDR
			primary key,
	PD_INGRD_MAINT_DT DATE not null,
	PD_INGRD_MAINT_SW CHAR(1 char) not null,
	PD_MAINT_TYP_CD CHAR(1 char) not null,
	CRE8_TS TIMESTAMP(6),
	CRE8_UID CHAR(20 char),
	LST_UPDT_TS TIMESTAMP(6),
	LST_UPDT_UID CHAR(20 char)
)
;

create table EMD.SL_INGRD_STMT_DTL
(
	PD_INGRD_STMT_NO NUMBER(7) not null,
	PD_INGRD_CD CHAR(5 char) not null,
	PD_INGRD_PCT NUMBER(7,4) not null,
	CRE8_TS TIMESTAMP(6),
	CRE8_UID CHAR(20 char),
	LST_UPDT_TS TIMESTAMP(6),
	LST_UPDT_UID CHAR(20 char),
	constraint PK_SL_INGRD_STMT_DTL
		primary key (PD_INGRD_STMT_NO, PD_INGRD_CD)
)
;

create table EMD.SL_SOI_INGREDIENT
(
	SO_INGREDIENT_CODE CHAR(5 char) not null,
	INGREDIENT_CODE CHAR(5 char) not null,
	SOI_SEQUENCE NUMBER(5) not null,
	CRE8_TS TIMESTAMP(6),
	CRE8_UID CHAR(20 char),
	LST_UPDT_TS TIMESTAMP(6),
	LST_UPDT_UID CHAR(20 char),
	constraint PK_SL_SOI_INGREDIENT
		primary key (SO_INGREDIENT_CODE, INGREDIENT_CODE)
)
;

create table EMD.PD_NUTRIENT
(
	PD_LBL_NTRNT_CD NUMBER(3) not null
		constraint PK_PD_NUTRIENT
			primary key,
	PD_NTRNT_MAINT_SW CHAR(1 char) not null,
	PD_LBL_MAINT_DT DATE not null,
	PD_LBL_NTRNT_DES CHAR(32 char) not null,
	PD_NTRNT_RDA_QTY NUMBER(5,1) not null,
	PD_LBL_FED_REQ_SW CHAR(1 char) not null,
	PD_PDD_REQ_SW CHAR(1 char) not null,
	PD_LBL_UOM_CD NUMBER(3) not null,
	PD_FED_REQ_SEQ_NO NUMBER(5,2) not null,
	PD_PDD_SEQ_NO NUMBER(5,2) not null,
	PD_NTRNT_PDV_SW CHAR(1 char) not null,
	PD_FED_REQ_PDV_SW CHAR(1 char) not null,
	CRE8_TS TIMESTAMP(6),
	CRE8_UID CHAR(20 char),
	LST_UPDT_TS TIMESTAMP(6),
	LST_UPDT_UID CHAR(20 char)
)
;

create table EMD.PD_NTRNT_STMT_HDR
(
	PD_NTRNT_STMT_NO NUMBER(7) not null
		constraint PK_PD_NTRNT_STMT_HDR
			primary key,
	PD_LBL_COMN_UOM_CD NUMBER(3) not null,
	PD_LBL_MET_UOM_CD NUMBER(3) not null,
	PD_COMN_SRVNG_QTY NUMBER(5,2) not null,
	PD_MET_SRVNG_QTY NUMBER(3) not null,
	PD_LBL_SPC_QTY NUMBER(3) not null,
	PD_STMT_MAINT_SW CHAR(1 char) not null,
	PD_STMT_MAINT_DT DATE not null,
	CRE8_TS TIMESTAMP(6),
	CRE8_UID CHAR(20 char),
	LST_UPDT_TS TIMESTAMP(6),
	LST_UPDT_UID CHAR(20 char)
)
;

create table EMD.PD_NTRNT_STMT_DTL
(
	PD_NTRNT_STMT_NO NUMBER(7) not null,
	PD_LBL_NTRNT_CD NUMBER(3) not null,
	PD_LBL_FED_REQ_QTY NUMBER(5,1) not null,
	PD_PDD_REQ_QTY NUMBER(7,2) not null,
	PD_NTRNT_PDV_QTY NUMBER(5) not null,
	CRE8_TS TIMESTAMP(6),
	CRE8_UID CHAR(20 char),
	LST_UPDT_TS TIMESTAMP(6),
	LST_UPDT_UID CHAR(20 char),
	constraint PK_PD_NTRNT_STMT_DTL
		primary key (PD_NTRNT_STMT_NO, PD_LBL_NTRNT_CD)
)
;

create table EMD.PD_UNIT_MEASURE
(
	PD_LBL_UOM_CD NUMBER(3) not null
		constraint PK_PD_UNIT_MEASURE
			primary key,
	PD_UOM_MAINT_SW CHAR(1 char) not null,
	PD_UOM_MAINT_DT DATE not null,
	PD_UOM_DES CHAR(15 char) not null,
	PD_UOM_EXT_DES CHAR(25 char) not null,
	PD_UOM_TYP_SW CHAR(1 char) not null,
	PD_UOM_FORM_SW CHAR(1 char) not null,
	CRE8_TS TIMESTAMP(6),
	CRE8_UID CHAR(20 char),
	LST_UPDT_TS TIMESTAMP(6),
	LST_UPDT_UID CHAR(20 char)
)
;

create table EMD.SL_SCALESCAN
(
	UPC_KEY NUMBER(13) not null
		constraint PK_SL_SCALESCAN
			primary key,
	MAINT_FUNCTION CHAR(1 char) not null,
	EFFECTIVE_DATE DATE not null,
	STRIP_FLAG CHAR(1 char) not null,
	TARE_SERV_COUNTER NUMBER(4,3) not null,
	TARE_PREPACK NUMBER(4,3) not null,
	SHELF_LIFE NUMBER(3) not null,
	EAT_BY_DAYS NUMBER(3) not null,
	FREEZE_BY_DAYS NUMBER(3) not null,
	INGR_STATEMENT_NUM NUMBER(7) not null,
	PD_NTRNT_STMT_NO NUMBER(7) not null,
	PD_HILITE_PRNT_CD NUMBER(5) not null,
	PD_SAFE_HAND_CD NUMBER(5) not null,
	PRODUCT_DESC_LINE1 VARCHAR2(50 char) default ' ' not null,
	PRODUCT_DESC_LINE2 VARCHAR2(50 char) default ' ' not null,
	SPANISH_DESC_LINE1 VARCHAR2(50 char) default ' ' not null,
	SPANISH_DESC_LINE2 VARCHAR2(50 char) default ' ' not null,
	SL_LBL_FRMAT_2_CD NUMBER(5) default 0 not null,
	SL_LBL_FRMAT_1_CD NUMBER(5) default 0 not null,
	FRC_TARE_SW CHAR(1 char) default 'N' not null,
	GRADE_NBR NUMBER(3) default 0 not null,
	NET_WT NUMBER(9,4) default 0 not null,
	PRC_OVRD_SW CHAR(1 char) default ' ' not null,
	PRODUCT_DESC_LINE3 CHAR(50 char) default ' ' not null,
	PRODUCT_DESC_LINE4 CHAR(50 char) default ' ' not null,
	SPANISH_DESC_LINE3 CHAR(50 char) default ' ' not null,
	SPANISH_DESC_LINE4 CHAR(50 char) default ' ' not null,
	CRE8_TS TIMESTAMP(6),
	CRE8_UID CHAR(20 char),
	LST_UPDT_TS TIMESTAMP(6),
	LST_UPDT_UID CHAR(20 char)
)
;

