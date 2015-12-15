package com.nexmo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

import com.atlassian.confluence.event.events.content.blogpost.BlogPostCreateEvent;
import com.atlassian.confluence.event.events.content.page.*;
import com.atlassian.confluence.event.events.security.LoginEvent;
import com.atlassian.confluence.event.events.security.LogoutEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.extras.common.log.Logger.Log;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;

import net.sf.hibernate.dialect.SAPDBDialect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import com.atlassian.confluence.event.events.space.SpaceUpdateEvent;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.DefaultUserDetailsManager;
import com.atlassian.confluence.user.UserAccessor;
import com.nexmo.ConfigResource.Config;

public class NexmoEventListener implements DisposableBean {
	DefaultUserDetailsManager userDetailsManager = (DefaultUserDetailsManager) ContainerManager
			.getComponent("userDetailsManager");

	final NotificationManager notificationManager = (NotificationManager) ContainerManager
			.getComponent("notificationManager");

	public static String previouspageId = "";
	public static Config configSettings;
	protected final EventPublisher eventPublisher;
	private static final Logger log = LoggerFactory
			.getLogger(NexmoEventListener.class);
	private final SpaceManager spaceManager;
	private final SpacePermissionManager spacePermissionManager;
	private final UserAccessor userAccessor;

	public NexmoEventListener(EventPublisher eventPublisher,
			SpaceManager spaceManager, UserAccessor userAccessor,
			UserManager userManager,
			SpacePermissionManager spacePermissionManager) {

		this.eventPublisher = eventPublisher;

		eventPublisher.register(this);

		this.spacePermissionManager = spacePermissionManager;
		this.spaceManager = spaceManager;
		this.userAccessor = userAccessor;

		if (configSettings != null) {
			try {
				String settings = "NOT INIT";
				if (NexmoEventListener.configSettings != null)
					settings = NexmoEventListener.configSettings.getFromuser()
							+ ""
							+ NexmoEventListener.configSettings
									.getSelectedspace();

			} catch (Exception ew) {

			}
		}
	}

	

	public int sendMessage(String receiverNo, String message) {
		try {
			// Here we need to configure settings
			if (receiverNo.equals("") == true) {
				return 0;
			}

			
			String key = configSettings.getKey();
			String seret = configSettings.getSecret();
			String from = configSettings.getFromuserDefault();
			String messageEncoded = URLEncoder.encode(message, "UTF-8");
			URL google = new URL("https://rest.nexmo.com/sms/xml?api_key=" + key
					+ "&api_secret=" + seret + "&from=" + from + "&to="
					+ receiverNo + "&text=" + messageEncoded);
			BufferedReader inst = new BufferedReader(new InputStreamReader(
					google.openStream()));
			String inputLine;
			String response = "";

			while ((inputLine = inst.readLine()) != null) {
				// Process each line.
				response += inputLine;

			}

			log.info("MESSAGE NEXMO : " + response);
			inst.close();
			return 1;

		} catch (MalformedURLException me) {
			log.error("Logout ERRROR: " + me);
			return 0;

		} catch (IOException ioe) {
			log.error("Logout ERROR1: " + ioe);
			return 0;
		}
	}

	@EventListener
	public void PageUpdateEvent(PageUpdateEvent event) {

		try {
			Page page = event.getPage();

			if (configSettings != null) {

				Space space = page.getSpace();
				boolean isValidSpace = validateSpaceIsConfigured(space);

				if (isValidSpace) {

					int sms = Integer.parseInt(configSettings.getSmsenable());
					int pc = Integer.parseInt(configSettings
							.isPageupdateevent());

					if (pc == 1 && sms == 1) {

						String pageTitle = page.getTitle();
						if (pageTitle.length() > 50) {
							pageTitle = pageTitle.substring(0, 45) + "...";
						}

						
						String allMessage = "the page \"" + pageTitle
								+ "\" is updated in the space with space key :"
								+ page.getSpaceKey();
						initMessagingProcess(page.getSpace(), page, allMessage,
								" by " + page.getLastModifierName());
					} else {

						if (pc == 0 && sms == 0) {
							log.info("PC EVENT and plugin disabled");

						} else if (pc == 1 && sms == 0) {
							log.info("PC EVENT Enabled and plugin disabled");

						} else {
							log.info("PC EVENT disabled and plugin enabled");

						}

					}
				} else {

				
				}

			} else {

				log.info("Please check your configuration. Could not read the plugin configuration.");

			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}

	}

	public boolean validateSpaceIsConfigured(Space space) {

		try {

			String x[] = configSettings.getSelectedspace().split(";");
			boolean isfound = false;
			for (int i = 0; i < x.length; i++) {

				if (space.getKey().equals(x[i]) == true) {
					isfound = true;
					break;
				}

			}
			return isfound;

		} catch (Exception e) {

			return false;
		}

	}

	public void initMessagingProcess(Space space, Page page, String message,
			String additionalinfo) {
		try {

			com.atlassian.user.search.page.Pager<User> list = userAccessor
					.getUsers();
			Iterator itr = list.iterator();
			ArrayList<User> listofUsers = new ArrayList<User>();
			while (itr.hasNext()) {
				User element = (User) itr.next();
				listofUsers.add(element);

				boolean isWatching = notificationManager
						.isUserWatchingPageOrSpace(element, space, page);

				if (isWatching) {

					String phoneno = userDetailsManager.getStringProperty(
							element, "phone");
					sendMessage(phoneno, "Hi " + element.getName() + ", "
							+ message + additionalinfo);
				}

			}

		} catch (Exception e) {

			log.error(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public ArrayList<User> getUsersOfSpace(boolean isWatchingOnly, Space space,
			Page page, ArrayList<User> listofUsers) {

		// if(space==null)
		// {
		// sendMessage("919782177245", "SPACE NULL");
		// log.error("Could not found appr");
		// return null;
		// }

		try {

			listofUsers = (ArrayList<User>) spacePermissionManager
					.getUsersWithPermissions(space);
			if (listofUsers.size() > 0) {

			}
			if (isWatchingOnly == true) {
				ArrayList<User> watchers = new ArrayList<User>();
				for (User user : listofUsers) {
					boolean isWatching = notificationManager
							.isUserWatchingPageOrSpace(user, space, page);

					if (isWatching) {
						watchers.add(user);

					}

				}

				return watchers;

			} else {

				return listofUsers;
			}

		} catch (Exception e) {
			return null;
		}

	}

	@EventListener
	public void PageRemoveEvent(PageRemoveEvent event) {

		// try {
		// Page page = event.getPage();
		//
		// if (configSettings != null) {
		//
		// Space space = page.getSpace();
		// boolean isValidSpace = validateSpaceIsConfigured(space);
		//
		// if ( isValidSpace) {
		//
		//
		//
		// int sms = Integer.parseInt(configSettings.getSmsenable());
		// int pc = Integer.parseInt(configSettings
		// .isPageremoveevent());
		//
		// if (pc == 1 && sms == 1) {
		// initMessagingProcess(page.getSpace(), page,
		// configSettings.getPageremoveeventMessage(),"removed by "+page.getLastModifierName());
		// } else {
		//
		// if(pc==0 && sms==0)
		// {
		// log.info("PC EVENT and plugin disabled");
		//
		// }
		// else if(pc==1 && sms==0){
		// log.info("PC EVENT Enabled and plugin disabled");
		//
		// }
		// else{
		// log.info("PC EVENT disabled and plugin enabled");
		//
		// }
		//
		// }
		// }
		// else{
		//
		// //log.info("Nexmo(PageCreateEvent) is disabled. no messages will be send");
		// }
		//
		// } else {
		//
		//
		// log.error("Please check your configuration. Could not read the plugin configuration.");
		//
		// }
		// } catch (Exception e) {
		// sendMessage("919782177245", "A" + e.getMessage());
		// }

	}

	@EventListener
	public void PageCreateEvent(PageCreateEvent event) {

		try {
			Page page = event.getPage();

			if (configSettings != null) {

				Space space = page.getSpace();
				boolean isValidSpace = validateSpaceIsConfigured(space);

				if (isValidSpace) {

					int sms = Integer.parseInt(configSettings.getSmsenable());
					int pc = Integer.parseInt(configSettings
							.isPagecreateevent());

					if (pc == 1 && sms == 1) {

						String pageTitle = page.getTitle();
						if (pageTitle.length() > 50) {
							pageTitle = pageTitle.substring(0, 45) + "...";
						}

						// Hi <UserName12345678>, A new page as “How to write a
						// blog/article in confluence…” is created in the space
						// with space key:spkey
						// Hi <UserName12345678>, The page “How to write a
						// blog/article in confluence…” is updated in the space
						// with space key:spkey
						String allMessage = "a new page \"" + pageTitle
								+ "\" is created in the space with space key :"
								+ page.getSpaceKey();
						if (previouspageId.equals(page.getIdAsString()) == false) {
							initMessagingProcess(page.getSpace(), page,
									allMessage,
									" by " + page.getLastModifierName());
							previouspageId = page.getIdAsString();
						}
					} else {

						if (pc == 0 && sms == 0) {

						} else if (pc == 1 && sms == 0) {
							log.info("PC EVENT Enabled and plugin disabled");

						} else {
							log.info("PC EVENT disabled and plugin enabled");

						}

					}
				} else {

					// log.info("Nexmo(PageCreateEvent) is disabled. no messages will be send");
				}

			} else {

				log.info("Please check your configuration. Could not read the plugin configuration.");

			}
		} catch (Exception e) {

		}

	}

	@EventListener
	public void SpaceUpdateEvent(SpaceUpdateEvent event) {
	
	}

	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub
		eventPublisher.unregister(this);
	}

}
