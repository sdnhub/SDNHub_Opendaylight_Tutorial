package org.opendaylight.tutorial.plugin_exercise.internal;

import java.util.List;

import org.opendaylight.tutorial.plugin_exercise.api.MdsalConsumer;
import org.opendaylight.tutorial.plugin_exercise.utils.openflow13.MatchUtils;
import org.opendaylight.tutorial.plugin_exercise.utils.openflow13.TransactionUtils;
import org.opendaylight.tutorial.plugin_exercise.utils.openflow13.NodeUtils;

import org.opendaylight.controller.sal.utils.EtherTypes;
import org.opendaylight.controller.sal.binding.api.NotificationService;
import org.opendaylight.controller.sal.utils.Status;
import org.opendaylight.controller.sal.utils.StatusCode;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Uri;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.InstructionsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.MatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.InstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.InstructionKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.ApplyActionsCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.apply.actions._case.ApplyActionsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.OutputActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.output.action._case.OutputActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.ActionKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorRemoved;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorUpdated;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeRemoved;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeUpdated;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.OpendaylightInventoryListener;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;

import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowCapableNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowCapableNodeBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowCapableNodeConnectorUpdated;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowCapableNodeUpdated;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowNodeConnector;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.port.rev130925.FlowCapablePort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.base.Preconditions;

public class TutorialFlowProgrammer implements OpendaylightInventoryListener {
    private volatile MdsalConsumer mdsalConsumer;
    private static final Logger logger = LoggerFactory.getLogger(TutorialFlowProgrammer.class);

    public void init() {
        logger.debug("Initializing");
        NotificationService notificationService = mdsalConsumer.getNotificationService();
        if (notificationService != null) {
            notificationService.registerNotificationListener(this);
        }
        logger.info("Registered {} to MD-SAL notification service", this);
    }

    @Override
    public void onNodeConnectorRemoved(NodeConnectorRemoved arg0) {
        //Noop
    }

    @Override
    public void onNodeConnectorUpdated(NodeConnectorUpdated nodeConnectorUpdated) {
        logger.info("MD-SAL NodeConnector Update: {}", nodeConnectorUpdated);
        FlowCapableNodeConnectorUpdated port = nodeConnectorUpdated.getAugmentation(FlowCapableNodeConnectorUpdated.class);
        NodeKey nodeKey = nodeConnectorUpdated.getNodeConnectorRef().getValue().firstKeyOf(Node.class, NodeKey.class);
        logger.info("MD-SAL NodeConnector: Parent node {}, PortInfo {}", nodeKey.getId().getValue(), port);
    }

    @Override
    public void onNodeRemoved(NodeRemoved nodeRemoved) {
        //Noop
    }

    @Override
    public void onNodeUpdated(NodeUpdated nodeUpdated) {
        logger.info("MD-SAL Node Update: {}", nodeUpdated);
        programDefaultDropRule(nodeUpdated.getId());
        programDefaultLLDPRule(nodeUpdated.getId());

        FlowCapableNodeUpdated node = nodeUpdated.getAugmentation(FlowCapableNodeUpdated.class);
        logger.info("MD-SAL FlowCapableNode: {}", node);
    }


    public Status programDefaultLLDPRule(NodeId nodeId) {
        Preconditions.checkNotNull(mdsalConsumer);
        NodeBuilder nodeBuilder = NodeUtils.createNodeBuilder(nodeId);

        //Matching on LLDP traffic
        MatchBuilder matchBuilder = new MatchBuilder();
        MatchUtils.createEtherTypeMatch(matchBuilder, (long)EtherTypes.LLDP.intValue());

        // Instructions List Stores Individual Instructions
        InstructionsBuilder isb = new InstructionsBuilder();
        List<Instruction> instructions = Lists.newArrayList();
        InstructionBuilder ib = new InstructionBuilder();
        ApplyActionsBuilder aab = new ApplyActionsBuilder();
        ActionBuilder ab = new ActionBuilder();
        List<org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.Action> actionList = Lists.newArrayList();

        // Set send-to-controller action
        OutputActionBuilder output = new OutputActionBuilder();
        output.setOutputNodeConnector(new Uri("CONTROLLER"));
        output.setMaxLength(65535); //Send full packet and No buffer
        ab.setAction(new OutputActionCaseBuilder().setOutputAction(output.build()).build());
        ab.setOrder(0);
        ab.setKey(new ActionKey(0));
        actionList.add(ab.build());

        // Create Apply Actions Instruction
        aab.setAction(actionList);
        ib.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(aab.build()).build());
        ib.setOrder(0);
        ib.setKey(new InstructionKey(0));
        instructions.add(ib.build());

        // Create Flow
        FlowBuilder flowBuilder = new FlowBuilder();
        flowBuilder.setMatch(matchBuilder.build());

        String flowId = "Default_LLDP_Rule";
        flowBuilder.setId(new FlowId(flowId));
        FlowKey key = new FlowKey(new FlowId(flowId));
        flowBuilder.setBarrier(true);
        flowBuilder.setTableId((short)0);
        flowBuilder.setKey(key);
        flowBuilder.setPriority(65535); //Highest priority
        flowBuilder.setFlowName(flowId);
        flowBuilder.setHardTimeout(0);
        flowBuilder.setIdleTimeout(0);
        flowBuilder.setInstructions(isb.setInstruction(instructions).build());

        TransactionUtils.writeFlow(mdsalConsumer.getDataBroker(), flowBuilder, nodeBuilder);
        return new Status(StatusCode.SUCCESS);
    }

    public Status programDefaultDropRule(NodeId nodeId) {
        Preconditions.checkNotNull(mdsalConsumer);
        NodeBuilder nodeBuilder = NodeUtils.createNodeBuilder(nodeId);

        // Create Flow
        FlowBuilder flowBuilder = new FlowBuilder();

        String flowId = "Default_Drop_Rule";
        flowBuilder.setId(new FlowId(flowId));
        FlowKey key = new FlowKey(new FlowId(flowId));
        flowBuilder.setBarrier(true);
        flowBuilder.setTableId((short)0);
        flowBuilder.setKey(key);
        flowBuilder.setPriority(0); //Lowest priority
        flowBuilder.setFlowName(flowId);
        flowBuilder.setHardTimeout(0);
        flowBuilder.setIdleTimeout(0);

        TransactionUtils.writeFlow(mdsalConsumer.getDataBroker(), flowBuilder, nodeBuilder);
        return new Status(StatusCode.SUCCESS);
    }
}
