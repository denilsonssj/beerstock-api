package br.com.beerstock.beerstockapi.common.builder;

import java.util.UUID;
import lombok.Builder;

import br.com.beerstock.beerstockapi.api.dtos.BeerDTO;
import br.com.beerstock.beerstockapi.domain.enums.BeerType;

@Builder
public class BeerDTOBuilder {
    
    @Builder.Default
    private UUID id = generateValidBeerUUID();

    @Builder.Default
    private String name = "Brahma";

    @Builder.Default
    private String brand = "Ambev";

    @Builder.Default
    private int max = 50;

    @Builder.Default
    private int quantity = 10;

    @Builder.Default
    private BeerType type = BeerType.LAGER;

    public BeerDTO toBeerDto() {
        return new BeerDTO(
            id,
            name,
            brand,
            max,
            quantity,
            type);
    }

    public static UUID generateValidBeerUUID() {
        return UUID.fromString("4e12a21d-ae3a-4045-97fd-0065107eb956");
    }

    public static UUID generateInvalidBeerUUID() {
        return UUID.fromString("0b9f6e7c-34c8-449a-ae3e-d518b6fe7fd7");
    }

}
