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
            //TODO: If tap configuration added, call programTap()
        }

        // Iterate over any deleted nodes or interfaces
        Map<InstanceIdentifier<?>, DataObject> originalData = change.getOriginalData();
        for (InstanceIdentifier<?> path : change.getRemovedPaths()) {
            dataObject = originalData.get(path);
            LOG.debug("REMOVED Path {}, Object {}", path, dataObject);
            //TODO: If tap configuration remove, call removeTap()
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
     *    2.2 For each sink-port add an output action to the list
     *    2.3 Create flow object with match + action list
     *    2.4 Program flow and preserve the flow-id for future
     */

    private void programTap(Tap tap) {
        NodeId nodeId = tap.getNode();
        String tapId = tap.getId();

        //Creating match object
        MatchBuilder matchBuilder = new MatchBuilder();

        /*
         * TODO: Based on what config is available,
         * set one or more of these match conditions
         *
        MatchUtils.createEthSrcMatch(matchBuilder, srcMac);
        MatchUtils.createEthDstMatch(matchBuilder, dstMac, null);
        MatchUtils.createSrcL3IPv4Match(matchBuilder, srcIpPrefix);
        MatchUtils.createDstL3IPv4Match(matchBuilder, dstIpPrefix);
        MatchUtils.createEtherTypeMatch(matchBuilder, dlType);
        MatchUtils.createIpProtocolMatch(matchBuilder, nwProto);
        if (nwProto == 6) {
            MatchUtils.createSetSrcTcpMatch(matchBuilder, new PortNumber(tpSrc));
            MatchUtils.createSetDstTcpMatch(matchBuilder, new PortNumber(tpDst));
        } else if (nwProto == 17) {
            MatchUtils.createSetSrcUdpMatch(matchBuilder, new PortNumber(tpSrc));
            MatchUtils.createSetDstUdpMatch(matchBuilder, new PortNumber(tpDst));
        }
         */

        /*
         * Special case: Each OF rule can only take 1 in-port match. But, it is
         * possible a tap configuration specifies multiple in-ports. It is important
         * to iterate over the list and send multiple flows to the switch
         *
        MatchUtils.createInPortMatch(matchBuilder, srcNodeConnector);
         */

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

        // Create generic Flow object
        String flowIdStr = "Tap_" + tapId + "SrcPort_"; // TODO: Concat srcNodeConnector to each flow;
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

        //Set the match and instructions to the  flow
        flowBuilder.setMatch(matchBuilder.build());
        flowBuilder.setInstructions(isb.build());

        //Program flow by adding it to the flow table in the opendaylight-inventory
        InstanceIdentifier<Flow> flowIID = InstanceIdentifier.builder(Nodes.class)
                .child(Node.class, new NodeKey(nodeId))
                .augmentation(FlowCapableNode.class)
                .child(Table.class, new TableKey(flowBuilder.getTableId()))
                .child(Flow.class, flowBuilder.getKey())
                .build();
        GenericTransactionUtils.writeData(dataBroker, LogicalDatastoreType.CONFIGURATION, flowIID, flowBuilder.build(), true);
    }

    private void removeTap(Tap tap) {
        NodeId nodeId = tap.getNode();
        String tapId = tap.getId();
        String flowIdStr = "Tap_" + tapId + "SrcPort_"; // TODO: Concat srcNodeConnector to each flow;
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
