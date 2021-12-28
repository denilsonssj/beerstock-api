package br.com.beerstock.beerstockapi.domain.services;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import br.com.beerstock.beerstockapi.api.dtos.BeerDTO;
import br.com.beerstock.beerstockapi.api.exception.BeerAlreadyRegisteredException;
import br.com.beerstock.beerstockapi.api.exception.BeerNotFoundException;
import br.com.beerstock.beerstockapi.api.exception.BeerStockExceededException;
import br.com.beerstock.beerstockapi.api.mappers.BeerMapper;
import br.com.beerstock.beerstockapi.api.repository.BeerRepository;
import br.com.beerstock.beerstockapi.common.builder.BeerDTOBuilder;
import br.com.beerstock.beerstockapi.domain.entity.Beer;

import static br.com.beerstock.beerstockapi.common.builder.BeerDTOBuilder.generateInvalidBeerUUID;;

@ExtendWith(MockitoExtension.class)
public class BeerServiceTest {

    private final UUID INVALID_BEER_ID = generateInvalidBeerUUID();

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
    @DisplayName("When already registered beer informed then an exception should be thrown")
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

    @Test
    @DisplayName("When valid beer name is given then return a beer")
    void whenValidBeerNameIsGivenThenReturnABeer() throws BeerNotFoundException {
        // given
        BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDto();
        Beer expectedFoundBeer = this.beerMapper.toBeer(expectedFoundBeerDTO);

        // when
        when(this.beerRepository.findByName(expectedFoundBeer.getName()))
            .thenReturn(Optional.of(expectedFoundBeer));
        // then
        BeerDTO foundBeerDTO = this.beerService.findByName(expectedFoundBeer.getName());

        assertThat(foundBeerDTO, is(equalTo(expectedFoundBeerDTO)));
    }

    @Test
    @DisplayName("When not registered beer name is given then throw an exception")
    void whenNotRegisteredBeerNameIsGivenThenThrowAnException() {
         // given
         BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDto();
 
         // when
         when(this.beerRepository.findByName(expectedFoundBeerDTO.getName()))
             .thenReturn(Optional.empty());
         // then
         assertThrows(BeerNotFoundException.class,
            () -> this.beerService.findByName(expectedFoundBeerDTO.getName()));
    }

    @Test
    @DisplayName("When list beer is called then return a list of beers")
    void WhenListBeerIsCalledThenReturnAListOfBeers() {
        // given
        BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDto();
        Beer expectedFoundBeer = this.beerMapper.toBeer(expectedFoundBeerDTO);

        // when
        when(this.beerRepository.findAll())
            .thenReturn(Collections.singletonList(expectedFoundBeer));
        // then
        List<BeerDTO> foundListBeersDTO = this.beerService.findAll();

        assertThat(foundListBeersDTO, is(not(empty())));
        assertThat(foundListBeersDTO.get(0), is(equalTo(expectedFoundBeerDTO)));
    }

    @Test
    @DisplayName("When list beer is called then return an empty list of beers")
    void WhenListBeerIsCalledThenReturnAnEmptyListOfBeers() {
        // when
        when(this.beerRepository.findAll())
            .thenReturn(Collections.EMPTY_LIST);

        // then
        List<BeerDTO> foundListBeersDTO = this.beerService.findAll();

        assertThat(foundListBeersDTO, is(empty()));
    }

    @Test
    @DisplayName("When exclusion is called with valid Id then a beer should be deleted")
    public void whenExclusionIsCalledWithValidIdThenABeerShouldBeDeleted() throws Exception {
        // given
        BeerDTO expectedDeletedBeerDTO = BeerDTOBuilder.builder().build().toBeerDto();
        Beer expectedDeletedBeer = this.beerMapper.toBeer(expectedDeletedBeerDTO);

        // when
        when(this.beerRepository.findById(expectedDeletedBeer.getId()))
            .thenReturn(Optional.of(expectedDeletedBeer));
        doNothing().when(this.beerRepository)
            .deleteById(expectedDeletedBeerDTO.getId());

        // then
        this.beerService.deleteById(expectedDeletedBeerDTO.getId());
        verify(this.beerRepository, times(1)).findById(expectedDeletedBeerDTO.getId());
        verify(this.beerRepository, times(1)).deleteById(expectedDeletedBeerDTO.getId());
    }

    @Test
    @DisplayName("When exclusion is called with valid Id then a beer should be deleted")
    public void whenExclusionIsCalledWithAnInvalidIdThenThrowAnException() throws BeerNotFoundException {
        //when 
        when(this.beerRepository.findById(this.INVALID_BEER_ID))
            .thenReturn(Optional.empty());
        
        // then
        assertThrows(BeerNotFoundException.class,
            () -> this.beerService.deleteById(this.INVALID_BEER_ID));
    }

    @Test
    @DisplayName("When increment is called then increment beer stock")
    void whenIncrementIsCalledThenIncrementBeerStock()
        throws BeerNotFoundException, BeerStockExceededException {
        // given
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDto();
        Beer expectedBeer = this.beerMapper.toBeer(expectedBeerDTO);

        // when
        when(this.beerRepository.findById(expectedBeerDTO.getId()))
            .thenReturn(Optional.of(expectedBeer));
        when(this.beerRepository.save(expectedBeer)).thenReturn(expectedBeer);

        final int QUANTITY_TO_INCREMENT = 10;
        int expectedQuantityAfterIncrement = expectedBeer.getQuantity()
            + QUANTITY_TO_INCREMENT;
        BeerDTO incrementedBeerDTO = this.beerService
            .increment(expectedBeerDTO.getId(), QUANTITY_TO_INCREMENT);
        
        // then 
        assertThat(
            expectedQuantityAfterIncrement,
            equalTo(incrementedBeerDTO.getQuantity()));
        assertThat(
            expectedQuantityAfterIncrement,
            lessThan(incrementedBeerDTO.getMax()));
    }

    @Test
    @DisplayName("When increment is greather than max then throw exception")
    void whenIncrementIsGreatherThanMaxThenThrowException() {
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDto();
        Beer expectedBeer = beerMapper.toBeer(expectedBeerDTO);

        when(this.beerRepository.findById(expectedBeerDTO.getId()))
            .thenReturn(Optional.of(expectedBeer));
            final int QUANTITY_TO_INCREMENT = 80;
        assertThrows(BeerStockExceededException.class, () ->
            this.beerService.increment(
                expectedBeerDTO.getId(),
                QUANTITY_TO_INCREMENT));
    }

    @Test
    @DisplayName("When increment after sum is greather than max then throw exception")
    void whenIncrementAfterSumIsGreatherThanMaxThenThrowException() {
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDto();
        Beer expectedBeer = beerMapper.toBeer(expectedBeerDTO);

        when(this.beerRepository.findById(expectedBeerDTO.getId()))
            .thenReturn(Optional.of(expectedBeer));
            final int QUANTITY_TO_INCREMENT = 45;
        assertThrows(BeerStockExceededException.class, () ->
            this.beerService.increment(
                expectedBeerDTO.getId(),
                QUANTITY_TO_INCREMENT));
    }

    @Test
    @DisplayName("When increment is called with invalid id then throw exception")
    void whenIncrementIsCalledWithInvalidIdThenThrowException() {
        final int QUANTITY_TO_INCREMENT = 10;

        //when
        when(this.beerRepository.findById(INVALID_BEER_ID))
            .thenReturn(Optional.empty());

        //then 
        assertThrows(BeerNotFoundException.class, () ->
            this.beerService.increment(INVALID_BEER_ID, QUANTITY_TO_INCREMENT));
    }

}
