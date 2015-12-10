package com.nexmo;
import java.util.Map;
import java.util.List;
import java.util.Random;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.user.User;
import com.opensymphony.util.TextUtils;
public class CustomMacro  implements Macro  {

	
	 private String helloworld="Sunil";
	 private final PageManager pageManager;
	    private final SpaceManager spaceManager;
	    private static final String MACRO_BODY_TEMPLATE = "listspaces.vm";
	    public CustomMacro(PageManager pageManager, SpaceManager spaceManager)
	    {
	        this.pageManager = pageManager;
	        this.spaceManager = spaceManager;
	    }

		@Override
		public String execute(Map<String, String> params, String arg1,
				ConversionContext arg2) throws MacroExecutionException {Map<String, Object> context = MacroUtils.defaultVelocityContext();

		        // check if the user supplied a "greeting" parameter
		        if (params.containsKey("greeting"))
		        {
		            context.put("greeting", params.get("greeting"));
		        }
		        else
		        {
		            // we'll construct one. get the currently logged in user and display
		            // their name
		            User user = AuthenticatedUserThreadLocal.getUser();
		            if (user != null)
		            {
		                context.put("greeting", "Hello " + user.getFullName());
		            }
		        }

		        // get all spaces in this installation
		        @SuppressWarnings("unchecked")
		            List<Space> spaces = spaceManager.getAllSpaces();
		        context.put("totalSpaces", spaces.size());

		        if (!spaces.isEmpty())
		        {
		            // pick a space at random and find its home page
		            Random random = new Random();
		            int randomSpaceIndex = random.nextInt(spaces.size());
		            Space randomSpace = spaces.get(randomSpaceIndex);
		            context.put("spaceName", randomSpace.getName());

		            Page homePage = randomSpace.getHomePage();
		            context.put("homePageTitle", homePage.getTitle());
		            context.put("homePageCreator", homePage.getCreatorName());
		        }

		        // render the Velocity template with the assembled context
		        return VelocityUtils.getRenderedTemplate(MACRO_BODY_TEMPLATE, context);}

		@Override
		public BodyType getBodyType() {
			// TODO Auto-generated method stub
			  return BodyType.NONE;
		}

		@Override
		public OutputType getOutputType() {
			// TODO Auto-generated method stub
			return OutputType.BLOCK;
		}
	
	
}
