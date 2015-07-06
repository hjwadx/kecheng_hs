package fm.jihua.chat.service;

import java.util.Date;

/**
 * Class which simplify an Xmpp text message.
 * 
 * @author Jean-Manuel Da Silva <dasilvj at beem-project dot com>
 */
public class MessageText {
	private String mBareJid;
	private String mName;
	private String mMessage;
	private boolean mIsError;
	private Date mTimestamp;
	private String mExtra_Type;
	private String mExtra_Json;
	private String mSubject;

	/**
	 * Constructor.
	 * 
	 * @param bareJid
	 *            A String containing the bare JID of the message's author.
	 * @param name
	 *            A String containing the name of the message's author.
	 * @param message
	 *            A String containing the message.
	 */
	public MessageText(final String bareJid, final String name,
			final String message) {
		mBareJid = bareJid;
		mName = name;
		mMessage = message;
		mIsError = false;
	}

	/**
	 * Constructor.
	 * 
	 * @param bareJid
	 *            A String containing the bare JID of the message's author.
	 * @param name
	 *            A String containing the name of the message's author.
	 * @param message
	 *            A String containing the message.
	 * @param isError
	 *            if the message is an error message.
	 */
	public MessageText(final String bareJid, final String name,
			final String message, final boolean isError) {
		mBareJid = bareJid;
		mName = name;
		mMessage = message;
		mIsError = isError;
	}

	/**
	 * Constructor.
	 * 
	 * @param bareJid
	 *            A String containing the bare JID of the message's author.
	 * @param name
	 *            A String containing the name of the message's author.
	 * @param message
	 *            A String containing the message.
	 * @param isError
	 *            if the message is an error message.
	 * @param date
	 *            the time of the message.
	 */
	public MessageText(final String bareJid, final String name,
			final String message, final boolean isError, final Date date) {
		mBareJid = bareJid;
		mName = name;
		mMessage = message;
		mIsError = isError;
		mTimestamp = date;
	}
	
	public MessageText(final String bareJid, final String name,
			final String message, final boolean isError, final Date date, final String extra_type, final String extra_json, final String subject) {
		mBareJid = bareJid;
		mName = name;
		mMessage = message;
		mIsError = isError;
		mTimestamp = date;
		mExtra_Type = extra_type;
		mExtra_Json = extra_json;
		mSubject = subject;
	}

	/**
	 * JID attribute accessor.
	 * 
	 * @return A String containing the bare JID of the message's author.
	 */
	public String getBareJid() {
		return mBareJid;
	}

	/**
	 * Name attribute accessor.
	 * 
	 * @return A String containing the name of the message's author.
	 */
	public String getName() {
		return mName;
	}

	/**
	 * Message attribute accessor.
	 * 
	 * @return A String containing the message.
	 */
	public String getMessage() {
		return mMessage;
	}

	/**
	 * JID attribute mutator.
	 * 
	 * @param bareJid
	 *            A String containing the author's bare JID of the message.
	 */
	public void setBareJid(String bareJid) {
		mBareJid = bareJid;
	}

	/**
	 * Name attribute mutator.
	 * 
	 * @param name
	 *            A String containing the author's name of the message.
	 */
	public void setName(String name) {
		mName = name;
	}

	/**
	 * Message attribute mutator.
	 * 
	 * @param message
	 *            A String containing a message.
	 */
	public void setMessage(String message) {
		mMessage = message;
	}

	/**
	 * Get the message type.
	 * 
	 * @return true if the message is an error message.
	 */
	public boolean isError() {
		return mIsError;
	}

	/**
	 * Set the Date of the message.
	 * 
	 * @param date
	 *            date of the message.
	 */
	public void setTimestamp(Date date) {
		mTimestamp = date;
	}

	/**
	 * Get the Date of the message.
	 * 
	 * @return if it is a delayed message get the date the message was sended.
	 */
	public Date getTimestamp() {
		return mTimestamp;
	}
	
	public void setExtraType(String extra_type) {
		mExtra_Type = extra_type;
	}
	
	public String getExtraType() {
		return mExtra_Type;
	}
	
	public void setExtraJson(String extra_json) {
		mExtra_Json = extra_json;
	}
	
	public String getExtraJson() {
		return mExtra_Json;
	}
	
	public void setSubject(String subject) {
		mSubject = subject;
	}
	
	public String getSubject() {
		return mSubject;
	}

	@Override
	public String toString() {
		return "MessageText [mBareJid=" + mBareJid + ", mName=" + mName
				+ ", mMessage=" + mMessage + ", mIsError=" + mIsError
				+ ", mTimestamp=" + mTimestamp + ", mExtra_Type=" + mExtra_Type + ", mExtra_Json=" + mExtra_Json + ", mSubject=" + mSubject + "]";
	}

}