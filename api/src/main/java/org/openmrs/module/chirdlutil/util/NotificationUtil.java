/**
 * 
 */
package org.openmrs.module.chirdlutil.util;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.notification.MessageException;



public class NotificationUtil
{
    protected static final Log log = LogFactory.getLog(NotificationUtil.class);

    public static void sendEmail(String body, String recipients, String subject) {

		try {
			send(body, recipients, subject, ChirdlUtilConstants.MESSAGE_CANNOT_SEND_EMAIL_TO_SUPPORT);

		} catch (Exception e) {
			log.error("Error sending support email. ", e);
			return;
		}
	}
	
	public static void sendSupportEmailNotification(String message) {

		try {
			  
			String subject = Context.getAdministrationService().getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_SUPPORT_EMAIL_SUBJECT);
			String recipients = Context.getAdministrationService().getGlobalProperty(
					 ChirdlUtilConstants.GLOBAL_PROP_SUPPORT_EMAIL);
						
			StringBuilder body = new StringBuilder("Support");
			body.append(ChirdlUtilConstants.GENERAL_INFO_COMMA);
			body.append(ChirdlUtilConstants.GENERAL_INFO_CARRIAGE_RETURN_LINE_FEED);
			body.append(ChirdlUtilConstants.GENERAL_INFO_CARRIAGE_RETURN_LINE_FEED);
			body.append("The following issue occurred at " + new Date() + ":");
			body.append(ChirdlUtilConstants.GENERAL_INFO_CARRIAGE_RETURN_LINE_FEED);
			body.append(ChirdlUtilConstants.GENERAL_INFO_CARRIAGE_RETURN_LINE_FEED);
			body.append(ChirdlUtilConstants.GENERAL_INFO_SINGLE_SPACE);
			body.append(ChirdlUtilConstants.GENERAL_INFO_SINGLE_SPACE);
			body.append(ChirdlUtilConstants.GENERAL_INFO_SINGLE_SPACE);
			body.append(ChirdlUtilConstants.GENERAL_INFO_SINGLE_SPACE);
			body.append(message);
			body.append(ChirdlUtilConstants.GENERAL_INFO_CARRIAGE_RETURN_LINE_FEED);
			body.append("Please check server logs for more information.");
			body.append(ChirdlUtilConstants.GENERAL_INFO_CARRIAGE_RETURN_LINE_FEED);
			body.append("Regards");
			body.append(ChirdlUtilConstants.GENERAL_INFO_COMMA);
			body.append(ChirdlUtilConstants.GENERAL_INFO_CARRIAGE_RETURN_LINE_FEED);
			body.append(ChirdlUtilConstants.GENERAL_INFO_CARRIAGE_RETURN_LINE_FEED);
			body.append("Support Notification");
			
			send(body.toString(), recipients, subject, ChirdlUtilConstants.MESSAGE_CANNOT_SEND_EMAIL_TO_SUPPORT);

		} catch (Exception e) {
			log.error("Error sending support email. ", e);
			return;
		}
	}
    
    /**
     * Sends the mail message.
     * @param body - The body of the email.
     * @param recipients - comma delimited list of recipient email addresses
     * @param subject - The subject of the email.
     * @param msg - message to log error
     */
    public static boolean send(String body, String recipients, String subject, String msg) {
        if (StringUtils.isBlank(recipients)) {
            log.error("Email recipients not specified" + msg);
            return false;
        }
   
        String sender = Context.getAdministrationService().getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_MAIL_FROM);
        if (StringUtils.isBlank(sender)) {
            log.error("Global Property " + ChirdlUtilConstants.GLOBAL_PROP_SUPPORT_EMAIL + " does not contain a valid email address"
                + msg);
            return false;
        }
        
        try {
            Context.getMessageService().sendMessage(recipients, sender, subject, body);
        }
        catch (MessageException e) {
            log.error("Error creating email message" + msg, e);
            return false;
        }
    	
    	return true;
    }
     
}