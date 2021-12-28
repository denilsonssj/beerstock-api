package br.com.beerstock.beerstockapi.api.exception;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StockLessThenZeroException extends Exception {

    public StockLessThenZeroException(UUID id, int quantityToDecrement) {  
        super(
            String.format("Beers with %s id informed to decrement is less than minimun stock capacity: %s", id, quantityToDecrement));
    }

}
