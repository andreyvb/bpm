-- begin BPM_PROC_DEFINITION
alter table BPM_PROC_DEFINITION add constraint FK_BPM_PD_MODEL_ID foreign key (MODEL_ID) references BPM_PROC_MODEL(ID)^
create index IDX_BPM_PD_MODEL_ID on BPM_PROC_DEFINITION (MODEL_ID)^
-- end BPM_PROC_DEFINITION
-- begin BPM_PROC_ROLE
alter table BPM_PROC_ROLE add constraint FK_BPM_PR_PROC_DEFINITION_ID foreign key (PROC_DEFINITION_ID) references BPM_PROC_DEFINITION(ID)^
create index IDX_BPM_PR_PROC_DEFINITION_ID on BPM_PROC_ROLE (PROC_DEFINITION_ID)^
-- end BPM_PROC_ROLE
-- begin BPM_PROC_INSTANCE
alter table BPM_PROC_INSTANCE add constraint FK_BPM_PI_PROC_DEFINITION_ID foreign key (PROC_DEFINITION_ID) references BPM_PROC_DEFINITION(ID)^
alter table BPM_PROC_INSTANCE add constraint FK_BPM_PI_STARTED_BY_ID foreign key (STARTED_BY_ID) references SEC_USER(ID)^
create index IDX_BPM_PI_PROC_DEFINITION_ID on BPM_PROC_INSTANCE (PROC_DEFINITION_ID)^
create index IDX_BPM_PI_STARTED_BY_ID on BPM_PROC_INSTANCE (STARTED_BY_ID)^
-- end BPM_PROC_INSTANCE
-- begin BPM_PROC_ACTOR
alter table BPM_PROC_ACTOR add constraint FK_BPM_PROC_ACTOR_USER_ID foreign key (USER_ID) references SEC_USER(ID)^
alter table BPM_PROC_ACTOR add constraint FK_BPM_PA_PROC_INSTANCE_ID foreign key (PROC_INSTANCE_ID) references BPM_PROC_INSTANCE(ID)^
alter table BPM_PROC_ACTOR add constraint FK_BPM_PA_PROC_ROLE_ID foreign key (PROC_ROLE_ID) references BPM_PROC_ROLE(ID)^
create index IDX_BPM_PA_PROC_ROLE_ID on BPM_PROC_ACTOR (PROC_ROLE_ID)^
create index IDX_BPM_PROC_ACTOR_USER_ID on BPM_PROC_ACTOR (USER_ID)^
create index IDX_BPM_PRCA_PROC_INSTANCE_ID on BPM_PROC_ACTOR (PROC_INSTANCE_ID)^
-- end BPM_PROC_ACTOR
-- begin BPM_PROC_TASK
alter table BPM_PROC_TASK add constraint FK_BPM_PT_PROC_INSTANCE_ID foreign key (PROC_INSTANCE_ID) references BPM_PROC_INSTANCE(ID)^
alter table BPM_PROC_TASK add constraint FK_BPM_PT_PROC_ACTOR_ID foreign key (PROC_ACTOR_ID) references BPM_PROC_ACTOR(ID)^
create index IDX_BPM_PT_PROC_ACTOR_ID on BPM_PROC_TASK (PROC_ACTOR_ID)^
create index IDX_BPM_PT_PROC_INSTANCE_ID on BPM_PROC_TASK (PROC_INSTANCE_ID)^
-- end BPM_PROC_TASK
-- begin BPM_PROC_ATTACHMENT
alter table BPM_PROC_ATTACHMENT add constraint FK_BPM_PA_FILE_ID1 foreign key (FILE_ID) references SYS_FILE(ID)^
alter table BPM_PROC_ATTACHMENT add constraint FK_BPM_PA_TYPE_ID1 foreign key (TYPE_ID) references BPM_PROC_ATTACHMENT_TYPE(ID)^
alter table BPM_PROC_ATTACHMENT add constraint FK_BPM_PA_PROC_INSTANCE_ID1 foreign key (PROC_INSTANCE_ID) references BPM_PROC_INSTANCE(ID)^
alter table BPM_PROC_ATTACHMENT add constraint FK_BPM_PA_PROC_TASK_ID foreign key (PROC_TASK_ID) references BPM_PROC_TASK(ID)^
alter table BPM_PROC_ATTACHMENT add constraint FK_BPM_PA_AUTHOR_ID foreign key (AUTHOR_ID) references SEC_USER(ID)^
create index IDX_BPM_PA_AUTHOR_ID on BPM_PROC_ATTACHMENT (AUTHOR_ID)^
create index IDX_BPM_PA_TYPE_ID on BPM_PROC_ATTACHMENT (TYPE_ID)^
create index IDX_BPM_PA_FILE_ID on BPM_PROC_ATTACHMENT (FILE_ID)^
create index IDX_BPM_PA_PROC_TASK_ID on BPM_PROC_ATTACHMENT (PROC_TASK_ID)^
create index IDX_BPM_PA_PROC_INSTANCE_ID on BPM_PROC_ATTACHMENT (PROC_INSTANCE_ID)^
-- end BPM_PROC_ATTACHMENT
-- begin BPM_PROC_MODEL
create unique index IDX_BPM_PROC_MODEL_UK_NAME on BPM_PROC_MODEL (NAME, DELETE_TS) ^
-- end BPM_PROC_MODEL
-- begin BPM_PROC_TASK_USER_LINK
alter table BPM_PROC_TASK_USER_LINK add constraint FK_BPTUL_PROC_TASK foreign key (PROC_TASK_ID) references BPM_PROC_TASK (ID)^
alter table BPM_PROC_TASK_USER_LINK add constraint FK_BPTUL_USER foreign key (USER_ID) references SEC_USER (ID)^
-- end BPM_PROC_TASK_USER_LINK
-- begin BPM_STENCIL_SET
create unique index IDX_BPM_STENCIL_SET_UK_NAME on BPM_STENCIL_SET (NAME, DELETE_TS)  ^
-- end BPM_STENCIL_SET

