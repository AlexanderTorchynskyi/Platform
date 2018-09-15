package ua.tor.platform.web.api.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class CrawlerRequest {
    @NotNull
    @JsonProperty("search")
    private String search;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
