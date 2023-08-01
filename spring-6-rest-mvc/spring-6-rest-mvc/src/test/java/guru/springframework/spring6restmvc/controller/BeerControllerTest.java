package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.services.BeerService;
import guru.springframework.spring6restmvc.services.BeerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.core.Is.is;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

//@SpringBootTest
@WebMvcTest(BeerController.class)
class BeerControllerTest {

//    @Autowired
//    BeerController beerController;
    BeerServiceImpl beerServiceImpl;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BeerService beerService;

    @Captor
    ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Captor
    ArgumentCaptor<BeerDTO> beerArgumentCaptor;

    @BeforeEach
    void setUp(){
        beerServiceImpl = new BeerServiceImpl();
    }

    @Test
    void getBeerByIdNotFound() throws Exception{

        given(beerService.getBeerById(any(UUID.class))).willReturn(Optional.empty());

        mockMvc.perform(get(BeerController.BEER_PATH_ID, UUID.randomUUID())).andExpect(status().isNotFound());
    }

    @Test
    void testCreateBeerNullBeerName() throws  Exception{

        BeerDTO beerDTO = BeerDTO.builder().build();

        given(beerService.saveNewBeer(any(BeerDTO.class))).willReturn(beerServiceImpl.listBeers(null, null, false, 1, 25).getContent().get(1));

      MvcResult mvcResult =  mockMvc.perform(post(BeerController.BEER_PATH)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beerDTO)))
                .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.length()", is(6)))
                                .andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    void testPatchBeer() throws Exception {
        BeerDTO beer = beerServiceImpl.listBeers(null, null, false, 1, 25).getContent().get(0);

        Map<String,Object> beerMap = new HashMap<>();
        beerMap.put("beerName","New Name");

        mockMvc.perform(patch(BeerController.BEER_PATH_ID, beer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerMap)))
                .andExpect(status().isNoContent());

        verify(beerService).patchBeerById(uuidArgumentCaptor.capture(),beerArgumentCaptor.capture());
        assertThat(beer.getId()).isEqualTo(uuidArgumentCaptor.getValue());
        assertThat(beerMap.get("beerName")).isEqualTo(beerArgumentCaptor.getValue().getBeerName());
    }

    @Test
    void testDeleteBeer() throws Exception {

        BeerDTO beer = beerServiceImpl.listBeers(null, null, false, 1, 25).getContent().get(0);

        given(beerService.deleteBeerById(any())).willReturn(true);

        mockMvc.perform(delete(BeerController.BEER_PATH_ID, beer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(beerService).deleteBeerById(uuidArgumentCaptor.capture());
        assertThat(beer.getId()).isEqualTo(uuidArgumentCaptor.getValue());
        //verify(beerService).deleteBeerById(any(UUID.class));
    }

    @Test
    void testUpdateBeer() throws Exception {

        BeerDTO beer = beerServiceImpl.listBeers(null, null, false, 1, 25).getContent().get(0);

        given(beerService.updateBeerById(any(),any())).willReturn(Optional.of(beer));

        mockMvc.perform(put(BeerController.BEER_PATH + "/" + beer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beer)))
                        .andExpect(status().isNoContent());

        verify(beerService).updateBeerById(any(UUID.class),any(BeerDTO.class)); //check ce que j'attends en retour
    }

    @Test
    void testUpdateBeerBlankName() throws Exception {

        BeerDTO beer = beerServiceImpl.listBeers(null, null, false, 1, 25).getContent().get(0);
        beer.setBeerName("");

        given(beerService.updateBeerById(any(), any())).willReturn(Optional.of(beer));

        mockMvc.perform(put(BeerController.BEER_PATH + "/" + beer.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beer)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)));

    }

    @Test
    void testCreateNewBeer() throws Exception {

        BeerDTO beer = beerServiceImpl.listBeers(null, null, false, 1, 25).getContent().get(0);
        beer.setVersion(null);
        beer.setId(null);

        given(beerService.saveNewBeer(any(BeerDTO.class))).willReturn(beerServiceImpl.listBeers(null, null, false, 1, 25).getContent().get(1));

        mockMvc.perform(post(BeerController.BEER_PATH )
                .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beer)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        //System.out.println(objectMapper.writeValueAsString(beer));
    }

    @Test
    void testListBeers() throws Exception {
        given(beerService.listBeers(any(), any(), any(), any(), any())).willReturn(beerServiceImpl.listBeers(null, null, false, 1, 25));

        mockMvc.perform(get(BeerController.BEER_PATH)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(11)));
    }

    @Test
    void getBeerById() throws Exception {
        BeerDTO testBeer = beerServiceImpl.listBeers(null, null, false, 1, 25).getContent().get(0);

        given(beerService.getBeerById(any(UUID.class))).willReturn(Optional.of(testBeer));

        mockMvc.perform(get(BeerController.BEER_PATH + "/" + UUID.randomUUID())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));
        //System.out.println(beerController.getBeerById(UUID.randomUUID()));

        //----------------------------
        BeerDTO testBeer1 = beerServiceImpl.listBeers(null, null, false, 1, 25).getContent().get(0);

        given(beerService.getBeerById(testBeer1.getId())).willReturn(Optional.of(testBeer1));

        mockMvc.perform(get(BeerController.BEER_PATH + "/" + testBeer1.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testBeer1.getId().toString())))
                .andExpect(jsonPath("$.beerName", is(testBeer1.getBeerName())));

    }
}