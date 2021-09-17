package com.elsevier.kd.graph.model;

import javax.json.bind.annotation.JsonbProperty;

/**
 * @author MOQUINR
 */
public class HealthCheckModel {

    @JsonbProperty("service_name")
    private String serviceName;

    @JsonbProperty("environment")
    private String environment;

    @JsonbProperty("build_version")
    private String buildVersion;

    @JsonbProperty("build_hash")
    private String buildHash;

    @JsonbProperty("service_start")
    private String serviceStart;

    public HealthCheckModel(String serviceName, String environment, String buildVersion, String buildHash, String serviceStart) {
        this.serviceName = serviceName;
        this.environment = environment;
        this.buildVersion = buildVersion;
        this.buildHash = buildHash;
        this.serviceStart = serviceStart;
    }

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @param serviceName the serviceName to set
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @return the environment
     */
    public String getEnvironment() {
        return environment;
    }

    /**
     * @param environment the environment to set
     */
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    /**
     * @return the buildVersion
     */
    public String getBuildVersion() {
        return buildVersion;
    }

    /**
     * @param buildVersion the buildVersion to set
     */
    public void setBuildVersion(String buildVersion) {
        this.buildVersion = buildVersion;
    }

    /**
     * @return the buildHash
     */
    public String getBuildHash() {
        return buildHash;
    }

    /**
     * @param buildHash the buildHash to set
     */
    public void setBuildHash(String buildHash) {
        this.buildHash = buildHash;
    }

    /**
     * @return the serviceStart
     */
    public String getServiceStart() {
        return serviceStart;
    }

    /**
     * @param serviceStart the serviceStart to set
     */
    public void setServiceStart(String serviceStart) {
        this.serviceStart = serviceStart;
    }
}