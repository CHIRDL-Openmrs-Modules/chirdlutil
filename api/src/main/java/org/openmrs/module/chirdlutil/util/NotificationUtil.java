/**
 * 
 */
package org.openmrs.module.chirdlutil.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.notification.MessageException;

public class NotificationUtil {

    protected static final Log log = LogFactory.getLog(NotificationUtil.class);
    private static Map<Integer, Long> messageToTimeMap = new ConcurrentHashMap<>();
    private static long thresholdTime = 3600000; // Default to 60 minutes.

    private NotificationUtil() {
    }

    public static void sendEmail(String body, String recipients, String subject) {

        try {
            send(body, recipients, subject, ChirdlUtilConstants.MESSAGE_CANNOT_SEND_EMAIL_TO_SUPPORT);

        } catch (Exception e) {
            log.error("Error sending support email. ", e);
        }
    }

    public static void sendSupportEmailNotification(String message) {

        try {

            if (canSendMessage(message)) {

                String recipients = Context.getAdministrationService()
                        .getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_SUPPORT_EMAIL);

                String subject = Context.getAdministrationService()
                        .getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_SUPPORT_EMAIL_SUBJECT);
                String salutation = Context.getAdministrationService()
                        .getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_SUPPORT_EMAIL_SALUTATION);

                StringBuilder body = new StringBuilder(salutation);
                body.append(ChirdlUtilConstants.GENERAL_INFO_COMMA);
                body.append(ChirdlUtilConstants.GENERAL_INFO_CARRIAGE_RETURN_LINE_FEED);
                body.append(ChirdlUtilConstants.GENERAL_INFO_CARRIAGE_RETURN_LINE_FEED);
                body.append(ChirdlUtilConstants.NOTIFICATION_EMAIL_LEADING_TEXT + new Date());
                body.append(ChirdlUtilConstants.GENERAL_INFO_PERIOD);
                body.append(ChirdlUtilConstants.GENERAL_INFO_SINGLE_SPACE);
                body.append(ChirdlUtilConstants.GENERAL_INFO_SINGLE_SPACE);
                body.append(ChirdlUtilConstants.NOTIFICATION_EMAIL_SUPPORT_INSTRUCTIONS);
                body.append(ChirdlUtilConstants.GENERAL_INFO_CARRIAGE_RETURN_LINE_FEED);
                body.append(ChirdlUtilConstants.GENERAL_INFO_CARRIAGE_RETURN_LINE_FEED);
                body.append(ChirdlUtilConstants.GENERAL_INFO_SINGLE_SPACE);
                body.append(ChirdlUtilConstants.GENERAL_INFO_SINGLE_SPACE);
                body.append(ChirdlUtilConstants.GENERAL_INFO_SINGLE_SPACE);
                body.append(ChirdlUtilConstants.GENERAL_INFO_SINGLE_SPACE);
                body.append(message);
                body.append(ChirdlUtilConstants.GENERAL_INFO_CARRIAGE_RETURN_LINE_FEED);
                body.append(ChirdlUtilConstants.GENERAL_INFO_CARRIAGE_RETURN_LINE_FEED);
                body.append(ChirdlUtilConstants.NOTIFICATION_EMAIL_CLOSING);
                body.append(ChirdlUtilConstants.GENERAL_INFO_COMMA);
                body.append(ChirdlUtilConstants.GENERAL_INFO_CARRIAGE_RETURN_LINE_FEED);
                body.append(ChirdlUtilConstants.GENERAL_INFO_CARRIAGE_RETURN_LINE_FEED);
                body.append(ChirdlUtilConstants.NOTIFICATION_EMAIL_SUPPORT_SIGNATURE);

                send(body.toString(), recipients, subject, ChirdlUtilConstants.MESSAGE_CANNOT_SEND_EMAIL_TO_SUPPORT);
            }

            reconcileMessageMap();

        } catch (Exception e) {
            log.error("Error sending support email. ", e);
        }
    }

    /**
     * Sends the mail message.
     * 
     * @param body       - The body of the email.
     * @param recipients - comma delimited list of recipient email addresses
     * @param subject    - The subject of the email.
     * @param msg        - message to log error
     */
    public static boolean send(String body, String recipients, String subject, String msg) {
        if (StringUtils.isBlank(recipients)) {
            log.error("Email recipients not specified" + msg);
            return false;
        }

        String sender = Context.getAdministrationService().getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_MAIL_FROM);
        if (StringUtils.isBlank(sender)) {
            log.error("Global Property " + ChirdlUtilConstants.GLOBAL_PROP_SUPPORT_EMAIL
                    + " does not contain a valid email address" + msg);
            return false;
        }

        try {
            Context.getMessageService().sendMessage(recipients, sender, subject, body);
        } catch (MessageException e) {
            log.error("Error creating email message" + msg, e);
            return false;
        }

        return true;
    }

    /**
     * Checks if message has already been sent within a specified time frame. This
     * prevents sending multiple instances of the same message too frequently.
     * 
     * @param message
     * @returns false if this message has been sent already within the specified
     *          time threshold.
     */
    private static boolean canSendMessage(String message) {
        Long time = null;
        String threshold = Context.getAdministrationService()
                .getGlobalProperty(ChirdlUtilConstants.GLOBAL_PROP_MESSAGE_FREQUENCY_THRESHOLD);
        if (StringUtils.isNotBlank(threshold) && StringUtils.isNumeric(StringUtils.trim(threshold))) {
            thresholdTime = Integer.parseInt(threshold);
            thresholdTime = thresholdTime * 60000;
        }

        long currTime = System.currentTimeMillis();
        int messageHash = message.hashCode();

        /*If the key does not exist, it is added with value of currTime, and the return
         value will be currTime. If the key already exists, the return value will be the value for the existing
         key. Send the message if the value was just added.*/
        if ((time = messageToTimeMap.computeIfAbsent(messageHash, k -> currTime)) == currTime) {
            return true;
        }

        /* If time has exceeded the wait threshold, assign current time
         *  to that key and send the message.*/
        if ((currTime - time) > thresholdTime) {
            messageToTimeMap.put(messageHash, currTime);
            return true;
        }
        return false;

    }

    /**
     * Traces through the message map to determine what messages have last been sent
     * over the threshold mark. If they have, they will be removed from the map so
     * they will be allowed to be sent again.
     */
    private static void reconcileMessageMap() {

        synchronized (messageToTimeMap) {

            long currTime = System.currentTimeMillis();

            ArrayList<Integer> removeKeys = new ArrayList<>();
            Set<Entry<Integer, Long>> entries = messageToTimeMap.entrySet();

            Iterator<Entry<Integer, Long>> iter = entries.iterator();
            while (iter.hasNext()) {
                Entry<Integer, Long> entry = iter.next();
                Integer messageHash = entry.getKey();
                Long time = entry.getValue();
                if ((currTime - time) > thresholdTime) {
                    removeKeys.add(messageHash);
                }
            }

            for (Integer key : removeKeys) {
                messageToTimeMap.remove(key);
            }
        }
    }

}