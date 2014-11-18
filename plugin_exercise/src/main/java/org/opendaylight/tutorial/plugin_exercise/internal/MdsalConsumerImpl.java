package org.opendaylight.tutorial.plugin_exercise.internal;

import java.util.Collection;

import org.apache.felix.dm.Component;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ConsumerContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareConsumer;
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider;
import org.opendaylight.controller.sal.binding.api.NotificationService;
import org.opendaylight.yangtools.yang.binding.RpcService;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.opendaylight.tutorial.plugin_exercise.api.MdsalConsumer;

public class MdsalConsumerImpl implements BindingAwareConsumer, MdsalConsumer {

    private BundleContext ctx = null;
    private volatile BindingAwareBroker broker;
    private ConsumerContext consumerContext = null;
    private DataBroker dataBroker;
    private NotificationService notificationService;

    static final Logger logger = LoggerFactory.getLogger(MdsalConsumerImpl.class);

    void init(Component c) {
        this.ctx = c.getDependencyManager().getBundleContext();
        logger.info("Registered consumer with MD-SAL");
        broker.registerConsumer(this, this.ctx);
    }

    void destroy() {
        // Now lets close MDSAL session
        if (this.consumerContext != null) {
            //this.consumerContext.close();
            this.dataBroker = null;
            this.consumerContext = null;
        }
    }

    void start() {
    }

    void stop() {
    }

    @Override
    public void onSessionInitialized(ConsumerContext session) {
        this.consumerContext = session;
        dataBroker = session.getSALService(DataBroker.class);
        notificationService = session.getSALService(NotificationService.class);
        logger.info("Initialized consumer context {}", session.toString());
    }

    @Override
    public ConsumerContext getConsumerContext() {
        return consumerContext;
    }
    @Override
    public DataBroker getDataBroker() {
        return dataBroker;
    }
    @Override
    public NotificationService getNotificationService() {
        return notificationService;
    }
}
