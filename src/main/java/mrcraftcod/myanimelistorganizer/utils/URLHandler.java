package mrcraftcod.myanimelistorganizer.utils;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.mashape.unirest.request.body.MultipartBody;
import mrcraftcod.myanimelistorganizer.Main;
import org.apache.http.client.utils.URIBuilder;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class URLHandler
{
	public static final String USER_AGENT_KEY = "User-Agent";
	public static final String USER_AGENT = "MyAnimeListOrganizer/" + Main.VERSION;
	public static final String CHARSET_TYPE_KEY = "charset";
	public static final String CHARSET_TYPE = "utf-8";
	public static final String CONTENT_TYPE_KEY = "Content-Type";
	public static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
	public static final String LANGUAGE_TYPE_KEY = "Accept-Language";
	public static final String LANGUAGE_TYPE = "fr-FR";
	private static final int TIMEOUT = 30000;

	public static String getAsString(URL url, Map<String, String> headers) throws UnirestException, URISyntaxException
	{
		return getAsString(url, headers, null);
	}

	public static String getAsString(URL url, Map<String, String> headers, Map<String, String> params) throws UnirestException, URISyntaxException
	{
		GetRequest req = getRequest(url, headers, params);
		HttpResponse<String> response = req.asString();
		return response.getBody();
	}

	public static int getStatus(URL url, Map<String, String> headers) throws UnirestException, URISyntaxException
	{
		return getStatus(url, headers, null);
	}

	public static int getStatus(URL url, Map<String, String> headers, Map<String, String> params) throws UnirestException, URISyntaxException
	{
		GetRequest request = getRequest(url, headers, params);
		return request.asBinary().getStatus();
	}

	private static GetRequest getRequest(URL url, Map<String, String> headers, Map<String, String> params) throws URISyntaxException
	{
		Unirest.clearDefaultHeaders();
		Unirest.setTimeouts(TIMEOUT, TIMEOUT);
		Unirest.setDefaultHeader(USER_AGENT_KEY, USER_AGENT);
		return Unirest.get(getBuiltURI(url, params).toString()).headers(headers).header(LANGUAGE_TYPE_KEY, LANGUAGE_TYPE).header(CONTENT_TYPE_KEY, CONTENT_TYPE).header(CHARSET_TYPE_KEY, CHARSET_TYPE).header(USER_AGENT_KEY, USER_AGENT);
	}

	public static URI getBuiltURI(URL url, Map<String, String> params) throws URISyntaxException
	{
		URIBuilder uriBuilder = new URIBuilder(url.toURI());
		if(params != null)
			for(String key : params.keySet())
				uriBuilder.addParameter(key, params.get(key));
		return uriBuilder.build();
	}

	private static MultipartBody postRequest(URL url, Map<String, String> headers, Map<String, String> params, String data) throws URISyntaxException
	{
		Unirest.clearDefaultHeaders();
		Unirest.setTimeouts(TIMEOUT, TIMEOUT);
		Unirest.setDefaultHeader(USER_AGENT_KEY, USER_AGENT);
		HttpRequestWithBody request = Unirest.post(getBuiltURI(url, params).toString()).headers(headers).header(LANGUAGE_TYPE_KEY, LANGUAGE_TYPE).header(CONTENT_TYPE_KEY, CONTENT_TYPE).header(CHARSET_TYPE_KEY, CHARSET_TYPE).header(USER_AGENT_KEY, USER_AGENT);
		return data == null ? request.field("", "") : request.field("data", data);
	}

	public static String postAsString(URL url, HashMap<String, String> headers, String data) throws URISyntaxException, UnirestException
	{
		MultipartBody request = postRequest(url, headers, null, data);
		return request.asString().getBody();
	}

	public static int postCode(URL url, HashMap<String, String> headers, String data) throws URISyntaxException, UnirestException
	{
		MultipartBody request = postRequest(url, headers, null, data);
		return request.asString().getStatus();
	}

	public static int postCode(URL url, HashMap<String, String> headers) throws URISyntaxException, UnirestException
	{
		return postCode(url, headers, null);
	}

	public static String getAsString(URL url) throws URISyntaxException, UnirestException
	{
		return getAsString(url, null);
	}
}
