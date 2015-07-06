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

import java.util.List;

import org.jivesoftware.smack.packet.Presence;

import android.os.RemoteException;
import fm.jihua.chat.service.aidl.IChatManager;
import fm.jihua.chat.service.aidl.IPrivacyListManager;
import fm.jihua.chat.service.aidl.IXmppConnection;
import fm.jihua.chat.service.aidl.IXmppFacade;
import fm.jihua.chat.utils.DatabaseHelper;
import fm.jihua.chat.utils.PresenceType;
import fm.jihua.chat.App;
import fm.jihua.chat.service.BeemService;

/**
 * This class is a facade for the Beem Service.
 * 
 * @author darisk
 */
public class XmppFacade extends IXmppFacade.Stub {

	private XmppConnectionAdapter mConnexion;
	private final BeemService service;
	private DatabaseHelper mDbHelper;

	/**
	 * Create an XmppFacade.
	 * 
	 * @param service
	 *            the service providing the facade
	 */
	public XmppFacade(final BeemService service) {
		this.service = service;
		this.mDbHelper = service.getDBHelper();
	}
	
	@Override
	public List<Message> requestHistoryMessage(String from, String to, int offset, int count){
		return mDbHelper.getMessages(mDbHelper.getWritableDatabase(), from, to, true, count, offset);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void changeStatus(int status, String msg) {
		initConnection();
		mConnexion.changeStatus(status, msg);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void connectAsync() throws RemoteException {
		initConnection();
		mConnexion.connectAsync();
	}

	/**
	 * 作为连接XMPP 服务器的唯一入口
	 */
	@Override
	public boolean connectSync(){
		App app = (App)service.getApplication();
		if (app.isConnecting()) {
			return false;
		}
		app.setConnecting(true);
		try {
			initConnection();
			return mConnexion.connectSync();
		} catch (Exception e) {
			e.printStackTrace();
		}
		app.setConnecting(false);
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IXmppConnection createConnection() throws RemoteException {
		initConnection();
		return mConnexion;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void disconnect() throws RemoteException {
		initConnection();
		mConnexion.disconnect();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IChatManager getChatManager() throws RemoteException {
		initConnection();
		return mConnexion.getChatManager();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IPrivacyListManager getPrivacyListManager() {
		initConnection();
		return mConnexion.getPrivacyListManager();
	}

	@Override
	public void sendPresencePacket(PresenceAdapter presence)
			throws RemoteException {
		initConnection();
		Presence presence2 = new Presence(
				PresenceType.getPresenceTypeFrom(presence.getType()));
		presence2.setTo(presence.getTo());
		mConnexion.getAdaptee().sendPacket(presence2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fm.jihua.chat.service.aidl.IXmppFacade#call(java.lang.String)
	 */
	@Override
	public void call(String jid) throws RemoteException {
	}


	@Override
	public UserInfo getUserInfo() throws RemoteException {
		initConnection();
		return mConnexion.getUserInfo();
	}

	/**
	 * Initialize the connection.
	 */
	private void initConnection() {
		if (mConnexion == null) {
			mConnexion = service.createConnection();
		}
	}

	@Override
	public boolean isLogined() throws RemoteException {
		if (mConnexion == null) {
			return false;
		}
		return mConnexion.isLogined();
	}
}
