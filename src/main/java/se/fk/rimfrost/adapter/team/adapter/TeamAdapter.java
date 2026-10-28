package se.fk.rimfrost.adapter.team.adapter;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.ServiceUnavailableException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.glassfish.jersey.apache5.connector.Apache5ConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.proxy.WebResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fk.rimfrost.team.jaxrsspec.controllers.generatedsource.TeamControllerApi;
import se.fk.rimfrost.team.jaxrsspec.controllers.generatedsource.model.GetIndividTeamResponse;
import se.fk.rimfrost.team.jaxrsspec.controllers.generatedsource.model.GetTeamMembersResponse;

/**
 * HTTP adapter for the team API, using the Jersey proxy client pattern.
 *
 * <p>Configured via {@code team.api.base-url}.
 */
@ApplicationScoped
public class TeamAdapter
{
   private static final Logger LOGGER = LoggerFactory.getLogger(TeamAdapter.class);

   @ConfigProperty(name = "team.api.base-url")
   String teamBaseUrl;

   private TeamControllerApi teamClient;

   private Client client;

   /**
    * Initialises the Jersey HTTP client and the team API proxy.
    */
   @PostConstruct
   void init()
   {
      ClientConfig clientConfig = new ClientConfig();
      clientConfig.connectorProvider(new Apache5ConnectorProvider());
      this.client = ClientBuilder.newClient(clientConfig);
      this.teamClient = WebResourceFactory.newResource(TeamControllerApi.class, client.target(this.teamBaseUrl));
   }

   /**
    * Cleans up the Jersey client on bean destruction.
    */
   @PreDestroy
   void destroy()
   {
      this.teamClient = null;
      if (this.client != null)
      {
         this.client.close();
         this.client = null;
      }
   }

   /**
    * Returns the teams that the given individ belongs to.
    *
    * @param idTyp   the identity type
    * @param idVarde the identity value
    * @return response containing the list of teams
    * @throws TeamException if the team service returns an error or is unreachable
    */
   public GetIndividTeamResponse getIndividTeam(String idTyp, String idVarde) throws TeamException
   {
      try
      {
         return teamClient.getIndividTeam(idTyp, idVarde);
      }
      catch (NotFoundException ex)
      {
         var message = "Individ not found for idTyp=" + idTyp + ", idVarde=" + idVarde;
         LOGGER.error(message, ex);
         throw new TeamException(TeamException.ErrorType.NOT_FOUND, message, ex);
      }
      catch (BadRequestException ex)
      {
         var message = "Bad request when fetching team for idTyp=" + idTyp + ", idVarde=" + idVarde;
         LOGGER.error(message, ex);
         throw new TeamException(TeamException.ErrorType.BAD_REQUEST, message, ex);
      }
      catch (ServiceUnavailableException ex)
      {
         var message = "Team service unavailable";
         LOGGER.error(message, ex);
         throw new TeamException(TeamException.ErrorType.SERVICE_UNAVAILABLE, message, ex);
      }
      catch (ProcessingException | WebApplicationException ex)
      {
         var message = "Unexpected error when fetching team for idTyp=" + idTyp + ", idVarde=" + idVarde;
         LOGGER.error(message, ex);
         throw new TeamException(TeamException.ErrorType.UNEXPECTED_ERROR, message, ex);
      }
   }

   /**
    * Returns all members of the given team.
    *
    * @param teamId the team's unique ID
    * @return response containing the list of team members
    * @throws TeamException if the team service returns an error or is unreachable
    */
   public GetTeamMembersResponse getTeamIndivider(Integer teamId) throws TeamException
   {
      try
      {
         return teamClient.getTeamIndivider(teamId);
      }
      catch (NotFoundException ex)
      {
         var message = "Team not found for teamId=" + teamId;
         LOGGER.error(message, ex);
         throw new TeamException(TeamException.ErrorType.NOT_FOUND, message, ex);
      }
      catch (BadRequestException ex)
      {
         var message = "Bad request when fetching individer for teamId=" + teamId;
         LOGGER.error(message, ex);
         throw new TeamException(TeamException.ErrorType.BAD_REQUEST, message, ex);
      }
      catch (ServiceUnavailableException ex)
      {
         var message = "Team service unavailable";
         LOGGER.error(message, ex);
         throw new TeamException(TeamException.ErrorType.SERVICE_UNAVAILABLE, message, ex);
      }
      catch (ProcessingException | WebApplicationException ex)
      {
         var message = "Unexpected error when fetching individer for teamId=" + teamId;
         LOGGER.error(message, ex);
         throw new TeamException(TeamException.ErrorType.UNEXPECTED_ERROR, message, ex);
      }
   }

}
