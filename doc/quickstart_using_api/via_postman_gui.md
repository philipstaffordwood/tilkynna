## Quickstart for using the Tilkynna API 

### Via Postman GUI Tool

### Pre-requisites

> This example uses:
> * [postman](https://www.getpostman.com/downloads/) (Postman is a GUI client for testing REST APIs) *NOTE:* You need to be using the full installation of postman and not the Chrome app   
> * [filezilla client](https://filezilla-project.org/download.php) (A free FTP client)


### Steps
A `Tilkynna Quickstart.postman_collection.json` Postman collection has been included within the `postman` folder. This collection is intended for exploring the Tilkynna API. There is also a `Tilkynna.postman_environment.json` Postman environment that can be used to interact with the
Tilkynna instance deployed using the `docker-compose` method in the Quickstart above.

* Get an authentication token in order to get API access:
> By running the `Get Key Session Token` request

* Create a data source 
> - First get the IP of postgresql container running from your docker-compose step, using: `docker inspect -f '{{ range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' tilkynna_postgresql_1`
> - Open the body of the `Create a data source.` request edit the URL to DB ie: replace <change_me> with the IP from above `"url": "jdbc:postgresql://<change_me>:5432/tilkynna"`

* Upload a report template:
> To use the sample report available in the postman testing collection:
> - Open the body of the `Upload a report template.` and select the file from `postman/tilkynna_sample_report.rptdesign` for file parameter 

* Create an SFTP report destination
> - First get the IP of SFTP container running from your docker-compose step, using: `docker inspect -f '{{ range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' tilkynna_sftp_1`
> - Open the body of the `Create an SFTP report destination` request edit the host field to SFTP server with the IP from above

* Generate a report
> By running the `Generate a report` request

* Check Status of Report Generation
> By running the `Check Status of Report Generation` request

* The report can be retrieved using SFTP
> - You can either user CLI tools for this described at: [CLI tools](docs/quickstart_using_api/via_cli_curl.md)
> - Or install filezilla and login from host machine using  
> ```
> host: 		sftp://localhost
> port:		2222
> user:		foo
> password:	pass  
> ```

