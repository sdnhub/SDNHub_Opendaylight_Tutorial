package org.opendaylight.tutorial.plugin_exercise.utils.openflow13;

import java.util.concurrent.ExecutionException;

import org.opendaylight.controller.sal.utils.Status;
import org.opendaylight.controller.sal.utils.StatusCode;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.ReadOnlyTransaction;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowCapableNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.Table;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.TableKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.Flow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeBuilder;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.CheckedFuture;

public final class TransactionUtils {
    static final Logger logger = LoggerFactory.getLogger(TransactionUtils.class);

    public static Status writeFlow(DataBroker dataBroker, FlowBuilder flowBuilder, NodeBuilder nodeBuilder) {
        if (dataBroker == null) {
            logger.error("Null DataBroker. Please check MD-SAL support on the Controller.");
            return new Status(StatusCode.NOTALLOWED);
        }

        ReadWriteTransaction modification = dataBroker.newReadWriteTransaction();
        InstanceIdentifier<org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node> nodePath = InstanceIdentifier.builder(Nodes.class)
                .child(org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node.class, nodeBuilder.getKey()).toInstance();

        modification.put(LogicalDatastoreType.CONFIGURATION, nodePath, nodeBuilder.build(), true);
        InstanceIdentifier<Flow> path1 = InstanceIdentifier.builder(Nodes.class).child(org.opendaylight.yang.gen.v1.urn.opendaylight.inventory
                .rev130819.nodes.Node.class, nodeBuilder.getKey()).augmentation(FlowCapableNode.class).child(Table.class,
                new TableKey(flowBuilder.getTableId())).child(Flow.class, flowBuilder.getKey()).build();

        modification.put(LogicalDatastoreType.CONFIGURATION, path1, flowBuilder.build(), true /*createMissingParents*/);

        CheckedFuture<Void, TransactionCommitFailedException> commitFuture = modification.submit();
        try {
            commitFuture.get();
            logger.debug("Transaction success for write of Flow "+flowBuilder.build());
            return new Status(StatusCode.SUCCESS);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            modification.cancel();
            return new Status(StatusCode.INTERNALERROR);
        }
    }

    public static Status removeFlow(DataBroker dataBroker, FlowBuilder flowBuilder, NodeBuilder nodeBuilder) {
        if (dataBroker == null) {
            logger.error("Null DataBroker. Please check MD-SAL support on the Controller.");
            return new Status(StatusCode.NOTALLOWED);
        }

        WriteTransaction modification = dataBroker.newWriteOnlyTransaction();
        InstanceIdentifier<Flow> path1 = InstanceIdentifier.builder(Nodes.class)
                .child(org.opendaylight.yang.gen.v1.urn.opendaylight.inventory
                               .rev130819.nodes.Node.class, nodeBuilder.getKey())
                .augmentation(FlowCapableNode.class).child(Table.class,
                                                           new TableKey(flowBuilder.getTableId())).child(Flow.class, flowBuilder.getKey()).build();
        modification.delete(LogicalDatastoreType.CONFIGURATION, path1);

        CheckedFuture<Void, TransactionCommitFailedException> commitFuture = modification.submit();
        try {
            commitFuture.get();
            logger.debug("Transaction success for deletion of Flow "+flowBuilder.getFlowName());
            return new Status(StatusCode.SUCCESS);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            modification.cancel();
            return new Status(StatusCode.INTERNALERROR);
        }
    }

    public static Flow getFlow(DataBroker dataBroker, FlowBuilder flowBuilder, NodeBuilder nodeBuilder) {
        if (dataBroker == null) {
            logger.error("Null DataBroker. Please check MD-SAL support on the Controller.");
            return null;
        }

        InstanceIdentifier<Flow> path1 = InstanceIdentifier.builder(Nodes.class).child(org.opendaylight.yang.gen.v1.urn.opendaylight.inventory
                .rev130819.nodes.Node.class, nodeBuilder.getKey()).augmentation(FlowCapableNode.class).child(Table.class,
                new TableKey(flowBuilder.getTableId())).child(Flow.class, flowBuilder.getKey()).build();

        ReadOnlyTransaction readTx = dataBroker.newReadOnlyTransaction();
        try {
            Optional<Flow> data = readTx.read(LogicalDatastoreType.CONFIGURATION, path1).get();
            if (data.isPresent()) {
                return data.get();
            }
        } catch (InterruptedException|ExecutionException e) {
            logger.error(e.getMessage(), e);
        }

        logger.debug("Cannot find data for Flow " + flowBuilder.getFlowName());
        return null;
    }
}
