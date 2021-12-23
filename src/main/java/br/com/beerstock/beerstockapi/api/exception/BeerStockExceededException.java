package br.com.beerstock.beerstockapi.api.exception;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BeerStockExceededException extends Exception {
    
    public BeerStockExceededException(UUID id, int quantityToIncrement) {  
        super(
            String.format("Beers with %s id to increment informed exceeds the max stock capacity: %s", id, quantityToIncrement));
    }

}
