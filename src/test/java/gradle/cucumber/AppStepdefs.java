package gradle.cucumber;

//import cucumber.api.java.en.And;
//import cucumber.api.java.en.Then;
//import cucumber.api.java.en.When;
import org.apache.commons.collections.map.HashedMap;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * PaaS-TA의 Application에 대한 동작을 테스트한다.
 *
 * @author 조민구
 * @version 1.0
 * @since 2016-08-24
 */
public class AppStepdefs extends AbstractDefs {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppStepdefs.class);

    private String testApp = null;
    private String testOrg = null;
    private String testSpace = null;
    private String testAppGuid = null;

    /**
     * feature에 정의된 ORG, SPACE의 APP을 테스트한다.
     *
     * @param application 어플리케이션 이름
     * @param org 조직 이름
     * @param space 공간 이름
     * @throws Throwable
     */
//    @Then("^i will test \"(.+)\" in org \"(.+)\", space \"(.+)\"")
    public void i_will_test_application_in_org_space(String application, String org, String space) throws Throwable {
        LOGGER.info(">> i will test {} in org {}, space {}", application, org, space);

        testApp = application;
        testOrg = org;
        testSpace = space;
        testAppGuid = getApplicationGuidByApllicationName(testOrg, testSpace, testApp);

        // TEST
        assertThat(lastResponse.getStatusCode().value(), is(200));

    }

    /**
     * feature에 정의된 동작을 수행한다.
     * start
     * stop
     * restage
     *
     * @param action 동작이름
     * @param url API Url
     * @throws Throwable
     */
//    @When("^\"(.+)\" application with url \"(.+)\"")
    public void action_application_with_url(String action, String url) throws Throwable {
        LOGGER.info(">> '{}' application with url '{}'", action, url);


        // API URL

        // HEADER
        HttpHeaders reqHeaders = new HttpHeaders();

        // BODY
        Map<String, Object> body = new HashedMap();
        body.put("guid", testAppGuid);
        body.put("name", testApp);
        body.put("orgName", testOrg);
        body.put("spaceName", testSpace);

        // REST TEMPLATE
        executePost(url, reqHeaders, body, Boolean.class);

        // TEST
        assertThat((Boolean)lastResponse.getBody(), is(true));
        assertThat(lastResponse.getStatusCode().value(), is(200));

    }

    /**
     * 이전 APP 동작수행 후에 기록된 APP 이벤트를 확인한다.
     *
     * @param event 이벤트 이름
     * @throws Throwable
     */
//    @Then("^\"(.+)\" event occurs")
    public void event_occurs_in_app(String event) throws Throwable {
        LOGGER.info(">> '{}' event occurs", event);

        // API URL
        String url = "/app/getAppEvents";

        // HEADER
        HttpHeaders reqHeaders = new HttpHeaders();

        // BODY
        Map<String, Object> body = new HashedMap();
        body.put("guid", testAppGuid);

        // REST TEMPLATE
        executePost(url, reqHeaders, body, String.class);

        String resBody = (String) lastResponse.getBody();
        JSONObject jsonBody = new JSONObject(resBody);

        // TEST
        assertThat(lastResponse.getStatusCode().value(), is(200));
        assertThat(jsonBody.getJSONArray("resources").getJSONObject(0).getJSONObject("entity").get("type"), is(event));

    }

    /**
     * 이전 APP 동작 후의 APP 상태를 확인한다.
     *
     * @param state 어플리케이션의 상태
     * @throws Throwable
     */
//    @And("^It is the \"(.+)\" status")
    public void It_is_the_state_status(String state) throws Throwable {
        String resBody = (String) lastResponse.getBody();
        JSONObject jsonBody = new JSONObject(resBody);

        // TEST
        assertThat(jsonBody.getJSONArray("resources").getJSONObject(0).getJSONObject("entity").getJSONObject("metadata").getJSONObject("request").get("state"), is(state));
    }

}
