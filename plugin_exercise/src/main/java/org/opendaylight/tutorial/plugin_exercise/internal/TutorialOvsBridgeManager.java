package org.opendaylight.tutorial.plugin_exercise.internal;

import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.utils.Status;
import org.opendaylight.controller.sal.utils.StatusCode;
import org.opendaylight.ovsdb.lib.error.SchemaVersionMismatchException;
import org.opendaylight.ovsdb.lib.notation.Column;
import org.opendaylight.ovsdb.lib.notation.OvsdbSet;
import org.opendaylight.ovsdb.lib.notation.Row;
import org.opendaylight.ovsdb.lib.notation.UUID;
import org.opendaylight.ovsdb.lib.schema.GenericTableSchema;
import org.opendaylight.ovsdb.plugin.api.Connection;
import org.opendaylight.ovsdb.plugin.api.OvsVswitchdSchemaConstants;
import org.opendaylight.ovsdb.plugin.api.OvsdbConfigurationService;
import org.opendaylight.ovsdb.plugin.api.OvsdbConnectionService;
import org.opendaylight.ovsdb.plugin.api.OvsdbInventoryListener;
import org.opendaylight.ovsdb.plugin.api.OvsdbInventoryService;
import org.opendaylight.ovsdb.plugin.api.StatusWithUuid;
import org.opendaylight.ovsdb.schema.openvswitch.Bridge;
import org.opendaylight.ovsdb.schema.openvswitch.Controller;
import org.opendaylight.ovsdb.schema.openvswitch.Interface;
import org.opendaylight.ovsdb.schema.openvswitch.OpenVSwitch;
import org.opendaylight.ovsdb.schema.openvswitch.Port;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

public class TutorialOvsBridgeManager implements OvsdbInventoryListener {
    static final Logger logger = LoggerFactory.getLogger(TutorialOvsBridgeManager.class);

    // The implementation for each of these services is resolved by the OSGi Service Manager
    private volatile OvsdbConfigurationService ovsdbConfigService;
    private volatile OvsdbConnectionService ovsdbConnectionService;
    private volatile OvsdbInventoryService ovsdbInventoryService;

    public void init() {
        logger.debug("Initializing");
    }

    @Override
    public void nodeAdded(Node node, InetAddress address, int port) {
        logger.info("OVS node added {}", node);

        //Create bridge

        //Create tunnel port

        //Set OpenFlow controller
        setBridgeOFController(node, "br0");
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
        if (tableName.equalsIgnoreCase(ovsdbConfigService.getTableName(node, Interface.class)))
            logger.info("Processing {} of {} node: {}, uuid: {}, row: {}", eventType, tableName, node, uuid, row);
        else if (tableName.equalsIgnoreCase(ovsdbConfigService.getTableName(node, Port.class)))
            logger.info("Processing {} of {} node: {}, port uuid: {}, row: {}", eventType, tableName, node, uuid, row);
        else if (tableName.equalsIgnoreCase(ovsdbConfigService.getTableName(node, OpenVSwitch.class)))
            logger.info("Processing {} of {} node: {}, ovs uuid: {}, row: {}", eventType, tableName, node, uuid, row);
    }

    /* Logic:
     * 1. Extract controller IP address
     * 2. Extract UUID of bridge from the inventory cache based on name of the bridge
     * 3. Check if the bridge already has a controller set.
     *             3.1 If yes, update that.
     *             3.2 If no, insert new controller entry for that bridge
     */
    public void setBridgeOFController(Node node, String bridgeIdentifier) {
        if (ovsdbConnectionService == null) {
            logger.error("Couldn't refer to the ConnectionService");
            return;
        }

        try{
            Connection connection = ovsdbConnectionService.getConnection(node);
            String myIP = connection.getClient().getConnectionInfo().getLocalAddress().getHostAddress();

            Bridge bridge = connection.getClient().getTypedRowWrapper(Bridge.class, null);
            Map<String, Row> brTableCache = ovsdbInventoryService.getTableCache(node, OvsVswitchdSchemaConstants.DATABASE_NAME, bridge.getSchema().getName());
            for (String uuid : brTableCache.keySet()) {
                bridge = connection.getClient().getTypedRowWrapper(Bridge.class, brTableCache.get(uuid));
                if (bridge.getName().contains(bridgeIdentifier)) {

                    String controllerTarget = "tcp:"+ myIP + ":6633";
                    Controller controller = connection.getClient().createTypedRowWrapper(Controller.class);
                    controller.setTarget(controllerTarget);

                    ConcurrentMap<String, Row> rows = ovsdbConfigService.getRows(node, controller.getSchema().getName());
                    if (rows != null) {
                        //Find existing controller
                        for (Map.Entry<String, Row> entry : rows.entrySet()) {
                            Controller currController = ovsdbConfigService.getTypedRow(node, Controller.class, entry.getValue());
                            Column<GenericTableSchema, String> column = currController.getTargetColumn();
                            String currTarget = column.getData();
                            if (currTarget != null && currTarget.equalsIgnoreCase(controllerTarget)) {
                                //Ok to update
                                bridge = connection.getClient().createTypedRowWrapper(Bridge.class);
                                bridge.setController(Sets.newHashSet(currController.getUuid()));
                                ovsdbConfigService.updateRow(node, bridge.getSchema().getName(), null, uuid, bridge.getRow());
                                return;
                            }
                        }
                    }
                    // Else
                    ovsdbConfigService.insertRow(node, controller.getSchema().getName(), uuid, controller.getRow());
                }
            }
        } catch(Exception e) {
            logger.error("Error in setBridgeOFController()", e);
        }
    }
}
