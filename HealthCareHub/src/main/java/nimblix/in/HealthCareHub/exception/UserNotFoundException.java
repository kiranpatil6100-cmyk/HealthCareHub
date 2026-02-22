package nimblix.in.HealthCareHub.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String s){
        super("User not found");
    }
}
