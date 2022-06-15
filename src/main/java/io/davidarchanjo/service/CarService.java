package io.davidarchanjo.service;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import io.davidarchanjo.model.Car;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@CacheConfig(cacheNames = {"CARS"})
@RequiredArgsConstructor
public class CarService {
    
    private static final String COLLECTION = "car";

    private final MongoTemplate template;

    @Cacheable(unless = "#result == null")
    public Car getCarByModelName(String modelName) {
        log.info("Executing getCarByModelName for model:{} - Cache Miss!", modelName);
        slowDown();
        Query query = new Query(Criteria.where("model").is(modelName));
        return template.findOne(query, Car.class, COLLECTION);
    }

    @CachePut(key = "#car.model", condition = "#car.topSpeed > 0", unless = "#result == null")
    public Car createCar(Car car) {
        log.info("Executing createCar, model:{}", car.getModel());
        try {
            template.insert(car, COLLECTION);
        } catch (DuplicateKeyException dke) {
        }
        return template.findOne(
            new Query(Criteria.where("model").is(car.getModel())),
            Car.class, COLLECTION);//has id
    }

    @CacheEvict(key = "#car.model")
    public void updateCar(Car car) {
        log.info("Executing updateCar, model:{} topSpeed: {} - Cache Evict!", car.getModel(), car.getTopSpeed());
        try {
            template.save(car, COLLECTION);
        } catch (DuplicateKeyException ex) {
        }
    }

    @CacheEvict(key = "#car.model")
    public void removeCar(Car car) {
        log.info("Executing removeCar, model:{} - Cache Evict!", car.getModel());
        template.remove(car, COLLECTION);
    }

    @Caching(evict = {
        @CacheEvict(value = "CARS", allEntries = true),
        @CacheEvict(value = "SECOND_CACHE", allEntries = true)
    })
    public void clearAllCaches() {
        log.info("Cleared all caches");
    }

    @SneakyThrows
    private void slowDown() {
        Thread.sleep(5000);
    }

}
