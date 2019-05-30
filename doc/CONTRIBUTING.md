Contributing to Tilkynna
======================

The Tilkynna team maintains guidelines for contributing to this repo. A Tilkynna team member will be happy to explain why a guideline is defined as it is.

General contribution guidance is included in this document. Additional guidance is defined in the documents linked below.

- [Copyright](copyright.md) describes the licensing practices for the project.
- [Contribution Workflow](contributing-workflow.md) describes the workflow that the team uses for considering and accepting changes.

Up for Grabs
------------

The team marks the most straightforward issues as "up for grabs". This set of issues is the place to start if you are interested in contributing but new to the codebase.

- [Tilkynna - "up for grabs"](https://github.com/GrindrodBank/Tilkynna/labels/up-for-grabs)

Contribution "Bar"
------------------

Project maintainers will merge changes that improve the product significantly and broadly and that align with the [Tilkynna roadmap](roadmap.md). 

Maintainers will not merge changes that have narrowly-defined benefits, due to compatibility risk. Other companies are building products on top of Tilkynna, too. We may revert changes if they are found to be breaking.

Contributions must also satisfy the other published guidelines defined in this document.

DOs and DON'Ts
--------------

Please do:

* **DO** follow our [coding style](coding-style.md) (Java code-specific)
* **DO** give priority to the current style of the project or file you're changing even if it diverges from the general guidelines.
* **DO** include tests when adding new features. When fixing bugs, start with
  adding a test that highlights how the current behavior is broken.
* **DO** keep the discussions focused. When a new or related topic comes up
  it's often better to create new issue than to side track the discussion.
* **DO** blog and tweet (or whatever) about your contributions, frequently!

Please do not:

* **DON'T** make PRs for style changes. 
* **DON'T** surprise us with big pull requests. Instead, file an issue and start
  a discussion so we can agree on a direction before you invest a large amount
  of time.
* **DON'T** commit code that you didn't write. If you find code that you think is a good fit to add to Tilkynna, file an issue and start a discussion before proceeding.
* **DON'T** submit PRs that alter licensing related files or headers. If you believe there's a problem with them, file an issue and we'll be happy to discuss it.
* **DON'T** add API additions without filing an issue and discussing with us first. See [API Review Process](api-review-process.md).

Commit Messages
---------------

Please format commit messages as follows (based on [A Note About Git Commit Messages](http://tbaggery.com/2008/04/19/a-note-about-git-commit-messages.html)):

```
Summarize change in 50 characters or less

Provide more detail after the first line. Leave one blank line below the
summary and wrap all lines at 72 characters or less.

If the change fixes an issue, leave another blank line after the final
paragraph and indicate which issue is fixed in the specific format
below.

Fix #42
```

Also do your best to factor commits appropriately, not too large with unrelated things in the same commit, and not too small with the same small change applied N times in N different commits.

File Headers
------------

Please review the [Using RAT licensing](license/using_license_rat.md) on how to add the correct file headers to source files.


Copying Files from Other Projects
---------------------------------

Tilkynna uses some files from other projects, typically where a binary distribution does not exist or would be inconvenient.

The following rules must be followed for PRs that include files from another project:

- The license of the file is [permissive](https://en.wikipedia.org/wiki/Permissive_free_software_licence).
- The license of the file is left in-tact.
- The contribution is correctly attributed in the [3rd party notices](THIRD-PARTY-NOTICES.TXT) file in the repository, as needed.

Porting Files from Other Projects
---------------------------------

There are many good algorithms implemented in other languages that would benefit the Tilkynna project. The rules for porting a C# file to Java, for example, are the same as would be used for copying the same file, as described above.

[Clean-room](https://en.wikipedia.org/wiki/Clean_room_design) implementations of existing algorithms that are not permissively licensed will generally not be accepted. If you want to create or nominate such an implementation, please create an issue to discuss the idea.

Developer Guidelines
---------------------------------
Here are some guidelines to getting started 
* [Initialise Maven repository](#initialise-maven-repository)
* [Build Project in your IDE](#build-project-in-your-IDE)
* [Styleguides](coding-style.md)

Before starting make sure you can build and run the project locally. 

## Initialise Maven repository
Tilkynna uses a jar which does not reside on any Maven Repository. As such to install this into your own maven repo and will need to run 
`./mvnw clean` 

## Building Project via CLI

Requirements:
* JDK 1.8 or later  (jdk1.8.0_101 used during development)
* Maven 3.2+        (apache-maven-3.3.9 used during development)

`./mvnw clean package` 

### Docker Build

Requirements:
* Docker

```bash
docker build -t org.tilkynna/tilkynna .
```

## Building Project in you IDE

This project uses Lombok you will need to install the IDE plugin. See install steps from [https://projectlombok.org/](https://projectlombok.org/)

## Running Project in you IDE

NOTE: you will need to include the `libs-no-repo` folder into the classpath of your project to get it to run via your favourite IDE.

## Running a Built Tilkynna Instance:
#### NOTE: You will need a Postgres instance with DB named tilkynna to connect to 
There are multiple ways to setup a Postgres instance: docker or you could already have one somewhere. 

### **To setup a posgtres instance via docker use command** (from Tilkynna root directory)

- `docker run -d --name tilkynnadb -p 5432:5432 -v "$PWD/my-postgres.conf":/etc/postgresql/postgresql.conf -e POSTGRES_PASSWORD=postgres postgres:10-alpine -c 'config_file=/etc/postgresql/postgresql.conf'`
- Get the IP of the docker container running above: 
	`docker inspect -f '{{ range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' tilkynnadb`
- Create DB named tilkynna on running posgres docker container:
SSH onto the docker container using: 
	`docker exec -it tilkynnadb sh`
then run  
	`PGPASSWORD=postgres createdb -h localhost -p 5432 -U postgres tilkynna`

### Running Tilkynna using Docker
Using IP or your Postgres instance above for <IP_ADDRESS>

* `docker run -it --name tilkynna -p 9981:9981 -e SPRING_DATASOURCE_URL='jdbc:postgresql://<IP_ADDRESS>:5432/tilkynna?user=postgres&password=postgres' tilkynna:latest`

### Running Tilkynna using Java

Ensure the IP address to DB in application.yml is changed 
* `java -Djava.security.egd=file:/dev/./urandom -Dloader.path=drivers,libs-no-repo -Duser.timezone=UTC -jar target/tilkynna.jar`

 
