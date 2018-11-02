package com.syolab.broker.atlasbroker.model;

import com.syolab.broker.atlasbroker.atlas.ClusterInfo;

import javax.persistence.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "service_instances")
public class ServiceInstance {
	@Id
	@Column(length = 50)
	private final String instanceId;

	@Column(length = 50)
	private final String serviceDefinitionId;

	@Column(length = 50)
	private final String planId;

	@ElementCollection
	@MapKeyColumn(name="parameter_name", length = 100)
	@Column(name = "parameter_value")
	@CollectionTable(name="service_instance_parameters", joinColumns = @JoinColumn(name = "instance_id"))
	@Convert(converter = ObjectToStringConverter.class, attributeName = "value")
	private final Map<String, Object> parameters;

	@Column(name = "link")
	private final String link;

	@Column(name = "uri")
	private String uri;

    @Column(name = "projectLink")
    private final String projectLink;

	@SuppressWarnings("unused")
	private ServiceInstance() {
		instanceId = null;
		serviceDefinitionId = null;
		planId = null;
		parameters = null;
		link = null;
        projectLink = null;
        this.uri = null;
	}

	public ServiceInstance(String instanceId, String serviceDefinitionId, String planId,
                           Map<String, Object> parameters, ClusterInfo clusterInfo) {
		this.instanceId = instanceId;
		this.serviceDefinitionId = serviceDefinitionId;
		this.planId = planId;
		this.link = clusterInfo.getProjectUrl();
		this.projectLink = clusterInfo.getDashboardUrl();
		this.parameters = parameters;
		this.uri = clusterInfo.getUri();
	}

	public String getInstanceId() {
		return instanceId;
	}

	public String getServiceDefinitionId() {
		return serviceDefinitionId;
	}

	public String getPlanId() {
		return planId;
	}

	public String getLink() {
		return link;
	}

    public String getProjectLink() { return projectLink; }

    public Map<String, Object> getParameters() {
		return parameters;
	}

    public String getUri() {
        return uri;
    }

	public void setUri(String uri) {
		this.uri = uri;
	}
}
