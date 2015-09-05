package org.sdnhub.odl.tutorial.netconf.exercise.impl;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;
import org.opendaylight.controller.sal.binding.api.NotificationProviderService;
import org.opendaylight.controller.sal.binding.api.RpcProviderRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class TutorialNetconfExerciseModule extends AbstractTutorialNetconfExerciseModule {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    public TutorialNetconfExerciseModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public TutorialNetconfExerciseModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver, AbstractTutorialNetconfExerciseModule oldModule, java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
        // add custom validation form module attributes here.
    }

    @Override
    public java.lang.AutoCloseable createInstance() {
    	BindingAwareBroker bindingAwareBroker = getBindingAwareBrokerDependency();
        DataBroker dataBrokerService = getDataBrokerDependency();
        RpcProviderRegistry rpcProviderRegistry = getRpcRegistryDependency();
        NotificationProviderService notificationService = getNotificationServiceDependency();

        Preconditions.checkNotNull(bindingAwareBroker);
        Preconditions.checkNotNull(dataBrokerService);
        Preconditions.checkNotNull(rpcProviderRegistry);
        Preconditions.checkNotNull(notificationService);

        MyRouterOrchestrator orchestrator = new MyRouterOrchestrator(bindingAwareBroker, dataBrokerService, notificationService, rpcProviderRegistry);
        LOG.info("Tutorial NetconfExercise (instance {}) initialized.", orchestrator);
        return orchestrator;
    }
}
