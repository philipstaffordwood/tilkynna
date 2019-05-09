-- testcontainers creates db named test

-- create schema _reports;

SET secret.key = 'new  value 8';    -- sets for current session 
ALTER DATABASE test SET secret.key = 'new  value 8';  -- sets for subsequent sessions

-- ALTER SYSTEM does not allow for setting of custom keys
-- ALTER SYSTEM SET secret.key = 'new  value 8';

-- set_config only sets the value for current transaction or current session. Flyway & our app setup different sessions to DB
-- SELECT set_config('secret.key', 'helloworld', false);

select current_setting('secret.key');

CREATE ROLE postgres WITH SUPERUSER;

commit;

select pg_reload_conf();

select current_setting('secret.key');
