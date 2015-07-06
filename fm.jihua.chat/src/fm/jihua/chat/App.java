package fm.jihua.chat;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public abstract class App extends fm.jihua.common.App {
	private fm.jihua.chat.utils.DatabaseHelper mChatDBHelper;
	
	public static String CHAT_HOST;
	
	public App() {
		CHAT_HOST = getChatHost();
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mSettings = PreferenceManager.getDefaultSharedPreferences(this);
	}
	
	public void initAtFirst(){
		mChatDBHelper = new fm.jihua.chat.utils.DatabaseHelper(this);
		mIsAccountConfigured = (getMyUserId() != 0);
		mSettings.registerOnSharedPreferenceChangeListener(mPreferenceListener);
	}
	
	public fm.jihua.chat.utils.DatabaseHelper getChatDBHelper() {
		return mChatDBHelper;
	}
	
	public SharedPreferences getDefaultPreferences(){
		return mSettings;
	}
	
	
	public abstract Class<?> getStartupActivity();
	
	public abstract Class<?> getChatActivity();
	
	public abstract int getIcon();
	
	public abstract User getUser(String jid);
	
	protected abstract String getChatHost();
	
	  /* Constants for PREFERENCE_KEY
     * The format of the Preference key is :
     * $name_KEY = "$name"
     */
    /** Preference key for account username. */
    public static final String ACCOUNT_USERNAME_KEY = "account_username";
    /** Preference key for account password. */
    public static final String ACCOUNT_PASSWORD_KEY = "account_password";
    /** Preference key for status (available, busy, away, ...). */
    public static final String STATUS_KEY = "status";
    /** Preference key for status message. */
    public static final String STATUS_TEXT_KEY = "status_text";
    /** Preference key for connection resource . */
    public static final String CONNECTION_RESOURCE_KEY = "connection_resource";
    /** Preference key for connection priority. */
    public static final String CONNECTION_PRIORITY_KEY = "connection_priority";
    /** Preference key for the use of a proxy. */
    public static final String PROXY_USE_KEY = "proxy_use";
    /** Preference key for the type of proxy. */
    public static final String PROXY_TYPE_KEY = "proxy_type";
    /** Preference key for the proxy server. */
    public static final String PROXY_SERVER_KEY = "proxy_server";
    /** Preference key for the proxy port. */
    public static final String PROXY_PORT_KEY = "proxy_port";
    /** Preference key for the proxy username. */
    public static final String PROXY_USERNAME_KEY = "proxy_username";
    /** Preference key for the proxy password. */
    public static final String PROXY_PASSWORD_KEY = "proxy_password";
    /** Preference key for vibrate on notification. */
    public static final String NOTIFICATION_VIBRATE_KEY = "notification_vibrate";
    /** Preference key for notification sound. */
    public static final String NOTIFICATION_SOUND_KEY = "notification_sound";
    /** Preference key for notification sound of course warn. */
    public static final String NOTIFICATION_SOUND_COURSE_WARN_KEY = "notification_sound_course_warn";
    /** Preference key for smack debugging. */
    public static final String SMACK_DEBUG_KEY = "smack_debug";
    /** Preference key for full Jid for login. */
    public static final String FULL_JID_LOGIN_KEY = "full_jid_login";
    /** Preference key for display offline contact. */
    public static final String SHOW_OFFLINE_CONTACTS_KEY = "show_offline_contacts";
    /** Preference key for hide the groups. */
    public static final String HIDE_GROUPS_KEY = "hide_groups";
    /** Preference key for auto away enable. */
    public static final String USE_AUTO_AWAY_KEY = "use_auto_away";
    /** Preference key for auto away message. */
    public static final String AUTO_AWAY_MSG_KEY = "auto_away_msg";
    /** Preference key for compact chat ui. */
    public static final String USE_COMPACT_CHAT_UI_KEY = "use_compact_chat_ui";
    /** Preference key for history path on the SDCard. */
    public static final String CHAT_HISTORY_KEY = "settings_chat_history_path";
    public static final String CONFIG_BASE = "base";
    public static final String SECRET_POST_READ_DATE = "secret_post";
    public static final String PREFERENCE_USERID = "userid";

    private boolean mIsConnected;
    private boolean mIsConnecting;
    private boolean mIsAccountConfigured;
    private boolean mPepEnabled;
    protected SharedPreferences mSettings;
    PreferenceListener mPreferenceListener = getPreferenceListener();

    public int getMyUserId() {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE, MODE_PRIVATE);
		return share.getInt(PREFERENCE_USERID, 0);
	}
    
    public void setMyUserId(int id) {
		SharedPreferences share = this.getSharedPreferences(CONFIG_BASE,
				MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putInt(PREFERENCE_USERID, id);
		editor.commit();
	}
    
    @Override
    public void onTerminate() {
	super.onTerminate();
	mSettings.unregisterOnSharedPreferenceChangeListener(mPreferenceListener);
    }

    /**
     * Tell if Beem is connected to a XMPP server.
     * @return false if not connected.
     */
    public boolean isConnected() {
    	return mIsConnected;
    }

    /**
     * Set the status of the connection to a XMPP server of BEEM.
     * @param isConnected set for the state of the connection.
     */
    public void setConnected(boolean isConnected) {
	mIsConnected = isConnected;
    }
    
    /**
     * Tell if Beem is connected to a XMPP server.
     * @return false if not connected.
     */
    public boolean isConnecting() {
	return mIsConnecting;
    }

    /**
     * Set the status of the connection to a XMPP server of BEEM.
     * @param isConnected set for the state of the connection.
     */
    public void setConnecting(boolean isConnecting) {
	mIsConnecting= isConnecting;
    }

    /**
     * Tell if a XMPP account is configured.
     * @return false if there is no account configured.
     */
    public boolean isAccountConfigured() {
	return mIsAccountConfigured;
    }

    /**
     * Enable Pep in the application context.
     *
     * @param enabled true to enable pep
     */
    public void setPepEnabled(boolean enabled) {
	mPepEnabled = enabled;
    }

    /**
     * Check if Pep is enabled.
     *
     * @return true if enabled
     */
    public boolean isPepEnabled() {
    	return mPepEnabled;
    }
    
    protected PreferenceListener getPreferenceListener(){
    	return new PreferenceListener();
    }
    
    /**
     * A listener for all the change in the preference file. It is used to maintain the global state of the application.
     */
    public class PreferenceListener implements SharedPreferences.OnSharedPreferenceChangeListener {

	/**
	 * Constructor.
	 */
	public PreferenceListener() {
	}

	@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			if (ACCOUNT_PASSWORD_KEY.equals(key)) {
				mIsAccountConfigured = getMyUserId() != 0;
			}
		}
	}
}
