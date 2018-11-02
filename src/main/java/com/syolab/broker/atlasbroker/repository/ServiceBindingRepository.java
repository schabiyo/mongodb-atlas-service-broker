package com.syolab.broker.atlasbroker.repository;

import com.syolab.broker.atlasbroker.model.ServiceBinding;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceBindingRepository extends JpaRepository<ServiceBinding, String> {
}
