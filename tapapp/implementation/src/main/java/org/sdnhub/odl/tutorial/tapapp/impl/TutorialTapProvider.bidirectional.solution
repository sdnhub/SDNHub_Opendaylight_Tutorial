package org.sdnhub.odl.tutorial.tapapp.impl;

import java.util.List;
import java.util.Map;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataChangeListener;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataBroker;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataChangeEvent;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.sal.binding.api.NotificationProviderService;
import org.opendaylight.controller.sal.binding.api.RpcProviderRegistry;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.OutputActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.output.action._case.OutputActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.ActionKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowCapableNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowCapableNodeConnectorUpdated;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowCapableNodeUpdated;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.Table;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.TableKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.Flow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.RemoveFlowInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.SalFlowService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.InstructionsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.MatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.ApplyActionsCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.apply.actions._case.ApplyActionsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.InstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.InstructionKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorRemoved;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorUpdated;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeRemoved;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeUpdated;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.OpendaylightInventoryListener;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeKey;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.FieldType;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.ProtocolInfo;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.ProtocolInfoBuilder;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.TapSpec;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.tap.grouping.IpAddressMatchBuilder;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.tap.grouping.MacAddressMatchBuilder;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.tap.spec.Tap;
import org.opendaylight.yang.gen.v1.urn.sdnhub.tutorial.odl.tap.rev150601.tap.spec.TapBuilder;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.concepts.Registration;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.sdnhub.odl.tutorial.utils.GenericTransactionUtils;
import org.sdnhub.odl.tutorial.utils.inventory.InventoryUtils;
import org.sdnhub.odl.tutorial.utils.openflow13.MatchUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class TutorialTapProvider implements AutoCloseable, DataChangeListener, OpendaylightInventoryListener {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    //Members related to MD-SAL operations
    private List<Registration> registrations;
    private DataBroker dataBroker;
    private SalFlowService salFlowService;
    private Map<String, Integer> tapFlows = Maps.newHashMap();

    public TutorialTapProvider(DataBroker dataBroker, NotificationProviderService notificationService, RpcProviderRegistry rpcProviderRegistry) {
        //Store the data broker for reading/writing from inventory store
        this.dataBroker = dataBroker;

        //Object used for flow programming through RPC calls
        this.salFlowService = rpcProviderRegistry.getRpcService(SalFlowService.class);

        //List used to track notification (both data change and YANG-defined) listener registrations
        this.registrations = registerDataChangeListeners();

        //Register this object for receiving notifications when there are New switches
        registrations.add(notificationService.registerNotificationListener(this));
    }

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
            InstanceIdentifier<TapSpec> tapSpecIID = InstanceIdentifier.builder(TapSpec.class)
                    .build();
            ListenerRegistration<DataChangeListener> registration = dataBroker.registerDataChangeListener(
                    LogicalDatastoreType.CONFIGURATION,
                    tapSpecIID, this, AsyncDataBroker.DataChangeScope.SUBTREE);
            LOG.debug("DataChangeListener registered with MD-SAL for path {}", tapSpecIID);
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
            LOG.debug("ADDED Path {}, Object {}", entry.getKey(), dataObject);
            if (dataObject instanceof Tap) {
                addTap((Tap) dataObject);
            }
        }

        // Iterate over any deleted nodes or interfaces
        Map<InstanceIdentifier<?>, DataObject> originalData = change.getOriginalData();
        for (InstanceIdentifier<?> path : change.getRemovedPaths()) {
            dataObject = originalData.get(path);
            LOG.debug("REMOVED Path {}, Object {}", path, dataObject);
            if (dataObject instanceof Tap) {
                removeTap((Tap) dataObject);
            }
        }

        for (Map.Entry<InstanceIdentifier<?>, DataObject> entry : change.getUpdatedData().entrySet()) {
            dataObject = entry.getValue();
            DataObject originalDataObject = originalData.get(entry.getKey());
            LOG.debug("UPDATED Path {}, New Object {}, Old Object {}", entry.getKey(), dataObject, originalDataObject);
        }
    }

    /*
     * General logic for tap:
     * 1. Extract header details from the tap object
     * 2. For each source-port
     *    2.1 Create match builder
     *    2.2 For each sink-port
     */
    private void addTap(Tap tap) {
        if (tap.getMacAddressMatch() != null) {
            if (tap.getMacAddressMatch().getType() == FieldType.Both) {
                //Program source direction
                MacAddressMatchBuilder macAddressMatchBuilder = new MacAddressMatchBuilder(tap.getMacAddressMatch())
                    .setType(FieldType.Source)
                    .setValue(tap.getMacAddressMatch().getValue());
                TapBuilder tapBuilder = new TapBuilder(tap)
                    .setMacAddressMatch(macAddressMatchBuilder.build());
                addTap(tapBuilder.build());

                //Program destination direction
                macAddressMatchBuilder.setType(FieldType.Destination);
                tapBuilder.setMacAddressMatch(macAddressMatchBuilder.build());
                addTap(tapBuilder.build());
            }
        }

        if (tap.getIpAddressMatch() != null) {
            if (tap.getIpAddressMatch().getType() == FieldType.Both) {
                //Program source direction
                IpAddressMatchBuilder macAddressMatchBuilder = new IpAddressMatchBuilder(tap.getIpAddressMatch())
                    .setType(FieldType.Source)
                    .setValue(tap.getIpAddressMatch().getValue());
                TapBuilder tapBuilder = new TapBuilder(tap)
                    .setIpAddressMatch(macAddressMatchBuilder.build());
                addTap(tapBuilder.build());

                //Program destination direction
                macAddressMatchBuilder.setType(FieldType.Destination);
                tapBuilder.setIpAddressMatch(macAddressMatchBuilder.build());
                addTap(tapBuilder.build());
            }
        }
        Integer dlType = null;
        Short nwProto = null;
        Integer tpPort = null;
        if (tap.getTrafficMatch() != null) {
            switch (tap.getTrafficMatch()) {
            case ARP:
                dlType = 0x806;
                break;
            case ICMP:
                dlType = 0x800;
                nwProto = 1;
                break;
            case TCP:
                dlType = 0x800;
                nwProto = 6;
                break;
            case HTTP:
                dlType = 0x800;
                nwProto = 6;
                tpPort = 80;
                break;
            case HTTPS:
                dlType = 0x800;
                nwProto = 6;
                tpPort = 443;
                break;
            case UDP:
                dlType = 0x800;
                nwProto = 0x11;
                break;
            case DNS:
                dlType = 0x800;
                nwProto = 0x11;
                tpPort = 53;
                break;
            case DHCP:
                dlType = 0x800;
                nwProto = 0x11;
                tpPort = 67;
            }
        }
        ProtocolInfoBuilder protocolInfoBuilder = new ProtocolInfoBuilder()
            .setDlType(dlType)
            .setNwProto(nwProto);
        TapBuilder tapBuilder = new TapBuilder(tap);

        if (tpPort != null) {
            //Forward direction
            protocolInfoBuilder.setTpSrc(tpPort);
            tapBuilder.addAugmentation(ProtocolInfo.class, protocolInfoBuilder.build());
            programTap(tapBuilder.build());

            //Reverse direction
            protocolInfoBuilder.setTpDst(tpPort);
            tapBuilder.addAugmentation(ProtocolInfo.class, protocolInfoBuilder.build());
            programTap(tapBuilder.build());
        }
        else {
            tapBuilder.addAugmentation(ProtocolInfo.class, protocolInfoBuilder.build());
            programTap(tap);
        }
    }

    private void programTap(Tap tap) {
        NodeId nodeId = tap.getNode();
        String tapId = tap.getId();
        int index;
        if (tapFlows.get(tapId) == null) {
            index = 1;
        } else {
            index = tapFlows.get(tapId) + 1;
        }
        tapFlows.put(tapId, index);
        String flowIdStr = "Tap_" + tapId + "Flow_" + index;

        List<Instruction> instructions = Lists.newArrayList();
        InstructionBuilder ib = new InstructionBuilder();
        ApplyActionsBuilder aab = new ApplyActionsBuilder();
        ActionBuilder ab = new ActionBuilder();
        List<Action> actionList = Lists.newArrayList();

        //For each sink node connector
        int outputIndex = 0;
        for (NodeConnectorId sinkNodeConnector: tap.getSinkNodeConnector()) {
            // Set output action
            OutputActionBuilder output = new OutputActionBuilder();
            output.setOutputNodeConnector(sinkNodeConnector);
            output.setMaxLength(65535); //Send full packet and No buffer
            ab.setAction(new OutputActionCaseBuilder().setOutputAction(output.build()).build());
            ab.setKey(new ActionKey(outputIndex));
            ab.setOrder(outputIndex++);
            actionList.add(ab.build());
        }

        // Create Apply Actions Instruction
        aab.setAction(actionList);
        ib.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(aab.build()).build());
        ib.setOrder(0);
        ib.setKey(new InstructionKey(0));
        instructions.add(ib.build());

        // Instructions List Stores Individual Instructions
        InstructionsBuilder isb = new InstructionsBuilder();
        isb.setInstruction(instructions);

        // Create Flow
        FlowBuilder flowBuilder = new FlowBuilder();
        flowBuilder.setId(new FlowId(flowIdStr));
        FlowKey key = new FlowKey(new FlowId(flowIdStr));
        flowBuilder.setBarrier(true);
        flowBuilder.setTableId((short)0);
        flowBuilder.setKey(key);
        flowBuilder.setPriority(32768);
        flowBuilder.setFlowName(flowIdStr);
        flowBuilder.setHardTimeout(0);
        flowBuilder.setIdleTimeout(0);
        flowBuilder.setInstructions(isb.build());

        //Creating match object
        MatchBuilder matchBuilder = new MatchBuilder();

        if (tap.getMacAddressMatch() != null) {
            if (tap.getMacAddressMatch().getType() == FieldType.Source)
                MatchUtils.createEthSrcMatch(matchBuilder, tap.getMacAddressMatch().getValue());
            if (tap.getMacAddressMatch().getType() == FieldType.Destination)
                MatchUtils.createEthDstMatch(matchBuilder, tap.getMacAddressMatch().getValue(), null);
        }
        if (tap.getIpAddressMatch() != null) {
            if (tap.getIpAddressMatch().getType() == FieldType.Source)
                MatchUtils.createSrcL3IPv4Match(matchBuilder, tap.getIpAddressMatch().getValue());
            if (tap.getIpAddressMatch().getType() == FieldType.Destination)
                MatchUtils.createDstL3IPv4Match(matchBuilder, tap.getIpAddressMatch().getValue());
        }
        if (tap.getAugmentation(ProtocolInfo.class) != null) {
            ProtocolInfo protocolInfo = tap.getAugmentation(ProtocolInfo.class);
            if (protocolInfo.getDlType() != null)
                MatchUtils.createEtherTypeMatch(matchBuilder, protocolInfo.getDlType().longValue());
            if (protocolInfo.getNwProto() != null) {
                MatchUtils.createIpProtocolMatch(matchBuilder, protocolInfo.getNwProto());
                if (protocolInfo.getNwProto().equals((short)6)) { // TCP
                    if (protocolInfo.getTpSrc() != null)
                        MatchUtils.createSetSrcTcpMatch(matchBuilder, new PortNumber(protocolInfo.getTpSrc()));
                    if (protocolInfo.getTpDst() != null)
                        MatchUtils.createSetDstTcpMatch(matchBuilder, new PortNumber(protocolInfo.getTpDst()));
                } else {
                    if (protocolInfo.getTpSrc() != null)
                        MatchUtils.createSetSrcUdpMatch(matchBuilder, new PortNumber(protocolInfo.getTpSrc()));
                    if (protocolInfo.getTpDst() != null)
                        MatchUtils.createSetDstUdpMatch(matchBuilder, new PortNumber(protocolInfo.getTpDst()));
                }
            }
        }

        //Typically tap rules are added per source node connector
        //Also for each OF rule, there is only 1 in-port match
        for (NodeConnectorId srcNodeConnector: tap.getSrcNodeConnector()) {
            MatchUtils.createInPortMatch(matchBuilder, srcNodeConnector);
            flowBuilder.setMatch(matchBuilder.build());

            InstanceIdentifier<Flow> flowIID = InstanceIdentifier.builder(Nodes.class)
                    .child(Node.class, new NodeKey(nodeId))
                    .augmentation(FlowCapableNode.class)
                    .child(Table.class, new TableKey(flowBuilder.getTableId()))
                    .child(Flow.class, flowBuilder.getKey())
                    .build();
            GenericTransactionUtils.writeData(dataBroker, LogicalDatastoreType.CONFIGURATION, flowIID, flowBuilder.build(), true);
        }
    }

    private void removeTap(Tap tap) {
        NodeId nodeId = tap.getNode();
        String tapId = tap.getId();
        Integer index = tapFlows.get(tapId);
        if (index != null) {
            for (int i=1; i <= index; i++) {
                String flowIdStr = "Tap_" + tapId + "Flow_" + i;
                FlowId flowId = new FlowId(flowIdStr);
                FlowBuilder flowBuilder = new FlowBuilder()
                    .setKey(new FlowKey(flowId))
                    .setId(flowId)
                    .setTableId((short)0);
                InstanceIdentifier<Flow> flowIID = InstanceIdentifier.builder(Nodes.class)
                        .child(Node.class, new NodeKey(nodeId))
                        .augmentation(FlowCapableNode.class)
                        .child(Table.class, new TableKey(flowBuilder.getTableId()))
                        .child(Flow.class, flowBuilder.getKey())
                        .build();
                GenericTransactionUtils.writeData(dataBroker, LogicalDatastoreType.CONFIGURATION, flowIID, flowBuilder.build(), false);
            }
        }
    }

    @Override
    public void onNodeRemoved(NodeRemoved nodeRemoved) {
        LOG.debug("Node removed {}", nodeRemoved);
    }

    @Override
    public void onNodeConnectorRemoved(NodeConnectorRemoved nodeConnectorRemoved) {
        LOG.debug("Node connector removed {}", nodeConnectorRemoved);
    }

    @Override
    public void onNodeUpdated(NodeUpdated nodeUpdated) {
        LOG.debug("Node updated {}", nodeUpdated);
        NodeId nodeId = nodeUpdated.getId();
        FlowCapableNodeUpdated switchDesc = nodeUpdated.getAugmentation(FlowCapableNodeUpdated.class);
        if (switchDesc != null) {
            LOG.info("Node {}, OpenFlow description {}", nodeId, switchDesc);
        }

        //Remove all flows using RPC call to MD-SAL Flow Service
        RemoveFlowInputBuilder flowBuilder = new RemoveFlowInputBuilder()
            .setBarrier(true)
            .setNode(InventoryUtils.getNodeRef(nodeId));
        salFlowService.removeFlow(flowBuilder.build());
    }

    @Override
    public void onNodeConnectorUpdated(NodeConnectorUpdated nodeConnectorUpdated) {
        LOG.debug("NodeConnector updated {}", nodeConnectorUpdated);
        NodeId nodeId = InventoryUtils.getNodeId(nodeConnectorUpdated.getNodeConnectorRef());
        FlowCapableNodeConnectorUpdated portDesc = nodeConnectorUpdated.getAugmentation(FlowCapableNodeConnectorUpdated.class);
        if (portDesc != null) {
            LOG.info("Node {}, OpenFlow Port description {}", nodeId, portDesc);
        }
    }
}
