package
import javax.mail.PasswordAuthentication;

/**
 * Authenticator
 * @author
 *
 */
public class Authenticator extends javax.mail.Authenticator  {

	   
    private PasswordAuthentication authentication;

    /**
     * Authenticator
     * @param username
     * @param password
     */
    public Authenticator(String username, String password) {
        authentication = new PasswordAuthentication(username, password);
    }

    /**
     * getPasswordAuthentication
     */
    protected PasswordAuthentication getPasswordAuthentication() {
        return authentication;
    }


}
