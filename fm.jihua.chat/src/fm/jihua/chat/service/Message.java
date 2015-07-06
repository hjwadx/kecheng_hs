/*
    BEEM is a videoconference application on the Android Platform.

    Copyright (C) 2009 by Frederic-Charles Barthelery,
                          Jean-Manuel Da Silva,
                          Nikita Kozlov,
                          Philippe Lago,
                          Jean Baptiste Vergely,
                          Vincent Veronis.

    This file is part of BEEM.

    BEEM is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BEEM is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with BEEM.  If not, see <http://www.gnu.org/licenses/>.

    Please send bug reports with examples or suggestions to
    contact@beem-project.com or http://dev.beem-project.com/

    Epitech, hereby disclaims all copyright interest in the program "Beem"
    written by Frederic-Charles Barthelery,
               Jean-Manuel Da Silva,
               Nikita Kozlov,
               Philippe Lago,
               Jean Baptiste Vergely,
               Vincent Veronis.

    Nicolas Sadirac, November 26, 2009
    President of Epitech.

    Flavien Astraud, November 26, 2009
    Head of the EIP Laboratory.

 */
package fm.jihua.chat.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smackx.packet.DelayInformation;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import fm.jihua.chat.App;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Xml;

/**
 * This class represents a instant message.
 * 
 * @author darisk
 */
public class Message implements Parcelable {

	/** Normal message type. Theese messages are like an email, with subject. */
	public static final int MSG_TYPE_NORMAL = 100;

	/** Chat message type. */
	public static final int MSG_TYPE_CHAT = 200;

	/** Group chat message type. */
	public static final int MSG_TYPE_GROUP_CHAT = 300;

	/** Error message type. */
	public static final int MSG_TYPE_ERROR = 400;

	/** Informational message type. */
	public static final int MSG_TYPE_INFO = 500;

	/** Parcelable.Creator needs by Android. */
	public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {

		@Override
		public Message createFromParcel(Parcel source) {
			return new Message(source);
		}

		@Override
		public Message[] newArray(int size) {
			return new Message[size];
		}
	};

	private int mId;
	private int mType;
	private String mBody;
	private String mSubject;
	private String mTo;
	private String mFrom;
	private String mThread;
	private Date mTimestamp;
	private int mState; // 1 for unread, 0 for read
	private String mExtra_Type;
	private String mExtra_Json;
	
	public static final int READ = 0;
	public static final int UNREAD = 1;

	// TODO ajouter l'erreur

	/**
	 * Constructor.
	 * 
	 * @param to
	 *            the destinataire of the message
	 * @param type
	 *            the message type
	 */
	public Message(final String to, final int type) {
		setTo(to);
		mType = type;
		mBody = "";
		mSubject = "";
		setThread("");
		setFrom(null);
		mTimestamp = new Date();
	}

	/**
	 * Constructor a message of type chat.
	 * 
	 * @param to
	 *            the destinataire of the message
	 */
	public Message(final String to) {
		this(to, MSG_TYPE_CHAT);
	}
	
	public Message(final int id, final String from, final String to, final String body, long timestamp, int state) {
		mId = id;
		setFrom(from);
		setTo(to);
		setThread(to);
		mBody = body;
		mTimestamp = new Date(timestamp);
		mState = state;
	}

	/**
	 * Construct a message from a smack message packet.
	 * 
	 * @param smackMsg
	 *            Smack message packet
	 */
	public Message(final org.jivesoftware.smack.packet.Message smackMsg) {
		this(smackMsg.getTo());
		switch (smackMsg.getType()) {
		case chat:
			mType = MSG_TYPE_CHAT;
			break;
		case groupchat:
			mType = MSG_TYPE_GROUP_CHAT;
			break;
		case normal:
			mType = MSG_TYPE_NORMAL;
			break;
		// TODO gerer les message de type error
		// this a little work around waiting for a better handling of error
		// messages
		case error:
			mType = MSG_TYPE_ERROR;
			break;
		default:
			mType = MSG_TYPE_NORMAL;
			break;
		}
		setFrom(smackMsg.getFrom());
		// TODO better handling of error messages
		if (mType == MSG_TYPE_ERROR) {
			XMPPError er = smackMsg.getError();
			String msg = er.getMessage();
			if (msg != null)
				mBody = msg;
			else
				mBody = er.getCondition();
		} else {
			mBody = smackMsg.getBody();
			mSubject = smackMsg.getSubject();
			setThread(smackMsg.getThread());
			if (smackMsg.getExtensions().size() > 0) {
				PacketExtension p[] = new PacketExtension[10];
				smackMsg.getExtensions().toArray(p);
				String xml = p[0].toXML();
				ByteArrayInputStream tInputStringStream = null;
				try {
					if (xml != null && !xml.trim().equals("")) {
						tInputStringStream = new ByteArrayInputStream(
								xml.getBytes());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				XmlPullParser parser = Xml.newPullParser();
				try {
					parser.setInput(tInputStringStream, "UTF-8");
					int eventType = parser.getEventType();
					while (eventType != XmlPullParser.END_DOCUMENT) {
						switch (eventType) {
						case XmlPullParser.START_DOCUMENT:// 文档开始事件,可以进行数据初始化处理
							break;
						case XmlPullParser.START_TAG:// 开始元素事件
							String name = parser.getName();
							if (name.equalsIgnoreCase("type")) {
								mExtra_Type = parser.nextText();
							} else if (name.equalsIgnoreCase("item")) {
								mExtra_Json = parser.nextText();
							}
							break;
						case XmlPullParser.END_TAG:// 结束元素事件
							break;
						}
						eventType = parser.next();
					}
					tInputStringStream.close();
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		mState = UNREAD;
		PacketExtension pTime = smackMsg
				.getExtension("delay", "urn:xmpp:delay");
		if (pTime instanceof DelayInformation) {
			mTimestamp = ((DelayInformation) pTime).getStamp();
		} else {
			mTimestamp = new Date();
		}
	}

	/**
	 * Construct a message from a parcel.
	 * 
	 * @param in
	 *            parcel to use for construction
	 */
	private Message(final Parcel in) {
		mType = in.readInt();
		setTo(in.readString());
		mBody = in.readString();
		setThread(in.readString());
		mThread = in.readString();
		setFrom(in.readString());
		mTimestamp = new Date(in.readLong());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(mType);
		dest.writeString(mTo);
		dest.writeString(mBody);
		dest.writeString(mSubject);
		dest.writeString(mThread);
		dest.writeString(mFrom);
		dest.writeLong(mTimestamp.getTime());
	}

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		this.mId = id;
	}

	/**
	 * Get the type of the message.
	 * 
	 * @return the type of the message.
	 */
	public int getType() {
		return mType;
	}

	/**
	 * Set the type of the message.
	 * 
	 * @param type
	 *            the type to set
	 */
	public void setType(int type) {
		mType = type;
	}

	/**
	 * Get the body of the message.
	 * 
	 * @return the Body of the message
	 */
	public String getBody() {
		return mBody;
	}

	/**
	 * Set the body of the message.
	 * 
	 * @param body
	 *            the body to set
	 */
	public void setBody(String body) {
		mBody = body;
	}

	/**
	 * Get the subject of the message.
	 * 
	 * @return the subject
	 */
	public String getSubject() {
		return mSubject;
	}

	/**
	 * Set the subject of the message.
	 * 
	 * @param subject
	 *            the subject to set
	 */
	public void setSubject(String subject) {
		mSubject = subject;
	}

	/**
	 * Get the destinataire of the message.
	 * 
	 * @return the destinataire of the message
	 */
	public String getTo() {
		return mTo;
	}

	/**
	 * Set the destinataire of the message.
	 * 
	 * @param to
	 *            the destinataire to set
	 */
	public void setTo(String to) {
		mTo = buildJid(to);
	}

	/**
	 * Set the from field of the message.
	 * 
	 * @param from
	 *            the mFrom to set
	 */
	public void setFrom(String from) {
		this.mFrom = buildJid(from);
	}

	/**
	 * Get the from field of the message.
	 * 
	 * @return the mFrom
	 */
	public String getFrom() {
		return mFrom;
	}

	/**
	 * Get the thread of the message.
	 * 
	 * @return the thread
	 */
	public String getThread() {
		return mThread;
	}

	/**
	 * Set the thread of the message.
	 * 
	 * @param thread
	 *            the thread to set
	 */
	public void setThread(String thread) {
		mThread = buildJid(thread);
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	public int getState() {
		return mState;
	}

	public void setState(int state) {
		this.mState = state;
	}
	
	public String getFromUserId(){
		return getUserId(getFrom());
	}
	
	public String getToUserId(){
		return getUserId(getTo());
	}
	
	public String getThreadUserId(){
		return getUserId(getThread());
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
	
	public static String getUserId(String jid){
		if (jid != null) {
			return jid.split("@")[0];
		}
		return null;
	}
	
	public static String buildJid(String userId){
		if (userId != null && userId.indexOf("@") == -1) {
			userId += "@" + App.CHAT_HOST;
		}
		return userId;
	}

	@Override
	public String toString() {
		return "Message [mId=" + mId + ", mType=" + mType + ", mBody=" + mBody + ", mSubject=" + mSubject + ", mTo=" + mTo + ", mFrom=" + mFrom + ", mThread=" + mThread + ", mTimestamp=" + mTimestamp
				+ ", mState=" + mState + "]";
	}
	
	

}
