package org.opendaylight.tutorial.plugin_exercise.api;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ConsumerContext;
import org.opendaylight.controller.sal.binding.api.NotificationService;

public interface MdsalConsumer {
    public ConsumerContext getConsumerContext();
    public DataBroker getDataBroker();
    public NotificationService getNotificationService();
}
