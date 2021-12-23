package br.com.beerstock.beerstockapi.domain.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.beerstock.beerstockapi.api.dtos.BeerDTO;
import br.com.beerstock.beerstockapi.api.exception.BeerAlreadyRegisteredException;
import br.com.beerstock.beerstockapi.api.exception.BeerNotFoundException;
import br.com.beerstock.beerstockapi.api.exception.BeerStockExceededException;
import br.com.beerstock.beerstockapi.api.mappers.BeerMapper;
import br.com.beerstock.beerstockapi.api.repository.BeerRepository;
import br.com.beerstock.beerstockapi.domain.entity.Beer;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper = BeerMapper.INSTANCE;

    private Beer verifyIfExistsById(UUID id) throws BeerNotFoundException {
       return this.beerRepository.findById(id)
        .orElseThrow(() -> new BeerNotFoundException(id));
    }

    private void verifyIfIsAlreadyRegistered(String name) throws BeerAlreadyRegisteredException {
        Optional<Beer> savedBeer = this.beerRepository.findByName(name);
        if (savedBeer.isPresent()) {
            throw new BeerAlreadyRegisteredException(name);
        }
    }

    public BeerDTO createBeer(BeerDTO beerDTO) throws BeerAlreadyRegisteredException {
        verifyIfIsAlreadyRegistered(beerDTO.getName());
        Beer beer = this.beerMapper.toBeer(beerDTO);
        Beer savedBeer = this.beerRepository.save(beer);
        return this.beerMapper.toBeerDTO(savedBeer);
    }

    public BeerDTO findByName(String name) throws BeerNotFoundException {
        Beer beer = this.beerRepository.findByName(name)
            .orElseThrow(() -> new BeerNotFoundException(name));
        return this.beerMapper.toBeerDTO(beer);
    }

    public List<BeerDTO> findAll() {
        return this.beerRepository.findAll()
            .stream()
            .map(this.beerMapper::toBeerDTO)
            .collect(Collectors.toList());
    }

    public BeerDTO increment(UUID id, int quantityToIncrement) throws BeerNotFoundException, BeerStockExceededException {
        Beer beerToIncrementStock = verifyIfExistsById(id);
        int quantityAfterIncrement = quantityToIncrement + beerToIncrementStock.getQuantity();
        if (quantityAfterIncrement <= beerToIncrementStock.getMax()) {
            beerToIncrementStock.setQuantity(quantityAfterIncrement);
            Beer incrementBeerStock = this.beerRepository.save(beerToIncrementStock);
            return this.beerMapper.toBeerDTO(incrementBeerStock);
        }
        throw new BeerStockExceededException(id, quantityToIncrement);
    }

    public void deleteById(UUID id) throws BeerNotFoundException {
        verifyIfExistsById(id);
        this.beerRepository.deleteById(id);
    }

}
