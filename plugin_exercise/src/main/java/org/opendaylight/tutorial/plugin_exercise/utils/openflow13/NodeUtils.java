/*
 * Copyright (C) 2014 SDN Hub

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
package org.opendaylight.tutorial.plugin_exercise.utils.openflow13;

import org.opendaylight.controller.sal.core.Node;
import org.opendaylight.controller.sal.core.NodeConnector;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeKey;

public final class NodeUtils {
    public static final String OPENFLOW_NODE_PREFIX = "openflow:";

    public static String getMdsalNodeIdString(Node node) {
        return OPENFLOW_NODE_PREFIX + node.getID();
    }

    public static String getMdsalNodeConnectorIdString(NodeConnector nodeConnector) {
        return OPENFLOW_NODE_PREFIX + nodeConnector.getNode().getID() + ":" + nodeConnector.getID();
    }

    public static NodeConnectorId getMdsalNodeConnectorId(NodeConnector nodeConnector) {
        return new NodeConnectorId(getMdsalNodeConnectorIdString(nodeConnector));
    }

    public static NodeBuilder createNodeBuilder(Node node) {
        return createNodeBuilder(new NodeId(getMdsalNodeIdString(node)));
    }

    public static NodeBuilder createNodeBuilder(NodeId nodeId) {
        NodeBuilder builder = new NodeBuilder();
        builder.setId(nodeId);
        builder.setKey(new NodeKey(nodeId));
        return builder;
    }

    public static NodeBuilder createNodeBuilder(String dpid) {
        String nodeName = OPENFLOW_NODE_PREFIX + dpid;
        NodeBuilder builder = new NodeBuilder();
        builder.setId(new NodeId(nodeName));
        builder.setKey(new NodeKey(builder.getId()));
        return builder;
    }
}
