package com.example.voter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VoteControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        this.restTemplate.getForEntity("/seeder", String.class);
    }

    @Test
    public void getVotesReturnsListOfVoteChoices() throws Exception {
        String expectedVoteList =
                "{\"choices\":[\"Chris Keniston\",\"Darrell Castle\",\"Donald Trump\",\"Gary Johnson\",\"Hillary Clinton\",\"Jill Stein\"]}";
        ResponseEntity<String> responseEntity = this.restTemplate.getForEntity("/choices", String.class);
        assertThat(responseEntity.getStatusCode().value() == 200);
        assertThat(responseEntity.getBody()).isEqualTo(expectedVoteList);

    }

    @Test
    public void setVoteReturnsNewVote() throws Exception {
        String expectedVote = "Test Vote";
        Vote vote = new Vote(expectedVote);
        ResponseEntity<Vote> responseEntity =
                this.restTemplate.postForEntity("/votes", vote, Vote.class);
        assertThat(responseEntity.getStatusCode().value() == 201);
        assertThat(responseEntity.getBody().getVote()).isEqualTo(expectedVote);
    }

    @Test
    public void getCountsReturnsVoteCounts() throws Exception {
        String expectedVote = "Chris Keniston";
        int expectedCount = 3;
        ParameterizedTypeReference<Map<String, List<VoteCount>>> typeRef =
                new ParameterizedTypeReference<Map<String, List<VoteCount>>>() {
                };
        ResponseEntity<Map<String, List<VoteCount>>> responseEntity =
                this.restTemplate.exchange("/results", HttpMethod.GET, null, typeRef);
        LinkedHashMap body = ((LinkedHashMap) responseEntity.getBody());
        Collection voteCountCollection = body.values();
        ArrayList voteCountArray = (ArrayList) voteCountCollection.toArray()[0];
        VoteCount voteCount = (VoteCount) voteCountArray.get(0);
        assertThat(responseEntity.getStatusCode().value() == 200);
        assertThat(voteCount.getVote()).isEqualTo(expectedVote);
        assertThat(voteCount.getCount()).isEqualTo(expectedCount);
    }

    @Test
    public void getFavoriteReturnsMaxCountVote() throws Exception {
        String expectedVote = "Hillary Clinton";
        int expectedCount = 14;
        ResponseEntity<VoteCount> responseEntity =
                this.restTemplate.getForEntity("/winner", VoteCount.class);
        VoteCount voteCount = responseEntity.getBody();
        assertThat(responseEntity.getStatusCode().value() == 200);
        assertThat(voteCount.getVote()).isEqualTo(expectedVote);
        assertThat(voteCount.getCount()).isEqualTo(expectedCount);
    }

}
