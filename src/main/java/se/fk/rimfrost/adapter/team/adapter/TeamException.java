package se.fk.rimfrost.adapter.team.adapter;

/**
 * Exception thrown by {@link TeamAdapter} to signal a failure when calling the team service.
 */
public class TeamException extends Exception
{
   private final ErrorType errorType;

   /**
    * @param errorType the category of error
    * @param message   human-readable description
    */
   public TeamException(ErrorType errorType, String message)
   {
      super(message);

      this.errorType = errorType;
   }

   /**
    * @param errorType the category of error
    * @param message   human-readable description
    * @param cause     the underlying exception
    */
   public TeamException(ErrorType errorType, String message, Throwable cause)
   {
      super(message, cause);

      this.errorType = errorType;
   }

   /**
    * @return the error type
    */
   public ErrorType getErrorType()
   {
      return errorType;
   }

   /** Categorises failures returned by the team service. */
   public enum ErrorType
   {
      NOT_FOUND, BAD_REQUEST, SERVICE_UNAVAILABLE, UNEXPECTED_ERROR,
   }
}
