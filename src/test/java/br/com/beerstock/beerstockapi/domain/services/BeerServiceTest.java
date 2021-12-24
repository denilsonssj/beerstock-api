package br.com.beerstock.beerstockapi.domain.services;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import br.com.beerstock.beerstockapi.api.dtos.BeerDTO;
import br.com.beerstock.beerstockapi.api.exception.BeerAlreadyRegisteredException;
import br.com.beerstock.beerstockapi.api.mappers.BeerMapper;
import br.com.beerstock.beerstockapi.api.repository.BeerRepository;
import br.com.beerstock.beerstockapi.common.builder.BeerDTOBuilder;
import br.com.beerstock.beerstockapi.domain.entity.Beer;

@ExtendWith(MockitoExtension.class)
public class BeerServiceTest {

    private static final long INVALID_BEER_ID = 1L;

    @Mock
    private BeerRepository beerRepository;

    private final BeerMapper beerMapper = BeerMapper.INSTANCE;

    @InjectMocks
    private BeerService beerService;

    @Test
    void whenBeerInformedThenItShouldBeCreated() throws BeerAlreadyRegisteredException {
        // given
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDto();
        Beer expectedSavedBeer = this.beerMapper.toBeer(expectedBeerDTO);

        // when
        when(this.beerRepository
            .findByName(expectedBeerDTO.getName()))
            .thenReturn(Optional.empty());

        when(beerRepository
            .save(expectedSavedBeer))
            .thenReturn(expectedSavedBeer);

        // then
        BeerDTO createdBeerDTO = this.beerService.createBeer(expectedBeerDTO);

        assertThat(createdBeerDTO.getId(), is(equalTo(expectedBeerDTO.getId())));
        assertThat(createdBeerDTO.getName(), is(equalTo(expectedBeerDTO.getName())));
        assertThat(createdBeerDTO.getQuantity(), is(equalTo(expectedBeerDTO.getQuantity())));
    }

    @Test
    void whenAlreadyRegisteredBeerInformedThenAnExceptionShouldBeThrown()
        throws BeerAlreadyRegisteredException {
        // given
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDto();
        Beer duplicatedBeer = this.beerMapper.toBeer(expectedBeerDTO);

        //when
        when(this.beerRepository
            .findByName(expectedBeerDTO.getName()))
            .thenReturn(Optional.of(duplicatedBeer));
        
        //then
        assertThrows(BeerAlreadyRegisteredException.class,
            () -> this.beerService.createBeer(expectedBeerDTO));
    }

}
