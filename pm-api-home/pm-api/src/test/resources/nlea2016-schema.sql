create table EMD.NLEA_UPC_PLU_RLOT
(
	SCN_CD_ID NUMBER(17) not null,
	LF_OVRD_ID NUMBER(5),
	USE_2016_NTRN_SW CHAR(1 char),
	LST_UPDT_TS TIMESTAMP(6) not null,
	LST_UPDT_UID VARCHAR2(20) not null
)
;

create table EMD.NTRN_PAN_TYP
(
	PAN_TYP_CD VARCHAR2(5 char) not null
		constraint PK_NTRN_PAN_TYP
			primary key,
	PAN_TYP_ABB VARCHAR2(6 char),
	PAN_TYP_DES VARCHAR2(50 char) not null
)
;

create table EMD.UOM_FORM
(
	UOM_FORM_CD VARCHAR2(5 char) not null
		constraint PK_UOM_FORM
			primary key,
	UOM_FORM_ABB VARCHAR2(6 char) not null,
	UOM_FORM_DES VARCHAR2(50 char) not null
)
;

create table EMD.PAN_SECT
(
	PAN_SECT_CD VARCHAR2(5 char) not null
		constraint PK_PAN_SECT
			primary key,
	PAN_SECT_ABB VARCHAR2(6 char) not null,
	PAN_SECT_DES VARCHAR2(50 char) not null
)
;

create table EMD.UOM
(
	UOM_ID NUMBER not null
		constraint PK_UOM
			primary key,
	UOM_DES VARCHAR2(30 char) not null,
	UOM_EXT_DES VARCHAR2(50 char),
	UOM_DSPLY_NM VARCHAR2(50 char) not null,
	MET_OR_IMPRL_UOM_SW CHAR(1 char) not null,
	UOM_FORM_CD VARCHAR2(5 char) not null
		constraint UOM_FK2
			references EMD.UOM_FORM,
	SRC_SYSTEM_ID NUMBER not null,
	SRC_SYS_REF_ID VARCHAR2(40 char)
)
;

create table EMD.NTRNT
(
	NTRNT_ID NUMBER not null
		constraint PK_NTRNT
			primary key,
	PARNT_NTRNT_ID NUMBER
		constraint NTRNT_FK1
			references EMD.NTRNT
				on delete set null,
	NTRNT_DES VARCHAR2(50 char) not null,
	NTRNT_DSPLY_SEQ_NBR NUMBER not null,
	PDV_OR_WHOLE_SW CHAR(1 char) not null,
	UOM_ID NUMBER not null
		constraint NTRNT_FK2
			references EMD.UOM
				on delete set null,
	RCMD_DALY_VAL_QTY NUMBER(9,4),
	PAN_VER_CD VARCHAR2(5 char) not null,
	BOLD_SW CHAR(1 char) not null,
	SRC_SYSTEM_ID NUMBER not null,
	SRC_SYS_REF_ID VARCHAR2(40 char),
	PAN_SECT_CD VARCHAR2(5 char) not null
		constraint NTRNT_FK4
			references EMD.PAN_SECT,
	ALLOW_LESS_THAN_SW CHAR(1 char) not null,
	LESS_THEN_VAL_QTY NUMBER(9,4)
)
;

create table EMD.NTRN_PAN_HDR
(
	NTRN_PAN_HDR_ID NUMBER not null
		constraint PK_NTRN_PAN_HDR
			primary key,
	SCN_CD_ID NUMBER(17),
	SRC_SYSTEM_ID NUMBER not null,
	SRC_SYS_REF_ID VARCHAR2(40 char),
	SRC_SYS_LST_UPDT_TS TIMESTAMP(6),
	PAN_NBR NUMBER not null,
	IMPRL_SRVNG_SZ_TXT VARCHAR2(255 char),
	IMPRL_SRVNG_SZ_UOM_ID NUMBER
		constraint NTRN_PAN_HDR_FK3
			references EMD.UOM
				on delete set null,
	SPC_TXT VARCHAR2(100 char),
	MET_SRVNG_SZ_TXT VARCHAR2(255 char),
	MET_SRVNG_SZ_UOM_ID NUMBER
		constraint NTRN_PAN_HDR_FK4
			references EMD.UOM
				on delete set null,
	SRVNG_SZ_TXT VARCHAR2(255 char),
	ACTV_SW CHAR(1 char) not null,
	EFF_DT DATE,
	PUBED_SW CHAR(1 char) not null,
	PAN_TYP_CD VARCHAR2(5 char) not null
		constraint NTRN_PAN_HDR_FK5
			references EMD.NTRN_PAN_TYP
)
;

create table EMD.NTRN_PAN_COL_HDR
(
	NTRN_PAN_HDR_ID NUMBER not null
		constraint NTRN_PAN_COL_HDR_FK1
			references EMD.NTRN_PAN_HDR,
	NTRN_PAN_COL_ID NUMBER not null,
	CLRS_QTY NUMBER(9,4),
	SRVNG_SZ_TXT VARCHAR2(255 char),
	constraint PK_NTRN_PAN_COL_HDR
		primary key (NTRN_PAN_HDR_ID, NTRN_PAN_COL_ID)
)
;

create table EMD.NTRN_PAN_DTL
(
	NTRN_PAN_HDR_ID NUMBER not null,
	NTRN_PAN_COL_ID NUMBER not null,
	NTRNT_ID NUMBER not null
		constraint NTRN_PAN_DTL_FK2
			references EMD.NTRNT,
	NTRNT_QTY NUMBER(7,2),
	DALY_VAL_PCT NUMBER(7,2),
	LESS_THAN_SW CHAR(1 char) not null,
	constraint PK_NTRN_PAN_DTL
		primary key (NTRN_PAN_HDR_ID, NTRN_PAN_COL_ID, NTRNT_ID),
	constraint NTRN_PAN_DTL_FK1
		foreign key (NTRN_PAN_HDR_ID, NTRN_PAN_COL_ID) references EMD.NTRN_PAN_COL_HDR
)
;