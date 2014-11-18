package org.opendaylight.tutorial.plugin_exercise;

import org.opendaylight.tutorial.plugin_exercise.api.MdsalConsumer;
import org.opendaylight.tutorial.plugin_exercise.internal.TutorialFlowProgrammer;
import org.opendaylight.tutorial.plugin_exercise.internal.MdsalConsumerImpl;
import org.opendaylight.tutorial.plugin_exercise.internal.TutorialOvsBridgeManager;
import org.apache.felix.dm.Component;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;
import org.opendaylight.controller.sal.core.ComponentActivatorAbstractBase;
import org.opendaylight.ovsdb.plugin.api.OvsdbConfigurationService;
import org.opendaylight.ovsdb.plugin.api.OvsdbConnectionService;
import org.opendaylight.ovsdb.plugin.api.OvsdbInventoryListener;
import org.opendaylight.ovsdb.plugin.api.OvsdbInventoryService;

/**
 * OSGi Bundle Activator for the plugin exercise
 */
public class Activator extends ComponentActivatorAbstractBase {
    /**
     * Function called when the activator starts just after some
     * initializations are done by the
     * ComponentActivatorAbstractBase.
     */
    @Override
    public void init() {
    }

    /**
     * Function called when the activator stops just before the
     * cleanup done by ComponentActivatorAbstractBase.
     *
     */
    @Override
    public void destroy() {
    }

    /**
     * Function that is used to communicate to dependency manager the
     * list of known implementations for services inside a container.
     *
     * @return An array containing all the CLASS objects that will be
     * instantiated in order to get an fully working implementation
     * Object
     */
    @Override
    public Object[] getImplementations() {
        Object[] res = {MdsalConsumerImpl.class,
                        TutorialFlowProgrammer.class,
                        TutorialOvsBridgeManager.class};
        return res;
    }

    /**
     * Function that is called when configuration of the dependencies
     * is required.
     *
     * @param c dependency manager Component object, used for
     * configuring the dependencies exported and imported
     * @param imp Implementation class that is being configured,
     * needed as long as the same routine can configure multiple
     * implementations
     * @param containerName The containerName being configured, this allow
     * also optional per-container different behavior if needed, usually
     * should not be the case though.
     */
    @Override
    public void configureInstance(Component c, Object imp,
                                  String containerName) {

        if (imp.equals(MdsalConsumerImpl.class)) {
            c.setInterface(MdsalConsumer.class.getName(), null);
            c.add(createServiceDependency().setService(BindingAwareBroker.class).setRequired(true));
        }

        if (imp.equals(TutorialFlowProgrammer.class)) {
            c.add(createServiceDependency().setService(MdsalConsumer.class).setRequired(true));
        }

        if (imp.equals(TutorialOvsBridgeManager.class)) {
            c.setInterface(OvsdbInventoryListener.class.getName(), null);
            c.add(createServiceDependency().setService(OvsdbInventoryService.class).setRequired(true));
            c.add(createServiceDependency().setService(OvsdbConfigurationService.class).setRequired(true));
            c.add(createServiceDependency().setService(OvsdbConnectionService.class).setRequired(true));
        }
    }
}
