create table APPL_NM
(
	APPL_ID NUMBER not null,
	APPL_NM varchar(20) not null,
	BUS_OWNER_DEPT varchar(30) not null
)
;

create table USR_ROLE
(
	USR_ROLE_CD NUMBER NOT NULL,
	USR_ROLE_ABB varchar(6) not null,
	USR_ROLE_DES varchar(50) not null,
	CRE8_TS  TIMESTAMP(0),
	JOB_CD_XREF varchar(6),
	LST_UID varchar(8),
	LST_UPDT_TS  TIMESTAMP(0),
	APPL_ID NUMBER not null
)
;

create table USR_SEC_GRP
(
	USR_ID varchar(20) not null,
	USR_ROLE_CD NUMBER not null,
	CRE8_TS TIMESTAMP(0),
	LST_UID varchar(8),
	LST_UPDT_TS TIMESTAMP(0)
)
;

create table IDM
(
	USR_ID varchar(20) not null,
	FRST_NM varchar(50),
	LST_NM varchar(50),
	FRST_LOG_IN_TS TIMESTAMP(0),
	LST_LOG_IN_TS TIMESTAMP(0),
	EMAIL_ID varchar(80)
)
;

create table SEC_GRP_RESRC
(
	RESRC_ID NUMBER not null,
	USR_ROLE_CD NUMBER not null,
	ACS_CD varchar(5) not null,
	EFF_DT TIMESTAMP(0) NOT NULL,
	EXPRN_DT TIMESTAMP(0) NOT NULL,
	CRE8_TS TIMESTAMP(0),
	LST_UID varchar(8) not null,
	LST_UPDT_TS TIMESTAMP(0) not null
)
;

create table ACCESS_TYPE
(
	ACS_CD varchar(5) not null,
	ACS_ABB varchar(6) not null,
	ACS_DES varchar(50) not null,
	CRE8_TS TIMESTAMP(0) not null,
	LST_UID varchar(8),
	LST_UPDT_TS TIMESTAMP(0) not null
)
;

create table RESRC
(
	RESRC_ID NUMBER,
	RESRC_NM varchar(30) not null,
	CRE8_TS TIMESTAMP(0) not null,
	LST_UID varchar(8) not null,
	LST_UPDT_TS TIMESTAMP(0) not null,
	RESRC_TYP_ID NUMBER,
	TOOL_TIP_SW varchar(1) not null,
	RESRC_DEFNTN varchar(255),
	SCREEN_NM varchar(50),
	APPL_ID NUMBER,
	PARNT_RESRC_ID NUMBER
)
;

create table JOB_CD
(
	JOB_CD varchar(6) not null,
	JOB_DES varchar(50) not null
)
;

create table USR_ROLE_JOB_CD
(
	USR_ROLE_CD NUMBER not null,
	JOB_CD varchar(6) not null
)
;












