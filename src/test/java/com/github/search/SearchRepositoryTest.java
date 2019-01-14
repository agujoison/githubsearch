package com.github.search;

import com.googlecode.zohhak.api.TestWith;
import com.googlecode.zohhak.api.runners.ZohhakRunner;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;

@RunWith(ZohhakRunner.class)
public class SearchRepositoryTest {

    public static final String BASE_URI = "http://api.github.com";
    public static final String BASE_PATH = "/search/repositories";
    public static final String MEDIA_TYPE = "application/vnd.github.mercy-preview+json";

    @TestWith({
        "defunkt,stars,stargazers_count,20",
        "defunkt,forks,forks_count,30",
        "defunkt,help-wanted-issues,open_issues_count,10",
        "defunkt,updated,updated_at,10",
    })
    public void shouldSearchByAuthorNameSortingAndPagination(String user, String sortingParam, String sortingKey, int pagination) {
        Map params = new HashMap<String, String>();
        params.put("q", "user:"+user);
        params.put("sort", sortingParam);
        params.put("page", "1");
        params.put("per_page", String.valueOf(pagination));

        given()
            .baseUri(BASE_URI)
            .basePath(BASE_PATH)
            .accept(MEDIA_TYPE)
        .when()
            .params(params)
            .get()
        .then()
            .body("items.find { it }.owner.login", Matchers.containsString((user)))
            .body("items[0]."+ sortingKey, response ->
                    Matchers.greaterThanOrEqualTo(response.path("items[1]."+sortingKey)))
            .body("items.size()", is(pagination));
    }
}
