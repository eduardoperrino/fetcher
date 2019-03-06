package me.perrino.fetcher.web;

import me.perrino.fetcher.domain.Advert;
import me.perrino.fetcher.domain.FetcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api")
public class AdController {

    @Autowired
    private FetcherService fetcherService;

    @GetMapping("/ads")
    Collection<Advert> adverts() {
        return fetcherService.fetchAds();
    }

}
