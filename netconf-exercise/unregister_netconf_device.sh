#!/bin/bash
if [ $# -ne 1 ]
then
    echo "Usage: unregister_netconf_device <device-name>"
    exit 0
fi

DEVICE_NAME=$1
CONTROLLER_IP=localhost

curl -u admin:admin -X DELETE http://${CONTROLLER_IP}:8181/restconf/config/opendaylight-inventory:nodes/node/controller-config/yang-ext:mount/config:modules/module/odl-sal-netconf-connector-cfg:sal-netconf-connector/$DEVICE_NAME
