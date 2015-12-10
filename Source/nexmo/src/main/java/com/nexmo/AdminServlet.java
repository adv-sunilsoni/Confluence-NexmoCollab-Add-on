package com.nexmo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atlassian.confluence.user.DefaultPersonalInformationManager;
import com.atlassian.confluence.user.DefaultUserDetailsManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.spring.container.ContainerManager;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.confluence.json.parser.JSONArray;
import com.atlassian.confluence.json.parser.JSONObject;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.http.HttpResponse;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.user.User;
import com.atlassian.confluence.security.SpacePermissionManager;




import com.atlassian.confluence.setup.settings.SettingsManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminServlet extends HttpServlet {

	 private final RequestFactory<?> requestFactory;
	    private SettingsManager settingsManager;
	
	String allUsers="";
	final NotificationManager notificationManager = (NotificationManager) ContainerManager
			.getComponent("notificationManager");
	DefaultUserDetailsManager userDetailsManager = (DefaultUserDetailsManager) ContainerManager
			.getComponent("userDetailsManager");
	private final UserManager userManager;
	private final LoginUriProvider loginUriProvider;
	private final TemplateRenderer renderer;
	private final SpaceManager spaceManager;
	private final SpacePermissionManager spacePermissionManager;
	UserAccessor userAccessor;
	private static final Logger log = LoggerFactory
			.getLogger(AdminServlet.class);
	public String allErrors="";
	public AdminServlet(UserManager userManager,
			LoginUriProvider loginUriProvider, TemplateRenderer renderer,
			SpaceManager spaceManager, UserAccessor userAccessor,
			SpacePermissionManager spacePermissionManager,RequestFactory<?> requestFactory, SettingsManager settingsManager) {
		this.userManager = userManager;
		this.loginUriProvider = loginUriProvider;
		this.renderer = renderer;
		this.spaceManager = spaceManager;
		this.userAccessor = userAccessor;
		this.spacePermissionManager = spacePermissionManager;
		this.settingsManager=settingsManager;
		this.requestFactory=requestFactory;
		log.error("SPACE MANAGER" + spaceManager);

	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String username = userManager.getRemoteUsername(request);
		if (username == null || !userManager.isSystemAdmin(username)) {
			redirectToLogin(request, response);
			return;
		}
	//	getAllWatchers();
	
	

		response.setContentType("text/html;charset=utf-8");

		Map<String, Object> context = MacroUtils.defaultVelocityContext();

		

		
	

		String settings = "NOT INIT";
//		com.atlassian.user.search.page.Pager<User> list = userAccessor.getUsers();
//		Iterator itr=list.iterator();
//		
//		ArrayList<User> listofUsers=new ArrayList<User>();
//		 while(itr.hasNext()) {
//	         User element =(User) itr.next();
//	         listofUsers.add(element);
//	        
//	      }
		
		
		if (NexmoEventListener.configSettings != null)
			settings = NexmoEventListener.configSettings.getFromuser() + ""
					+ NexmoEventListener.configSettings.getSelectedspace();

		context.put("spaceManager", spaceManager);
		context.put("w", settings);
		context.put("allUsers", allUsers);
		context.put("error", allErrors);
		// context.put("otherwindowsCustomVar", otherCustomVar);
		// Render the Template
		// String result = VelocityUtils.getRenderedTemplate("admin.vm",
		// context);

		renderer.render("admin.vm", context, response.getWriter());
		// renderer.render("admin.vm", response.getWriter());
	}

	
	public void getAllWatchers()
	{
		try{
			allErrors+="Starting the call ";
//			DefaultHttpClient client = new DefaultHttpClient();
//			HttpResponse response = null;
//			 
//			HttpGet request = new HttpGet("http://localhost:8090/json/listwatchers.action?pageId=2359351");
//		
//			
//			try{
//				
//				response = client.execute(request);
//				final int statusCode = response.getStatusLine().getStatusCode();
//				allErrors+=response.toString()+response.getStatusCode()+response.getStatusMessage();
//				
//			}
//			catch(Exception e){
//				allErrors+=e.getMessage();
//			}
			
//			allErrors+="Starting the call ";
//			URL google = new URL("http://localhost:8090/json/listwatchers.action?pageId=2359351");
//			BufferedReader inst = new BufferedReader(new InputStreamReader(
//					google.openStream()));
//			String inputLine;
//			String response = "RESPONSE";
//
//			while ((inputLine = inst.readLine()) != null) {
//				// Process each line.
//				response += inputLine;
//					
//			}
//
//			allUsers+=response;
//			allErrors+="gettings response ";
//			
//			JSONObject parent=new JSONObject(response);
//			JSONArray pageWatchers=parent.getJSONArray("pageWatchers");
//			JSONArray spaceWatchers=parent.getJSONArray("spaceWatchers");
//			
//			String s="Size is "+pageWatchers.length()+"  "+spaceWatchers.length();
//			log.error("MESSAGE NEXMO : " + response);
//			inst.close();
			allErrors+="closing the call ";
		}
		catch(Exception e){
			allErrors+=e.getMessage();
		}
	}
	public ArrayList<User> getUsersOfSpace(boolean isWatchingOnly, Space space,
			Page page,ArrayList<User> listofUsers) {

		if (space == null) {
		
			return null;
		}
	
		try {

			
			
			
		
			if (isWatchingOnly == true) {
				ArrayList<User> watchers = new ArrayList<User>();
				
				
				for (User user : listofUsers) {
					
					allUsers+="All user:"+user.getName()+" "+listofUsers.size();
					boolean isWatching = notificationManager
							.isUserWatchingPageOrSpace(user, space, page);

				
					if (isWatching) {
						watchers.add(user);
						String phoneno = userDetailsManager.getStringProperty(user,
								"phone");
						allErrors+="[Watching "+user.getFullName()+" "+space.getName()+"-"+phoneno+"]";
					}

				}
			
				return watchers;

			} else {

				return listofUsers;
			}

		} catch (Exception e) {
			allErrors+=e.getMessage()+"</br>";
			log.error(e.getMessage());
			return null;
		}

	}

	private void redirectToLogin(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.sendRedirect(loginUriProvider.getLoginUri(getUri(request))
				.toASCIIString());
	}

	private URI getUri(HttpServletRequest request) {
		StringBuffer builder = request.getRequestURL();
		if (request.getQueryString() != null) {
			builder.append("?");
			builder.append(request.getQueryString());
		}
		return URI.create(builder.toString());
	}
}
