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
package org.sdnhub.odl.tutorial.utils.inventory;

import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorRef;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeRef;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.node.NodeConnector;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.node.NodeConnectorBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.node.NodeConnectorKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

public final class InventoryUtils {
    public static final String OPENFLOW_NODE_PREFIX = "openflow:";

    public static NodeRef getNodeRef(NodeId nodeId) {
        return new NodeRef(InstanceIdentifier.builder(Nodes.class)
            .child(Node.class, new NodeKey(nodeId))
            .build());
    }

	public static NodeRef getNodeRef(NodeConnectorRef nodeConnectorRef) {
        InstanceIdentifier<Node> nodeIID = nodeConnectorRef.getValue()
    		.firstIdentifierOf(Node.class);
        return new NodeRef(nodeIID);
	}

    public static NodeBuilder getNodeBuilder(NodeId nodeId) {
        NodeBuilder builder = new NodeBuilder()
        	.setId(nodeId)
        	.setKey(new NodeKey(nodeId));
        return builder;
    }

    public static NodeBuilder getNodeBuilder(String dpid) {
        String nodeName = OPENFLOW_NODE_PREFIX + dpid;
        NodeBuilder builder = new NodeBuilder();
        builder.setId(new NodeId(nodeName));
        builder.setKey(new NodeKey(builder.getId()));
        return builder;
    }

	public static NodeId getNodeId(NodeConnectorRef nodeConnectorRef) {
		return nodeConnectorRef.getValue()
	        .firstKeyOf(Node.class, NodeKey.class)
	        .getId();
	}
	
    public static NodeId getNodeId(NodeConnectorId nodeConnectorId) {
    	if (nodeConnectorId == null)
    		return null;
    	String[] tokens = nodeConnectorId.getValue().split(":");
    	if (tokens.length == 3)
    		return new NodeId(OPENFLOW_NODE_PREFIX + Long.parseLong(tokens[1]));
    	else
    		return null;
    }

    public static NodeConnectorId getNodeConnectorId(NodeId nodeId, long portNumber) {
    	if (nodeId == null)
    		return null;
    	String nodeConnectorIdStr = nodeId.getValue() + ":" + portNumber;
		return new NodeConnectorId(nodeConnectorIdStr);
    }

    public static NodeConnectorRef getNodeConnectorRef(NodeConnectorId nodeConnectorId) {
    	NodeId nodeId = getNodeId(nodeConnectorId);
        return new NodeConnectorRef(InstanceIdentifier.builder(Nodes.class)
                .child(Node.class, new NodeKey(nodeId))
                .child(NodeConnector.class, new NodeConnectorKey(nodeConnectorId))
                .build());
    }

    public static NodeConnectorBuilder getNodeConnectorBuilder(NodeConnectorId nodeConnectorId) {
        NodeConnectorBuilder builder = new NodeConnectorBuilder()
        	.setId(nodeConnectorId)
        	.setKey(new NodeConnectorKey(nodeConnectorId));
        return builder;
    }

	public static NodeConnectorId getNodeConnectorId(NodeConnectorRef nodeConnectorRef) {
        return nodeConnectorRef.getValue()
            .firstKeyOf(NodeConnector.class, NodeConnectorKey.class)
            .getId();
	}
}
