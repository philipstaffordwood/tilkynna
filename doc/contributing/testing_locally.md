# Postman tests with Newman

Once you have followed the setup steps on README to get a Tilkynna Docker container running. You can run the postman tests against this container using:

```terminal
docker run -v ~/work/bb/repository/tilkynna/src/test/postman:/etc/postman -t postman/newman_alpine33 run /etc/postman/Tilkynna.postman_collection.json --global-var "host=http://<TILKYNNA_IP_ADDRESS>" --global-var "port=9981" --global-var 'files_folder=/etc/postman'  --global-var 'pg_url=jdbc:postgresql://<DB_IP_ADDRESS>:5432/tilkynna' 
```

Where the host is the IP of Docker container for Tilkynna

**Postman Collections are under **

- `src/test/postman/`



# Testing Helm Chart locally

### Using: mk8s

When docker files are built locally [Docker](https://www.docker.com/) stores the resulting image within a local repository. By default this is on the host machine created when Docker was installed. mk8s has its own Docker repository running at localhost:32000. All images used in the cluster will be pulled into and sourced from this repository (i.e: localhost:32000). 

To test your own Docker image within the mk8s cluster, when using Helm. You will need to push your Docker image from local docker repository to mk8s repository at localhost:32000. To do this use the following steps: 

- Enable mk8s registry using
  `microk8s.enable registry`
- Build docker image locally as usual: 
  `docker build -t org.tilkynna/tilkynna .`
- Tag this image 
  `docker tag org.tilkynna/tilkynna localhost:32000/org.tilkynna/tilkynna`
- Push your tagged image to mk8s repository 
  `docker push localhost:32000/org.tilkynna/tilkynna`

Your newly built docker image will now be in your local Docker repository as well as the mk8s repository. 

Then when using Helm commands as described in [Tilkynna Helm Chart](../../helm-chart/README.md) it'll use your most recent docker image.

