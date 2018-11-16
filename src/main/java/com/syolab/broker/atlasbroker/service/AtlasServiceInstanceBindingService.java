package com.syolab.broker.atlasbroker.service;

import com.syolab.broker.atlasbroker.atlas.AtlasClient;
import com.syolab.broker.atlasbroker.atlas.AtlasException;
import com.syolab.broker.atlasbroker.atlas.UserGenerator;
import com.syolab.broker.atlasbroker.model.ServiceBinding;
import com.syolab.broker.atlasbroker.model.ServiceInstance;
import com.syolab.broker.atlasbroker.repository.ServiceBindingRepository;
import com.syolab.broker.atlasbroker.repository.ServiceInstanceRepository;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceBindingDoesNotExistException;
import org.springframework.cloud.servicebroker.exception.ServiceInstanceDoesNotExistException;
import org.springframework.cloud.servicebroker.model.binding.*;
import org.springframework.cloud.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
public class AtlasServiceInstanceBindingService  implements ServiceInstanceBindingService {

    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";
    private static final String PROJECT_ID_KEY = "groupId";
    private static final String DATABASE_KEY = "database";
    private static final String MONGODB_URI_KEY = "mongodbUri";
    private static final String URI_KEY = "uri";

    private static final String DEFAULT_DATABASE = "test";

    private static final Logger log = LoggerFactory.getLogger(AtlasServiceInstanceBindingService.class);

    @Autowired
    private AtlasClient atlasClient;

    @Autowired
    private ServiceInstanceRepository instanceRepository;


    private final ServiceBindingRepository bindingRepository;
    private final UserGenerator userGenerator;


    public AtlasServiceInstanceBindingService(ServiceBindingRepository bindingRepository,UserGenerator userGenerator){
        this.bindingRepository = bindingRepository;
        this.userGenerator = userGenerator;
    }



    @Override
    public void deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest request) {
        log.info("deleteServiceInstanceBinding::" + request.toString());
        String bindingId = request.getBindingId();
        Optional<ServiceBinding> binding = bindingRepository.findById(request.getBindingId());
        if(binding.isPresent()) {
            try {
                Map<String, Object> credentials = binding.get().getCredentials();
                atlasClient.deleteUser((String) credentials.get(PROJECT_ID_KEY), (String) credentials.get(USERNAME_KEY));
                bindingRepository.deleteById(bindingId);
            }catch(AtlasException ex){
                ex.printStackTrace();
            }
        }else {
            log.error("ServiceInstance binding NOT FOUND");
            throw new ServiceInstanceBindingDoesNotExistException(bindingId);
        }

    }

    @Override
    public CreateServiceInstanceBindingResponse createServiceInstanceBinding(CreateServiceInstanceBindingRequest request) {
        log.info("createServiceInstanceBinding::" + request.toString());
        CreateServiceInstanceAppBindingResponse.CreateServiceInstanceAppBindingResponseBuilder responseBuilder =
                CreateServiceInstanceAppBindingResponse.builder();

        Optional<ServiceBinding> binding = bindingRepository.findById(request.getBindingId());
        if (binding.isPresent()) {
            responseBuilder
                    .bindingExisted(true)
                    .credentials(binding.get().getCredentials());
        } else {
            //Create the User in Atlas
            try {
                Map<String, Object> credentials = buildCredentials(request.getServiceInstanceId());
                if (atlasClient.createUser((String) credentials.get(PROJECT_ID_KEY), (String) credentials.get(USERNAME_KEY), (String) credentials.get(PASSWORD_KEY)))
                    saveBinding(request, credentials);
                else
                    log.error("User could not be saved");
                responseBuilder
                        .bindingExisted(false)
                        .credentials(credentials);
            }catch(AtlasException ex){
                ex.printStackTrace();
            }
        }
        return responseBuilder.build();
    }

    @Override
    public GetServiceInstanceBindingResponse getServiceInstanceBinding(GetServiceInstanceBindingRequest request) {
        log.info("getServiceInstanceBinding:" + request);

        String bindingId = request.getBindingId();
        Optional<ServiceBinding> serviceBinding = bindingRepository.findById(bindingId);

        if (serviceBinding.isPresent()) {
            return GetServiceInstanceAppBindingResponse.builder()
                    .parameters(serviceBinding.get().getParameters())
                    .credentials(serviceBinding.get().getCredentials())
                    .build();
        } else {
            throw new ServiceInstanceBindingDoesNotExistException(bindingId);
        }
    }

    private Map<String, Object> buildCredentials(String instanceId) throws AtlasException{
        Optional<ServiceInstance> serviceInstance = instanceRepository.findById(instanceId);
        if (!serviceInstance.isPresent())
            throw new ServiceInstanceDoesNotExistException("Service instance not found");

        JSONObject response = atlasClient.getCluster(serviceInstance.get().getLink());
        String projectId = atlasClient.getProjectId(response);
        String svrAddress = atlasClient.getClusterURI(response);

        log.info("svrAddress::" + svrAddress);
        String password = userGenerator.generatePassword();

        Map<String, Object> credentials = new HashMap<>();
        credentials.put(URI_KEY,svrAddress.concat("/test?retryWrites=true"));
        credentials.put(USERNAME_KEY, instanceId);
        credentials.put(PASSWORD_KEY, password);
        credentials.put(DATABASE_KEY, DEFAULT_DATABASE);
        credentials.put(MONGODB_URI_KEY, svrAddress.concat("/test?retryWrites=true"));
        credentials.put(PROJECT_ID_KEY,projectId);
        return credentials;

    }


    private void saveBinding(CreateServiceInstanceBindingRequest request, Map<String, Object> credentials) {
        ServiceBinding serviceBinding =
                new ServiceBinding(request.getBindingId(), request.getParameters(), credentials);
        bindingRepository.save(serviceBinding);
        log.info("Binding instance :" + request.getBindingId() + " saved successfully");
    }


}
