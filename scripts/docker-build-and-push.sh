#
# *************************************************
# Copyright (c) 2019, Grindrod Bank Limited
# License MIT: https://opensource.org/licenses/MIT
# **************************************************
#
echo "####################################"
echo "####Docker build and push script####"
echo "####################################"

#we make sure we have been given all required parameters
if [ $# != 6 ]
  then
    echo "THIS EXPECTS to be run from scripts,  i.e.:"
    echo "./scripts/docker-build-and-push.sh"
    echo ""
    echo "Missing parameters. Expecting:"
    echo './scripts/docker-build-and-push.sh $REPO $IMAGE_NAME $TAG $DOCKER_REPO_HOST $DOCKER_USER $DOCKER_PASS' #'-to prevent variable subst
    echo "e.g."
    echo "./scripts/docker-build-and-push.sh philipstaffordwood tilkynna 0.0.1 index.docker.io philipstaffordwood SOMEPASSWORD"
    echo "Docker hub is index.docker.io"
    echo 'This script at commit #XXXX does not echo the $DOCKER_PASS,' #'-to prevent variable subst
    echo "but take care to prevent it's exposure in build tooling."
    exit 1
fi
#password set before -x to prevent it being visible in the logs
DOCKER_PASS=$6

set -v -x  #we want to see what is being executed
export DOCKER_REPO=$1
export IMAGE_NAME=$2
export TAG=$3
DOCKER_REPO_HOST=$4
DOCKER_USER=$5

./scripts/docker-build.sh $DOCKER_REPO $IMAGE_NAME $TAG
if [ $? -ne 0 ]; then
    echo "Docker build failed."
    exit 2
fi


set +x #switch of -x to prevent passwords being visible in the logs
docker login -u $DOCKER_USER -p $DOCKER_PASS && \
set -v -x  #we want to see what is being executed again

if [ $? -ne 0 ]; then
    echo "Docker login to $DOCKER_REPO_HOST for user $DOCKER_USER failed."
    exit 3
fi



docker push $DOCKER_REPO/$IMAGE_NAME:$TAG
# Tag and push latest
docker tag $DOCKER_REPO/$IMAGE_NAME:$TAG $DOCKER_REPO/$IMAGE_NAME:latest
docker push $DOCKER_REPO/$IMAGE_NAME:latest
