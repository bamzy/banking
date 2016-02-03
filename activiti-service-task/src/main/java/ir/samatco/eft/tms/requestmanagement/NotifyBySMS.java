package ir.samatco.eft.tms.requestmanagement;

import com.github.mustachejava.DefaultMustacheFactory;
import ir.samatco.eft.common.sms.SmsPanel;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

/**
 * @author alireza ghassemi
 */
public class NotifyBySMS implements JavaDelegate{
	@Override
	public void execute(DelegateExecution execution) throws Exception{
		String baseDir = "dc=switch,dc=samat,dc=com";
		String usersDir = "ou=people," + baseDir;
		Logger log = LoggerFactory.getLogger(getClass());
		Hashtable environment = new Hashtable(5);
		environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		environment.put(Context.PROVIDER_URL, "ldap://security-interface:389/");
		environment.put(Context.SECURITY_AUTHENTICATION, "simple");
		environment.put(Context.SECURITY_PRINCIPAL, "cn=admin,dc=switch,dc=samat,dc=com");
		environment.put(Context.SECURITY_CREDENTIALS, "1");
		DirContext ctx = new InitialDirContext(environment);
		String assignee = (String)execution.getVariable("assignee");
		String mobile = (String)ctx.getAttributes("cn=" + assignee + "," + usersDir).get("mobile").get();

		SaajSoapMessageFactory messageFactory = new SaajSoapMessageFactory();
		messageFactory.afterPropertiesSet();
		WebServiceTemplate webServiceTemplate = new WebServiceTemplate(messageFactory);
		webServiceTemplate.setDefaultUri("http://87.107.121.54/post/Send.asmx");
		SmsPanel smsPanel = new SmsPanel(new DefaultMustacheFactory(), webServiceTemplate);
		log.info("assignee={}, mobile={}", assignee, mobile);
		Long sendSmsResult = smsPanel.sendOne("9124398904", "9777", mobile, "", "slm", false);
		log.info("sendSmsResult={}", sendSmsResult);
	}
}
