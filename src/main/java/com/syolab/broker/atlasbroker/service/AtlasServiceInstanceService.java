package com.syolab.broker.atlasbroker.service;

import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.CreateServiceInstanceResponse;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceRequest;
import org.springframework.cloud.servicebroker.model.instance.DeleteServiceInstanceResponse;
import org.springframework.cloud.servicebroker.service.ServiceInstanceService;
import org.springframework.stereotype.Service;

@Service
public class AtlasServiceInstanceService implements ServiceInstanceService {


    @Override
    public CreateServiceInstanceResponse createServiceInstance(CreateServiceInstanceRequest request) {
        String instanceId = request.getServiceInstanceId();

        CreateServiceInstanceResponse.CreateServiceInstanceResponseBuilder responseBuilder = CreateServiceInstanceResponse.builder();


        return responseBuilder.build();
    }
    @Override
    public DeleteServiceInstanceResponse deleteServiceInstance(DeleteServiceInstanceRequest request) {
        String instanceId = request.getServiceInstanceId();

       /* if (instanceRepository.existsById(instanceId)) {
            storeService.deleteBookStore(instanceId);
            instanceRepository.deleteById(instanceId);

            return DeleteServiceInstanceResponse.builder().build();
        } else {
            throw new ServiceInstanceDoesNotExistException(instanceId);
        }*/
       return null;
    }
}
