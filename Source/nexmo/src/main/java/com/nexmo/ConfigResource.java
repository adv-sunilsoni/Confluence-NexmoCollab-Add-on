package com.nexmo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.atlassian.confluence.json.parser.JSONArray;
import com.atlassian.confluence.json.parser.JSONException;
import com.atlassian.confluence.json.parser.JSONObject;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
public class ConfigResource {
	private final UserManager userManager;
	private final PluginSettingsFactory pluginSettingsFactory;
	private final TransactionTemplate transactionTemplate;
	private static final Logger log = LoggerFactory
			.getLogger(ConfigResource.class);

	public ConfigResource(UserManager userManager,
			PluginSettingsFactory pluginSettingsFactory,
			TransactionTemplate transactionTemplate) {
		this.userManager = userManager;
		this.pluginSettingsFactory = pluginSettingsFactory;
		this.transactionTemplate = transactionTemplate;
	}

	public void getSettingsCallBack(Config config) {

	}

	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class Config {

		@XmlElement
		private String key;
		@XmlElement
		private String secret;
		@XmlElement
		private String fromuser;
		@XmlElement
		private String fromuserDefault;
		@XmlElement
		private String thresholdamount;
		@XmlElement
		private String smsenable;
		@XmlElement
		private String selectedspace;
		@XmlElement
		private String pagecreateevent;
		@XmlElement
		private String pageupdateevent;
		@XmlElement
		private String pageremoveevent;
		@XmlElement
		private String pagecreateeventmessage;
		@XmlElement
		private String pageupdateeventmessage;
		@XmlElement
		private String pageremoveeventmessage;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getSecret() {
			return secret;
		}

		public void setSecret(String secret) {
			this.secret = secret;
		}

		public String getFromuser() {
			return fromuser;
		}

		public void setFromuser(String fromuser) {
			this.fromuser = fromuser;
		}

		public String getThresholdamount() {
			return thresholdamount;
		}

		public void setThresholdamount(String thresholdamount) {
			this.thresholdamount = thresholdamount;
		}

		public String getSmsenable() {
			return smsenable;
		}

		public void setSmsenable(String smsenable) {
			this.smsenable = smsenable;
		}

		public String getSelectedspaces() {
			return selectedspace;
		}

		public void setSelectedspaces(String selectedspaces) {
			this.selectedspace = selectedspaces;
		}

		public String getSelectedspace() {
			return selectedspace;
		}

		public void setSelectedspace(String selectedspace) {
			this.selectedspace = selectedspace;
		}

		public String isPagecreateevent() {
			return pagecreateevent;
		}

		public void setPagecreateevent(String pagecreateevent) {
			this.pagecreateevent = pagecreateevent;
		}

		public String isPageupdateevent() {
			return pageupdateevent;
		}

		public void setPageupdateevent(String pageupdateevent) {
			this.pageupdateevent = pageupdateevent;
		}

		public String isPageremoveevent() {
			return pageremoveevent;
		}

		public void setPageremoveevent(String pageremoveevent) {
			this.pageremoveevent = pageremoveevent;
		}

		public String getPagecreateeventMessage() {
			return pagecreateeventmessage;
		}

		public void setPagecreateeventMessage(String pagecreateeventMessage) {
			this.pagecreateeventmessage = pagecreateeventMessage;
		}

		public String getPageupdateeventMessage() {
			return pageupdateeventmessage;
		}

		public void setPageupdateeventMessage(String pageupdateeventMessage) {
			this.pageupdateeventmessage = pageupdateeventMessage;
		}

		public String getPageremoveeventMessage() {
			return pageremoveeventmessage;
		}

		public void setPageremoveeventMessage(String pageremoveeventMessage) {
			this.pageremoveeventmessage = pageremoveeventMessage;
		}

		public void setFromuserDefault(String fromuserDefault) {
			this.fromuserDefault = fromuserDefault;
		}

		public String getFromuserDefault() {
			return fromuserDefault;
		}

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@Context HttpServletRequest request) {

		//log.error("GET MET CALLED");
		String username = userManager.getRemoteUsername(request);
		if (username == null || !userManager.isSystemAdmin(username)) {
			return Response.status(Status.UNAUTHORIZED).build();
		}

		return Response.ok(
				transactionTemplate.execute(new TransactionCallback() {
					public Object doInTransaction() {
						PluginSettings settings = pluginSettingsFactory
								.createGlobalSettings();
						Config config = new Config();
						config.setKey((String) settings.get(Config.class
								.getName() + ".key"));
						config.setSecret((String) settings.get(Config.class
								.getName() + ".secret"));

						config.setFromuser((String) settings.get(Config.class
								.getName() + ".fromuser"));
						config.setFromuserDefault((String) settings
								.get(Config.class.getName()
										+ ".fromuserDefault"));
						config.setThresholdamount((String) settings
								.get(Config.class.getName()
										+ ".thresholdamount"));
						config.setSmsenable((String) settings.get(Config.class
								.getName() + ".smsenable"));

						config.setPagecreateevent((String) settings
								.get(Config.class.getName()
										+ ".pagecreateevent"));
						config.setPageupdateevent((String) settings
								.get(Config.class.getName()
										+ ".pageupdateevent"));
						config.setPageremoveevent((String) settings
								.get(Config.class.getName()
										+ ".pageremoveevent"));
						config.setSelectedspaces((String) settings
								.get(Config.class.getName() + ".selectedspace"));
						config.setPagecreateeventMessage((String) settings
								.get(Config.class.getName()
										+ ".pagecreateeventmessage"));
						config.setPageupdateeventMessage((String) settings
								.get(Config.class.getName()
										+ ".pageupdateeventmessage"));
						config.setPageremoveeventMessage((String) settings
								.get(Config.class.getName()
										+ ".pageremoveeventmessage"));
						NexmoEventListener.configSettings = config;
						return config;
					}
				})).build();
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response put(final Config config, @Context HttpServletRequest request) {

//		log.error("PUT MET CALLED");
		
		String username = userManager.getRemoteUsername(request);
		if (username == null || !userManager.isSystemAdmin(username)) {
			return Response.status(Status.UNAUTHORIZED).build();
		}

		
		return Response.ok(transactionTemplate.execute(new TransactionCallback() {
			
			
			
			public Object doInTransaction() {
				PluginSettings pluginSettings = pluginSettingsFactory
						.createGlobalSettings();
				pluginSettings.put(Config.class.getName() + ".key", config
						.getKey().trim());
				pluginSettings.put(Config.class.getName() + ".secret", config
						.getSecret().trim());
				String checking = validateKeys(config.getKey().trim(), config
						.getSecret().trim());
			
				if (checking.equals("ERROR")) {
					config.setSmsenable("0");
				}

			//	config.setFromuser(checking);;
				pluginSettings.put(Config.class.getName() + ".fromuser",
						checking + "");
				pluginSettings.put(Config.class.getName() + ".fromuserDefault",
						config.fromuserDefault + "");
				pluginSettings.put(Config.class.getName() + ".thresholdamount",
						config.getThresholdamount() + "");
				pluginSettings.put(Config.class.getName() + ".smsenable",
						config.getSmsenable() + "");
				pluginSettings.put(Config.class.getName() + ".selectedspace",
						config.getSelectedspaces());
				pluginSettings.put(Config.class.getName() + ".pagecreateevent",
						config.isPagecreateevent());
				pluginSettings.put(Config.class.getName() + ".pageupdateevent",
						config.isPageupdateevent());
				pluginSettings.put(Config.class.getName() + ".pageremoveevent",
						config.isPageremoveevent());
				pluginSettings.put(Config.class.getName()
						+ ".pagecreateeventmessage",
						config.getPagecreateeventMessage());
				pluginSettings.put(Config.class.getName()
						+ ".pageupdateeventmessage",
						config.getPageupdateeventMessage());
				pluginSettings.put(Config.class.getName()
						+ ".pageremoveeventmessage",
						config.getPageremoveeventMessage());
				NexmoEventListener.configSettings = config;
				return checking;
			}
		})).build();
		
	}

	public String validateKeys(String apiKey, String apiSecret) {
		try {
			// Here we need to configure settings
			if (apiKey.equals("") == true || apiSecret.equals("") == true) {
				return "ERROR";
			}

		
			String url = "https://rest.nexmo.com/account/numbers/" + apiKey
					+ "/" + apiSecret + "";
			String from = "ERROR";

			URL google = new URL(url);
			BufferedReader inst = new BufferedReader(new InputStreamReader(
					google.openStream()));
			String inputLine;
			String response = "";

			while ((inputLine = inst.readLine()) != null) {
				// Process each line.
				response += inputLine;

			}

			try {
				JSONObject parent = new JSONObject(response);
				JSONArray numberes = new JSONArray(parent.getString("numbers"));

				// create a loop and return string which contains list of
				// numbers
				from = "";
				for (int i = 0; i <numberes.length(); i++) {

					JSONArray features = new JSONArray(numberes
							.getJSONObject(0).getString("features"));
					String featuresString = "";
					for (int j = 0; j < features.length(); j++) {

						if(j==features.length()-1){
							featuresString += features.getString(j) + "";	
						}
						else{
							featuresString += features.getString(j) + ",";
						}
						
					}
					from+= numberes.getJSONObject(i).getString("msisdn") + ":"
							+ featuresString + ";";

				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				log.error("ERROR NEXMO : " + response);
				e.printStackTrace();
				return "ERROR";
			}

//			log.info("MESSAGE NEXMO : " + response);
			inst.close();
			return from;

		} catch (MalformedURLException me) {
			log.error("ERRROR: " + me);
			return "ERROR";

		} catch (IOException ioe) {
			log.error("ERROR: " + ioe);
			return "ERROR";
		}
	}

}
