package se.fk.rimfrost.adapter.team.adapter;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.quarkus.test.component.QuarkusComponentTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
   void testGetIndividTeam()
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
   void testGetIndividTeamThrowsNotFound()
   {
      server.stubFor(WireMock.get(WireMock.urlPathEqualTo("/individ/PERSONNUMMER/unknown/team"))
            .willReturn(WireMock.aResponse().withStatus(404)));

      assertThrows(NotFoundException.class, () -> teamAdapter.getIndividTeam("PERSONNUMMER", "unknown"));
   }

   @Test
   void testGetTeamIndivider()
   {
      server.stubFor(WireMock.get(WireMock.urlPathEqualTo("/team/1/individer"))
            .willReturn(WireMock.aResponse().withStatus(200)
                  .withHeader("Content-Type", "application/json")
                  .withBody("{\"individer\":[{\"typId\":\"4c34906c-03d9-425f-9a1a-062ef6eb88c7\",\"varde\":\"197001011234\"}]}")));

      var response = teamAdapter.getTeamIndivider(1);

      assertEquals(1, response.getIndivider().size());
      assertEquals(UUID.fromString("4c34906c-03d9-425f-9a1a-062ef6eb88c7"), response.getIndivider().get(0).getTypId());
      assertEquals("197001011234", response.getIndivider().get(0).getVarde());
   }

   @Test
   void testGetTeamIndividerThrowsNotFound()
   {
      server.stubFor(WireMock.get(WireMock.urlPathEqualTo("/team/999/individer"))
            .willReturn(WireMock.aResponse().withStatus(404)));

      assertThrows(NotFoundException.class, () -> teamAdapter.getTeamIndivider(999));
   }

   @Test
   void testHasSidPermission()
   {
      server.stubFor(WireMock.get(WireMock.urlPathEqualTo("/individ/PERSONNUMMER/197001011234/hasSidPermission"))
            .willReturn(WireMock.aResponse().withStatus(200)
                  .withHeader("Content-Type", "application/json")
                  .withBody("true")));

      var response = teamAdapter.hasSidPermission("PERSONNUMMER", "197001011234");

      assertEquals(Boolean.TRUE, response);
   }

   @Test
   void testHasSidPermissionThrowsNotFound()
   {
      server.stubFor(WireMock.get(WireMock.urlPathEqualTo("/individ/PERSONNUMMER/unknown/hasSidPermission"))
            .willReturn(WireMock.aResponse().withStatus(404)));

      assertThrows(NotFoundException.class, () -> teamAdapter.hasSidPermission("PERSONNUMMER", "unknown"));
   }
}
