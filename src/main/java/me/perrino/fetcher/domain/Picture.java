package me.perrino.fetcher.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Picture {

    private String link;
    private String title;
}
