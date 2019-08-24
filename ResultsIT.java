package 


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;

import org.apache.poi.util.SystemOutLogger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import


public class ResultsIT {
	
	protected static HashMap<String, String> setting;
	protected static HashMap<String,String> book;	

	public ResultsIT() {
		new EnviormentSetting("./data/Settings.xls", "Book_Sheet");
		book = EnviormentSetting.settings;
		new EnviormentSetting("./data/Settings.xls",book.get("CurrentBook"));
	 	setting = EnviormentSetting.settings;
	}
	
	@BeforeClass
    void setupMailConfig(){
    }		
    
    Date today = new Date();
    
    String host = "smtp.gmail.com";
    String from = "owlv2.automation@gmail.com";
    String password = "";
    String  port = "587";
    String isMailSend="yes";

    @Test
    public void sendResultsMail() {
        if (isMailSend.equalsIgnoreCase("yes")){            
        	Properties props = new Properties();
        	props.put("mail.smtps.auth", "true");       
            Session session = Session.getInstance(props,null);
            Message message = new MimeMessage(getSession());
            try{
            setMailRecipient(message);  
            message.setSubject(setMailSubject());              
            message.setContent(setAttachement());     
            message.setFrom(new InternetAddress("from","Automation"));
			
            Transport transport = session.getTransport("smtps");
            transport.connect(host, from, password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            }catch(Exception e){ System.out.println(e);}
            }
    }

    public Session getSession() {
        Authenticator authenticator = new Authenticator(from, password);       
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", "smtps");
        properties.put("mail.smtps.auth", "true");       
        properties.setProperty("mail.smtp.submitter", authenticator.getPasswordAuthentication().getUserName());
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", port);
        return Session.getInstance(properties, authenticator);
    }
   
    public String setBodyText(){
    	String mailtext = "";
        mailtext = "Hi All,<br>" ;       
        mailtext = mailtext + "</br><br>OWLv2 Automated Smoke test suite was executed for Title <b>\"" +setting.get("BookName")+"\"</b> on build <b>\"" +setting.get("version1") +"\".</b> Please find below summary of the test results:- </br><br>";
        mailtext = mailtext + "<br><b>Application URL: </b>" +setting.get("switchURL");
        mailtext = mailtext + "<br><b>Browser: </b>"+setting.get("browser");
        mailtext = mailtext + "<br><b>Instructor Account: </b>" +setting.get("InstructorUserName");
        mailtext = mailtext + "<br><b>Student Account: </b>" +setting.get("studentUserName");
        mailtext = mailtext + "<br><br>Please find detailed test results in the attached <i>emailable-report.html</i> <br><br><br>";
        mailtext = mailtext + "--";
        mailtext = mailtext + "<br>Best Regards" + "<br>";
        mailtext = mailtext + "Automation" + "<br><br><br>";
    //    mailtext = mailtext + "<b>" +testSetResult()+ "</b>";
        return mailtext ;      
    }
    
    public String setMailSubject(){
        return ("Automated Smoke test on " + setting.get("BookTitle") +" - "+ today);
    }
    
    public void setMailRecipient(Message message) throws AddressException, MessagingException{
   	
//    message.addRecipient(Message.RecipientType.BCC,new InternetAddress(""));
	message.addRecipient(Message.RecipientType.BCC,new InternetAddress(""));

 
   
    }
   
    public Multipart setAttachement(){
        // Create the message part 
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        // Fill the message
        //messageBodyPart.setText(setBodyText());  
        try {
			messageBodyPart.setContent(setBodyText(),"text/html");
		} catch (MessagingException e) {
			e.printStackTrace();
		}
        MimeMultipart multipart = new MimeMultipart();
        try {
			multipart.addBodyPart(messageBodyPart);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // Part two is attachment
        messageBodyPart = new MimeBodyPart();
        
        try {
			messageBodyPart.attachFile(".//target//test-output//emailable-report.html");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			multipart.addBodyPart(messageBodyPart);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
        return multipart;
    }     
 
    public String getTestName(){
        String test = System.getProperty("test", "null");
        String testsuite = System.getProperty("testsuite", "null");
        String testName;
                
        if (test != "null"){
            testName = test + "was executed";
            return testName;
        }
        else{
            if (testsuite != "null"){
                testName = testsuite + "were executed";
                return testName;
            }
            else{
                testName = "complete automation test suite or TestNg xml was executed";
            }
        }
        return "testName";     
    }
    
  
    public String testSetResult(){
        String messageToBeSent =("");
        try{
            Thread.sleep(10000);
        }
        catch(Exception e){           
        }
        FileInputStream fstream;
		try {
			fstream = new FileInputStream(".//target//test-output//emailable-report.html");
			 DataInputStream ds = new DataInputStream(fstream);
			 BufferedReader br = new BufferedReader(new InputStreamReader(ds));
			 String strLine = new String("");
			 int i=0;
			 try {
				while ((strLine = br.readLine()) != null)   {
						messageToBeSent = messageToBeSent + strLine;
						if(strLine.contains("Total")){
							i++;
							if(i==2){
								strLine = br.readLine();
								messageToBeSent = messageToBeSent + "<br>" + strLine + "</br>";
								messageToBeSent = messageToBeSent + "<br>" + "</table></body></html>" + "</br>";

								break;
							}
						}
				 }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        return messageToBeSent;
    }
    public String getFilePath(){     
        File folder = new File("./target/surefire-reports");
        String[] fileNames = folder.list();
        String textFile="";
        int total = 0;
        for (int i = 0; i< fileNames.length; i++){
            if (fileNames[i].contains(".txt")){
                total ++;
                System.out.println("total is"  +total);
                assert total == 1;
                textFile = fileNames[i];        
                System.out.println("The filename is:"+textFile);           
                
            }
        }
		return textFile;      
    }    

    public String testSetResult1() {           
        String messageToBeSent = ("");
        String textFilePath="./target/surefire-reports/"+getFilePath()+"";
        FileInputStream fstream;
		try {
			fstream = new FileInputStream(textFilePath);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
	        int num = 4;
	        //Read File Line By Line
	        String[] strLine = new String[num];
	        for (int i = 0; i < num; i++) {            
	            strLine[i] = br.readLine();
	            messageToBeSent = messageToBeSent + "<br>" + strLine[i] + "</br>";
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return messageToBeSent;
    }
}
