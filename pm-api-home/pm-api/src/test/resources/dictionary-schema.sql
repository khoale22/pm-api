-- auto-generated definition
create table EMD.VOCABULARY
(
    WORD_TXT     VARCHAR2(64) not null,
    WORD_CD      VARCHAR2(5)  not null,
    CS_CD        VARCHAR2(5)  not null,
    LST_UPDT_TS  TIMESTAMP(6),
    LST_UPDT_UID VARCHAR2(20),
    ACTV_SW      VARCHAR2(1),
    constraint VOCABULARY_PK
        primary key (WORD_TXT, CS_CD)
)
;

create table EMD.SUGGESTION
(
    WORD_TXT     VARCHAR2(64)  not null,
    CORR_TXT     VARCHAR2(255) not null,
    ACTV_SW      VARCHAR2(1),
    LST_UPDT_TS  TIMESTAMP(6),
    LST_UPDT_UID VARCHAR2(20),
    constraint SUGGESTION_PK
        primary key (WORD_TXT, CORR_TXT)
)
;

create table EMD.WORD_CD
(
    WORD_CD  VARCHAR2(5) not null
        constraint WORD_CD_PK
            primary key,
    WORD_ABB VARCHAR2(6),
    WORD_DES VARCHAR2(50)
)
;

create table EMD.CASE_CD
(
    CS_CD  VARCHAR2(5) not null
        constraint CASE_CD_PK
            primary key,
    CS_ABB VARCHAR2(6),
    CS_DES VARCHAR2(64)
)
;
