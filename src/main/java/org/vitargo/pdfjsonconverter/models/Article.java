package org.vitargo.pdfjsonconverter.models;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Article {
    private String name;
    private String authors;
    private String journal;
    private String abstractArticle;
}
