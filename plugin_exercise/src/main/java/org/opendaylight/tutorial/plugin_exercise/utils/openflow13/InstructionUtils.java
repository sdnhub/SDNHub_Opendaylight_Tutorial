/*
 * Copyright (C) 2013 Red Hat, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 */
package org.opendaylight.tutorial.plugin_exercise.utils.openflow13;

import static org.opendaylight.tutorial.plugin_exercise.utils.openflow13.ActionUtils.dropAction;

import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Prefix;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.PortNumber;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Uri;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.DecNwTtlCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.DropActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.OutputActionCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.OutputActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.PopVlanActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.PushVlanActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.SetDlDstActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.SetDlSrcActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.SetFieldCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.SetNwDstActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.SetNwSrcActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.SetVlanIdActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.dec.nw.ttl._case.DecNwTtl;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.dec.nw.ttl._case.DecNwTtlBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.drop.action._case.DropAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.drop.action._case.DropActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.output.action._case.OutputActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.pop.vlan.action._case.PopVlanActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.push.vlan.action._case.PushVlanActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.set.dl.dst.action._case.SetDlDstActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.set.dl.src.action._case.SetDlSrcActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.set.field._case.SetFieldBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.set.nw.dst.action._case.SetNwDstActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.set.nw.src.action._case.SetNwSrcActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.set.vlan.id.action._case.SetVlanIdActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.ActionKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.address.address.Ipv4Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.Instructions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.InstructionsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.ApplyActionsCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.ApplyActionsCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.GoToTableCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.WriteMetadataCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.apply.actions._case.ApplyActionsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.go.to.table._case.GoToTableBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.write.metadata._case.WriteMetadataBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.InstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.InstructionKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.l2.types.rev130827.VlanId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.arp.match.fields.ArpSourceHardwareAddressBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.arp.match.fields.ArpTargetHardwareAddressBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.Icmpv4MatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.TunnelBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.layer._3.match.ArpMatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.layer._3.match.TunnelIpv4MatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.layer._4.match.TcpMatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.layer._4.match.UdpMatchBuilder;

import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class InstructionUtils {
    private static final Logger logger = LoggerFactory.getLogger(InstructionUtils.class);
    private static final int IPV4 = 0x8100;
    private static final int MAX_LENGTH = 0xffff;

    /**
     * Create Send to Controller Reserved Port Instruction (packet_in)
     *
     * @param ib Map InstructionBuilder without any instructions
     * @return ib Map InstructionBuilder with instructions
     */
    public static InstructionBuilder createSendToControllerInstructions(InstructionBuilder ib) {

        List<Action> actionList = Lists.newArrayList();
        ActionBuilder ab = new ActionBuilder();

        OutputActionBuilder output = new OutputActionBuilder();
        output.setMaxLength(MAX_LENGTH);
        Uri value = new Uri("CONTROLLER");
        output.setOutputNodeConnector(value);
        ab.setAction(new OutputActionCaseBuilder().setOutputAction(output.build()).build());
        ab.setOrder(0);
        ab.setKey(new ActionKey(0));
        actionList.add(ab.build());

        // Create an Apply Action
        ApplyActionsBuilder aab = new ApplyActionsBuilder();
        aab.setAction(actionList);

        // Wrap our Apply Action in an Instruction
        ib.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(aab.build()).build());

        return ib;
    }

    /**
     * Create NORMAL Reserved Port Instruction (packet_in)
     *
     * @param ib Map InstructionBuilder without any instructions
     * @return ib Map InstructionBuilder with instructions
     */

    public static InstructionBuilder createNormalInstructions(InstructionBuilder ib) {

        List<Action> actionList = Lists.newArrayList();
        ActionBuilder ab = new ActionBuilder();

        OutputActionBuilder output = new OutputActionBuilder();
        Uri value = new Uri("NORMAL");
        output.setOutputNodeConnector(value);
        ab.setAction(new OutputActionCaseBuilder().setOutputAction(output.build()).build());
        ab.setOrder(0);
        ab.setKey(new ActionKey(0));
        actionList.add(ab.build());

        // Create an Apply Action
        ApplyActionsBuilder aab = new ApplyActionsBuilder();
        aab.setAction(actionList);

        // Wrap our Apply Action in an Instruction
        ib.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(aab.build()).build());

        return ib;
    }

    /**
     * Create Output Port Instruction
     *
     * @param ib       Map InstructionBuilder without any instructions
     * @param dpidLong Long the datapath ID of a switch/node
     * @param port     Long representing a port on a switch/node
     * @return ib InstructionBuilder Map with instructions
     */
    public static InstructionBuilder createOutputPortInstructions(InstructionBuilder ib, Long dpidLong, Long port) {

        NodeConnectorId ncid = new NodeConnectorId("openflow:" + dpidLong + ":" + port);
        logger.debug("createOutputPortInstructions() Node Connector ID is - Type=openflow: DPID={} inPort={} ",
                dpidLong, port);

        List<Action> actionList = Lists.newArrayList();
        ActionBuilder ab = new ActionBuilder();
        OutputActionBuilder oab = new OutputActionBuilder();
        oab.setOutputNodeConnector(ncid);

        ab.setAction(new OutputActionCaseBuilder().setOutputAction(oab.build()).build());
        ab.setOrder(0);
        ab.setKey(new ActionKey(0));
        actionList.add(ab.build());

        // Create an Apply Action
        ApplyActionsBuilder aab = new ApplyActionsBuilder();
        aab.setAction(actionList);
        ib.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(aab.build()).build());

        return ib;
    }

    /**
     * add Output Port action to Instruction action list.
     * This is use for flow with single output port actions.
     * Flow with mutiple output port actions should use createOutputPortInstructions() method.
     *
     * @param ib       Map InstructionBuilder without any instructions
     * @param dpidLong Long the datapath ID of a switch/node
     * @param port     Long representing a port on a switch/node
     * @return ib InstructionBuilder Map with instructions
     */
    public static InstructionBuilder addOutputPortInstructions(InstructionBuilder ib,
            Long dpidLong, Long port,
            List<Instruction> instructions) {
        NodeConnectorId ncid = new NodeConnectorId("openflow:" + dpidLong + ":" + port);
        logger.debug(
                "addOutputPortInstructions() Node Connector ID is - Type=openflow: DPID={} port={} existingInstructions={}",
                dpidLong, port, instructions);

        List<Action> actionList = Lists.newArrayList();
        ActionBuilder ab = new ActionBuilder();

        List<Action> existingActions;
        if (instructions != null) {
            for (Instruction in : instructions) {
                if (in.getInstruction() instanceof ApplyActionsCase) {
                    existingActions = (((ApplyActionsCase) in.getInstruction()).getApplyActions().getAction());
                    actionList.addAll(existingActions);
                }
            }
        }

        /* Create output action for this port*/
        OutputActionBuilder oab = new OutputActionBuilder();
        oab.setOutputNodeConnector(ncid);
        ab.setAction(new OutputActionCaseBuilder().setOutputAction(oab.build()).build());
        ab.setOrder(actionList.size());
        ab.setKey(new ActionKey(actionList.size()));
        actionList.add(ab.build());

        // Create an Apply Action
        ApplyActionsBuilder aab = new ApplyActionsBuilder();
        aab.setAction(actionList);
        ib.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(aab.build()).build());

        return ib;
    }

    /**
     * Remove Output Port from Instruction
     *
     * @param ib       Map InstructionBuilder without any instructions
     * @param dpidLong Long the datapath ID of a switch/node
     * @param port     Long representing a port on a switch/node
     * @return ib InstructionBuilder Map with instructions
     */
    public static boolean removeOutputPortFromInstructions(InstructionBuilder ib,
            Long dpidLong, Long port, List<Instruction> instructions) {

        NodeConnectorId ncid = new NodeConnectorId("openflow:" + dpidLong + ":" + port);
        logger.debug(
                "removeOutputPortFromInstructions() Node Connector ID is - Type=openflow: DPID={} port={} existingInstructions={}",
                dpidLong, port, instructions);

        List<Action> actionList = Lists.newArrayList();
        ActionBuilder ab;

        List<Action> existingActions;
        if (instructions != null) {
            for (Instruction in : instructions) {
                if (in.getInstruction() instanceof ApplyActionsCase) {
                    existingActions = (((ApplyActionsCase) in.getInstruction()).getApplyActions().getAction());
                    actionList.addAll(existingActions);
                    break;
                }
            }
        }

        int index = 0;
        boolean isPortDeleted = false;
        boolean removeFlow = true;
        for (Action action : actionList) {
            if (action.getAction() instanceof OutputActionCase) {
                OutputActionCase opAction = (OutputActionCase) action.getAction();
                if (opAction.getOutputAction().getOutputNodeConnector().equals(new Uri(ncid))) {
                    /* Find the output port in action list and remove */
                    index = actionList.indexOf(action);
                    actionList.remove(action);
                    isPortDeleted = true;
                    break;
                }
                removeFlow = false;
            }
        }

        if (isPortDeleted) {
            for (int i = index; i < actionList.size(); i++) {
                Action action = actionList.get(i);
                if (action.getOrder() != i) {
                    /* Shift the action order */
                    ab = new ActionBuilder();
                    ab.setAction(action.getAction());
                    ab.setOrder(i);
                    ab.setKey(new ActionKey(i));
                    Action actionNewOrder = ab.build();
                    actionList.remove(action);
                    actionList.add(i, actionNewOrder);
                }
                if (action.getAction() instanceof OutputActionCase) {
                    removeFlow = false;
                }
            }
        }

        /* Put new action list in Apply Action instruction */
        if (!removeFlow) {
            ApplyActionsBuilder aab = new ApplyActionsBuilder();
            aab.setAction(actionList);
            ib.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(aab.build()).build());
            logger.debug("removeOutputPortFromInstructions() : applyAction {}", aab.build());
            return false;
        } else {
            /* if all output port are removed. Return true to indicate flow remove */
            return true;
        }
    }

    /**
     * Create Set Vlan ID Instruction - This includes push vlan action, and set field -> vlan vid action
     *
     * @param ib     Map InstructionBuilder without any instructions
     * @param vlanId Integer representing a VLAN ID Integer representing a VLAN ID
     * @return ib Map InstructionBuilder with instructions
     */
    public static InstructionBuilder createSetVlanInstructions(InstructionBuilder ib, VlanId vlanId) {

        List<Action> actionList = Lists.newArrayList();
        ActionBuilder ab = new ActionBuilder();

        /* First we push vlan header */
        PushVlanActionBuilder vlan = new PushVlanActionBuilder();
        vlan.setEthernetType(IPV4);
        ab.setAction(new PushVlanActionCaseBuilder().setPushVlanAction(vlan.build()).build());
        ab.setOrder(0);
        actionList.add(ab.build());

        /* Then we set vlan id value as vlanId */
        SetVlanIdActionBuilder vl = new SetVlanIdActionBuilder();
        vl.setVlanId(vlanId);
        ab = new ActionBuilder();
        ab.setAction(new SetVlanIdActionCaseBuilder().setSetVlanIdAction(vl.build()).build());
        ab.setOrder(1);
        actionList.add(ab.build());
        // Create an Apply Action
        ApplyActionsBuilder aab = new ApplyActionsBuilder();
        aab.setAction(actionList);

        // Wrap our Apply Action in an Instruction
        ib.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(aab.build()).build());

        return ib;
    }

    /**
     * Create Pop Vlan Instruction - this remove vlan header
     *
     * @param ib Map InstructionBuilder without any instructions
     * @return ib Map InstructionBuilder with instructions
     */
    public static InstructionBuilder createPopVlanInstructions(InstructionBuilder ib) {

        List<Action> actionList = Lists.newArrayList();
        ActionBuilder ab = new ActionBuilder();

        PopVlanActionBuilder popVlanActionBuilder = new PopVlanActionBuilder();
        ab.setAction(new PopVlanActionCaseBuilder().setPopVlanAction(popVlanActionBuilder.build()).build());
        ab.setOrder(0);
        actionList.add(ab.build());

        // Create an Apply Action
        ApplyActionsBuilder aab = new ApplyActionsBuilder();
        aab.setAction(actionList);

        // Wrap our Apply Action in an Instruction
        ib.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(aab.build()).build());

        return ib;
    }

    /**
     * Create Set IPv4 Source Instruction
     *
     * @param ib        Map InstructionBuilder without any instructions
     * @param prefixsrc String containing an IPv4 prefix
     * @return ib Map InstructionBuilder with instructions
     */
    public static InstructionBuilder createNwSrcInstructions(InstructionBuilder ib, Ipv4Prefix prefixsrc) {

        List<Action> actionList = Lists.newArrayList();
        ActionBuilder ab = new ActionBuilder();

        SetNwSrcActionBuilder setNwsrcActionBuilder = new SetNwSrcActionBuilder();
        Ipv4Builder ipsrc = new Ipv4Builder();
        ipsrc.setIpv4Address(prefixsrc);
        setNwsrcActionBuilder.setAddress(ipsrc.build());
        ab.setAction(new SetNwSrcActionCaseBuilder().setSetNwSrcAction(setNwsrcActionBuilder.build()).build());
        ab.setOrder(0);
        ab.setKey(new ActionKey(0));
        actionList.add(ab.build());

        // Create an Apply Action
        ApplyActionsBuilder aab = new ApplyActionsBuilder();
        aab.setAction(actionList);

        // Wrap our Apply Action in an Instruction
        ib.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(aab.build()).build());

        return ib;
    }

    /**
     * Create Set IPv4 Destination Instruction
     *
     * @param ib        Map InstructionBuilder without any instructions
     * @param prefixdst String containing an IPv4 prefix
     * @return ib Map InstructionBuilder with instructions
     */
    public static InstructionBuilder createNwDstInstructions(InstructionBuilder ib, Ipv4Prefix prefixdst) {

        List<Action> actionList = Lists.newArrayList();
        ActionBuilder ab = new ActionBuilder();

        SetNwDstActionBuilder setNwDstActionBuilder = new SetNwDstActionBuilder();
        Ipv4Builder ipdst = new Ipv4Builder();
        ipdst.setIpv4Address(prefixdst);
        setNwDstActionBuilder.setAddress(ipdst.build());
        ab.setAction(new SetNwDstActionCaseBuilder().setSetNwDstAction(setNwDstActionBuilder.build()).build());
        ab.setOrder(0);
        ab.setKey(new ActionKey(0));
        actionList.add(ab.build());

        // Create an Apply Action
        ApplyActionsBuilder aab = new ApplyActionsBuilder();
        aab.setAction(actionList);

        // Wrap our Apply Action in an Instruction
        ib.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(aab.build()).build());

        return ib;
    }

    /**
     * Create Drop Instruction
     *
     * @param ib Map InstructionBuilder without any instructions
     * @return ib Map InstructionBuilder with instructions
     */
    public static InstructionBuilder createDropInstructions(InstructionBuilder ib) {

        DropActionBuilder dab = new DropActionBuilder();
        DropAction dropAction = dab.build();
        ActionBuilder ab = new ActionBuilder();
        ab.setAction(new DropActionCaseBuilder().setDropAction(dropAction).build());
        ab.setOrder(0);
        ab.setKey(new ActionKey(0));

        // Add our drop action to a list
        List<Action> actionList = Lists.newArrayList();
        actionList.add(ab.build());

        // Create an Apply Action
        ApplyActionsBuilder aab = new ApplyActionsBuilder();
        aab.setAction(actionList);

        // Wrap our Apply Action in an Instruction
        ib.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(aab.build()).build());

        return ib;
    }

    /**
     * Create GOTO Table Instruction Builder
     *
     * @param ib      Map InstructionBuilder without any instructions
     * @param tableId short representing a flow table ID short representing a flow table ID
     * @return ib Map InstructionBuilder with instructions
     */
    public static InstructionBuilder createGotoTableInstructions(InstructionBuilder ib, short tableId) {

        GoToTableBuilder gttb = new GoToTableBuilder();
        gttb.setTableId(tableId);

        // Wrap our Apply Action in an InstructionBuilder
        ib.setInstruction(new GoToTableCaseBuilder().setGoToTable(gttb.build()).build());

        return ib;
    }

    /**
     * Create Set Tunnel ID Instruction Builder
     *
     * @param ib       Map InstructionBuilder without any instructions
     * @param tunnelId BigInteger representing a tunnel ID
     * @return ib Map InstructionBuilder with instructions
     */
    public static InstructionBuilder createSetTunnelIdInstructions(InstructionBuilder ib, BigInteger tunnelId) {

        List<Action> actionList = Lists.newArrayList();
        ActionBuilder ab = new ActionBuilder();
        SetFieldBuilder setFieldBuilder = new SetFieldBuilder();

        // Build the Set Tunnel Field Action
        TunnelBuilder tunnel = new TunnelBuilder();
        tunnel.setTunnelId(tunnelId);
        setFieldBuilder.setTunnel(tunnel.build());
        ab.setAction(new SetFieldCaseBuilder().setSetField(setFieldBuilder.build()).build());
        ab.setOrder(0);
        ab.setKey(new ActionKey(0));
        actionList.add(ab.build());

        ApplyActionsBuilder aab = new ApplyActionsBuilder();
        aab.setAction(actionList);

        // Wrap the Apply Action in an InstructionBuilder and return
        ib.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(aab.build()).build());

        return ib;
    }

    /**
     * Create Set Source TCP Port Instruction
     *
     * @param ib      Map InstructionBuilder without any instructions
     * @param tcpport Integer representing a source TCP port
     * @return ib Map InstructionBuilder with instructions
     */
    public static InstructionBuilder createSetSrcTCPPort(InstructionBuilder ib, PortNumber tcpport) {

        List<Action> actionList = Lists.newArrayList();
        ActionBuilder ab = new ActionBuilder();
        SetFieldBuilder setFieldBuilder = new SetFieldBuilder();

        // Build the Destination TCP Port
        PortNumber tcpsrcport = new PortNumber(tcpport);
        TcpMatchBuilder tcpmatch = new TcpMatchBuilder();
        tcpmatch.setTcpSourcePort(tcpsrcport);

        setFieldBuilder.setLayer4Match(tcpmatch.build());
        ab.setAction(new SetFieldCaseBuilder().setSetField(setFieldBuilder.build()).build());
        ab.setOrder(0);
        ab.setKey(new ActionKey(0));
        actionList.add(ab.build());

        ApplyActionsBuilder aab = new ApplyActionsBuilder();
        aab.setAction(actionList);
        ib.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(aab.build()).build());

        return ib;
    }

    /**
     * Create Set Destination TCP Port Instruction
     *
     * @param ib      Map InstructionBuilder without any instructions
     * @param tcpport Integer representing a source TCP port
     * @return ib Map InstructionBuilder with instructions
     */
    public static InstructionBuilder createSetDstTCPPort(InstructionBuilder ib, PortNumber tcpport) {

        List<Action> actionList = Lists.newArrayList();
        ActionBuilder ab = new ActionBuilder();
        SetFieldBuilder setFieldBuilder = new SetFieldBuilder();

        // Build the Destination TCP Port
        PortNumber tcpdstport = new PortNumber(tcpport);
        TcpMatchBuilder tcpmatch = new TcpMatchBuilder();
        tcpmatch.setTcpDestinationPort(tcpdstport);

        setFieldBuilder.setLayer4Match(tcpmatch.build());
        ab.setAction(new SetFieldCaseBuilder().setSetField(setFieldBuilder.build()).build());
        ab.setOrder(0);
        ab.setKey(new ActionKey(0));
        actionList.add(ab.build());

        ApplyActionsBuilder aab = new ApplyActionsBuilder();
        aab.setAction(actionList);
        ib.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(aab.build()).build());

        return ib;
    }

    /**
     * Create Set Source UDP Port Instruction
     *
     * @param ib      Map InstructionBuilder without any instructions
     * @param udpport Integer representing a source UDP port
     * @return ib Map InstructionBuilder with instructions
     */
    public static InstructionBuilder createSetSrcUDPPort(InstructionBuilder ib, PortNumber udpport) {

        List<Action> actionList = Lists.newArrayList();
        ActionBuilder ab = new ActionBuilder();
        SetFieldBuilder setFieldBuilder = new SetFieldBuilder();

        // Build the Destination TCP Port
        PortNumber udpsrcport = new PortNumber(udpport);
        UdpMatchBuilder udpmatch = new UdpMatchBuilder();
        udpmatch.setUdpSourcePort(udpsrcport);

        setFieldBuilder.setLayer4Match(udpmatch.build());
        ab.setAction(new SetFieldCaseBuilder().setSetField(setFieldBuilder.build()).build());
        ab.setOrder(0);
        ab.setKey(new ActionKey(0));
        actionList.add(ab.build());

        ApplyActionsBuilder aab = new ApplyActionsBuilder();
        aab.setAction(actionList);
        ib.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(aab.build()).build());

        return ib;
    }

    /**
     * Create Set Destination UDP Port Instruction
     *
     * @param ib      Map InstructionBuilder without any instructions
     * @param udpport Integer representing a destination UDP port
     * @return ib Map InstructionBuilder with instructions
     */
    public static InstructionBuilder createSetDstUDPPort(InstructionBuilder ib, PortNumber udpport) {

        List<Action> actionList = Lists.newArrayList();
        ActionBuilder ab = new ActionBuilder();
        SetFieldBuilder setFieldBuilder = new SetFieldBuilder();

        // Build the Destination TCP Port
        PortNumber udpdstport = new PortNumber(udpport);
        UdpMatchBuilder udpmatch = new UdpMatchBuilder();
        udpmatch.setUdpDestinationPort(udpdstport);

        setFieldBuilder.setLayer4Match(udpmatch.build());
        ab.setAction(new SetFieldCaseBuilder().setSetField(setFieldBuilder.build()).build());
        ab.setOrder(0);
        ab.setKey(new ActionKey(0));
        actionList.add(ab.build());

        ApplyActionsBuilder aab = new ApplyActionsBuilder();
        aab.setAction(actionList);
        ib.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(aab.build()).build());

        return ib;
    }

    /**
     * Create Set ICMP Code Instruction
     *
     * @param ib   Map InstructionBuilder without any instructions
     * @param code short repesenting an ICMP code
     * @return ib Map InstructionBuilder with instructions
     */

    public static InstructionBuilder createSetIcmpCodeInstruction(InstructionBuilder ib, short code) {

        List<Action> actionList = Lists.newArrayList();
        ActionBuilder ab = new ActionBuilder();
        SetFieldBuilder setFieldBuilder = new SetFieldBuilder();
        Icmpv4MatchBuilder icmpv4match = new Icmpv4MatchBuilder();

        // Build the ICMPv4 Code Match
        icmpv4match.setIcmpv4Code(code);
        setFieldBuilder.setIcmpv4Match(icmpv4match.build());

        ab.setAction(new SetFieldCaseBuilder().setSetField(setFieldBuilder.build()).build());
        ab.setOrder(0);
        ab.setKey(new ActionKey(0));
        actionList.add(ab.build());
        ApplyActionsBuilder aab = new ApplyActionsBuilder();
        aab.setAction(actionList);

        // Wrap our Apply Action in an Instruction
        ib.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(aab.build()).build());

        return ib;
    }

    /**
     * Create Set ICMP Code Instruction
     *
     * @param ib Map InstructionBuilder without any instructions
     * @return ib Map InstructionBuilder with instructions
     */
    public static InstructionBuilder createSetIcmpTypeInstruction(InstructionBuilder ib, short type) {

        List<Action> actionList = Lists.newArrayList();
        ActionBuilder ab = new ActionBuilder();
        SetFieldBuilder setFieldBuilder = new SetFieldBuilder();
        Icmpv4MatchBuilder icmpv4match = new Icmpv4MatchBuilder();

        // Build the ICMPv4 Code Match
        icmpv4match.setIcmpv4Code(type);
        setFieldBuilder.setIcmpv4Match(icmpv4match.build());

        ab.setAction(new SetFieldCaseBuilder().setSetField(setFieldBuilder.build()).build());
        ab.setOrder(0);
        ab.setKey(new ActionKey(0));
        actionList.add(ab.build());
        ApplyActionsBuilder aab = new ApplyActionsBuilder();
        aab.setAction(actionList);

        // Wrap our Apply Action in an Instruction
        ib.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(aab.build()).build());

        return ib;
    }

    /**
     * Create Decrement TTL Instruction
     *
     * @param ib Map InstructionBuilder without any instructions
     * @return ib Map InstructionBuilder with instructions
     */
    public static InstructionBuilder createDecNwTtlInstructions(InstructionBuilder ib) {
        DecNwTtlBuilder decNwTtlBuilder = new DecNwTtlBuilder();
        DecNwTtl decNwTtl = decNwTtlBuilder.build();
        ActionBuilder ab = new ActionBuilder();
        ab.setAction(new DecNwTtlCaseBuilder().setDecNwTtl(decNwTtl).build());
        ab.setOrder(0);
        ab.setKey(new ActionKey(0));

        // Add our drop action to a list
        List<Action> actionList = Lists.newArrayList();
        actionList.add(ab.build());

        // Create an Apply Action
        ApplyActionsBuilder aab = new ApplyActionsBuilder();
        aab.setAction(actionList);

        // Wrap our Apply Action in an Instruction
        ib.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(aab.build()).build());

        return ib;
    }

    /**
     * Set ARP_SHA Instructions
     * @param ib Map InstructionBuilder
     * @param macsrc the macsrc
     * @return instructionbuilder with new instructions
     */
    public static InstructionBuilder createSrcArpMacInstructions(InstructionBuilder ib, MacAddress macsrc) {

        List<Action> actionList = Lists.newArrayList();
        ActionBuilder ab = new ActionBuilder();

        SetFieldBuilder setFieldBuilder = new SetFieldBuilder();
        ArpMatchBuilder arpmatch = new ArpMatchBuilder();
        ArpSourceHardwareAddressBuilder arpsrc = new ArpSourceHardwareAddressBuilder();
        arpsrc.setAddress(macsrc);
        arpmatch.setArpSourceHardwareAddress(arpsrc.build());
        setFieldBuilder.setLayer3Match(arpmatch.build());
        ab.setAction(new SetFieldCaseBuilder().setSetField(setFieldBuilder.build()).build());
        ab.setOrder(0);
        ab.setKey(new ActionKey(0));
        actionList.add(ab.build());

        ApplyActionsBuilder aab = new ApplyActionsBuilder();
        aab.setAction(actionList);
        // Wrap our Apply Action in an Instruction
        ib.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(aab.build()).build());

        return ib;
    }

    /**
     * Set ARP_THA Instructions
     * @param ib Map InstructionBuilder
     * @param macdst the macdst
     * @return instructionbuilder with new attributes
     */
    public static InstructionBuilder createDstArpMacInstructions(InstructionBuilder ib, MacAddress macdst) {

        List<Action> actionList = Lists.newArrayList();
        ActionBuilder ab = new ActionBuilder();
        SetFieldBuilder setFieldBuilder = new SetFieldBuilder();

        ArpMatchBuilder arpmatch = new ArpMatchBuilder();
        ArpTargetHardwareAddressBuilder arpdst = new ArpTargetHardwareAddressBuilder();
        arpdst.setAddress(macdst);
        setFieldBuilder.setLayer3Match(arpmatch.build());
        ab.setAction(new SetFieldCaseBuilder().setSetField(setFieldBuilder.build()).build());
        ab.setOrder(0);
        ab.setKey(new ActionKey(0));
        actionList.add(ab.build());

        ApplyActionsBuilder aab = new ApplyActionsBuilder();
        aab.setAction(actionList);
        // Wrap our Apply Action in an Instruction
        ib.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(aab.build()).build());

        return ib;
    }

    /**
     * Set ARP_TPA Instructions
     * @param ib Map InstructionBuilder
     * @param dstiparp the dstiparp
     * @return instructionbuilder with new attributes
     */
    public static InstructionBuilder createDstArpIpInstructions(InstructionBuilder ib, Ipv4Prefix dstiparp) {

        List<Action> actionList = Lists.newArrayList();
        ActionBuilder ab = new ActionBuilder();
        SetFieldBuilder setFieldBuilder = new SetFieldBuilder();

        ArpMatchBuilder arpmatch = new ArpMatchBuilder();
        arpmatch.setArpTargetTransportAddress(dstiparp);
        setFieldBuilder.setLayer3Match(arpmatch.build());
        ab.setAction(new SetFieldCaseBuilder().setSetField(setFieldBuilder.build()).build());
        ab.setOrder(0);
        ab.setKey(new ActionKey(0));
        actionList.add(ab.build());

        ApplyActionsBuilder aab = new ApplyActionsBuilder();
        aab.setAction(actionList);
        // Wrap our Apply Action in an Instruction
        ib.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(aab.build()).build());

        return ib;
    }

    /**
     * Set ARP_SPA Instructions
     * @param ib Map InstructionBuilder
     * @param srciparp the srciparp
     * @return instructionbuilder with new attributes
     */
    public static InstructionBuilder createSrcArpIpInstructions(InstructionBuilder ib, Ipv4Prefix srciparp) {

        List<Action> actionList = Lists.newArrayList();
        ActionBuilder ab = new ActionBuilder();
        SetFieldBuilder setFieldBuilder = new SetFieldBuilder();

        ArpMatchBuilder arpmatch = new ArpMatchBuilder();
        arpmatch.setArpSourceTransportAddress(srciparp);
        setFieldBuilder.setLayer3Match(arpmatch.build());
        ab.setAction(new SetFieldCaseBuilder().setSetField(setFieldBuilder.build()).build());
        ab.setOrder(0);
        ab.setKey(new ActionKey(0));
        actionList.add(ab.build());

        ApplyActionsBuilder aab = new ApplyActionsBuilder();
        aab.setAction(actionList);
        // Wrap our Apply Action in an Instruction
        ib.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(aab.build()).build());

        return ib;
    }

    /**
     * Set the Tunnel EndpointIPv4 Source Address
     * @param ib Map InstructionBuilder
     * @param srcIp Ipv4Prefix source IP for the tunnel endpoint (TEP)
     * @return instructionbuilder with new attributes
     */
    public static InstructionBuilder createTunnelIpv4SrcInstructions(InstructionBuilder ib, Ipv4Prefix srcIp) {

        List<Action> actionList = new ArrayList<Action>();
        ActionBuilder ab = new ActionBuilder();

        // Build the tunnel endpoint source IPv4 address
        SetFieldBuilder setFieldBuilder = new SetFieldBuilder();

        // Add the new IPv4 object as the tunnel destination
        TunnelIpv4MatchBuilder tunnelIpv4MatchBuilder = new TunnelIpv4MatchBuilder();
        tunnelIpv4MatchBuilder.setTunnelIpv4Source(srcIp);
        setFieldBuilder.setLayer3Match(tunnelIpv4MatchBuilder.build());

        // Add the IPv4 tunnel src to the set_field value
        ab.setAction(new SetFieldCaseBuilder().setSetField(setFieldBuilder.build()).build());
        ab.setOrder(0);
        ab.setKey(new ActionKey(0));
        actionList.add(ab.build());

        // Resulting action is a per/flow src TEP (set_field:172.16.100.100->tun_src)
        ApplyActionsBuilder aab = new ApplyActionsBuilder();
        aab.setAction(actionList);
        // Wrap our Apply Action in an Instruction
        ib.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(aab.build()).build());

        return ib;
    }

    /**
     * Set the Tunnel EndpointIPv4 Destination Address
     * @param ib Map InstructionBuilder
     * @param dstIp Ipv4Prefix destination IP for the tunnel endpoint (TEP)
     * @return instructionbuilder with new attributes
     */
    public static InstructionBuilder createTunnelIpv4DstInstructions(InstructionBuilder ib, Ipv4Prefix dstIp) {

        List<Action> actionList = new ArrayList<Action>();
        ActionBuilder ab = new ActionBuilder();

        // Build the tunnel endpoint dst IPv4 address
        SetFieldBuilder setFieldBuilder = new SetFieldBuilder();

        // Add the new IPv4 object as the tunnel destination
        TunnelIpv4MatchBuilder tunnelIpv4MatchBuilder = new TunnelIpv4MatchBuilder();
        tunnelIpv4MatchBuilder.setTunnelIpv4Destination(dstIp);
        setFieldBuilder.setLayer3Match(tunnelIpv4MatchBuilder.build());

        // Add the IPv4 tunnel src to the set_field value
        ab.setAction(new SetFieldCaseBuilder().setSetField(setFieldBuilder.build()).build());
        ab.setOrder(0);
        ab.setKey(new ActionKey(0));
        actionList.add(ab.build());

        // Resulting action is a per/flow src TEP (set_field:172.16.100.100->tun_src)
        ApplyActionsBuilder aab = new ApplyActionsBuilder();
        aab.setAction(actionList);
        // Wrap our Apply Action in an Instruction
        ib.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(aab.build()).build());

        return ib;
    }

    /**
     * Set IPv4 Source Address Instructions
     * @param ib Map InstructionBuilder
     * @param metaData BigInteger representing the OpenFlow metadata
     * @param metaDataMask  BigInteger representing the OpenFlow metadata mask
     * @return instructionbuilder with new attributes
     */
    public static InstructionBuilder createMetadataInstructions(InstructionBuilder ib, BigInteger metaData, BigInteger metaDataMask) {

        WriteMetadataBuilder aab = new WriteMetadataBuilder();
        aab.setMetadata(metaData);
        aab.setMetadataMask(metaDataMask);
        ib.setInstruction(new WriteMetadataCaseBuilder().setWriteMetadata(aab.build()).build());

        /**
        *
        * TODO Determine why the following fails serialization
        * with a null value. Per the spec it is optional.
        * The metadata match, the Flowmod works w/o an accompanying mask.
        *
        * if (metaDataMask != null) {
        *    aab.setMetadataMask(metaDataMask);
        * }
        *
        */

        return ib;
    }

    public static InstructionBuilder createDlSrcInstructions(InstructionBuilder ib, MacAddress macAddress) {

        List<Action> actionList = Lists.newArrayList();
        ActionBuilder ab = new ActionBuilder();

        SetDlSrcActionBuilder dlSrcActionBuilder= new SetDlSrcActionBuilder();

        dlSrcActionBuilder.setAddress(macAddress);

        ab.setAction(new SetDlSrcActionCaseBuilder().setSetDlSrcAction(dlSrcActionBuilder.build()).build());
        ab.setOrder(0);
        ab.setKey(new ActionKey(0));
        actionList.add(ab.build());


        // Create an Apply Action
        ApplyActionsBuilder aab = new ApplyActionsBuilder();
        aab.setAction(actionList);

        // Wrap our Apply Action in an Instruction
        ib.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(aab.build()).build());

        return ib;
    }

    public static InstructionBuilder createDlDstInstructions(InstructionBuilder ib, MacAddress macAddress) {

        List<Action> actionList = Lists.newArrayList();
        ActionBuilder ab = new ActionBuilder();

        SetDlDstActionBuilder dlDstActionBuilder= new SetDlDstActionBuilder();

        dlDstActionBuilder.setAddress(macAddress);

        ab.setAction(new SetDlDstActionCaseBuilder().setSetDlDstAction(dlDstActionBuilder.build()).build());
        ab.setOrder(0);
        ab.setKey(new ActionKey(0));
        actionList.add(ab.build());

        // Create an Apply Action
        ApplyActionsBuilder aab = new ApplyActionsBuilder();
        aab.setAction(actionList);

        // Wrap our Apply Action in an Instruction
        ib.setInstruction(new ApplyActionsCaseBuilder().setApplyActions(aab.build()).build());

        return ib;
    }

    public static ArrayList<org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.Action>
                  actionList (org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.Action... actions) {

        ArrayList<org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.Action> alist
            = new ArrayList<>();
        int count = 0;
        for (org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.Action action : actions) {
            alist.add(new ActionBuilder()
                .setOrder(count)
                .setKey(new ActionKey(count))
                .setAction(action)
                .build());
            count++;
        }
        return alist;
    }

    public static org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.Instruction
        applyActionIns(org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.Action... actions) {

        return new ApplyActionsCaseBuilder()
            .setApplyActions(new ApplyActionsBuilder()
                .setAction(actionList(actions))
                .build())
            .build();
    }

    public static Instructions getInstructions(org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.Instruction... instructions) {
        ArrayList<org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.Instruction> ins
            = new ArrayList<>();
        int order = 0;
        for (org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.Instruction i : instructions) {
            ins.add(new InstructionBuilder()
                .setOrder(order)
                .setKey(new InstructionKey(order))
                .setInstruction(i)
                .build());
            order++;
        }
        return new InstructionsBuilder().setInstruction(ins).build();
    }

    public static Instructions dropInstructions() {
        return getInstructions(applyActionIns(dropAction()));
    }

    /**
     * Get a list of Instructions containing Nicira extensions that can have
     * additional OF/OXM instructions added to the returned Instruction list
     *
     * @param instructions org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.Instructions
     * @return instruction list that additional
     */
    public static List<Instruction> getInstructionList(
            org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.Instruction... instructions) {
        ArrayList<org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.Instruction> ins
                = new ArrayList<>();
        int order = 0;
        for (org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.Instruction i : instructions) {
            ins.add(new InstructionBuilder()
                    .setOrder(order++)
                    .setInstruction(i)
                    .build());
        }
        return ins;
    }
}
