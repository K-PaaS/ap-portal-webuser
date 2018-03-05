package org.openpaas.paasta.portal.web.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 공통 기능을 구현한 서비스 클래스이다.
 *
 * @author 김도준
 * @version 1.0
 * @since 2016.07.28 최초작성
 */
@Service
public class CommonService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonService.class);
    private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    private static final String CF_AUTHORIZATION_HEADER_KEY = "cf-Authorization";


    @Value("${paasta.portal.api.url}")
    private String apiUrl;
    @Value("${paasta.portal.api.authorization.base64}")
    private String base64Authorization;

   @Autowired
   private RestTemplate restTemplate;

//    @Autowired
//    public CommonService(@Qualifier("loadBalancedRestTemplate") RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }


    /**
     * REST TEMPLATE 처리
     *
     * @param reqUrl     the req url
     * @param httpMethod the http method
     * @param obj        the obj
     * @param reqToken   the req token
     * @return map map
     */
    public Map<String, Object> procRestTemplate(String reqUrl, HttpMethod httpMethod, Object obj, String reqToken) {

        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.add(AUTHORIZATION_HEADER_KEY, base64Authorization);
        if (null != reqToken && !"".equals(reqToken)) reqHeaders.add(CF_AUTHORIZATION_HEADER_KEY, reqToken);

        HttpEntity<Object> reqEntity = new HttpEntity<>(obj, reqHeaders);

        ResponseEntity<Map> resEntity = restTemplate.exchange(apiUrl + reqUrl, httpMethod, reqEntity, Map.class);
        Map<String, Object> resultMap = resEntity.getBody();

        LOGGER.info("procRestTemplate reqUrl :: {} || resultMap :: {}", reqUrl, resultMap.toString());
        return resultMap;
    }


    /**
     * REST TEMPLATE 처리
     *
     * @param reqUrl   the req url
     * @param file     the file
     * @param reqToken the req token
     * @return map map
     * @throws Exception the exception
     */
    public Map<String, Object> procRestTemplate(String reqUrl, MultipartFile file, String reqToken) throws Exception {
        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.add(AUTHORIZATION_HEADER_KEY, base64Authorization);
        if (null != reqToken && !"".equals(reqToken)) reqHeaders.add(CF_AUTHORIZATION_HEADER_KEY, reqToken);

        final MultiValueMap<String, Object> data = new LinkedMultiValueMap<String, Object>();

        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() throws IllegalStateException {
                return file.getOriginalFilename();
            }
        };

        data.add("file", resource);
        final HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(data, reqHeaders);
        final ResponseEntity<Map> resEntity = restTemplate.exchange(apiUrl + reqUrl, HttpMethod.POST, requestEntity, Map.class);

        Map resultMap = resEntity.getBody();

        LOGGER.info("procRestTemplate resultMap :: {}", resultMap.toString());
        return resultMap;
    }


    /**
     * REST TEMPLATE 처리
     *
     * @param <T>          the type parameter
     * @param reqUrl       the req url
     * @param httpMethod   the http method
     * @param obj          the obj
     * @param reqToken     the req token
     * @param responseType the response type
     * @return response entity
     */
    public <T> ResponseEntity<T> procRestTemplate(String reqUrl, HttpMethod httpMethod, Object obj, String reqToken, Class<T> responseType) {

        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.add(AUTHORIZATION_HEADER_KEY, base64Authorization);

        if (null != reqToken && !"".equals(reqToken)) reqHeaders.add(CF_AUTHORIZATION_HEADER_KEY, reqToken);

        HttpEntity<Object> reqEntity = new HttpEntity<>(obj, reqHeaders);
        ResponseEntity<T> result = restTemplate.exchange(apiUrl + reqUrl, httpMethod, reqEntity, responseType);

        //LOGGER.info("procRestTemplate reqUrl :: {} || resultBody :: {}", reqUrl, result.getBody().toString());

        return result;
    }


    /**
     * REST TEMPLATE 처리
     *
     * @param <T>          the type parameter
     * @param reqUrl       the req url
     * @param httpMethod   the http method
     * @param obj          the obj
     * @param reqToken     the req token
     * @param responseType the response type
     * @return response entity
     */
    public <T> ResponseEntity<T> procRestTemplateV2(String reqUrl, HttpMethod httpMethod, Object obj, String reqToken, Class<T> responseType) {

        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.add(AUTHORIZATION_HEADER_KEY, base64Authorization);

        if (null != reqToken && !"".equals(reqToken)) reqHeaders.add(CF_AUTHORIZATION_HEADER_KEY, reqToken);

        HttpEntity<Object> reqEntity = new HttpEntity<>(obj, reqHeaders);
        ResponseEntity<T> result = restTemplate.exchange("http://PORTAL-API-V2" + reqUrl, httpMethod, reqEntity, responseType);

        //LOGGER.info("procRestTemplate reqUrl :: {} || resultBody :: {}", reqUrl, result.getBody().toString());

        return result;
    }


    /**
     * USER ID를 조회한다.
     *
     * @return user id
     */
    public String getUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }


    /**
     * USER ID를 설정한다.
     *
     * @param classObject the class object
     * @return the object
     * @throws Exception the exception
     */
    public Object setUserId(Object classObject) throws Exception {
        String methodName = "setUserId";
        Method method = classObject.getClass().getMethod(methodName, String.class);
        Object[] paramObject = new Object[]{this.getUserId()};

        method.invoke(classObject, paramObject);

        return classObject;
    }

    /**
     * REST TEMPLATE 처리
     *
     * @param reqUrl     the req url
     * @param httpMethod the http method
     * @param obj        the obj
     * @param reqToken   the req token
     * @return map map
     */
    public Map<String, Object> common(String reqUrl, HttpMethod httpMethod, Object obj, String reqToken) {

        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.add(AUTHORIZATION_HEADER_KEY, base64Authorization);
        if (null != reqToken && !"".equals(reqToken)) reqHeaders.add(CF_AUTHORIZATION_HEADER_KEY, reqToken);

        HttpEntity<Object> reqEntity = new HttpEntity<>(null, reqHeaders);

        ResponseEntity<Map> resEntity = restTemplate.exchange("PORTALCOMMONAPI" + reqUrl, httpMethod, reqEntity, Map.class);
        LOGGER.info("###################### " + resEntity.getBody().toString()  );
        try {
            Map<String, Object> resultMap = resEntity.getBody();
        }catch (Exception e){
            e.printStackTrace();
        }
        //LOGGER.info("procRestTemplate reqUrl :: {} || resultMap :: {}", reqUrl, resultMap.toString());
        return null;
    }
}

