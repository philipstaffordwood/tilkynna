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
	
	tilkynna=# select min(requested_at), max(generated_at), age(max(generated_at), min(requested_at)), count(*)   from _reports.generated_report WHERE report_status = 'FINISHED';
	            min            |            max             |     age      | count 
	---------------------------+----------------------------+--------------+-------
	 2019-05-27 15:29:13.52+00 | 2019-05-27 15:43:01.696+00 | 00:13:48.176 | 10000
	(1 row)

tilkynna=# select  (sum(age(generated_at, requested_at))/count(*)) as avg, count(*)  from _reports.generated_report WHERE report_status = 'FINISHED';
       avg       | count 
-----------------+-------
 00:00:00.507647 | 10000
(1 row)

tilkynna=# 
	
	
	
	
	
TEST7
tilkynna=# select min(requested_at), max(generated_at), age(max(generated_at), min(requested_at)), count(*)   from _reports.generated_report WHERE report_status = 'FINISHED';
             min              |            max             |      age       | count 
------------------------------+----------------------------+----------------+-------
 2019-05-28 07:40:48.86141+00 | 2019-05-28 07:48:59.304+00 | 00:08:10.44259 |  9997
(1 row)

tilkynna=# select report_status, count(*) from _reports.generated_report GROUP BY report_status;
 report_status | count 
---------------+-------
 STARTED       |     3
 FINISHED      |  9997
(2 rows)

tilkynna=# 
	