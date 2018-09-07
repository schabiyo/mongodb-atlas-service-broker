package com.syolab.broker.atlasbroker.service;

import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingDoesNotExistException;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceAppBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.model.binding.CreateServiceInstanceBindingResponse;
import org.springframework.cloud.servicebroker.model.binding.DeleteServiceInstanceBindingRequest;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class AtlasServiceInstanceBindingService  implements ServiceInstanceBindingService {

    @Override
    public void deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest request) {
        String bindingId = request.getBindingId();


    }

    @Override
    public CreateServiceInstanceBindingResponse createServiceInstanceBinding(CreateServiceInstanceBindingRequest request) {
        CreateServiceInstanceAppBindingResponse.CreateServiceInstanceAppBindingResponseBuilder responseBuilder =
                CreateServiceInstanceAppBindingResponse.builder();

        return responseBuilder.build();
    }


}
