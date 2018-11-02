package com.syolab.broker.atlasbroker.service;

import com.syolab.broker.atlasbroker.atlas.AtlasClient;
import com.syolab.broker.atlasbroker.atlas.AtlasException;
import com.syolab.broker.atlasbroker.atlas.ClusterInfo;
import com.syolab.broker.atlasbroker.atlas.ClusterState;
import com.syolab.broker.atlasbroker.model.ServiceInstance;
import com.syolab.broker.atlasbroker.repository.ServiceInstanceRepository;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerException;
import org.springframework.cloud.servicebroker.exception.ServiceBrokerInvalidParametersException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.model.instance.*;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
public class AtlasServiceInstanceService implements ServiceInstanceService {

    private static final Logger log = LoggerFactory.getLogger(AtlasServiceInstanceService.class);

    @Autowired
    private AtlasClient atlasClient;

    @Autowired
    private ServiceInstanceRepository instanceRepository;

    @Override
    public CreateServiceInstanceResponse createServiceInstance(CreateServiceInstanceRequest request) {
        String instanceId = request.getServiceInstanceId();
        System.out.println("instanceId="+instanceId);
        System.out.println("request="+request.toString());
        CreateServiceInstanceResponse.CreateServiceInstanceResponseBuilder responseBuilder = CreateServiceInstanceResponse.builder();
        if (instanceRepository.existsById(instanceId)) {
            responseBuilder.instanceExisted(true);
        } else {
            try {
                ClusterInfo clusterInfo = atlasClient.createCluster(request.getPlanId(),instanceId.replace("-",""));
                saveInstance(request, instanceId,clusterInfo);
                return CreateServiceInstanceResponse.builder()
                        .dashboardUrl(clusterInfo.getDashboardUrl())
                        .async(true)
                        .build();

            }catch (AtlasException ex){
                throw new ServiceBrokerException(ex.getMessage());
            }
        }

        return responseBuilder.build();
    }
    @Override
    public DeleteServiceInstanceResponse deleteServiceInstance(DeleteServiceInstanceRequest request) {
        String instanceId = request.getServiceInstanceId();
        Optional<ServiceInstance> serviceInstance = instanceRepository.findById(instanceId);
       if (serviceInstance.isPresent()){
           try {
               String link =  serviceInstance.get().getLink();
               atlasClient.deleteCluster(link);
               //instanceRepository.deleteById(instanceId);
               return DeleteServiceInstanceResponse.builder()
                       .async(true)
                       .build();

           }catch (AtlasException ex){
                log.error(ex.getMessage());
                throw new ServiceBrokerInvalidParametersException(ex.getMessage());
           }
        } else {
            throw new ServiceInstanceDoesNotExistException(instanceId);
        }
    }

    @Override
    public GetLastServiceOperationResponse getLastOperation(GetLastServiceOperationRequest request) {
        String serviceInstanceId = request.getServiceInstanceId();
        log.info("getLastOperation:" + serviceInstanceId );
        Optional<ServiceInstance> serviceInstance = instanceRepository.findById(serviceInstanceId);
        ServiceInstance instance = serviceInstance.get();
        String link =  instance.getLink();
        String projectLink =  instance.getProjectLink();
        log.debug("link:" + link );
        log.debug("Project Link:" + projectLink);
        try {
            JSONObject response = atlasClient.getCluster(link);
            ClusterState state = atlasClient.getClusterStatus(response);
            OperationState opState = OperationState.IN_PROGRESS;
            switch (state){
                case IDLE: opState = OperationState.SUCCEEDED;
                    String uri = atlasClient.getClusterURI(response);
                    log.info("uri=" + uri);
                break;
                case NOTFOUND: opState = OperationState.SUCCEEDED;
                //If The project is empty, delete it
                    atlasClient.deleteProject(projectLink);
                    //Delete from the DB
                    instanceRepository.deleteById(serviceInstanceId);
                break;
                case CREATING:
                case UPDATING:
                case REPAIRING:
                case DELETING: opState = OperationState.IN_PROGRESS;break;
            }
            return GetLastServiceOperationResponse.builder()
                    .operationState(opState)
                    .build();
        }catch(AtlasException ex){
            throw new ServiceBrokerException(ex.getMessage());
        }
    }

    private void saveInstance(CreateServiceInstanceRequest request, String instanceId, ClusterInfo clusterInfo ) {
        ServiceInstance serviceInstance = new ServiceInstance(instanceId, request.getServiceDefinitionId(),
                request.getPlanId(), request.getParameters(),clusterInfo);
        instanceRepository.save(serviceInstance);
    }
}
