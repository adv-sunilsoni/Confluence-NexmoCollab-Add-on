AJS.toInit(function() {

var baseUrl = AJS.$("meta[name='confluence-base-url']").attr("content");

  
  function populateForm() {
  
  
  		AJS.$("#messagecreate").hide();
      	AJS.$("#messageupdate").hide();
    $("#progressSpinner").show();
	   $("#progressText").text("Getting configuration"); 
	   
	   
	     // AJS.$("#thresholdamount").hide();
	    // AJS.$("#fromuser").hide();
	      $("#upm-incompatible-plugins-msg").hide();
    AJS.$.ajax({
      url: baseUrl + "/rest/nexmo-admin/1.0/",
      dataType: "json",
      success: function(config) {
		
		 $("#progressSpinner").hide();
        AJS.$("#key").attr("value", config.key);
        AJS.$("#secret").attr("value", config.secret);
     //   AJS.$("#thresholdamount").attr("value", config.thresholdamount);
       
        if(config.fromuser=="ERROR")
        {
        
         $("#aftervalidation").hide();
          $("#upm-incompatible-plugins-msg").show();
            $("#validatekeys").show();
	 	 $("#error").text("Please enter valid Nexmo Key and Secret.");
	 	   
        // alert("Please enter valid Nexmo Key and Secret.");
       
        
        }
        else{
          $("#aftervalidation").show();
           $("#validatekeys").hide();
        }
        
        
        
        
        
        AJS.$("#smsenable").attr("value", config.smsenable);
       
        AJS.$("#pagecreateeventmessage").attr("value", config.pagecreateeventmessage);
        AJS.$("#pageupdateeventmessage").attr("value", config.pageupdateeventmessage);
     //   AJS.$("#pageremoveeventmessage").attr("value", config.pageremoveeventmessage);
       
        
        if(config.smsenable==1)
        {
        $("#smsenable").attr('checked', true);
          $("#eventspanel :input").removeAttr('disabled');
         
         
		  
		  
		  
         
        }
        else
        {
         $("#smsenable").attr('checked', false);
           $("#eventspanel :input").attr('disabled', true);
        }
        
        
    
        
         if(config.pagecreateevent==1)
        {
        $("#pagecreateevent").attr('checked', true);
        AJS.$("#messagecreate").hide(300);
        }
        else
        {
        AJS.$("#messagecreate").hide(300);
         $("#pagecreateevent").attr('checked', false);
        }
        
        
         if(config.pageupdateevent==1)
        {
        $("#pageupdateevent").attr('checked', true);
        AJS.$("#messageupdate").hide(300);
        
        }
        else
        {
        AJS.$("#messageupdate").hide(300);
         $("#pageupdateevent").attr('checked', false);
        }
        
        
        
        var froms=config.fromuser;
        var allfroms = froms.split(';');
    	  $('#fromNumbers')
   		 .empty();    
       
    
            AJS.$("#fromuser").attr("value",config.fromuserDefault);
        
                 for(i=0;i<allfroms.length-1;i++)
    	          {
    	      
    	      		 var allNum=allfroms[i].split(':');	
    	             var str=allNum[0]+"   ("+allNum[1]+")"
    	           
    	  		
    	  		
    	  		
    	  		if(allNum[0]==config.fromuserDefault)
    	  		{
    	  		
    	  		
    	  			 $('<option/>', {
   					 'text': str,
   					 'value':allNum[0],
   					 'selected':'selected'
					}).appendTo('#fromNumbers');
    	  		
    	  		
    	  		}
    	  		else{
    	  	
    	  					 $('<option/>', {
   					 'text': str,
   					 'value':allNum[0],
   					
					}).appendTo('#fromNumbers');
    	  		
    	  		
    	  		}
    	  		
    	  		    
    	  		  
    	  			
    	  		
    	  		
    	         	 
    	         	 
    	         	 
    	           }
    	           
    	          
    	          
		
        $("#fromNumbers select").val(config.fromuserDefault);
        var sel=config.selectedspace;
        var arr = sel.split(';');
        
        
    
        $("#listspace > option").each(function() {
    	         
    	    
    	          for(i=0;i<arr.length-1;i++)
    	          {
    	      
    	           if(arr[i]==this.value)
    	           {
    	          $('<option/>', {
   					 'text': this.text,
   					 'value':this.value
					}).appendTo('#selectedspace');
    	         	 $(this).remove();
    	           }
    	          }
    	          
			});  
        
                
      },
       error: function (XMLHttpRequest, textStatus, errorThrown) {
         alert("Could not retrieve configuration");
          $("#progressSpinner").hide();
     }
    });
  }
  function validateKeys()
  {
  
    var apiKeys=AJS.$("#key").attr("value");
    var secret=AJS.$("#secret").attr("value");
    apiKeys=$.trim(apiKeys)
    alert(apiKeys);
    secret=$.trim(secret);
    alert(secret);
    urls= "https://rest.nexmo.com/account/numbers/"+apiKeys+"/"+secret+"";
 
   

    $.ajax({
      url: urls,
      type: "GET",
      dataType: "jsonp",
     cache: false,
     crossDomain: true,
     processData: true,
     contentType: "application/json",
	  success:function(resp){
	  		
	  		 alert(JSON.stringify(resp));
		 //  populateForm();
		 //  alert(resp.numbers[0].msisdn);
	  },
	   error: function (XMLHttpRequest, textStatus, errorThrown) {
         alert("error");
     }
    });
  
  
  }
  function updateConfig() {
	  
	  
	  
	  if( AJS.$("#key").attr("value")=="")
	  {
	//  alert("Please enter the Nexmo Key");
	 $("#error").text("Please enter the Nexmo Key");
	  $("#upm-incompatible-plugins-msg").show();
	 
	
		$("#key").focus();
	  return ;
	  }
	  if( AJS.$("#secret").attr("value")=="")
	  {

	    $("#error").text("Please enter the Nexmo Secret");
	  $("#upm-incompatible-plugins-msg").show();
	
	  $("#secret").focus();
	  return ;
	  }
	  
	  
	  
	 var selected="";
	$("#selectedspace > option").each(function() {
    	selected+=this.value+";";
	}); 
	
	 var smsenable_="0";
	 var pagecreate_="0";
	 var pageupdate_="0";
	 var pageremove_="0";
	 
	 
	 if(AJS.$("#smsenable").is(":checked"))
	 {
	  smsenable_="1";
	   
	  	
	  
	 }
	 else{
	 
	//  alert("SMS will not be sent to the contact list");
	    $("#upm-incompatible-plugins-msg").show();
	  $("#error").text("SMS will not be sent to the contact list");
	  	$('#smsenable').focus();
	 }
	 
	 
	 if(AJS.$("#pagecreateevent").is(":checked"))
	 {
	  pagecreate_="1";
	  
	   
	 }
	 
	  if(AJS.$("#pageupdateevent").is(":checked"))
	 {
	  pageupdate_="1";
	 
	 }
	 
	 
	 
	var defaultFromNumber = $('#fromNumbers').find(":selected").val();
	 var datas='{ "key": "' + AJS.$("#key").attr("value") + '","secret": "' + AJS.$("#secret").attr("value") + '", "thresholdamount": "' +  baseUrl+ '","fromuser": "' +  AJS.$("#fromuser").attr("value") + '","smsenable": "' + smsenable_+ '","selectedspace": "' + selected + '","pagecreateevent": "' +pagecreate_ +'","pagecreateeventmessage": "' + AJS.$("#pagecreateeventmessage").attr("value")+'","pageupdateevent": "' +pageupdate_ +'","pageupdateeventmessage": "' + AJS.$("#pageupdateeventmessage").attr("value")+'","pageremoveevent": "' + pageremove_+'","pageremoveeventmessage": "NO","fromuserDefault":"'+defaultFromNumber+'" }';
	  $("#progressSpinner").show();
	   $("#progressText").text("Please wait..."); 
	  $("#upm-incompatible-plugins-msg").hide();
    AJS.$.ajax({
      url: baseUrl + "/rest/nexmo-admin/1.0/",
      type: "PUT",
      contentType: "application/json",
      data: datas,
      processData: false,
	  success:function(config){
	  	
	   
	  	config = config.replace(/(\r\n|\n|\r)/gm,"");
	  	if(config=="ERROR")
        {
       
        
           
           // alert("Please enter valid Nexmo Key and Secret.");
            $("#progressSpinner").hide();
              populateForm();
         
		
        }
        else{
        
        	
        	$("#progressSpinner").hide();
        	
		    alert("Configuration settings saved successfully.");
		    window.location.reload();
        }
	  	
	  
		    
	  },
	  error: function (XMLHttpRequest, textStatus, errorThrown) {
         $("#progressSpinner").hide();
           //	  alert("Failed to save configuration");	
            $("#error").text("Failed to save configuration.");
           	  $("#upm-incompatible-plugins-msg").show();
		      
     }
    });
  }  
  populateForm();

	
	AJS.$("#pageupdateevent").click(function() {
	
		
	
	  });
	  
	  AJS.$("#pagecreateevent").click(function() {
	
		
	
	  });
	
	AJS.$("#smsenable").click(function() {
	
		  if(AJS.$("#smsenable").is(":checked"))
		  {
		   $("#eventspanel :input").removeAttr('disabled');
		  }
		  else{
		  
		   $("#eventspanel :input").attr('disabled', true);;
		  }
	
	  });

	AJS.$("#remove").click(function() {
		$('#selectedspace option:selected').remove().appendTo('#listspace');
	
	
	  });
	AJS.$("#add").click(function() {
		$('#listspace option:selected').remove().appendTo('#selectedspace');
	
	
	  });
  AJS.$("#admin").submit(function(e) {
    e.preventDefault();
		//validateKeys();
       updateConfig();
  });
});
