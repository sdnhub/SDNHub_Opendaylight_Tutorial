package org.sdnhub.odl.tutorial.tapapp.impl;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.sal.binding.api.NotificationProviderService;
import org.opendaylight.controller.sal.binding.api.RpcProviderRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class TutorialTapAppModule extends AbstractTutorialTapAppModule {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    public TutorialTapAppModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public TutorialTapAppModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver, AbstractTutorialTapAppModule oldModule, java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
        // add custom validation form module attributes here.
    }

    @Override
    public java.lang.AutoCloseable createInstance() {
        DataBroker dataBrokerService = getDataBrokerDependency();
        RpcProviderRegistry rpcProviderRegistry = getRpcRegistryDependency();
        NotificationProviderService notificationService = getNotificationServiceDependency();

        Preconditions.checkNotNull(dataBrokerService);
        Preconditions.checkNotNull(rpcProviderRegistry);
        Preconditions.checkNotNull(notificationService);

        TutorialTapProvider tapProvider = new TutorialTapProvider(dataBrokerService, notificationService, rpcProviderRegistry);
        LOG.info("Tutorial TapApp (instance {}) initialized.", tapProvider);
        return tapProvider;
    }
}
