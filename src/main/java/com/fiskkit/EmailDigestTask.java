package com.fiskkit;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fiskkit.util.ArticleSource;
import com.fiskkit.util.TimeTracker;
import com.fiskkit.util.frequency.DailyFrequency;

/**
 * Created by joshuaellinger on 3/24/15.
 */
public class EmailDigestTask {

	private static final Logger LOGGER = Logger.getLogger("");

	private EmailDigest emailDigest;
	private InetAddress IP;
	private final TimeTracker time = new TimeTracker();;
	private String apiKey;

	public EmailDigestTask() {
		try {
			IP = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			LOGGER.log(Level.SEVERE, "", e);
		}

		LOGGER.info("EmailDigestTask<>:time=" + time);

		this.apiKey = ArticleSource.createSource(System.getProperty("EMAIL_DIGEST_API_KEY_ENC", "").trim());
	}

	public void run() {

		LOGGER.info("EmailDigestTask.run()");

		String serviceDisabled = System.getProperty("FISKKIT_DISABLE_SERVICE", "");
		if (serviceDisabled.trim().equalsIgnoreCase("true")) {
			LOGGER.info("EmailDigestTask.run():FISKKIT_DISABLE_SERVICE=true ... no action taken");
			return;
		}

		

		long estimatedCompletionTime;
		LOGGER.info(String.format("Email digest task started from %s", IP.toString()));

		time.setTime();
		emailDigest = new EmailDigest(8000, this.apiKey, new DailyFrequency());
		
		
		
		emailDigest.run();

		estimatedCompletionTime = System.nanoTime() - time.getTime();
		estimatedCompletionTime = TimeUnit.NANOSECONDS.toMillis(estimatedCompletionTime);

		LOGGER.log(Level.FINE, "Email digest complete");
		LOGGER.log(Level.INFO,
				String.format("Execution Time: %.3f seconds", (double) (estimatedCompletionTime / 1000.00)));
	}

}
