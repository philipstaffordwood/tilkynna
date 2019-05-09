insert into export_format(name, media_type, is_active) values('PDF', 'application/pdf', true);
insert into export_format(name, media_type, is_active) values('HTML', 'text/html', true);
insert into export_format(name, media_type, is_active) values('XLSX', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', true);

insert into  _reports.destination 
(
	type,
	name,
	description,
	security_protocol,	
    downloadable,
	is_active)
values ('STREAM', 'STREAM', 'STREAM', 'ssl', true, true);
