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
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.Flow;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;

/**
 * 
 */
public interface FlowCommitWrapper {

    /**
     * Starts and commits data change transaction which 
     * modifies provided flow path with supplied body.
     * 
     * @param flowPath 
     * @param flowBody 
     * @return transaction commit 
     * 
     */
    Future<RpcResult<TransactionStatus>> writeFlowToConfig(InstanceIdentifier<Flow> flowPath, Flow flowBody);

}
