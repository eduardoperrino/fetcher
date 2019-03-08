package me.perrino.fetcher.domain.trovit;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import me.perrino.fetcher.domain.Advert;
import me.perrino.fetcher.domain.FetcherService;
import me.perrino.fetcher.domain.Picture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import java.util.stream.Collectors;


@Component
@Slf4j
public class TrovitFetcherService implements FetcherService {

    @Value("${trovit.feed.url}")
    private String feed_url;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Collection<Advert> fetchAds()  {
        return Try.of(() -> (List<Map<String, Object>>) restTemplate.getForObject(feed_url, List.class))
                .onFailure(ex -> log.error("Error " + ex.getMessage()))
                .map(AdsMapper::convert)
                .getOrElse(Collections.emptyList());
    }

    private static class AdsMapper {

        private static String default_picture_link = "http://mb.cision.com/Public/16135/logo/ae5ed08781c9e77f_org.png";
        private static String default_picture_title = "Image title";

        static Collection<Advert> convert(List<Map<String, Object>> ads) {
            return ads.stream().map(AdsMapper::buildAdvert).collect(Collectors.toList());
        }

        static Advert buildAdvert(Map<String, Object> data) {
            return Advert.builder()
                    .id(extractKey(data, "id"))
                    .title(extractKey(data, "title"))
                    .city(extractKey(data, "city"))
                    .link(extractKey(data, "url"))
                    .type(extractKey(data, "type"))
                    .mainPicture(buildPicture((Map)data.get("pictures")))
                    .build();
        }

        static Picture buildPicture(Map picture) {
            if (picture == null) {
                return Picture.builder().link(default_picture_link).title(default_picture_title).build();
            }
            Map properties = picture.containsKey("picture") ? (Map)picture.get("picture") : Collections.emptyMap();
            return Picture.builder()
                    .link(extractKey(properties,"picture_url"))
                    .title(extractKey(properties,"picture_title"))
                    .build();
        }

        static String extractKey(Map collection, String key) {
            return collection.containsKey(key) ? collection.get(key).toString() : "";
        }


    }


}
