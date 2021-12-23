package br.com.beerstock.beerstockapi.api.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import br.com.beerstock.beerstockapi.api.dtos.BeerDTO;
import br.com.beerstock.beerstockapi.domain.entity.Beer;

@Mapper
public interface BeerMapper {
    
    BeerMapper INSTANCE = Mappers.getMapper(BeerMapper.class);

    Beer toBeer(BeerDTO beerDTO);

    BeerDTO toBeerDTO(Beer beer);

}
