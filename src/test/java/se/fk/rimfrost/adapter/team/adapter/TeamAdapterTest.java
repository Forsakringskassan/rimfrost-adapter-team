package se.fk.rimfrost.adapter.team.adapter;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.quarkus.test.component.QuarkusComponentTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusComponentTest(useSystemConfigSources = true)
public class TeamAdapterTest
{
   private static WireMockServer server;

   @Inject
   TeamAdapter teamAdapter;

   @BeforeAll
   public static void setup()
   {
      server = new WireMockServer(options().dynamicPort());
      server.start();

      System.setProperty("team.api.base-url", server.baseUrl());
   }

   @BeforeEach
   void resetStubs()
   {
      server.resetToDefaultMappings();
   }

   @AfterAll
   public static void teardown()
   {
      if (server != null)
      {
         server.stop();
      }
   }

   @Test
   @DisplayName("TEAM-FR-01.1: getIndividTeam returns team list when service responds with 200")
   void testGetIndividTeam() throws TeamException
   {
      server.stubFor(WireMock.get(WireMock.urlPathEqualTo("/individ/PERSONNUMMER/197001011234/team"))
            .willReturn(WireMock.aResponse().withStatus(200)
                  .withHeader("Content-Type", "application/json")
                  .withBody("{\"team\":[{\"id\":1,\"namn\":\"Team A\",\"kontor\":\"Stockholm\"}]}")));

      var response = teamAdapter.getIndividTeam("PERSONNUMMER", "197001011234");

      assertEquals(1, response.getTeam().size());
      assertEquals(1, response.getTeam().get(0).getId());
      assertEquals("Team A", response.getTeam().get(0).getNamn());
      assertEquals("Stockholm", response.getTeam().get(0).getKontor());
   }

   @Test
   @DisplayName("TEAM-FR-03.1: getIndividTeam throws TeamException with NOT_FOUND when service responds with 404")
   void testGetIndividTeamThrowsNotFound()
   {
      server.stubFor(WireMock.get(WireMock.urlPathEqualTo("/individ/PERSONNUMMER/unknown/team"))
            .willReturn(WireMock.aResponse().withStatus(404)));

      var ex = assertThrows(TeamException.class, () -> teamAdapter.getIndividTeam("PERSONNUMMER", "unknown"));
      assertEquals(TeamException.ErrorType.NOT_FOUND, ex.getErrorType());
   }

   @Test
   @DisplayName("TEAM-FR-03.2: getIndividTeam throws TeamException with BAD_REQUEST when service responds with 400")
   void testGetIndividTeamThrowsBadRequest()
   {
      server.stubFor(WireMock.get(WireMock.urlPathEqualTo("/individ/OGILTIG/123/team"))
            .willReturn(WireMock.aResponse().withStatus(400)));

      var ex = assertThrows(TeamException.class, () -> teamAdapter.getIndividTeam("OGILTIG", "123"));
      assertEquals(TeamException.ErrorType.BAD_REQUEST, ex.getErrorType());
   }

   @Test
   @DisplayName("TEAM-FR-03.3: getIndividTeam throws TeamException with SERVICE_UNAVAILABLE when service responds with 503")
   void testGetIndividTeamThrowsServiceUnavailable()
   {
      server.stubFor(WireMock.get(WireMock.urlPathEqualTo("/individ/PERSONNUMMER/197001011234/team"))
            .willReturn(WireMock.aResponse().withStatus(503)));

      var ex = assertThrows(TeamException.class, () -> teamAdapter.getIndividTeam("PERSONNUMMER", "197001011234"));
      assertEquals(TeamException.ErrorType.SERVICE_UNAVAILABLE, ex.getErrorType());
   }

   @Test
   @DisplayName("TEAM-FR-03.4: getIndividTeam throws TeamException with UNEXPECTED_ERROR when service responds with 500")
   void testGetIndividTeamThrowsUnexpectedError()
   {
      server.stubFor(WireMock.get(WireMock.urlPathEqualTo("/individ/PERSONNUMMER/197001011234/team"))
            .willReturn(WireMock.aResponse().withStatus(500)));

      var ex = assertThrows(TeamException.class, () -> teamAdapter.getIndividTeam("PERSONNUMMER", "197001011234"));
      assertEquals(TeamException.ErrorType.UNEXPECTED_ERROR, ex.getErrorType());
   }

   @Test
   @DisplayName("TEAM-FR-02.1: getTeamIndivider returns member list when service responds with 200")
   void testGetTeamIndivider() throws TeamException
   {
      server.stubFor(WireMock.get(WireMock.urlPathEqualTo("/team/1/individer"))
            .willReturn(WireMock.aResponse().withStatus(200)
                  .withHeader("Content-Type", "application/json")
                  .withBody(
                        "{\"individer\":[{\"typId\":\"4c34906c-03d9-425f-9a1a-062ef6eb88c7\",\"varde\":\"197001011234\"}]}")));

      var response = teamAdapter.getTeamIndivider(1);

      assertEquals(1, response.getIndivider().size());
      assertEquals(UUID.fromString("4c34906c-03d9-425f-9a1a-062ef6eb88c7"), response.getIndivider().get(0).getTypId());
      assertEquals("197001011234", response.getIndivider().get(0).getVarde());
   }

   @Test
   @DisplayName("TEAM-FR-03.1: getTeamIndivider throws TeamException with NOT_FOUND when service responds with 404")
   void testGetTeamIndividerThrowsNotFound()
   {
      server.stubFor(WireMock.get(WireMock.urlPathEqualTo("/team/999/individer"))
            .willReturn(WireMock.aResponse().withStatus(404)));

      var ex = assertThrows(TeamException.class, () -> teamAdapter.getTeamIndivider(999));
      assertEquals(TeamException.ErrorType.NOT_FOUND, ex.getErrorType());
   }

   @Test
   @DisplayName("TEAM-FR-03.2: getTeamIndivider throws TeamException with BAD_REQUEST when service responds with 400")
   void testGetTeamIndividerThrowsBadRequest()
   {
      server.stubFor(WireMock.get(WireMock.urlPathEqualTo("/team/-1/individer"))
            .willReturn(WireMock.aResponse().withStatus(400)));

      var ex = assertThrows(TeamException.class, () -> teamAdapter.getTeamIndivider(-1));
      assertEquals(TeamException.ErrorType.BAD_REQUEST, ex.getErrorType());
   }

   @Test
   @DisplayName("TEAM-FR-03.3: getTeamIndivider throws TeamException with SERVICE_UNAVAILABLE when service responds with 503")
   void testGetTeamIndividerThrowsServiceUnavailable()
   {
      server.stubFor(WireMock.get(WireMock.urlPathEqualTo("/team/1/individer"))
            .willReturn(WireMock.aResponse().withStatus(503)));

      var ex = assertThrows(TeamException.class, () -> teamAdapter.getTeamIndivider(1));
      assertEquals(TeamException.ErrorType.SERVICE_UNAVAILABLE, ex.getErrorType());
   }

   @Test
   @DisplayName("TEAM-FR-03.4: getTeamIndivider throws TeamException with UNEXPECTED_ERROR when service responds with 500")
   void testGetTeamIndividerThrowsUnexpectedError()
   {
      server.stubFor(WireMock.get(WireMock.urlPathEqualTo("/team/1/individer"))
            .willReturn(WireMock.aResponse().withStatus(500)));

      var ex = assertThrows(TeamException.class, () -> teamAdapter.getTeamIndivider(1));
      assertEquals(TeamException.ErrorType.UNEXPECTED_ERROR, ex.getErrorType());
   }

}
