/*
 * Copyright (C) 2015 SDN Hub

 Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3.
 You may not use this file except in compliance with this License.
 You may obtain a copy of the License at

    http://www.gnu.org/licenses/gpl-3.0.txt

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 implied.

 *
 */

package org.sdnhub.odl.tutorial.acl.impl;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataChangeListener;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataBroker;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataChangeEvent;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.sal.binding.api.NotificationProviderService;
import org.opendaylight.controller.sal.binding.api.RpcProviderRegistry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowCapableNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.Table;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.TableKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.Flow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.MatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorRef;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeRef;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.PacketProcessingListener;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.PacketProcessingService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.PacketReceived;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.TransmitPacketInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.TransmitPacketInputBuilder;
import org.opendaylight.yang.gen.v1.urn.sdnhub.odl.tutorial.acl.rev150722.AclSpec;
import org.opendaylight.yang.gen.v1.urn.sdnhub.odl.tutorial.acl.rev150722.acl.spec.Acl;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.concepts.Registration;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.sdnhub.odl.tutorial.utils.GenericTransactionUtils;
import org.sdnhub.odl.tutorial.utils.PacketParsingUtils;
import org.sdnhub.odl.tutorial.utils.inventory.InventoryUtils;
import org.sdnhub.odl.tutorial.utils.openflow13.MatchUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class TutorialACL  implements AutoCloseable, DataChangeListener, PacketProcessingListener {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    private final static long FLOOD_PORT_NUMBER = 0xfffffffbL;

    //Members related to MD-SAL operations
    private List<Registration> registrations;
    private DataBroker dataBroker;
    private PacketProcessingService packetProcessingService;

    public TutorialACL(DataBroker dataBroker, NotificationProviderService notificationService, RpcProviderRegistry rpcProviderRegistry) {
        //Store the data broker for reading/writing from inventory store
        this.dataBroker = dataBroker;

        //Get access to the packet processing service for making RPC calls later
        this.packetProcessingService = rpcProviderRegistry.getRpcService(PacketProcessingService.class);

        //List used to track notification (both data change and YANG-defined) listener registrations
        this.registrations = registerDataChangeListeners();

        //Register this object for receiving notifications when there are PACKET_INs
        registrations.add(notificationService.registerNotificationListener(this));
    }

    @Override
    public void close() throws Exception {
        for (Registration registration : registrations) {
            registration.close();
        }
        registrations.clear();
    }

    private List<Registration> registerDataChangeListeners() {
        Preconditions.checkNotNull(dataBroker);
        List<Registration> registrations = Lists.newArrayList();
        try {
            //Register listener for config updates and topology
            InstanceIdentifier<AclSpec> aclSpecIID = InstanceIdentifier.builder(AclSpec.class)
                    .build();
            ListenerRegistration<DataChangeListener> registration = dataBroker.registerDataChangeListener(
                    LogicalDatastoreType.CONFIGURATION,
                    aclSpecIID, this, AsyncDataBroker.DataChangeScope.SUBTREE);
            LOG.debug("DataChangeListener registered with MD-SAL for path {}", aclSpecIID);
            registrations.add(registration);

        } catch (Exception e) {
            LOG.error("Exception reached {}", e);
        }
        return registrations;
    }

    @Override
    public void onDataChanged(AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> change) {
        LOG.debug("Data changed: {} created, {} updated, {} removed",
                change.getCreatedData().size(), change.getUpdatedData().size(), change.getRemovedPaths().size());

        DataObject dataObject;

        // Iterate over any created nodes or interfaces
        for (Map.Entry<InstanceIdentifier<?>, DataObject> entry : change.getCreatedData().entrySet()) {
            dataObject = entry.getValue();
            if (dataObject instanceof Acl) {
                programACL((Acl)dataObject);
            }
        }
    }

    @Override
    public void onPacketReceived(PacketReceived notification) {
        NodeConnectorRef ingressNodeConnectorRef = notification.getIngress();
        NodeRef ingressNodeRef = InventoryUtils.getNodeRef(ingressNodeConnectorRef);
        // NodeConnectorId ingressNodeConnectorId = InventoryUtils.getNodeConnectorId(ingressNodeConnectorRef);
        NodeId ingressNodeId = InventoryUtils.getNodeId(ingressNodeConnectorRef);

        // Useful to create it beforehand
        NodeConnectorId floodNodeConnectorId = InventoryUtils.getNodeConnectorId(ingressNodeId, FLOOD_PORT_NUMBER);
        NodeConnectorRef floodNodeConnectorRef = InventoryUtils.getNodeConnectorRef(floodNodeConnectorId);

        //Ignore LLDP packets, or you will be in big trouble
        byte[] etherTypeRaw = PacketParsingUtils.extractEtherType(notification.getPayload());
        int etherType = (0x0000ffff & ByteBuffer.wrap(etherTypeRaw).getShort());
        if (etherType == 0x88cc) {
            return;
        }

        // Flood packet
        packetOut(ingressNodeRef, floodNodeConnectorRef, notification.getPayload());
    }

    private void packetOut(NodeRef egressNodeRef, NodeConnectorRef egressNodeConnectorRef, byte[] payload) {
        Preconditions.checkNotNull(packetProcessingService);
        LOG.debug("Flooding packet of size {} out of port {}", payload.length, egressNodeConnectorRef);

        //Construct input for RPC call to packet processing service
        TransmitPacketInput input = new TransmitPacketInputBuilder()
                .setPayload(payload)
                .setNode(egressNodeRef)
                .setEgress(egressNodeConnectorRef)
                .build();
        packetProcessingService.transmitPacket(input);
    }

    private void programACL(Acl acl) {
        /* Programming a flow involves:
         * 1. Creating a Flow object that has a match and drop packets to destination address,
         * 2. Adding Flow object as an augmentation to the Node object in the inventory. 
         * 3. FlowProgrammer module of OpenFlowPlugin will pick up this data change and eventually program the switch.
         */

    	NodeId nodeId = acl.getNode();

        //Creating match object
        MatchBuilder matchBuilder = new MatchBuilder();
        MatchUtils.createDstL3IPv4Match(matchBuilder, acl.getIpAddr());

        /*
         *  Create Flow
         */
        String flowId = "L3_ACL_Rule_" + acl.getDestination();
        FlowBuilder flowBuilder = new FlowBuilder();
        flowBuilder.setMatch(matchBuilder.build());
        flowBuilder.setId(new FlowId(flowId));
        FlowKey key = new FlowKey(new FlowId(flowId));
        flowBuilder.setBarrier(true);
        flowBuilder.setTableId((short)0);
        flowBuilder.setKey(key);
        flowBuilder.setPriority(65535);
        flowBuilder.setFlowName(flowId);
        flowBuilder.setHardTimeout(0);
        flowBuilder.setIdleTimeout(0);

        /* Perform transaction to store rule
         *
         */
        InstanceIdentifier<Flow> flowIID = InstanceIdentifier.builder(Nodes.class)
                .child(Node.class, new NodeKey(nodeId))
                .augmentation(FlowCapableNode.class)
                .child(Table.class, new TableKey(flowBuilder.getTableId()))
                .child(Flow.class, flowBuilder.getKey())
                .build();
        GenericTransactionUtils.writeData(dataBroker, LogicalDatastoreType.CONFIGURATION, flowIID, flowBuilder.build(), true);
        LOG.debug("Programming ACL rule to block destination: {}",acl.getIpAddr());

    }
}
