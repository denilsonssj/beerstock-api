package br.com.beerstock.beerstockapi.api.exception;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BeerNotFoundException extends Exception {
    
    public BeerNotFoundException(String beerName) {
        super(String.format(
            "Beer with nem %s not found in the system.", beerName));
    }

    public BeerNotFoundException(UUID id) {
        super(String.format("Beer with id %s not found in the system.", id));
    }

}
