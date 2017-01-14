package com.fiskkit.emailnotify.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fiskkit.EmailDigestTask;
import com.fiskkit.exception.FiskkitException;
import com.fiskkit.network.MYSQLAccess;

public class EmailNotifyWorker extends HttpServlet {
	public static final String VERSION = "beanstalk-email-notification-2.0.1";
	
	private static final Logger LOGGER = Logger.getLogger("");
	
	
	
	static {
		try {
			System.out.println("====>EmailNotifyWorker static block");
			setLogLevel();
		} catch (Exception e){
			LOGGER.log(Level.SEVERE, "unable to set logging level", e);
		}
		
		
	}
//	static {
//		LOGGER.setUseParentHandlers(false);
//	}
	private static final long serialVersionUID = 1L;
	

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException {
		
		LOGGER.info("====>EmailNotifyWorker.doPost(1):" + new Date());
		LOGGER.log(Level.INFO, "====>EmailNotifyWorker.doPost(2):" + new Date());
		LOGGER.log(Level.INFO, "====>EmailNotifyWorker.request.getRequestURL()=" + request.getRequestURL());

		try {
			printEnvironment(request);
			processRequest(request);
			LOGGER.log(Level.INFO,"processRequest() complete");
			PrintWriter out = response.getWriter();
			response.setStatus(200);
			out.flush();
			LOGGER.log(Level.INFO, "returned 200:success");
			
			

		} catch (Exception any) {
			LOGGER.log(Level.SEVERE, "unable to process request", any);
			PrintWriter out = response.getWriter();
			response.setStatus(500);
			out.flush();
			LOGGER.log(Level.SEVERE, "returned 500:failure");
		}

	}

	private static void printEnvironment(final HttpServletRequest request) {
		
		Map<String, String> env = System.getenv();
		for (String var : env.keySet()) {
			System.out.println("env:" + var + "=" + env.get(var));
			LOGGER.log(Level.INFO, "env:{0}={1}", new Object[] { var, env.get(var) });
		}
		
		

		// probably do not need this
//		for (Enumeration<?> e = request.getAttributeNames(); e.hasMoreElements();) {
//			Object attribute = e.nextElement();
//			LOGGER.log(Level.INFO, "attribute:{0}={1}",
//					new Object[] { attribute, request.getAttribute(attribute.toString()) });
//		}

		for (Enumeration<?> e = request.getHeaderNames(); e.hasMoreElements();) {
			Object header = e.nextElement();
			LOGGER.log(Level.INFO, "header:{0}={1}",
					new Object[] { header, request.getHeader(header.toString()) });

		}

		for (Object key : System.getProperties().keySet()) {
			LOGGER.log(Level.INFO, "system:{0}={1}",
					new Object[] { key, System.getProperty(key.toString(), "<unset>") });
		}
		
		LOGGER.info("request.getRequestURL()=" + request.getRequestURL());

		
		Runtime runtime = Runtime.getRuntime();

		LOGGER.info("     version:" + VERSION);
		LOGGER.info(" free memory:" + runtime.freeMemory());
		LOGGER.info("total memory:" + runtime.totalMemory());
	}

	private static void processRequest(final HttpServletRequest request) throws IOException, FiskkitException {
		
		

		byte[] bytes = new byte[request.getContentLength() + 1];
		StringBuilder content = new StringBuilder(request.getContentLength() + 1);
		while (true) {
			int cc = request.getInputStream().read(bytes);
			if (cc <= 0) {
				break;
			}
			content.append(new String(bytes, 0, cc));
		}

		String message = content.toString();
		LOGGER.info("====SQS message=====");
		LOGGER.info(message);
		LOGGER.info("====SQS message=====");
		
		
		
		
		try {
			
			//FIXME move to MYSQLAccess constructor?
			MYSQLAccess.getConnection();
			
			
			LOGGER.info("====>Connection obtained -- ready to process email notification");
			
			new EmailDigestTask().run();
			
		} catch (Exception e) {
			throw new FiskkitException("unable to process request", e);
		} finally {
			MYSQLAccess.closeConnection();
			
		}

	}
	
	
	
	
	private static void processEmail(MYSQLAccess mysqlAccess)
			throws FiskkitException {
		System.out.println("========>processEmail()");
	}
	
	
	
	
	

	
	private static void setLogLevel(){
		HashMap<String,Level> levelMap = new HashMap<String,Level>();
		levelMap.put("INFO", Level.INFO);
		levelMap.put("FINE", Level.FINE);
		levelMap.put("FINER", Level.FINER);
		levelMap.put("FINEST", Level.FINEST);
		
		String setting = System.getProperty("LOG_LEVEL", "INFO");
		Level level = levelMap.get(setting);
		if (level == null){
			level = Level.INFO;
		}
		LOGGER.setLevel(level);
		for (Handler handler: LOGGER.getHandlers()) {
			handler.setLevel(level);
		}
		
		LOGGER.log(level, "setLogLevel():setting=" + setting + ":" + level);
		LOGGER.fine("This is a FINE level message");
	}
	
	
	public static final void main(String[] args) throws Exception {
		//processArticle("http://www.huffingtonpost.com/dorothy-samuels/freedom-of-information-improvement-act_b_10535208.html", null);
		//processArticle("https://www.newswhip.com/2016/06/biggest-politics-publishers-social/#6bkIQT4Oepzz7hjT.99", null);
		//String url = "http://www.usnews.com/news/best-countries/articles/2016-06-27/in-south-africa-some-prostitutes-demand-the-chance-to-sell-sex-legally?int=a14709";
		//url = "http://www.usnews.com/news/best-countries/articles/2016-06-27/in-south-africa-some-prostitutes-demand-the-chance-to-sell-sex-legally";
		
		
		
		//String url = "http://www.msn.com/en-us/news/world/newly-expanded-panama-canal-opens-for-bigger-business/ar-AAhCzB9?li=BBnb4R7";
		//String url = "https://www.theguardian.com/uk-news/2016/jun/25/sturgeon-seeks-urgent-brussels-talks-to-protect-scotlands-eu-membership";
		
		//String url = "http://www.msn.com/en-us/news/world/21-million-brits-signed-a-petition-for-another-eu-referendum-they-shouldn't-hold-their-breath/ar-AAhCuCf?li=BBnb7Kz";
		
		
		
		
		//String url = "http://www.msn.com/en-us/news/world/21-million-brits-signed-a-petition-for-another-eu-referendum-they-shouldn%E2%80%99t-hold-their-breath/ar-AAhCuCf";
		//url = URLDecoder.decode(url, "UTF-8");
		
		
		
//		System.out.println("decoded:" + url);
//		//url = URLEncoder.encode(url, "UTF-8");
//		
//		String[] ss = url.split("://");
//		String urlBody = ss[1];
//		String encodedBody = URLEncoder.encode(ss[1], "UTF-8");
//		encodedBody = encodedBody.replace("%2F", "/");
//		url = ss[0] + "://" + encodedBody;
//		
//		System.out.println("encoded:" + url);
		
		
		//String url = "http://financialmentor.com/financial-advice/financial-education-best-investment/13173";
		//String url = "https://www.donaldjtrump.com/positions/pay-for-the-wall";
		//String url = "http://finance.yahoo.com/news/tom-lee-reasons-stocks-further-gain-post-brexit-shock-uncertainty-eu-referendum-230839446.html";
		//String url = "http://www.huffingtonpost.com/entry/jimmy-fallon-obama-2016-2009-video_us_57e6419ae4b0e80b1ba24f4e?section=&";
		//String url = "http%3A%2F%2Fwww.huffingtonpost.com%2Fentry%2Fjimmy-fallon-obama-2016-2009-video_us_57e6419ae4b0e80b1ba24f4e%3Fsection%3D%26";
		
		String url = "https://www.theguardian.com/commentisfree/2016/sep/20/clinton-hasnt-won-millennials-sexism-isnt-to-blame";
		//processArticle(url,null);
		
		
		
	}
	
	
}
