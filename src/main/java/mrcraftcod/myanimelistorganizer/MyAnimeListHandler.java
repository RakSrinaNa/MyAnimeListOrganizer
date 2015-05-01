package mrcraftcod.myanimelistorganizer;

import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

public class MyAnimeListHandler
{
	public static final int WATCHING = 1, COMPLETED = 2, ONHOLD = 3, DROPPED = 4, PLANEDTOWATCH = 6, LOW = 0, MEDIUM = 1, HIGH = 2;
	private final String auth;

	public MyAnimeListHandler(String user, String password) throws Exception
	{
		auth = Base64.getEncoder().encodeToString((user + ":" + password).getBytes());
		if(!connect())
			throw new Exception("Credentials aren't correct");
	}

	private boolean connect() throws IOException, URISyntaxException, UnirestException
	{
		HashMap<String, String> header = new HashMap<>();
		header.put("Authorization", "Basic " + auth);
		return URLHandler.getStatus(new URL("http://myanimelist.net/api/account/verify_credentials.xml"), header) == 200;
	}

	public boolean addAnime(int ID) throws IOException, URISyntaxException, UnirestException
	{
		return  addAnime(ID, PLANEDTOWATCH);
	}

	public boolean addAnime(int ID, int status) throws IOException, URISyntaxException, UnirestException
	{
		HashMap<String, String> header = new HashMap<>();
		header.put("Authorization", "Basic " + auth);
		return URLHandler.postCode(new URL("http://myanimelist.net/api/animelist/add/" + ID + ".xml"), header, genXMLAnime(0, status, 0, null, null, 0)) == 201;
	}

	public boolean deleteAnime(int ID) throws IOException, URISyntaxException, UnirestException
	{
		HashMap<String, String> header = new HashMap<>();
		header.put("Authorization", "Basic " + auth);
		return URLHandler.postCode(new URL("http://myanimelist.net/api/animelist/delete/" + ID + ".xml"), header) == 200;
	}

	private String genXMLAnime(int episode, int status, int score, Date start, Date end, int priority)
	{
		DateFormat df = new SimpleDateFormat("MMddyyyy");
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<entry>");
		sb.append("<episode>");
		sb.append(episode);
		sb.append("</episode>");
		sb.append("<status>");
		sb.append(status);
		sb.append("</status>");
		sb.append("<score>");
		sb.append(score);
		sb.append("</score>");
		if(start != null)
		{
			sb.append("<date_start>");
			sb.append(df.format(start));
			sb.append("</date_start>");
		}
		if(end != null)
		{
			sb.append("<date_finish>");
			sb.append(df.format(end));
			sb.append("</date_finish>");
		}
		sb.append("<priority>");
		sb.append(priority);
		sb.append("</priority>");
		sb.append("</entry>");
		return sb.toString();
	}

	public boolean updateAnime(int ID, int episode, int status, int score, Date start, Date end, int priority) throws MalformedURLException, URISyntaxException, UnirestException
	{
		HashMap<String, String> header = new HashMap<>();
		header.put("Authorization", "Basic " + auth);
		return URLHandler.postCode(new URL("http://myanimelist.net/api/animelist/update/" + ID + ".xml"), header, genXMLAnime(episode, status, score, start, end, priority)) == 200;
	}
}
