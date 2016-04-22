#!/bin/bash
if [ $# -ne 1 ]
then
    echo "Usage: spawn_router <device-name>"
    exit 0
fi

######## Spin up containers
DEVICE_NAME=$1
docker rm -f "$DEVICE_NAME"
DOCKER_ID=`docker run --name "$DEVICE_NAME" -dit sdnhub/netopeer /bin/bash`
echo $DOCKER_ID
echo "Spawned container with IP `sudo docker inspect --format '{{ .NetworkSettings.IPAddress }}' $DEVICE_NAME`"

######## Start netconf server with custom YANG model
docker cp base_datastore.xml "$DEVICE_NAME:/usr/local/etc/netopeer/cfgnetopeer/datastore.xml"
docker cp router.yang "$DEVICE_NAME:/root/router.yang"
docker exec $DEVICE_NAME pyang -f yin /root/router.yang -o /root/router.yin
docker exec $DEVICE_NAME netopeer-manager add --name router --model router.yin --datastore /usr/local/etc/netopeer/cfgnetopeer/router.xml
docker exec $DEVICE_NAME netopeer-server -d
