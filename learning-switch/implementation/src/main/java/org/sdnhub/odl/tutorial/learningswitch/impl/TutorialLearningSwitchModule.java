package org.sdnhub.odl.tutorial.learningswitch.impl;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.sal.binding.api.NotificationProviderService;
import org.opendaylight.controller.sal.binding.api.RpcProviderRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TutorialLearningSwitchModule extends org.sdnhub.odl.tutorial.learningswitch.impl.AbstractTutorialLearningSwitchModule {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    public TutorialLearningSwitchModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public TutorialLearningSwitchModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver, org.sdnhub.odl.tutorial.learningswitch.impl.TutorialLearningSwitchModule oldModule, java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
        // add custom validation form module attributes here.
    }

    @Override
    public java.lang.AutoCloseable createInstance() {
    	//Get all MD-SAL provider objects
        DataBroker dataBroker = getDataBrokerDependency();
        RpcProviderRegistry rpcRegistry = getRpcRegistryDependency();
        NotificationProviderService notificationService = getNotificationServiceDependency();
        
        TutorialL2Forwarding tutorialL2Forwarding = new TutorialL2Forwarding(dataBroker, notificationService, rpcRegistry);
        LOG.info("Tutorial Learning Switch (instance {}) initialized.", tutorialL2Forwarding);
        return tutorialL2Forwarding;
    }

}
