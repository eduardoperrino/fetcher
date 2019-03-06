package me.perrino.fetcher.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Advert {

    private String id;
    private String title;
    private String link;
    private String type;
    private String city;
    private Picture mainPicture;

}
