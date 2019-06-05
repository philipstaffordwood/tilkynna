
-- object: processed_by | type: COLUMN --
-- ALTER TABLE _reports.generated_report DROP COLUMN IF EXISTS processed_by CASCADE;
ALTER TABLE _reports.generated_report ADD COLUMN processed_by text;
-- ddl-end --

COMMENT ON COLUMN _reports.generated_report.processed_by IS 'The name of instance/thread that last processed, this generated_report request. ';
-- ddl-end --