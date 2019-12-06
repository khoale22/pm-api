create table EMD.PROD_DESC_TXT
(
	PROD_ID NUMBER not null,
	DES_TYP_CD CHAR(5 char) not null,
	LANG_TYP_CD CHAR(5 char) not null,
	PROD_DES VARCHAR2(3000 char),
	LST_UPDT_UID CHAR(20 char) default ' ' not null,
	constraint PK_PROD_DESC_TXT
		primary key (PROD_ID, DES_TYP_CD, LANG_TYP_CD)
)
;

