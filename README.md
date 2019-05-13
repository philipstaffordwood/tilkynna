[![CircleCI](https://circleci.com/gh/GrindrodBank/tilkynna.svg?style=svg)](https://circleci.com/gh/GrindrodBank/tilkynna)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2FGrindrodBank%2Ftilkynna.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2FGrindrodBank%2Ftilkynna?ref=badge_shield)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Grindrodbank_tilkynna&metric=alert_status)](https://sonarcloud.io/dashboard?id=Grindrodbank_tilkynna)

# Tilkynna
Tilkynna is an enterprise grade, utility-style report server wrapper written in Java.

## Quickstart

Taking it out for a spin in 5-10 minutes:

### Pre-requisites

> This example uses:
> * [docker](https://www.docker.com): docker must be installed on the host.
> * [docker-compose](https://docs.docker.com/compose/): docker-compose must be installed on the host.


* Clone Github repo:
```bash
git clone git@github.com:GrindrodBank/tilkynna.git
```

* Run using docker-compose:
```bash
cd tilkynna
cd quickstart/docker-compose
docker-compose -p tilkynna up
```

4 services are installed:
* `Web_1` - The actual Tilkynna reporting service installed on port 9981
* `PostgreSQL` Database installed on port 5432
* `Keycloak` Authentication service installed on port 9191 and can be accessed by going to http://localhost:9191
* `SFTP` - An SFTP server running on port 2222

### Credentials for PostgreSQL database
* Password: postgres
* Database: tilkynna

### Credentials for Keycloak
* Username: admin
* Password: admin

### Credentials for SFTP
* Username: foo
* Password: pass

## Using the API 

- [Via CLI curl commands](doc/quickstart_using_api/via_cli_curl.md)
- [Using Postman GUI Tool](doc/quickstart_using_api/via_postman_gui.md)

## To undeploy everything:

```bash
docker-compose -p tilkynna down
```

# Project Documentation

All project documentation is currently available within the `/doc` folder.

## License
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2FGrindrodBank%2Ftilkynna.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2FGrindrodBank%2Ftilkynna?ref=badge_large)
