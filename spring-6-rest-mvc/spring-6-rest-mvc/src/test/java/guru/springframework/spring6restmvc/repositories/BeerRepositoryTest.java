package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.bootstrap.BootstrapData;
import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.services.BeerCsvServiceImpl;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@DataJpaTest
@Import({BootstrapData.class, BeerCsvServiceImpl.class})
@ActiveProfiles("localmysql")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BeerRepositoryTest {

    @Autowired
    BeerRepository beerRepository;


    @Test
    void testGetBeerListByName(){
        Page<Beer> list = beerRepository.findAllByBeerNameIsLikeIgnoreCase("%IPA%", null);

        assertThat(list.getContent().size()).isEqualTo(336);
    }

    @Test
    void testSaveBeerNameTooLong(){

        assertThrows(ConstraintViolationException.class, () -> {

            Beer savedBeer = beerRepository.save(Beer.builder()
                    .beerName("My Beer 0123345678945164449444464555655845454461466464646466465666446464664")
                    .beerStyle(BeerStyle.PALE_ALE)
                    .upc("2312455525522")
                    .price(new BigDecimal("11.99"))
                    .build());

            beerRepository.flush();
        });

//        assertThat(savedBeer).isNotNull();
//        assertThat(savedBeer.getId()).isNotNull();
    }

    @Test
    void testSaveBeer(){
        Beer savedBeer = beerRepository.save(Beer.builder()
                .beerName("My Beer")
                        .beerStyle(BeerStyle.PALE_ALE)
                        .upc("2312455525522")
                        .price(new BigDecimal("11.99"))
                .build());

        beerRepository.flush();

        assertThat(savedBeer).isNotNull();
        assertThat(savedBeer.getId()).isNotNull();
    }
}