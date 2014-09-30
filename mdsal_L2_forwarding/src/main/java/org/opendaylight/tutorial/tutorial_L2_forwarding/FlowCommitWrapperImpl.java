/**
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.tutorial.tutorial_L2_forwarding;

import java.util.concurrent.Future;

import org.opendaylight.controller.md.sal.common.api.TransactionStatus;
import org.opendaylight.controller.sal.binding.api.data.DataBrokerService;
import org.opendaylight.controller.sal.binding.api.data.DataModificationTransaction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.Flow;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;

/**
 * 
 */
public class FlowCommitWrapperImpl implements FlowCommitWrapper {
    
    private DataBrokerService dataBrokerService;

    /**
     * @param dataBrokerService
     */
    public FlowCommitWrapperImpl(DataBrokerService dataBrokerService) {
        this.dataBrokerService = dataBrokerService;
    }

    @Override
    public Future<RpcResult<TransactionStatus>> writeFlowToConfig(InstanceIdentifier<Flow> flowPath, 
            Flow flowBody) {
        DataModificationTransaction addFlowTransaction = dataBrokerService.beginTransaction();
        addFlowTransaction.putConfigurationData(flowPath, flowBody);
        return addFlowTransaction.commit();
    }

}
