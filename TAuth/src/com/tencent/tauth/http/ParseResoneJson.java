package com.tencent.tauth.http;


import org.json.JSONException;
import org.json.JSONObject;

public class ParseResoneJson {
	
    public static JSONObject parseJson(String response)
    throws JSONException, NumberFormatException, CommonException {
  // Edge case: when sending a POST request to /[post_id]/likes
  // the return value is 'true' or 'false'. Unfortunately
  // these values cause the JSONObject constructor to throw
  // an exception.
  if (response.equals("false")) {
      throw new CommonException("request failed");
  }
  if (response.equals("true")) {
      response = "{value : true}";
  }
  
  if (response.endsWith(");")) {
	  response = response.replaceAll("([a-z]*)\\(([^\\)]*)\\);", "$2");
	  response = response.trim();
  }
  
  JSONObject json = new JSONObject(response);

  // errors set by the server are not consistent
  // they depend on the method and endpoint
  /*if (json.has("error")) {
      JSONObject error = json.getJSONObject("error");
      throw new CommonException(
              error.getString("message"), error.getString("type"), 0);
  }
  if (json.has("error_code") && json.has("error_msg")) {
      throw new CommonException(json.getString("error_msg"), "",
              Integer.parseInt(json.getString("error_code")));
  }
  if (json.has("error_code")) {
      throw new CommonException("request failed", "",
              Integer.parseInt(json.getString("error_code")));
  }
  if (json.has("error_msg")) {
      throw new CommonException(json.getString("error_msg"));
  }
  if (json.has("error_reason")) {
      throw new CommonException(json.getString("error_reason"));
  }*/
  return json;
}

}
