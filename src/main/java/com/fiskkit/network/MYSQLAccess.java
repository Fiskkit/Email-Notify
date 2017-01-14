package com.fiskkit.network;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fiskkit.exception.FiskkitException;
import com.fiskkit.model.Article;
import com.fiskkit.model.EmailPriority;
import com.fiskkit.model.Fisk;
import com.fiskkit.model.Respect;
import com.fiskkit.model.SentenceComment;
import com.fiskkit.model.User;
import com.fiskkit.util.ArticleSource;
import com.fiskkit.util.frequency.Frequency;
import com.fiskkit.util.picture.PictureUtil;

/**
 * Created by joshuaellinger on 3/25/15.
 */
public class MYSQLAccess {

	private static final Logger LOGGER = Logger.getLogger("");

	private static final PictureUtil pictureUtil = new PictureUtil();
	private static Map<String, String> pictureSettings = new HashMap<String, String>();

	private static Statement statement = null;
	// private PreparedStatement preparedStatement = null;
	private static ResultSet resultSet = null;
	private static Connection CONNECTION;
	
	
	
	
	private final String mysql_name;
	private final String mysql_user;
	private final String mysql_pass;
	private final String mysql_port;
	private final String mysql_db;
	private final String db_type;
	
	
	private MYSQLAccess() {

		LOGGER.log(Level.INFO, "Loading MySQL JDBC Driver");
		try {
			Class.forName("com.mysql.jdbc.Driver");
			LOGGER.log(Level.INFO, "Driver found");
		} catch (ClassNotFoundException e) {
			LOGGER.log(Level.SEVERE, "Cannot find MySQL JDBC Driver", e);
		}
		
		this.mysql_name = System.getProperty("MYSQL_NAME");
		this.mysql_user = System.getProperty("MYSQL_USER");
		this.mysql_pass = ArticleSource.createSource(System.getProperty("MYSQL_PASSWORD_ENC","").trim());
		this.mysql_port = System.getProperty("MYSQL_PORT");
		this.mysql_db = System.getProperty("MYSQL_DB");
		this.db_type = System.getProperty("DB_TYPE");
		
	}
	
	
	
	public static Connection getConnection() throws FiskkitException
	{
		try {
			if (
					(CONNECTION == null) || 
					(CONNECTION.isClosed()) || 
					(! CONNECTION.isValid(1))
			){
				CONNECTION = new MYSQLAccess().getConnectionInstance();
				LOGGER.info("MySQL Connection complete:" + CONNECTION);
			}
			
			return CONNECTION;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw new FiskkitException(e.getClass().getName() + ":" + e.getMessage());
		}
	}
	
	// NB should onlyy be called by getConnection()
	private Connection getConnectionInstance() {

		String connectionString = "jdbc:mysql://" + mysql_name + ":" + mysql_port + "/" + mysql_db;
		Connection connection = null;

		try {
			connection = DriverManager.getConnection(connectionString, mysql_user, mysql_pass);
			if (connection == null || connection.isClosed()) {
				LOGGER.log(Level.SEVERE, "Unable to connect.");
				return null;
			}
			LOGGER.log(Level.INFO, "Connected to " + db_type + " db.:" + connection);
			return connection;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Cannot connect to database:" + connectionString, e);
			throw new RuntimeException("Cannot connect to database!");
		}

	}
	
	
	
	
	
	
	
	
	
	

//	public MYSQLAccess() {
//		LOGGER.log(Level.FINE, "Loading MySQL JDBC Driver");
//		try {
//			Class.forName("com.mysql.jdbc.Driver");
//		} catch (ClassNotFoundException e) {
//			LOGGER.log(Level.SEVERE, "Cannot find MySQL JDBC Driver", e);
//			e.printStackTrace();
//		}
//		String mysqlURL;
//		String mysqlPort;
//		String mysqlDB;
//		String mysqlUser;
//		String mysqlPass;
//
//		try {
//			if (System.getProperty("debug").equals("false")) {
//				mysqlURL = "aa5vqh1gggvj49.cwblf8lajcuh.us-west-1.rds.amazonaws.com";
//				mysqlPort = "3306";
//				mysqlDB = "ebdb";
//				mysqlUser = "tester";
//				mysqlPass = "12345";
//			} else {
//				mysqlURL = "aa106w2ihlwnfld.cwblf8lajcuh.us-west-1.rds.amazonaws.com";
//				mysqlPort = "3306";
//				mysqlDB = "ebdb";
//				mysqlUser = "tester";
//				mysqlPass = "12345";
//			}
//		} catch (Exception e) {
//			mysqlURL = "aa106w2ihlwnfld.cwblf8lajcuh.us-west-1.rds.amazonaws.com";
//			mysqlPort = "3306";
//			mysqlDB = "ebdb";
//			mysqlUser = "tester";
//			mysqlPass = "12345";
//		}
//
//		try {
//			LOGGER.info("Connecting to DB @ http://" + mysqlURL);
//			// Development DB.
//
//			if (connection == null) {
//				connection = DriverManager.getConnection("jdbc:mysql://" + mysqlURL + ":" + mysqlPort + "/" + mysqlDB,
//						mysqlUser, mysqlPass);
//			}
//
//		} catch (SQLException e) {
//			LOGGER.log(Level.SEVERE, "Cannot connect to database!", e);
//			throw new RuntimeException("Cannot connect to database!");
//		}
//		this.pictureSettings.put("type", "large-square");
//	}

	/**
	 * Get the priority list of users from email_priority.
	 *
	 * @param limit
	 *            The number of items on the list to process
	 * @param frequency
	 *            The digest frequency object.
	 * @return Deque<EmailPriority>
	 */
	public static Deque<EmailPriority> getPriorityList(int limit, Frequency frequency) {

		Deque<EmailPriority> priorityList = new ArrayDeque<EmailPriority>();
		// TODO try-with-resources
		Connection connection = null;
		try {
			connection = getConnection();
			
			statement = connection.createStatement();
			String sql = "" + "SELECT * " + "FROM email_priority " + "WHERE digest_frequency = " + frequency.getId()
					+ " " + "AND updated_at < DATE(DATE_ADD(CURDATE(), INTERVAL send_after HOUR)) "
					+ "ORDER BY updated_at DESC LIMIT " + limit + ";";
			LOGGER.log(Level.FINE, "Email priority SQL = " + sql);
			resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				EmailPriority emailPriority = new EmailPriority();
				emailPriority.setUserId(resultSet.getInt("user_id"));
				emailPriority.setSendAfter(resultSet.getInt("send_after"));

				priorityList.add(emailPriority);
			}
			LOGGER.log(Level.FINE, "priorityList length = " + priorityList.size());
			return priorityList;

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "", e);
			throw new RuntimeException("Query Failed!");

		} finally {

			try {
				if (resultSet != null && statement != null) {
					resultSet.close();
					statement.close();
				}
			} catch (SQLException ignore) {
				closeConnection();
			}

		}

	}

	public static void touchPriorityList(User user) {

		Connection connection = null;
		try {
			connection = getConnection();
			
			statement = connection.createStatement();
			String sql = "" + "UPDATE email_priority " + "SET updated_at = NOW() " + "WHERE user_id = " + user.getId()
					+ ";";
			statement.executeUpdate(sql);

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "", e);
			closeConnection();
			throw new RuntimeException("Query Failed!");

		}

	}

	public static User getUser(int userId) {

		Connection connection = null;
		// FIXME remove in() clause
		try {
			connection = getConnection();
			
			statement = connection.createStatement();
			String sql = "" + "SELECT users.*, " + "(" + "SELECT COUNT(*) " + "FROM respects r "
					+ "WHERE r.author_id = users.id" + ") AS respect_count " + "FROM users " + " WHERE id = " + userId
					+ " " + " AND id in (19, 43, 46, 355, 382, 385, 386, 387, 388)" + ";";
			resultSet = statement.executeQuery(sql);

			if (!resultSet.next()) {
				return null;
			}

			return new User(resultSet);

		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "", e);
			closeConnection();
			throw new RuntimeException("Query Failed!");

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "", e);
			closeConnection();
			throw new RuntimeException("Query Failed!");

		} finally {

			try {
				if (resultSet != null && statement != null) {
					resultSet.close();
					statement.close();
				}
			} catch (SQLException ignore) {
			}

		}

	}

	/**
	 * Get a user's respects in a period.
	 *
	 * @param userId
	 * @param start
	 * @param finish
	 * @return
	 */
	public static List<Article> getUserArticleRespectBetween(int userId, String start, String finish) {
		Map<String, Article> articleMap = new HashMap<String, Article>();
		// TODO try-with-resources
		Connection connection = null;
		try {
			connection = getConnection();
			
			statement = connection.createStatement();
			String SQLString = "" + "SELECT * " + "FROM respects r " + "JOIN users u ON u.id = r.user_id "
					+ "JOIN sentence_comments sc ON r.comment_id = sc.id " + "JOIN articles a ON r.article_id = a.id "
					+ "WHERE r.author_id = " + userId + " " + "AND (r.updated_at BETWEEN '" + start + "' AND '" + finish
					+ "') " + "GROUP BY r.article_id, r.user_id " + "ORDER BY r.updated_at DESC;";

			LOGGER.log(Level.FINE, "getUserArticleRespectBetween SQL = " + SQLString);

			resultSet = statement.executeQuery(SQLString);
			while (resultSet.next()) {
				Article article;
				// Set respect user.
				User user = new User();
				user.setId(resultSet.getInt("user_id"));
				user.setFirstName(resultSet.getString("first_name"));
				user.setLastName(resultSet.getString("last_name"));
				user.setName(resultSet.getString("name"));
				user.setFacebookId(resultSet.getString("facebook_id"));
				user.setLinkedinProfileImage(resultSet.getString("linkedin_profile_image"));
				// Set the image for the user.
				user.setImageUrl(pictureUtil.getUserPictureUrl(user, pictureSettings));

				// Set sentence comment
				SentenceComment comment = new SentenceComment();
				comment.setId(resultSet.getInt("comment_id"));
				comment.setBody(resultSet.getString("body").trim());
				comment.setWordCount(resultSet.getInt("word_count"));

				// Set respect
				Respect respect = new Respect();
				respect.setId(resultSet.getInt("id"));
				respect.setArticleId(resultSet.getString("article_id"));
				respect.setFiskId(resultSet.getInt("fisk_id"));

				// Assign objects to respect.
				respect.setUser(user);
				respect.setSentenceComment(comment);

				if (articleMap.containsKey(resultSet.getString("article_id"))) {
					article = articleMap.get(resultSet.getString("article_id"));
				} else {
					article = new Article();
					article.setId(resultSet.getString("article_id"));
					article.setAuthor(resultSet.getString("author"));
					article.setPublisher(resultSet.getString("publisher"));
					article.setTitle(resultSet.getString("title"));

					Fisk fisk = new Fisk();
					fisk.setId(resultSet.getInt("fisk_id"));
					fisk.setUrl(article.getId());
					article.addFisk(fisk);
				}
				article.addRespect(respect);
				articleMap.put(article.getId(), article);
				LOGGER.info("result row = " + resultSet.getRow());
			}

			// Only return 3 respect per article.
			for (Iterator<Article> i = articleMap.values().iterator(); i.hasNext();) {
				Article article = i.next();
				List<Respect> respectList = article.getRespectList();
				int respectCount = article.getNewRespectCount();

				if (respectCount > 3) {
					// Limit respect returned to 3.
					article.setRespectList(respectList.subList(0, 3));
					// reset to original fisk count.
					article.setNewRespectCount(respectCount);

					// reassign the article to the articleMap.
					articleMap.put(article.getId(), article);
				}
			}

			// Turn Articles into a List and sort.
			ArrayList<Article> articleList = new ArrayList<Article>(articleMap.values());
			Comparator<Article> articleRespectComparator = new Comparator<Article>() {
				@Override
				public int compare(Article o1, Article o2) {
					return o2.getNewRespectCount() - o1.getNewRespectCount();
				}
			};
			Collections.sort(articleList, articleRespectComparator);

			return articleList;

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "", e);
			closeConnection();
			throw new RuntimeException("Query Failed!");

		} finally {

			try {
				if (resultSet != null && statement != null) {
					resultSet.close();
					statement.close();
				}
			} catch (SQLException ignore) {
			}

		}
	}

	/**
	 * Get a user's respect count in a period.
	 *
	 * @param start
	 * @param finish
	 * @return
	 */
	public static int getTotalUserRespectCountBetween(int userId, String start, String finish) {
		// TODO try-with-resources
		Connection connection = null;
		try {
			connection = getConnection();
			
			statement = connection.createStatement();
			String SQLString = "" + "SELECT COUNT(*) as respect_count " + "FROM respects r " + "WHERE r.author_id = "
					+ userId + " " + "AND (r.updated_at BETWEEN '" + start + "' AND '" + finish + "');";
			LOGGER.log(Level.FINE, "getTotalUserRespectCountBetween SQL = " + SQLString);
			resultSet = statement.executeQuery(SQLString);
			resultSet.next();
			return resultSet.getInt("respect_count");

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "", e);
			closeConnection();
			throw new RuntimeException("Query Failed!");

		} finally {

			try {
				if (resultSet != null && statement != null) {
					resultSet.close();
					statement.close();
				}
			} catch (SQLException ignore) {
			}

		}
	}

	/**
	 * Get users fisks between
	 *
	 * @param userId
	 * @param start
	 * @param finish
	 * @return
	 */
	public static List<Article> getUserFiskCountBetween(int userId, String start, String finish) {
		Map<String, Article> articleMap = new HashMap<String, Article>();
		List<Article> articleList = new ArrayList<Article>();
		
		Connection connection = null;
		try {
			connection = getConnection();
			
			statement = connection.createStatement();
			String SQLString = "" + "SELECT *, f.id AS fisk_id " + "FROM fisks f "
					+ "JOIN articles a ON f.article_id = a.id " + "JOIN users u ON u.id = f.user_id "
					+ "WHERE f.user_id != " + userId + " " + "AND f.has_content = 1 " + "AND f.article_id IN ("
					+ "SELECT article_id FROM fisks WHERE user_id = " + userId + " AND has_content = 1" + ") "
					+ "AND (f.created_at BETWEEN '" + start + "' AND '" + finish + "');";
			LOGGER.log(Level.FINE, "getTotalUserRespectCountBetween SQL = " + SQLString);
			resultSet = statement.executeQuery(SQLString);
			while (resultSet.next()) {
				Article article;
				Fisk fisk;
				if (articleMap.containsKey(resultSet.getString("article_id"))) {
					article = articleMap.get(resultSet.getString("article_id"));
				} else {
					article = new Article();
					article.setId(resultSet.getString("article_id"));
					article.setTitle(resultSet.getString("title"));
					article.setAuthor(resultSet.getString("author"));
					article.setPublisher(resultSet.getString("publisher"));
				}
				User user = new User();
				user.setId(resultSet.getInt("user_id"));
				user.setName(resultSet.getString("name"));
				user.setFirstName(resultSet.getString("first_name"));
				user.setLastName(resultSet.getString("last_name"));
				user.setFacebookId(resultSet.getString("facebook_id"));
				user.setLinkedinProfileImage(resultSet.getString("linkedin_profile_image"));
				// Set the image for the user.
				user.setImageUrl(pictureUtil.getUserPictureUrl(user, pictureSettings));

				fisk = new Fisk();
				fisk.setId(resultSet.getInt("fisk_id"));
				fisk.setUser(user);
				fisk.setUrl(resultSet.getString("article_id"));

				article.addFisk(fisk);

				article.setTopComment(getTopRecentCommentForArticle(article.getId(), start, finish));

				articleMap.put(article.getId(), article);

			}
			// Only return 3 fisks per article.
			for (Iterator<Article> i = articleMap.values().iterator(); i.hasNext();) {
				Article article = i.next();
				List<Fisk> fisks = article.getFiskList();
				int fiskCount = article.getFiskCount();

				if (fiskCount > 3) {
					// Limit fisks returned to 3.
					article.setFiskList(fisks.subList(0, 3));
					// reset to original fisk count.
					article.setFiskCount(fiskCount);

					// reassign the article to the articleMap.
					articleMap.put(article.getId(), article);
				}
			}
			// Turn Articles into a List and sort.
			articleList = new ArrayList<Article>(articleMap.values());

			return articleList;

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "", e);
			closeConnection();
			throw new RuntimeException("Query Failed!");

		} finally {

			try {
				if (resultSet != null && statement != null) {
					resultSet.close();
					statement.close();
				}
			} catch (SQLException ignore) {
			}

		}
	}

	/**
	 * Get the top sentence for an article.
	 *
	 * @param article_id
	 * @param start
	 * @param finish
	 * @return
	 */
	public static SentenceComment getTopRecentCommentForArticle(String article_id, String start, String finish) {
		SentenceComment comment = new SentenceComment();
		ResultSet rs = null;
		Statement st = null;
		
		Connection connection = null;
		try {
			connection = getConnection();
			
			st = connection.createStatement();
			String SQLString = "" + "SELECT sc.*, u.*, "
					+ "(SELECT COUNT(*) FROM respects r WHERE sc.id = r.comment_id) AS respect_count "
					+ "FROM sentence_comments sc " + "JOIN users u ON sc.user_id = u.id "
					+ "JOIN fisks f ON sc.fisk_id = f.id " + "WHERE sc.word_count > 4 " + "AND f.article_id = '"
					+ article_id + "' " +
					// "AND (sc.created_at BETWEEN '" + start + "' AND '" +
					// finish + "') "+
					"ORDER BY respect_count DESC " + "LIMIT 1;";
			LOGGER.log(Level.FINE, "getTopRecentCommentForArticle SQL = " + SQLString);
			rs = st.executeQuery(SQLString);
			while (rs.next()) {

				comment.setId(rs.getInt("id"));
				comment.setFiskId(rs.getInt("fisk_id"));
				comment.setWordCount(rs.getInt("word_count"));
				comment.setBody(rs.getString("body"));
				comment.setRespectCount(rs.getInt("respect_count"));
				comment.setCommentUrl(article_id);

				User user = new User();
				user.setId(rs.getInt("user_id"));
				user.setName(rs.getString("name"));
				user.setFirstName(rs.getString("first_name"));
				user.setLastName(rs.getString("last_name"));
				user.setFacebookId(rs.getString("facebook_id"));
				user.setLinkedinProfileImage(rs.getString("linkedin_profile_image"));
				// Set the image for the user.
				user.setImageUrl(pictureUtil.getUserPictureUrl(user, pictureSettings));

				comment.setUser(user);
			}
			return comment;
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "", e);
			closeConnection();
			throw new RuntimeException("Query Failed!");

		} finally {

			try {
				if (rs != null && st != null) {
					rs.close();
					st.close();
				}
			} catch (SQLException ignore) {
			}

		}
	}

	public static void closeConnection() {
		try {
			if (CONNECTION != null) {
				CONNECTION.close();
				LOGGER.info("Connection closed. Connection.isClosed()==" + CONNECTION.isClosed());
			}
		} catch (Exception catchall) {
			LOGGER.log(Level.SEVERE, "unable to close connection", catchall);
		} finally {
			CONNECTION = null;
		}
	}
}
