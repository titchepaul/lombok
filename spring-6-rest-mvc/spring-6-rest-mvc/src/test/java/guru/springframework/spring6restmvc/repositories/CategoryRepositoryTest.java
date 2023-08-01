package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.entities.Category;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("localmysql")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BeerRepository beerRepository;
    Beer testBeer;


    @BeforeEach
    void setUp() {
        testBeer = beerRepository.findAll().get(0);
    }

    @Transactional
    @Test
    void  testAddCategory() {
        Category savedCat = categoryRepository.save(Category.builder()
                .description("Ales")
                .build());

        testBeer.addCategory(savedCat);
        Beer saveBeer = beerRepository.save(testBeer);

        System.out.println(saveBeer.getBeerName());
    }
}