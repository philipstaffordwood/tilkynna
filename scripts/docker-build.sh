#
# *************************************************
# Copyright (c) 2019, Grindrod Bank Limited
# License MIT: https://opensource.org/licenses/MIT
# **************************************************
#
echo "###########################"
echo "####Docker build script####"
echo "###########################"

#we make sure we have been given all required parameters
if [ $# != 3 ]
  then
    echo "THIS EXPECTS to be run from scripts,  i.e.:"
    echo "./scripts/docker-build.sh"
    echo ""
    echo "Missing parameters. Expecting:"
    echo './scripts/docker-build.sh $REPO $IMAGE_NAME $TAG'
    echo "e.g."
    echo "./scripts//docker-build.sh philipstaffordwood tilkynna 0.0.1"
    exit 1
fi


set -v -x  #we want to see what is being executed
DOCKER_REPO=$1
IMAGE_NAME=$2
TAG=$3

docker build -t $DOCKER_REPO/$IMAGE_NAME:$TAG . 

if [ $? -ne 0 ]; then
    echo "Docker build failed."
    exit 2
fi


