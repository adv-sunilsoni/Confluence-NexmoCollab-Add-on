# Welcome to the Confluence NexmoCollab Add-on!
## Introduction
NexmoCollab add-on provides extended notification feature in Confluence (for Confluence Hosted on Server or Data Center options). Confluence users can receive notification on their mobile via SMS. NexmoCollab add-on allows to send page created and content updated notifications via SMS to the watchers of the page and space. Confluence administrator can configure Confluence’s Spaces, which require SMS notification and also enable and disable the selected events. 
## Use case
Build add-on for Confluence to send SMS notification to all the subscribed users of a specific space whenever any new page is created or an existing page is updated.
For example - any new knowledgebase article is created in XYZ space, the add-on will send SMS to all the users who are watchers (Subscribed to space) on that space.
## Prerequisites 

- Preinstalled and running Confluence server hosted on server
- Confluence user with Administrative privileges
- The Confluence Add-on requires Nexmo subscription and corresponding Nexmo API keys (Keys and Secret). To access the Nexmo API keys, see appendix section
## Features 
- Sends SMS to page and space watchers
- Enable and disable SMS functionality for the Confluence platform
- Allow users to enable and disable SMS feature for individual events such as Page Create and Page Update events
- It supports all the custom page templates, but Blog post
