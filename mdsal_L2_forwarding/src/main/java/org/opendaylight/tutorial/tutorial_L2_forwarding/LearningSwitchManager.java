/**
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.tutorial.tutorial_L2_forwarding;

import org.opendaylight.controller.sal.binding.api.NotificationService;
import org.opendaylight.controller.sal.binding.api.data.DataBrokerService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.PacketProcessingService;

/**
 * 
 */
public interface LearningSwitchManager {

    /**
     * stop manager
     */
    void stop();

    /**
     * start manager
     */
    void start();

    /**
     * Set's Data Broker dependency.
     * 
     * Data Broker is used to access overal operational and configuration
     * tree.
     * 
     *  In simple Learning Switch handler, data broker is used to listen 
     *  for changes in Openflow tables and to configure flows which will
     *  be provisioned down to the Openflow switch.
     * 
     * inject {@link DataBrokerService}
     * @param data
     */
    void setDataBroker(DataBrokerService data);

    /**
     * Set's Packet Processing dependency.
     * 
     * Packet Processing service is used to send packet Out on Openflow
     * switch.
     * 
     * inject {@link PacketProcessingService}
     * 
     * @param packetProcessingService
     */
    void setPacketProcessingService(
            PacketProcessingService packetProcessingService);

    /**
     * Set's Notification service dependency.
     * 
     * Notification service is used to register for listening 
     * packet-in notifications.
     * 
     * inject {@link NotificationService}
     * @param notificationService
     */
    void setNotificationService(NotificationService notificationService);
}
