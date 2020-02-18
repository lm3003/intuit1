package com.intuit1.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit1.demo.models.RestaurantInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CityService {

    @Value("${numberOfRestaurants}")
    private String numberOfRestaurants;

    Logger logger = LoggerFactory.getLogger(CityService.class);

    public List<RestaurantInfo> getRestaurantData(String radius, String location) {
        List<RestaurantInfo> restaurantInfoList = new ArrayList<>();
        try{
            int radiusInMeters = 0;
            if(isInteger(radius)){
                radiusInMeters = Integer.parseInt(radius);
            }else{
                return null;
            }

            logger.debug("Given location: {}", location);
            String[] multiPartLocation = location.split(" ");
            location = String.join("-", Arrays.asList(multiPartLocation));
            logger.debug("New location: {}", location);

            //setting up the URL with parameters.
            logger.info("Sending the request to the Yelp API for top "+numberOfRestaurants+" restaurants near "+location+" in approx radius of "+radius+" meters.");
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            httpHeaders.setBearerAuth("1zynhUfpqVLB5mJ81mIHs5SI2v7ZzKmGTDz_HSa6tt8XntpmOtVIPTaxXe46xU1DdkeDozZDpKUyWk6RM3XTq5HSl_wt56sCvFCjKADYSe5eQ0ByK9cnOxHt4VXEXXYx");
            String url = "https://api.yelp.com/v3/businesses/search?term=restaurants&location="+location+"&radius="+radiusInMeters+"&sort_by=review_count&limit=" + numberOfRestaurants;
            HttpEntity requestEntity = new HttpEntity(httpHeaders);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(response.getBody());
            // check response
            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("Request Successful.");
                logger.debug(response.getBody());
            } else {
                logger.info("Request Failed");
            }
            JsonNode jsonArray = jsonNode.get("businesses");
            for(JsonNode node: jsonArray){
                RestaurantInfo restaurantInfo = new RestaurantInfo();
                restaurantInfo.setName(node.get("name").asText());
                restaurantInfo.setReviewCount(node.get("review_count").asText());
                restaurantInfo.setRating(node.get("rating").asText());
                String loc = "";
                JsonNode dispAddList = node.get("location").get("display_address");
                for(JsonNode node1: dispAddList){
                    loc += node1.asText() + " ";
                }
                restaurantInfo.setLocation(loc.trim());
                restaurantInfoList.add(restaurantInfo);
            }

        }catch (RuntimeException ex) {
            ex.printStackTrace();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return restaurantInfoList;
    }

    private boolean isInteger(String radius) {
        return radius.chars().allMatch(c -> Character.isDigit(c));
    }
}
