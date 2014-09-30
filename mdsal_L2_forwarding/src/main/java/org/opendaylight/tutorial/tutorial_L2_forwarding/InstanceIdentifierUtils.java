package org.opendaylight.tutorial.tutorial_L2_forwarding;

import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowCapableNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.Table;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.TableKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.Flow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.node.NodeConnector;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.node.NodeConnectorKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeKey;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;


public class InstanceIdentifierUtils {

    /**
     * Creates an Instance Identifier (path) for node with specified id
     * 
     * @param nodeId
     * @return
     */
    public static final InstanceIdentifier<Node> createNodePath(NodeId nodeId) {
        return InstanceIdentifier.builder(Nodes.class) //
                .child(Node.class, new NodeKey(nodeId)) //
                .build();
    }

    /**
     * Shorten's node child path to node path.
     * 
     * @param nodeChild child of node, from which we want node path.
     * @return
     */
    public static final InstanceIdentifier<Node> getNodePath(InstanceIdentifier<?> nodeChild) {
        return nodeChild.firstIdentifierOf(Node.class);
    }
    
    
    /**
     * Creates a table path by appending table specific location to node path
     * 
     * @param nodePath
     * @param tableKey
     * @return
     */
    public static final InstanceIdentifier<Table> createTablePath(InstanceIdentifier<Node> nodePath,TableKey tableKey) {
        return InstanceIdentifier.builder(nodePath)
                .augmentation(FlowCapableNode.class)
                .child(Table.class, tableKey)
                .build();
    }

    /**
     * Creates a path for particular flow, by appending flow-specific information
     * to table path.
     * 
     * @param flowId
     * @param tablePathArg 
     * @return path to flow
     */
    public static InstanceIdentifier<Flow> createFlowPath(InstanceIdentifier<Table> table,FlowKey flowKey) {
        return InstanceIdentifier.builder(table)
                .child(Flow.class, flowKey)
                .build();
    }

    /**
     * Extract table id from table path.
     * 
     * @param tablePath
     * @return
     */
    public static Short getTableId(InstanceIdentifier<Table> tablePath) {
        return tablePath.firstKeyOf(Table.class, TableKey.class).getId();
    }
    
    /**
     * Extracts NodeConnectorKey from node connector path.
     * 
     */
    public static NodeConnectorKey getNodeConnectorKey(InstanceIdentifier<?> nodeConnectorPath) {
        return nodeConnectorPath.firstKeyOf(NodeConnector.class, NodeConnectorKey.class);
    }
    
    
    //
    public static final InstanceIdentifier<NodeConnector> createNodeConnectorPath(InstanceIdentifier<Node> nodeKey,NodeConnectorKey nodeConnectorKey) {
        return InstanceIdentifier.builder(nodeKey) //
                .child(NodeConnector.class,nodeConnectorKey) //
                .build();
    }
}
