INSERT INTO "tenant" (`company_name`)
VALUES ('datarget-company');

insert into "role" (`role_name`, `tenant_id`,`is_deleted`)
select 'datarget-admin',  id ,0 from tenant where company_name = 'datarget-company';