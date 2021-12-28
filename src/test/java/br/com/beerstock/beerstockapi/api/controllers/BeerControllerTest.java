package br.com.beerstock.beerstockapi.api.controllers;

import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.core.Is.is;
import static br.com.beerstock.beerstockapi.common.utils.JsonConversionUtils.asJsonString;

import br.com.beerstock.beerstockapi.api.dtos.BeerDTO;
import br.com.beerstock.beerstockapi.api.dtos.QuantityDTO;
import br.com.beerstock.beerstockapi.api.exception.BeerNotFoundException;
import br.com.beerstock.beerstockapi.common.builder.BeerDTOBuilder;
import br.com.beerstock.beerstockapi.domain.services.BeerService;

import static br.com.beerstock.beerstockapi.common.builder.BeerDTOBuilder.generateValidBeerUUID;
import static br.com.beerstock.beerstockapi.common.builder.BeerDTOBuilder.generateInvalidBeerUUID;

@ExtendWith(MockitoExtension.class)
public class BeerControllerTest {

    private static final String BEER_API_URL_PATH = "/api/v1/beers";
    private static final UUID VALID_BEER_ID = generateValidBeerUUID();
    private static final UUID INVALID_BEER_ID = generateInvalidBeerUUID();
    private static final String BEER_API_SUBPATH_INCREMENT_URL = "/increment";
    private static final String BEER_API_SUBPATH_DECREMENT_URL = "/decrement";

    private MockMvc mockMvc;

    @Mock
    private BeerService beerService;

    @InjectMocks
    private BeerController beerController;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(beerController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
            .build();
    }

    @Test
    @DisplayName("When POST is called then a beer is created")
    void whenPOSTIsCalledThenABeerIsCreated() throws Exception {
        //given
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDto();

        //when
        when(this.beerService.createBeer(beerDTO)).thenReturn(beerDTO);

        mockMvc.perform(post(BEER_API_URL_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(beerDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name", is(beerDTO.getName())))
            .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
            .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())));
    }

    @Test
    @DisplayName("When POST is called without required field then an error is returned")
    void whenPOSTIsCalledWithoutRequiredFieldThenAnErrorIsReturned() throws Exception {
        //given
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDto();
        beerDTO.setBrand(null);

        // then
        mockMvc.perform(post(BEER_API_URL_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(beerDTO)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("When GET is called with valid name then ok status is returned")
    void whenGETIsCalledWithValidNameThenOkStatusIsReturned() throws Exception {
        // given
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDto();

        // when
        when(this.beerService.findByName(beerDTO.getName())).thenReturn(beerDTO);

        // then
        mockMvc.perform(get(BEER_API_URL_PATH + "/" +beerDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is(beerDTO.getName())))
            .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
            .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())));
    }

    @Test
    @DisplayName("When GET is called without registered name then not found status is returned")
    void whenGETIsCalledWithoutRegisteredNameThenNotFoundStatusIsReturned() throws Exception {
        // given
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDto();

        // when
        when(this.beerService.findByName(beerDTO.getName())).thenThrow(BeerNotFoundException.class);

        // then
        mockMvc.perform(get(BEER_API_URL_PATH + "/" +beerDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("When GET list with beers is called then Ok status is returned")
    void whenGETListWithBeersIsCalledThenOkStatusIsReturned() throws Exception {
        // given
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDto();

        // when
        when(this.beerService.findAll())
            .thenReturn(Collections.singletonList(beerDTO));

        // then
        mockMvc.perform(get(BEER_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name", is(beerDTO.getName())))
            .andExpect(jsonPath("$[0].brand", is(beerDTO.getBrand())))
            .andExpect(jsonPath("$[0].type", is(beerDTO.getType().toString())));
    }

    @Test
    @DisplayName("When GET list without beers is called then Ok status is returned")
    void whenGETListWithoutBeersIsCalledThenOkStatusIsReturned() throws Exception {
        // given
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDto();

        // when
        when(this.beerService.findAll())
            .thenReturn(Collections.singletonList(beerDTO));

        // then
        mockMvc.perform(get(BEER_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("When DELETE is called with valid id then no content is returned")
    void whenDELETEIsCalledWithValidIdThenNoContentIsReturned() throws Exception {
        // given
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDto();

        // when
        doNothing().when(this.beerService).deleteById(beerDTO.getId());

        // then
        mockMvc.perform(
                MockMvcRequestBuilders.delete(BEER_API_URL_PATH + "/" + beerDTO.getId())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("When DELETE is called with invalid id then not found status is returned")
    void whenDELETEIsCalledWithInvalidIdThenNotFoundStatusIsReturned() throws Exception {
        // when
        doThrow(BeerNotFoundException.class).when(this.beerService)
            .deleteById(INVALID_BEER_ID);

        // then
        mockMvc.perform(
                MockMvcRequestBuilders.delete(BEER_API_URL_PATH + "/" + INVALID_BEER_ID)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("When PATCH is called to increment discount then OK status is returned")
    void whenPATCHIsCalledToIncrementDiscountThenOKStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder().quantity(10).build();
        BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDto();
        beerDTO.setQuantity(beerDTO.getQuantity() + quantityDTO.getQuantity());

        //when
        when(this.beerService.increment(VALID_BEER_ID, quantityDTO.getQuantity()))
            .thenReturn(beerDTO);
        
        mockMvc.perform(
                patch(String.format("%s/%s/%s",
                    BEER_API_URL_PATH, BEER_API_SUBPATH_INCREMENT_URL, VALID_BEER_ID))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(quantityDTO)))
            .andExpect(jsonPath("$.name", is(beerDTO.getName())))
            .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
            .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())))
            .andExpect(jsonPath("$.quantity", is(beerDTO.getQuantity())));
    }

    @Test
    @DisplayName("When PATCH is called with invalid beer id to increment then not found status is returned")
    void whenPATCHIsCalledWithInvalidBeerIdToIncrementThenNotFoundStatusIsReturned() throws Exception {
        // given
        QuantityDTO quantityDTO = QuantityDTO.builder().quantity(10).build();

        // when
        when(this.beerService.increment(INVALID_BEER_ID, quantityDTO.getQuantity()))
            .thenThrow(BeerNotFoundException.class);
        
        // then
        mockMvc.perform(patch(String.format("%s/%s/%s",
                BEER_API_URL_PATH, BEER_API_SUBPATH_INCREMENT_URL, INVALID_BEER_ID))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO)))
            .andExpect(status().isNotFound());
    }

}
