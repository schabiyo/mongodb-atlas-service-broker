package com.syolab.broker.atlasbroker.atlas;

public class ClusterInfo {

    private String uri;
    private String projectUrl;
    private String dashboardUrl;

    public ClusterInfo(String uri, String projectUrl, String dashboardUrl) {
        this.uri = uri;
        this.projectUrl = projectUrl;
        this.dashboardUrl = dashboardUrl;
    }


    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getProjectUrl() {
        return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
        this.projectUrl = projectUrl;
    }

    public String getDashboardUrl() {
        return dashboardUrl;
    }

    public void setDashboardUrl(String dashboardUrl) {
        this.dashboardUrl = dashboardUrl;
    }
}
