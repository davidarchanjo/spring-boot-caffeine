package io.davidarchanjo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import com.github.benmanes.caffeine.cache.Cache;

import io.davidarchanjo.model.Car;
import io.davidarchanjo.service.CarService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableCaching
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(CarService service, CacheManager cacheManager) {
        return args -> {
            log.info("Using cache Manager {}", cacheManager.getClass().getSimpleName());
    
            Car piper = Car.builder().model("Piper PA-31-300").topSpeed(195).build();
            Car cessna = Car.builder().model("Cessna 650").topSpeed(478).build();
            Car aerostar = Car.builder().model("Aerostar PA-602P").topSpeed(262).build();
    
            //store in cache upon creation
            service.createCar(cessna);
            service.createCar(aerostar);
            service.createCar(piper);
    
            log.info("Calling getCarByModelName() ...");
            service.getCarByModelName(cessna.getModel()); //hit
            log.info("Calling getCarByModelName() ...");
            service.getCarByModelName(aerostar.getModel());//hit
            log.info("Calling getCarByModelName() ...");
            service.getCarByModelName(piper.getModel());//hit
    
            service.updateCar(piper);//evict
            service.getCarByModelName(piper.getModel());//miss
            service.getCarByModelName(piper.getModel());//hit
    
            service.removeCar(piper);//evict
            service.getCarByModelName(piper.getModel());//miss
    
            service.clearAllCaches();//evict all caches
            service.getCarByModelName(cessna.getModel()); //miss
            service.getCarByModelName(aerostar.getModel());//miss

            Cache nativeCoffeeCache = (Cache) cacheManager.getCache("CARS").getNativeCache();
            log.info("{}", nativeCoffeeCache.stats());
        };
    }
}
