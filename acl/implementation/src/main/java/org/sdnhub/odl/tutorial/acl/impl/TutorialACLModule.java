package org.sdnhub.odl.tutorial.acl.impl;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.sal.binding.api.NotificationProviderService;
import org.opendaylight.controller.sal.binding.api.RpcProviderRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TutorialACLModule extends org.sdnhub.odl.tutorial.acl.impl.AbstractTutorialACLModule {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    public TutorialACLModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public TutorialACLModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver, org.sdnhub.odl.tutorial.acl.impl.TutorialACLModule oldModule, java.lang.AutoCloseable oldInstance) {
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

            TutorialACL tutorialACL = new TutorialACL(dataBroker, notificationService, rpcRegistry);
            LOG.info("Tutorial Access Control List (instance {}) initialized.", tutorialACL);
            return tutorialACL;
        }
}
