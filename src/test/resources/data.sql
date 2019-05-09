DELETE FROM _reports.template_tag;
DELETE FROM _reports.template_datasource WHERE template_id IN (SELECT id FROM _reports.template);
DELETE FROM _reports.template;

