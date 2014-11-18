package org.opendaylight.tutorial.plugin_exercise.internal;

import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.utils.Status;
import org.opendaylight.controller.sal.utils.StatusCode;
import org.opendaylight.ovsdb.lib.error.SchemaVersionMismatchException;
import org.opendaylight.ovsdb.lib.notation.Row;
import org.opendaylight.ovsdb.lib.notation.UUID;
import org.opendaylight.ovsdb.plugin.api.OvsdbConfigurationService;
import org.opendaylight.ovsdb.plugin.api.OvsdbConnectionService;
import org.opendaylight.ovsdb.plugin.api.OvsdbInventoryListener;
import org.opendaylight.ovsdb.plugin.api.StatusWithUuid;
import org.opendaylight.ovsdb.schema.openvswitch.Bridge;
import org.opendaylight.ovsdb.schema.openvswitch.Interface;
import org.opendaylight.ovsdb.schema.openvswitch.OpenVSwitch;
import org.opendaylight.ovsdb.schema.openvswitch.Port;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TutorialOvsBridgeManager implements OvsdbInventoryListener {
    static final Logger logger = LoggerFactory.getLogger(TutorialOvsBridgeManager.class);

    // The implementation for each of these services is resolved by the OSGi Service Manager
    private volatile OvsdbConfigurationService ovsdbConfigurationService;
    private volatile OvsdbConnectionService ovsdbConnectionService;

    public void init() {
        logger.debug("Initializing");
    }

    @Override
    public void nodeAdded(Node node, InetAddress address, int port) {
        logger.info("OVS node added {}", node);

        //Create bridge

        //Create tunnel port

        //Set OpenFlow controller
    }

    @Override
    public void nodeRemoved(Node node) {
        logger.info("OVS node removed {}", node);
    }

    @Override
    public void rowAdded(Node node, String tableName, String uuid, Row row) {
        processRowEvent(node, tableName, uuid, row, null, "ADD");
    }

    @Override
    public void rowUpdated(Node node, String tableName, String uuid, Row oldRow, Row newRow) {
        processRowEvent(node, tableName, uuid, newRow, null, "UPDATE");
    }

    @Override
    public void rowRemoved(Node node, String tableName, String uuid, Row row, Object context) {
        processRowEvent(node, tableName, uuid, row, context, "DELETE");
    }

    private void processRowEvent(Node node, String tableName, String uuid, Row row, Object context, String eventType) {
        if (tableName.equalsIgnoreCase(ovsdbConfigurationService.getTableName(node, Interface.class)))
            logger.info("Processing {} of {} node: {}, uuid: {}, row: {}", eventType, tableName, node, uuid, row);
        else if (tableName.equalsIgnoreCase(ovsdbConfigurationService.getTableName(node, Port.class)))
            logger.info("Processing {} of {} node: {}, port uuid: {}, row: {}", eventType, tableName, node, uuid, row);
        else if (tableName.equalsIgnoreCase(ovsdbConfigurationService.getTableName(node, OpenVSwitch.class)))
            logger.info("Processing {} of {} node: {}, ovs uuid: {}, row: {}", eventType, tableName, node, uuid, row);
    }
}
