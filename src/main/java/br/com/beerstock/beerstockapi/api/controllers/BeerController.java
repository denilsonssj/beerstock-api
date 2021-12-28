package br.com.beerstock.beerstockapi.api.controllers;

import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;

import br.com.beerstock.beerstockapi.api.dtos.BeerDTO;
import br.com.beerstock.beerstockapi.api.dtos.QuantityDTO;
import br.com.beerstock.beerstockapi.api.exception.BeerAlreadyRegisteredException;
import br.com.beerstock.beerstockapi.api.exception.BeerNotFoundException;
import br.com.beerstock.beerstockapi.api.exception.BeerStockExceededException;
import br.com.beerstock.beerstockapi.api.exception.StockLessThenZeroException;
import br.com.beerstock.beerstockapi.domain.services.BeerService;

@RestController
@RequestMapping("/api/v1/beers")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BeerController {

    private final BeerService beerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BeerDTO createBeer(@RequestBody @Valid BeerDTO beerDTO)
        throws BeerAlreadyRegisteredException {
        return this.beerService.createBeer(beerDTO);
    }

    @GetMapping("/{name}")
    public BeerDTO findByName(@PathVariable String name) throws BeerNotFoundException {
        return this.beerService.findByName(name);
    }

    @GetMapping
    public List<BeerDTO> findAll() {
        return this.beerService.findAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
        public void deleteById(@PathVariable UUID id) throws BeerNotFoundException {
            this.beerService.deleteById(id);
    }

    @PatchMapping("/increment/{id}")
    public BeerDTO increment(
        @PathVariable UUID id,
        @RequestBody
        @Valid QuantityDTO quantityDTO)
        throws BeerNotFoundException, BeerStockExceededException {
        return this.beerService.increment(id, quantityDTO.getQuantity());
    }

    @PatchMapping("/decrement/{id}")
    public BeerDTO decrement(
        @PathVariable UUID id,
        @RequestBody
        @Valid QuantityDTO quantityDTO)
        throws BeerNotFoundException, StockLessThenZeroException {
        return this.beerService.decrement(id, quantityDTO.getQuantity());
    }

}
