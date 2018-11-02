package com.syolab.broker.atlasbroker.atlas;

import com.syolab.broker.atlasbroker.repository.ServiceInstanceRepository;
import com.syolab.broker.atlasbroker.service.HttpComponentsClientHttpRequestFactoryDigestAuth;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.json.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.apache.http.HttpHost;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import io.pivotal.spring.cloud.service.config.PlainTextConfigClient;

import java.io.*;
import java.nio.charset.Charset;

@Component
@Configuration
public class AtlasClient {

    private static final Logger log = LoggerFactory.getLogger(AtlasClient.class);
    private String orgId ="5a9d32f20bd66b47765d52bf";
    private String apiKey ="32b5cf8e-4535-4746-8e8b-b1ede3c6fbfd";
    final String rootEndpoint = "https://cloud.mongodb.com/api/atlas/v1.0";

    @Autowired
    ServiceInstanceRepository instanceRepository;

   // @Autowired
  //  PlainTextConfigClient configClient;


    final RestTemplate restTemplate;

    AtlasClient() {

        HttpHost host = new HttpHost(rootEndpoint);
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials =
                new UsernamePasswordCredentials("sani.chabi-yo", apiKey);

        provider.setCredentials(AuthScope.ANY, credentials);
        CloseableHttpClient client = HttpClientBuilder.create().
                setDefaultCredentialsProvider(provider).useSystemProperties().build();
        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactoryDigestAuth(host, client);

        restTemplate = new RestTemplate(requestFactory);
    }


    public String createProject(String projectName, String orgId) throws AtlasException {
        log.info("Creating a new Project: " + projectName);
        String resourceUrl = rootEndpoint + "/groups?pretty=true";
        JSONObject json = new JSONObject();
        json.put("name", projectName);
        json.put("orgId", orgId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity <String> httpEntity = new HttpEntity<String>(json.toString(), headers);
        try{
            ResponseEntity<String>  response = restTemplate.postForEntity(resourceUrl, httpEntity, String.class);
            log.info("Status Code = " + response.getStatusCode());
            log.info("Response: " + new JSONObject(response));
            String body = new JSONObject(response).getString("body");
            log.info("Body: " + body);
            JSONObject jsonObj = new JSONObject(body);
            return jsonObj.getString("id");
        }catch (HttpClientErrorException exception){
            log.error(exception.getStatusText());
            throw new AtlasException(exception.getMessage(),exception);
        }


    }

    public ClusterInfo createCluster(String planId, String instanceId) throws AtlasException{

        log.info("Creating a new Cluster: " + instanceId);
        //Load the Plan definition from the JSON file
        JSONParser parser = new JSONParser();

        try {

            String projectId = createProject("PCF-"+instanceId, orgId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String resourceUrl = rootEndpoint + "/groups/"+ projectId + "/clusters";
            log.info("resourceUrl = " + resourceUrl);


            InputStream in = AtlasClient.class.getResourceAsStream("/plans/".concat(planId).concat(".json"));
            InputStreamReader inr = new InputStreamReader(in, "UTF-8");

            org.json.simple.JSONObject request = ( org.json.simple.JSONObject) parser.parse(inr);

            //InputStream input = configClient.getConfigFile("Prod-plan.json").getInputStream();
           // log.info(StreamUtils.copyToString(input, Charset.defaultCharset()));

           // JSONObject request = (JSONObject) parser.parse( new FileReader("/plans/".concat(planId).concat(".json")));


            request.put("name",instanceId);

            log.info("request : " + request);
            HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(resourceUrl, httpEntity, String.class);
            JSONObject clusterInfo = new JSONObject(response.getBody());
            log.info("clusterInfo: " + clusterInfo);
            String groupId = clusterInfo.getString("groupId");
            String dashboardUrl = new String("https://cloud.mongodb.com/api/atlas/v1.0/groups/")
                     .concat(groupId);
            String clusterUrl = rootEndpoint.concat("/groups/").concat(groupId).concat("/clusters/").concat(instanceId);
            return new  ClusterInfo(null,clusterUrl,dashboardUrl);
        }catch (ParseException e){
            e.printStackTrace();
            System.out.println("Unable to load the plan details: " + planId);
            throw new AtlasException("Unable to load the plan definition:" + planId, e);
        }
        catch (IOException e){
            e.printStackTrace();
            System.out.println("Unable to load the plan details: " + planId);
            throw new AtlasException("Unable to load the plan definition:" + planId, e);
        }
        catch(HttpClientErrorException exception){
            exception.printStackTrace();
            throw new AtlasException(exception.getMessage(),exception);
        }

    }

    public void deleteProject(String projectLink) throws AtlasException{
        log.info("Deleting a  Project: " + projectLink);
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> httpEntity = new HttpEntity<String>("", headers);
            ResponseEntity<String> response = restTemplate.exchange(projectLink, HttpMethod.DELETE, httpEntity, String.class,1);
        }catch (HttpClientErrorException exception){
            log.error(exception.getStatusText());
            throw new AtlasException(exception.getMessage(),exception);
        }
    }

    public void deleteCluster(String link) throws AtlasException{
        log.info("deleteCluster : " + link);
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> httpEntity = new HttpEntity<String>("", headers);
            ResponseEntity<String> response = restTemplate.exchange(link, HttpMethod.DELETE, httpEntity, String.class,1);
            log.info("response: " + response);
        }catch (HttpClientErrorException exception){
            log.error(exception.getStatusText());
            throw new AtlasException(exception.getMessage(),exception);
        }
    }


    public ClusterState getClusterStatus(JSONObject clusterData) throws AtlasException{
        log.debug("getClusterStatus : " + clusterData);
        try{
            if(clusterData == null)
                return ClusterState.NOTFOUND;
            String body = clusterData.getString("body");
            log.debug("Body: " + body);
            JSONObject jsonObj = new JSONObject(body);
            ClusterState state  = ClusterState.valueOf(jsonObj.getString("stateName"));
            log.info("stateName: " + state);
            return state;
        }catch (Exception exception){
            throw new AtlasException(exception.getMessage(),exception);
        }
    }

    public JSONObject getCluster(String link) throws AtlasException{
        log.info("getCluster : " + link);
        try{
            ResponseEntity<String>  response = restTemplate.getForEntity(link, String.class);
            log.info("Status Code = " + response.getStatusCode());
            log.debug("Response: " + new JSONObject(response));
            return new JSONObject(response);
        }catch (HttpClientErrorException exception){
            log.error("Code:"+exception.getStatusCode());
            if(exception.getStatusCode() == HttpStatus.NOT_FOUND)
                return null;
            throw new AtlasException(exception.getMessage(),exception);
        }
    }

    public String getClusterURI(JSONObject clusterData){

        log.debug("getClusterInfo : " + clusterData);
        String body = clusterData.getString("body");
        return new JSONObject(body).getString("mongoURIWithOptions");

    }

    public String getProjectId(JSONObject clusterData){

        log.info("getProjectId : " + clusterData);
        String body = clusterData.getString("body");
        return new JSONObject(body).getString("groupId");

    }

    public boolean createUser(String projectId, String username, String password) throws AtlasException{

        log.info("Creating a new  Database User: " + projectId + "," +  username + "," + password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String resourceUrl = rootEndpoint.concat("/groups/").concat(projectId).concat("/databaseUsers");
        log.info("resourceUrl = " + resourceUrl);

        JSONObject request = new JSONObject();
        JSONArray roles = new JSONArray();
        request.put("username",username);
        request.put("password",password);
        request.put("groupId",projectId);

        //RoleUserAdmin,
        //RoleDBAdmin
        //RoleReadWrite,

        JSONObject dbRole = new JSONObject();
        dbRole.put("databaseName","admin");
        dbRole.put("roleName","atlasAdmin");

        roles.put(dbRole);
        request.put("roles",roles);

        request.put("databaseName","admin");


        log.debug("request:" +  request);
        try {
            HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(resourceUrl, httpEntity, String.class);
            log.info("Status Code = " + response.getStatusCode());
            log.info("Response: " + response);
            return true;
        }catch(HttpClientErrorException exception){
            exception.printStackTrace();
            throw new AtlasException(exception.getMessage(),exception);
        }

    }

    public void deleteUser(String projectId, String username) throws AtlasException{

        log.info("Deleting a DB User: " + projectId + "," +  username);
        String resourceUrl = rootEndpoint.concat("/groups/").concat(projectId).concat("/databaseUsers/admin/").concat(username);
        log.info("resourceUrl = " + resourceUrl);

        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> httpEntity = new HttpEntity<String>("", headers);
            //TODO Can we just pass a null entity?
            ResponseEntity<String> response = restTemplate.exchange(resourceUrl, HttpMethod.DELETE, httpEntity, String.class,1);
        }catch (HttpClientErrorException exception){
            log.error(exception.getStatusText());
            throw new AtlasException(exception.getMessage(),exception);
        }

    }

    public boolean createAtlasUser(String projectId, String username, String password) throws AtlasException{

        log.info("Creating a new User: " + projectId + "," +  username + "," + password);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String resourceUrl = rootEndpoint + "/users";
        log.info("resourceUrl = " + resourceUrl);

        JSONObject request = new JSONObject();
        JSONArray roles = new JSONArray();
        request.put("username",username);
        request.put("password","Zy43tt36!");
        request.put("emailAddress","sani.chabi-yo@mongdb.com");
        request.put("mobileNumber","2125550198");
        request.put("firstName","Jon");
        request.put("lastName","Doe");
        request.put("country","US");


        JSONObject orgRole = new JSONObject();
        orgRole.put("orgId",orgId);
        orgRole.put("roleName","ORG_MEMBER");


        JSONObject projectRole = new JSONObject();
        projectRole.put("groupId",projectId);
        projectRole.put("roleName","GROUP_OWNER");

        roles.put(orgRole);
        roles.put(projectRole);

        request.put("roles",roles);

        log.info("request:" +  request);
        try {
            HttpEntity<String> httpEntity = new HttpEntity<String>(request.toString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(resourceUrl, httpEntity, String.class);
            log.info("Status Code = " + response.getStatusCode());
            log.info("Response: " + response);
            //Construct Dashboard URL
            return true;
        }catch(HttpClientErrorException exception){
            exception.printStackTrace();
            throw new AtlasException(exception.getMessage(),exception);
        }

    }

}
