package br.com.beerstock.beerstockapi.api.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.beerstock.beerstockapi.domain.entity.Beer;

@Repository
public interface BeerRepository extends JpaRepository<Beer, UUID> {

    Optional<Beer> findByName(String name);

}