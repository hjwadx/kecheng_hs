package fm.jihua.kecheng.rest.service;

import java.io.UnsupportedEncodingException;

import org.json.JSONArray;
import org.json.JSONException;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.JsonSyntaxException;


public class JSONArrayRequest extends JsonRequest<JSONArray> {
    
    public JSONArrayRequest(RestEntity restEntity,
            Listener<JSONArray> listener,
            ErrorListener errorListener) {
    	super(restEntity.methodCode, restEntity.url, (restEntity.requestData == null) ? null : restEntity.requestData.toString(), listener, errorListener);
	}


    @Override
    protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            JSONArray responseArray = new JSONArray(json);
            return Response.success(responseArray,
                                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
        	return Response.error(new ParseError(e));
		}
    }
}