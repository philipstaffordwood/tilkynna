all are being run with STREAM destination (ie: no SFTP)
TEST1
		DEFAULT application.ym
		
		-Djava.security.egd=file:/dev/./urandom -Xmx1g -Xms1g -XX:ActiveProcessorCount=1
		
		200 reports
		
		tilkynna=# select count(*) from _reports.generated_report where report_status = 'FINISHED';
		 count 
		-------
		   200
		(1 row)
		
		
		tilkynna=# select  (sum(age(generated_at, requested_at))/count(*)) as avg, count(*)  from _reports.generated_report WHERE report_status = 'FINISHED';
		       avg       | count 
		-----------------+-------
		 00:01:40.744835 |   200
		(1 row)
		
		tilkynna=# 

TEST2
	application.yml change:   fixedRateInMilliseconds: 10 # make this like 100 ms
	
	-Djava.security.egd=file:/dev/./urandom -Xmx1g -Xms1g -XX:ActiveProcessorCount=1
	
	200 reports
	tilkynna=# select count(*) from _reports.generated_report where report_status = 'FINISHED';
	 count 
	-------
	   200
	(1 row)
	
	tilkynna=# select  (sum(age(generated_at, requested_at))/count(*)) as avg, count(*)  from _reports.generated_report WHERE report_status = 'FINISHED';
	      avg       | count 
	----------------+-------
	 00:00:16.27526 |   200
	(1 row)
	
	tilkynna=# 
	
	
TEST3
	generate: 
	    threading:
	      queueCapacity: 1
	      
	-Djava.security.egd=file:/dev/./urandom -Xmx1g -Xms1g -XX:ActiveProcessorCount=1
	200 reports
	
	tilkynna=# select count(*) from _reports.generated_report where report_status = 'FINISHED';
	 count 
	-------
	   200
	(1 row)
	
	tilkynna=# select  (sum(age(generated_at, requested_at))/count(*)) as avg, count(*)  from _reports.generated_report WHERE report_status = 'FINISHED';
	      avg       | count 
	----------------+-------
	 00:00:19.22962 |   200
	(1 row)
	
	tilkynna=# 
	
TEST4 (same as above: just getting logs from Tilkynna too)
	change longs for : 		org.tilkynna.report.generate: INFO
	      
	-Djava.security.egd=file:/dev/./urandom -Xmx1g -Xms1g -XX:ActiveProcessorCount=1
	200 reports
	
	tilkynna=# select count(*) from _reports.generated_report where report_status = 'FINISHED';
	 count 
	-------
	   200
	(1 row)
	
	tilkynna=# select  (sum(age(generated_at, requested_at))/count(*)) as avg, count(*)  from _reports.generated_report WHERE report_status = 'FINISHED';
	       avg       | count 
	-----------------+-------
	 00:00:17.295845 |   200
	(1 row)
	
	tilkynna=# 
		
		
TEST5
	-Djava.security.egd=file:/dev/./urandom -Xmx1g -Xms1g -XX:ActiveProcessorCount=1 -Duser.timezone=UTC
	scheduler:
      poolSize: 15		
    fixedRateInMilliseconds: 1 # make this like 100 ms
    
	tilkynna=# select count(*) from _reports.generated_report;
	 count 
	-------
	   200
	(1 row)
	
	tilkynna=# select  (sum(age(generated_at, requested_at))/count(*)) as avg, count(*)  from _reports.generated_report WHERE report_status = 'FINISHED';
	       avg       | count 
	-----------------+-------
	 00:00:17.735445 |   200
	(1 row)
	
	tilkynna=# 
	
	
	
TEST6
	-Djava.security.egd=file:/dev/./urandom -Xmx1g -Xms1g -Duser.timezone=UTC  -XX:ActiveProcessorCount=2
    scheduler:
      poolSize: 1
      	
    tilkynna=# select count(*) from _reports.generated_report;
	 count 
	-------
	   200
	(1 row)
	
	tilkynna=# select  (sum(age(generated_at, requested_at))/count(*)) as avg, count(*)  from _reports.generated_report WHERE report_status = 'FINISHED';
	       avg       | count 
	-----------------+-------
	 00:00:13.952913 |   104
	(1 row)
	
	tilkynna=# 
      
	      
		
		
TEST7
	-Djava.security.egd=file:/dev/./urandom -Xmx1g -Xms1g -Duser.timezone=UTC  -XX:ActiveProcessorCount=4
	
	tilkynna=# select count(*) from _reports.generated_report;
	 count 
	-------
	   200
	(1 row)
	
	tilkynna=#  select  (sum(age(generated_at, requested_at))/count(*)) as avg, count(*)  from _reports.generated_report WHERE report_status = 'FINISHED';
	       avg       | count 
	-----------------+-------
	 00:00:12.712776 |    67
	(1 row)
	
	tilkynna=# 
	
	
TEST8
	-Djava.security.egd=file:/dev/./urandom -Xmx1g -Xms1g -Duser.timezone=UTC  -XX:ActiveProcessorCount=4	
	threading:
      poolSize: 4
      
	tilkynna=# select count(*) from _reports.generated_report;
	 count 
	-------
	   200
	(1 row)
	
	tilkynna=# select  (sum(age(generated_at, requested_at))/count(*)) as avg, count(*)  from _reports.generated_report WHERE report_status = 'FINISHED';
	       avg       | count 
	-----------------+-------
	 00:00:14.217918 |   146
	(1 row)
	
	tilkynna=# 
	
      
      