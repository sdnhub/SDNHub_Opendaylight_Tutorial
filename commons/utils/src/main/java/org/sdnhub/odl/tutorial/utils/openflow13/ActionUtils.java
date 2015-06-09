/*
 * Copyright (C) 2014 Red Hat, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 */
package org.sdnhub.odl.tutorial.utils.openflow13;

import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Uri;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev100924.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.l2.types.rev130827.VlanId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.DecMplsTtlCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.DecNwTtlCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.DropActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.GroupActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.OutputActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.PopMplsActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.PushMplsActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.PushVlanActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.PopVlanActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.SetDlDstActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.SetDlSrcActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.SetFieldCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.SetMplsTtlActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.SetNwDstActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.SetNwSrcActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.SetNwTtlActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.dec.mpls.ttl._case.DecMplsTtlBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.dec.nw.ttl._case.DecNwTtlBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.drop.action._case.DropActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.group.action._case.GroupActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.output.action._case.OutputActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.push.mpls.action._case.PushMplsActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.push.vlan.action._case.PushVlanActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.pop.mpls.action._case.PopMplsActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.pop.vlan.action._case.PopVlanActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.set.dl.dst.action._case.SetDlDstActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.set.dl.src.action._case.SetDlSrcActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.set.field._case.SetFieldBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.set.mpls.ttl.action._case.SetMplsTtlActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.set.nw.dst.action._case.SetNwDstActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.set.nw.src.action._case.SetNwSrcActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.set.nw.ttl.action._case.SetNwTtlActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.address.Address;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.ProtocolMatchFieldsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.TunnelBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.VlanMatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.vlan.match.fields.VlanIdBuilder;

import com.google.common.net.InetAddresses;

import java.math.BigInteger;

public final class ActionUtils {
    public static Action dropAction() {
        return new DropActionCaseBuilder()
            .setDropAction(new DropActionBuilder()
                .build())
            .build();
    }

    public static Action outputAction(NodeConnectorId id) {
        return new OutputActionCaseBuilder()
            .setOutputAction(new OutputActionBuilder()
                .setOutputNodeConnector(new Uri(id.getValue()))
                .build())
            .build();
    }

    public static Action groupAction(Long id) {
        return new GroupActionCaseBuilder()
            .setGroupAction(new GroupActionBuilder()
                .setGroupId(id)
                .build())
            .build();
    }

    public static Action pushVlanAction(Integer ethernetType) {
        return new PushVlanActionCaseBuilder()
            .setPushVlanAction(new PushVlanActionBuilder()
                    .setEthernetType(ethernetType)
                    .build())
            .build();
    }

    public static Action popVlanAction() {
        return new PopVlanActionCaseBuilder()
            .setPopVlanAction(new PopVlanActionBuilder()
                    .build())
            .build();
    }

    public static Action setVlanVidAction(Integer vlanId) {
        VlanMatchBuilder vlanMatchBuilder = new VlanMatchBuilder();
        VlanIdBuilder vlanIdBuilder = new VlanIdBuilder();
        vlanIdBuilder.setVlanId(new VlanId(vlanId));
        vlanIdBuilder.setVlanIdPresent(true);
        vlanMatchBuilder.setVlanId(vlanIdBuilder.build());
        SetFieldBuilder setFieldBuilder = new SetFieldBuilder()
                .setVlanMatch((vlanMatchBuilder.build()));

        return new SetFieldCaseBuilder()
                .setSetField(setFieldBuilder.build())
                .build();
    }

    public static Action pushMplsAction(Integer ethernetType) {
        return new PushMplsActionCaseBuilder()
            .setPushMplsAction(new PushMplsActionBuilder()
                    .setEthernetType(ethernetType)
                    .build())
            .build();
    }

    public static Action popMplsAction(Integer payloadEthernetType) {
        return new PopMplsActionCaseBuilder()
            .setPopMplsAction(new PopMplsActionBuilder()
                    .setEthernetType(payloadEthernetType)
                    .build())
            .build();
    }

    public static Action setMplsLabelBosAction(Long label, boolean bos) {
        ProtocolMatchFieldsBuilder matchFieldsBuilder = new ProtocolMatchFieldsBuilder()
                .setMplsLabel(label)
                .setMplsBos((short) (bos?1:0));
        SetFieldBuilder setFieldBuilder = new SetFieldBuilder()
                .setProtocolMatchFields(matchFieldsBuilder.build());
        return new SetFieldCaseBuilder()
                .setSetField(setFieldBuilder.build())
                .build();
    }

    public static Action setDlSrcAction(MacAddress mac) {
        return new SetDlSrcActionCaseBuilder()
            .setSetDlSrcAction(new SetDlSrcActionBuilder()
                .setAddress(mac)
                .build())
            .build();
    }

    public static Action setDlDstAction(MacAddress mac) {
        return new SetDlDstActionCaseBuilder()
            .setSetDlDstAction(new SetDlDstActionBuilder()
                .setAddress(mac)
                .build())
            .build();
    }

    public static Action setNwSrcAction(Address ip) {
        return new SetNwSrcActionCaseBuilder()
            .setSetNwSrcAction(new SetNwSrcActionBuilder()
                .setAddress(ip)
                .build())
            .build();
    }

    public static Action setNwDstAction(Address ip) {
        return new SetNwDstActionCaseBuilder()
            .setSetNwDstAction(new SetNwDstActionBuilder()
                .setAddress(ip)
                .build())
            .build();
    }

    public static Action SetNwTtlAction(Short ttlValue) {
        return new SetNwTtlActionCaseBuilder()
            .setSetNwTtlAction(new SetNwTtlActionBuilder()
                .setNwTtl(ttlValue)
                .build())
            .build();
    }

    public static Action decNwTtlAction() {
        return new DecNwTtlCaseBuilder()
            .setDecNwTtl(new DecNwTtlBuilder()
                .build())
            .build();
    }

    public static Action decMplsTtlAction() {
        return new DecMplsTtlCaseBuilder()
            .setDecMplsTtl(new DecMplsTtlBuilder()
                .build())
            .build();
    }

    public static Action SetMplsTtlAction(Short ttlValue) {
        return new SetMplsTtlActionCaseBuilder()
            .setSetMplsTtlAction(new SetMplsTtlActionBuilder()
                .setMplsTtl(ttlValue)
                .build())
            .build();
    }

    public static Action setTunnelIdAction(BigInteger tunnelId) {

        SetFieldBuilder setFieldBuilder = new SetFieldBuilder();

        // Build the Set Tunnel Field Action
        TunnelBuilder tunnel = new TunnelBuilder();
        tunnel.setTunnelId(tunnelId);
        setFieldBuilder.setTunnel(tunnel.build());

        return new SetFieldCaseBuilder()
                .setSetField(setFieldBuilder.build())
                .build();
    }

    /**
     * Accepts a MAC address and returns the corresponding long, where the
     * MAC bytes are set on the lower order bytes of the long.
     * @param macAddress
     * @return a long containing the mac address bytes
     */
    public static long toLong(byte[] macAddress) {
        long mac = 0;
        for (int i = 0; i < 6; i++) {
            long t = (macAddress[i] & 0xffL) << ((5-i)*8);
            mac |= t;
        }
        return mac;
    }

    /**
     * Accepts a MAC address of the form 00:aa:11:bb:22:cc, case does not
     * matter, and returns the corresponding long, where the MAC bytes are set
     * on the lower order bytes of the long.
     *
     * @param macAddress
     *            in String format
     * @return a long containing the mac address bytes
     */
    public static long toLong(MacAddress macAddress) {
        return toLong(toMACAddress(macAddress.getValue()));
    }

    /**
     * Accepts a MAC address of the form 00:aa:11:bb:22:cc, case does not
     * matter, and returns a corresponding byte[].
     * @param macAddress
     * @return
     */
    public static byte[] toMACAddress(String macAddress) {
        final String HEXES = "0123456789ABCDEF";
        byte[] address = new byte[6];
        String[] macBytes = macAddress.split(":");
        if (macBytes.length != 6)
            throw new IllegalArgumentException(
                    "Specified MAC Address must contain 12 hex digits" +
                    " separated pairwise by :'s.");
        for (int i = 0; i < 6; ++i) {
            address[i] = (byte) ((HEXES.indexOf(macBytes[i].toUpperCase()
                                                        .charAt(0)) << 4) | HEXES.indexOf(macBytes[i].toUpperCase()
                                                                                                  .charAt(1)));
        }
        return address;
    }
}
