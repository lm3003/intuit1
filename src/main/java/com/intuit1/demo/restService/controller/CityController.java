package com.intuit1.demo.restService.controller;


import com.intuit1.demo.models.RestaurantInfo;
import com.intuit1.demo.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CityController {

    @Autowired
    private CityService cityService;

    @GetMapping("/top10")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getInfo(@RequestParam(value = "radius", defaultValue = "0") String radius,
                                                       @RequestParam(value = "location", defaultValue = "Sunnyvale") String location){
        List<RestaurantInfo> restaurantData = cityService.getRestaurantData(radius, location);
        return restaurantData == null ? ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request") : ResponseEntity.ok(restaurantData);
    }
}
