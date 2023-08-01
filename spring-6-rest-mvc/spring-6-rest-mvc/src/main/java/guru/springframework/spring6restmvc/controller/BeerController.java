package guru.springframework.spring6restmvc.controller;


import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.services.BeerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@Slf4j
//@AllArgsConstructor
@RequiredArgsConstructor
@RestController
//@RequestMapping("/api/v1/beer")
public class BeerController {

    private final BeerService beerService;

    public static final String BEER_PATH = "/api/v1/beer";
    public static final String BEER_PATH_ID = BEER_PATH + "/{beerId}";

    @PatchMapping(BEER_PATH_ID) //pour une maj partielle
    public ResponseEntity updateBeerPatchById(@PathVariable("beerId") UUID beerId, @RequestBody BeerDTO beer){

        beerService.patchBeerById(beerId, beer);
        return  new ResponseEntity(HttpStatus.NO_CONTENT);
    }
    @DeleteMapping(BEER_PATH_ID)
    public ResponseEntity deleteBeer(@PathVariable("beerId") UUID beerId){

        if(! beerService.deleteBeerById(beerId)){
            throw  new NotFoundException();
        }

        return  new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PutMapping(BEER_PATH_ID) //maj complète
    public ResponseEntity updateById(@PathVariable("beerId") UUID beerId, @Validated @RequestBody BeerDTO beer){

        if(beerService.updateBeerById(beerId, beer).isEmpty()){
            throw new NotFoundException();
        }
        return  new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PostMapping(BEER_PATH)
    //@RequestMapping(method = RequestMethod.POST )
    public ResponseEntity handlePost(@Validated  @RequestBody BeerDTO beer){

        BeerDTO savedBeer = beerService.saveNewBeer(beer);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Location", BEER_PATH + "/" + savedBeer.getId().toString());

        return new ResponseEntity(httpHeaders,HttpStatus.CREATED);
    }
    //@RequestMapping(method = RequestMethod.GET)
    @GetMapping(value = BEER_PATH)
    public Page<BeerDTO> listBeers(@RequestParam(required = false) String beerName,
                                   @RequestParam(required = false) BeerStyle beerStyle,
                                   @RequestParam(required = false)  Boolean showInventory,
                                   @RequestParam(required = false)  Integer pageNumber,
                                   @RequestParam(required = false)  Integer pageSize){

        return beerService.listBeers(beerName, beerStyle, showInventory, pageNumber, pageSize);
    }

    //@RequestMapping(value = BEER_PATH_ID, method = RequestMethod.GET)
    @GetMapping(value = BEER_PATH_ID )
    public BeerDTO getBeerById(@PathVariable("beerId") UUID beerId){

        log.debug("Get Beer by Id - in controller - 1234");
        return beerService.getBeerById(beerId).orElseThrow(NotFoundException::new);
    }
}
