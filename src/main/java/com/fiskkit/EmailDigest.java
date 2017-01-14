package com.fiskkit;

import static humanize.Humanize.metricPrefix;

import java.text.DateFormat;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fiskkit.emailnotify.servlet.EmailNotifyWorker;
import com.fiskkit.model.Article;
import com.fiskkit.model.EmailPriority;
import com.fiskkit.model.User;
import com.fiskkit.network.MYSQLAccess;
import com.fiskkit.util.frequency.Frequency;
import com.fiskkit.util.picture.PictureUtil;
import com.jobhive.sakimonkey.MandrillAsyncClient;
import com.jobhive.sakimonkey.api.async.callback.ObjectResponseCallback;
import com.jobhive.sakimonkey.data.request.Message;
import com.jobhive.sakimonkey.data.request.Message.Recipient;
import com.jobhive.sakimonkey.data.request.Message.Var;
import com.jobhive.sakimonkey.data.response.MessageStatus;

/**
 * Created by joshuaellinger on 3/25/15.
 */
public class EmailDigest {
	private static final Logger LOGGER = Logger.getLogger("");
    
    
    private static final DateFormat TIME_FORMAT = DateFormat.getTimeInstance(DateFormat.LONG);
	private static final TreeSet<Long> RUN_SET = new TreeSet<Long>();
	
    private Frequency frequency;
    private int USER_PROCESS_LIMIT = 100;
    private final String apiKey;
    private MandrillAsyncClient mandrillAsyncClient = null; // lazy instantiation
    private PictureUtil pictureUtil = null; // lazy instantiation
    Map<String, String> pictureSettings = new HashMap<String, String>();

    public EmailDigest(int userProcessLimit, String apiKey, Frequency frequency) {
        this.USER_PROCESS_LIMIT = userProcessLimit;
        this.apiKey = apiKey;
        
        
        this.frequency = frequency;
        pictureSettings.put("type", "large-square");
        
        
    }
    
    public void run(){
    	
    	LOGGER.info("Email Scheduler:" + EmailNotifyWorker.VERSION);
    	
    	
    	
    	final long runStart = System.currentTimeMillis();
    	synchronized (RUN_SET) {
    		RUN_SET.add(runStart);
    	}
    	
    	
    	try {
    		runx();
    	} catch (Exception any){
    		LOGGER.log(Level.SEVERE, "", any);
    	} finally {
    		cleanup(runStart);
    	}
    	
    }
    

    private void runx() {
    	
    	LOGGER.info("... emailDigest.runx()");
    	
        Deque<EmailPriority> prioritylist;
        Message message = new Message();
        boolean sendToMandrill = false;
//        String templateName = "fiskkit-daily-04-2015";
        String templateName = "daily-digest-wip-template";
        LOGGER.log(Level.FINE, "template = " + templateName);
        LOGGER.info("template = " + templateName);

        message.setFromEmail("contact@fiskkit.com");
        message.setSubject("{{user.first_name}}, {{subject_end}}");

        prioritylist = MYSQLAccess.getPriorityList(USER_PROCESS_LIMIT, frequency);
        LOGGER.log(Level.FINE, "prioritylist.size()=" + prioritylist.size());
        LOGGER.log(Level.INFO, "prioritylist.size()=" + prioritylist.size());
        // Calculate global stats.

        // Calculate per user stats, combine stats and send emails.
        while (!prioritylist.isEmpty()) {
            List<Article> articleFiskList;
            List<Article> articleRespectList;
            String subjectEnd = new String();

            // Initalize send email variable.
            boolean sendEmail = false;
            EmailPriority priority = prioritylist.pop();

            // Get the user object for current user.
            User user = MYSQLAccess.getUser(priority.getUserId());
            
            // FIXME added code to guard against spamming during testing
            if (user == null){
            	
            	// FIXME
            	//LOGGER.log(Level.FINE, "User not current ... skipping:" + priority.getUserId());
            	continue;
            } else {
            	LOGGER.log(Level.FINE, "User current ... processing ... :" + priority.getUserId());
            }
            
            
            LOGGER.log(Level.FINE, "Email for " + user.getName());

            // Set the image for the user.
            

            // Create a recipient to send.
            Recipient recipient = new Recipient(user.getEmail(), user.getName());
            recipient.addVar(new Var("user", user));

            // Check respects for user in the last 24 hours.
            int userRespectCount = MYSQLAccess.getTotalUserRespectCountBetween(priority.getUserId(), frequency.getFormattedStart(), frequency.getFormattedFinish());
            // Add the total respect count.
            final Var respectCount = new Var("new_respect_count", userRespectCount);
            recipient.addVar(respectCount);
            Var respectCountHumanized = new Var("new_respect_count_humanized", metricPrefix(userRespectCount));
            recipient.addVar(respectCountHumanized);

            articleRespectList = MYSQLAccess.getUserArticleRespectBetween(priority.getUserId(), frequency.getFormattedStart(), frequency.getFormattedFinish());
            LOGGER.log(Level.FINE, "Email for " + user.getName() + ":respects=" + articleRespectList.size());

            // If 1 or more add the user who added the respect
            if (!articleRespectList.isEmpty()) {
                if (articleRespectList.size() > 3) {
                    articleRespectList = articleRespectList.subList(0, 3);
                }
                
               

                // Only return top 3 articles in respect list.
                recipient.addVar(new Var("new_respects_articles", articleRespectList));

                // We will be sending an email since we have data.
                sendEmail = true;
                LOGGER.log(Level.FINE, "Sending email for " + user.getName());
                
                if (subjectEnd.isEmpty()) {
                    subjectEnd = "You got " + metricPrefix(userRespectCount) + " new respect.";
                }
            }

            articleFiskList = MYSQLAccess.getUserFiskCountBetween(priority.getUserId(), frequency.getFormattedStart(), frequency.getFormattedFinish());
            LOGGER.log(Level.FINE, "Email for " + user.getName() + ":fisks=" + articleFiskList.size());
            
            
            if (!articleFiskList.isEmpty()) {
                if (articleFiskList.size() > 5) {
                    articleFiskList = articleFiskList.subList(0, 5);
                }

                // Only return top 5 articles.
                recipient.addVar(new Var("newly_fisked_articles", articleFiskList));

                // We will be sending an email since we have data.
                sendEmail = true;
                if (subjectEnd.isEmpty()) {
                    subjectEnd = "Someone else has fisked your article.";
                }
            }

            MYSQLAccess.touchPriorityList(user);

            
            LOGGER.log(Level.FINE, "Email for " + user.getName() + ":sendEmail=" + sendEmail);
            
            
            // Do not send email if we don't have new data.
            if (!sendEmail) {
                continue;
            } else {
            	
            	if (pictureUtil == null){
            		pictureUtil = new PictureUtil();
            	}
            	user.setImageUrl(pictureUtil.getUserPictureUrl(user, pictureSettings));
            	
                sendToMandrill = true;
                if (subjectEnd.isEmpty()) {
                    subjectEnd = "Your Fiskkit Daily";
                }
            }
            // Add subject end to recipient.
            recipient.addVar(new Var("subject_end", subjectEnd));

            // Add recipient to message.
            message.addRecipient(recipient);
        }
        
        LOGGER.log(Level.FINE, "Done checking users:sendToMandrill=" + sendToMandrill);
        
		if (sendToMandrill) {
			LOGGER.info("message to send = " + message.toString());

			try {
				
				if (mandrillAsyncClient == null){
					mandrillAsyncClient = new MandrillAsyncClient(apiKey, null);
				}
				
				
				mandrillAsyncClient.api().messages().sendTemplate(templateName, message,
						new ObjectResponseCallback<MessageStatus[]>() {

							@Override
							public void onSuccess(com.jobhive.sakimonkey.data.Result<MessageStatus[]> result) {
								
								/* Do something with result */
								if (!result.isError()) {
									LOGGER.info("Message successfully sent to mandrill.");
								} else {
									LOGGER.log(Level.SEVERE, "", result.getErrorInfo());
								}
								
							}
							
							
							// LEAVING THIS COMMENTED CODE FOR REFERENCE - WAS NECESSARY TO MODIFY DURING PORT
//							@Override
//							public void onSuccess(Result<MessageStatus[]> result) {
//								/* Do something with result */
//								if (!result.isError()) {
//									LOGGER.info("Message successfully sent to mandrill.");
//								} else {
//									LOGGER.log(Level.SEVERE, "", result.getErrorInfo());
//								}
//							}
							
							
							
						});
			} catch (Exception catchall) {
				LOGGER.log(Level.SEVERE, "Mandrill error", catchall);
			}
		}
    }
    
    private void cleanup(long runStart) {
		try {
			
			
			Runtime runtime = Runtime.getRuntime();
			

			LOGGER.info(" free:" + (runtime.freeMemory()/1024) + "k");
			LOGGER.info("total:" + (runtime.totalMemory()/1024) + "k");
			runtime.runFinalization();
			runtime.gc();
			
			LOGGER.info("active EmailDigest runs:" + RUN_SET.size());
			for (long started : RUN_SET) {
				LOGGER.info("start:" + TIME_FORMAT.format(new Date(started)));
			}
			synchronized (RUN_SET) {
				RUN_SET.remove(runStart);
			}

			LOGGER.info(" exit:" + TIME_FORMAT.format(new Date(runStart)));
		} catch (Exception unexpected) {
			unexpected.printStackTrace();
		}
	}

}
