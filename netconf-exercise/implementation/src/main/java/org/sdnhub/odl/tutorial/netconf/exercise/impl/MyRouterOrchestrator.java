package org.sdnhub.odl.tutorial.netconf.exercise.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataChangeListener;
import org.opendaylight.controller.md.sal.binding.api.MountPoint;
import org.opendaylight.controller.md.sal.binding.api.MountPointService;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataBroker.DataChangeScope;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataChangeEvent;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider;
import org.opendaylight.controller.sal.binding.api.NotificationProviderService;
import org.opendaylight.controller.sal.binding.api.RpcProviderRegistry;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.network.topology.topology.topology.types.TopologyNetconf;
import org.opendaylight.yang.gen.v1.urn.sdnhub.odl.tutorial.router.rev150728.Interfaces;
import org.opendaylight.yang.gen.v1.urn.sdnhub.odl.tutorial.router.rev150728.InterfacesBuilder;
import org.opendaylight.yang.gen.v1.urn.sdnhub.odl.tutorial.router.rev150728.InterfacesKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TopologyId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.NodeKey;
import org.opendaylight.yangtools.concepts.Registration;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.sdnhub.odl.tutorial.utils.GenericTransactionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class MyRouterOrchestrator implements BindingAwareProvider, AutoCloseable, DataChangeListener {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    //Members related to MD-SAL operations
    private List<Registration> registrations = Lists.newArrayList();
    private MountPointService mountService;

    public MyRouterOrchestrator(BindingAwareBroker bindingAwareBroker, DataBroker dataBroker, NotificationProviderService notificationService, RpcProviderRegistry rpcProviderRegistry) {
        //Register this object as a provider for receiving session context
        bindingAwareBroker.registerProvider(this);
        
        //Register this object as listener for changes in netconf topology
        InstanceIdentifier<Topology> netconfTopoIID = InstanceIdentifier.builder(NetworkTopology.class)
                .child(Topology.class, new TopologyKey(new TopologyId(TopologyNetconf.QNAME.getLocalName())))
                .build();
        registrations.add(dataBroker.registerDataChangeListener(LogicalDatastoreType.OPERATIONAL, netconfTopoIID.child(Node.class), this, DataChangeScope.SUBTREE));
    }

	@Override
	public void onSessionInitiated(ProviderContext session) {
		// Get the mount service provider
        this.mountService = session.getSALService(MountPointService.class);
	}

    public void close() throws Exception {
        for (Registration registration : registrations) {
            registration.close();
        }
        registrations.clear();
    }

    @Override
    public void onDataChanged(AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> change) {
        LOG.debug("Data changed: {} created, {} updated, {} removed",
                change.getCreatedData().size(), change.getUpdatedData().size(), change.getRemovedPaths().size());

        DataObject dataObject;

        // Iterate over any created nodes 
        for (Map.Entry<InstanceIdentifier<?>, DataObject> entry : change.getCreatedData().entrySet()) {
            dataObject = entry.getValue();
            if (dataObject instanceof Node) {
            	LOG.debug("ADDED Path {}, Object {}", entry.getKey(), dataObject);            	
            	NodeId nodeId = entry.getKey().firstKeyOf(Node.class, NodeKey.class).getNodeId();
            	final String routerName = nodeId.getValue();

            	//Add interface configs 2 seconds later to give the mount point time to settle down
            	final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
            	exec.schedule(new Runnable(){
            	    @Override
            	    public void run(){
		            	//Configure interfaces for each router we care about
		            	if (routerName.equals("router1")) {
		                	configureRouterInterface(routerName, "eth0", "10.10.1.1/24");	
		                	configureRouterInterface(routerName, "eth1", "100.100.100.1/24");	
			            } else if (routerName.equals("router2")) {
		                	configureRouterInterface(routerName, "eth0", "10.10.1.2/24");	
		                	configureRouterInterface(routerName, "eth1", "100.100.100.2/24");	
			            }
            	    }
            	}, 2, TimeUnit.SECONDS);
            }
        }

        // Iterate over any deleted nodes 
        Map<InstanceIdentifier<?>, DataObject> originalData = change.getOriginalData();
        for (InstanceIdentifier<?> path : change.getRemovedPaths()) {
            dataObject = originalData.get(path);
            if (dataObject instanceof Node) {
            	LOG.debug("REMOVED Path {}, Object {}", path, dataObject);
            }
        }
        
        //FIXME: Handle relevant updates
    }

	/*
	 * In this method, we write data to a data store location referenced by the REST URL
	       /restconf/config/network-topology:network-topology/topology/topology-netconf
			  /node/<node-id>/yang-ext:mount
			      /router:interfaces/<interface-name>
     * 
     */
    private void configureRouterInterface(String routerName, String interfaceName, String subnetPrefix) {
    	LOG.debug("Configuring interface {} for {}", interfaceName, routerName);    	
		
    	//Step 1: Get access to mount point
        InstanceIdentifier<Node> netconfNodeIID = InstanceIdentifier.builder(NetworkTopology.class)
            .child(Topology.class, new TopologyKey(new TopologyId(TopologyNetconf.QNAME.getLocalName())))
            .child(Node.class, new NodeKey(new NodeId(routerName)))
            .build();
    	final Optional<MountPoint> netconfNodeOptional = mountService.getMountPoint(netconfNodeIID);
    	
    	//Step 2: Write to the mount point
    	if (netconfNodeOptional.isPresent()) {
    		
    		//Step 2.1: access data broker within this mount point
    		MountPoint netconfNode = netconfNodeOptional.get();
    		DataBroker netconfNodeDataBroker = netconfNode.getService(DataBroker.class).get();

    		//Step 2.2: construct data and the relative iid
    		InterfacesBuilder builder = new InterfacesBuilder()
            	.setId(interfaceName)
            	.setKey(new InterfacesKey(interfaceName))
            	.setIpAddress(subnetPrefix);
            InstanceIdentifier<Interfaces> interfacesIID = InstanceIdentifier
            		.builder(Interfaces.class, builder.getKey())
            		.build();
            
            //Step 2.3: write to the config data store
    	    GenericTransactionUtils.writeData(netconfNodeDataBroker, LogicalDatastoreType.CONFIGURATION, interfacesIID, builder.build(), true);

    	} else {
    		LOG.warn("Mount point not ready for {}", routerName);
    	}
    }
}
