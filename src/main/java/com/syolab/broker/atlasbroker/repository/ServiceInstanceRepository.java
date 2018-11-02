package com.syolab.broker.atlasbroker.repository;

import com.syolab.broker.atlasbroker.model.ServiceInstance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceInstanceRepository extends JpaRepository<ServiceInstance, String> {
}
