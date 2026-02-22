package nimblix.in.HealthCareHub.exception;

public class AdminNotFoundException extends RuntimeException{

    public AdminNotFoundException(String s){
        super("Admin not found");
    }

}
