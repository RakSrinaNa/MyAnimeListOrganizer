package mrcraftcod.myanimelistorganizer.utils;

import com.mashape.unirest.http.exceptions.UnirestException;
import mrcraftcod.myanimelistorganizer.frame.ModifyAnimeDialogFrame;
import mrcraftcod.myanimelistorganizer.object.Anime;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.utils.URIBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeSet;

public class MyAnimeListHandler
{
	private final String auth;
	private final String user;
	private final TreeSet<Anime> watchingAnime;
	private final TreeSet<Anime> completedAnime;
	private final TreeSet<Anime> onHoldAnime;
	private final TreeSet<Anime> droppedAnime;
	private final TreeSet<Anime> plannedAnime;

	public MyAnimeListHandler(String user, String password) throws Exception
	{
		this.user = user;
		auth = Base64.getEncoder().encodeToString((getUser() + ":" + password).getBytes());
		if(!connect())
			throw new Exception("Credentials aren't correct");
		watchingAnime = new TreeSet<>();
		completedAnime = new TreeSet<>();
		onHoldAnime = new TreeSet<>();
		droppedAnime = new TreeSet<>();
		plannedAnime = new TreeSet<>();
		try
		{
			getAnimes();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void getAnimes() throws IOException, URISyntaxException, UnirestException, ParserConfigurationException, SAXException
	{
		HashMap<String, String> params = new HashMap<>();
		params.put("u", getUser());
		params.put("status", "all");
		params.put("type", "anime");
		URIBuilder uriBuilder = new URIBuilder(new URL("http://myanimelist.net/malappinfo.php").toURI());
		for(String key : params.keySet())
			uriBuilder.addParameter(key, params.get(key));
		HttpURLConnection con = (HttpURLConnection)new URL(uriBuilder.toString()).openConnection();
		con.setRequestProperty(URLHandler.USER_AGENT_KEY, URLHandler.USER_AGENT);
		con.setRequestProperty(URLHandler.CHARSET_TYPE_KEY, URLHandler.CHARSET_TYPE);
		con.setRequestProperty(URLHandler.CONTENT_TYPE_KEY, URLHandler.CONTENT_TYPE);
		con.setRequestProperty(URLHandler.LANGUAGE_TYPE_KEY, URLHandler.LANGUAGE_TYPE);
		con.connect();
		String xml = IOUtils.toString(con.getInputStream());
		con.disconnect();
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setNamespaceAware(true);
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
		doc.getDocumentElement().normalize();
		NodeList animeList = doc.getElementsByTagName("anime");
		for(int i = 0; i < animeList.getLength(); i++)
		{
			Node animeNode = animeList.item(i);
			if(animeNode.getNodeType() != Node.ELEMENT_NODE)
				continue;
			try
			{
				Anime anime = Anime.bindXML(animeNode);
				switch(anime.getStatus())
				{
					case Anime.WATCHING:
						watchingAnime.add(anime);
						break;
					case Anime.COMPLETED:
						completedAnime.add(anime);
						break;
					case Anime.ONHOLD:
						onHoldAnime.add(anime);
						break;
					case Anime.DROPPED:
						droppedAnime.add(anime);
						break;
					case Anime.PLANEDTOWATCH:
						plannedAnime.add(anime);
						break;
				}
			}
			catch(ParseException e)
			{
				e.printStackTrace();
			}

		}
	}

	private boolean connect() throws IOException, URISyntaxException, UnirestException
	{
		HashMap<String, String> header = new HashMap<>();
		header.put("Authorization", "Basic " + auth);
		return URLHandler.getStatus(new URL("http://myanimelist.net/api/account/verify_credentials.xml"), header) == 200;
	}

	public boolean addAnime(int ID) throws IOException, URISyntaxException, UnirestException
	{
		return addAnime(ID, Anime.PLANEDTOWATCH);
	}

	public boolean addAnime(int ID, int status) throws IOException, URISyntaxException, UnirestException
	{
		HashMap<String, String> header = new HashMap<>();
		header.put("Authorization", "Basic " + auth);
		if(URLHandler.postCode(new URL("http://myanimelist.net/api/animelist/add/" + ID + ".xml"), header, genXMLAnime(0, status, 0, null, null, 0)) == 201)
		{
			addAnimeToSets(getAnimeInfos(ID));
			return true;
		}
		return false;
	}

	private Anime getAnimeInfos(int ID)
	{
		return new Anime();
	}

	public boolean deleteAnime(Anime anime) throws IOException, URISyntaxException, UnirestException
	{
		HashMap<String, String> header = new HashMap<>();
		header.put("Authorization", "Basic " + auth);
		return URLHandler.postCode(new URL("http://myanimelist.net/api/animelist/delete/" + anime.getID() + ".xml"), header) == 200;
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

	public String getUser()
	{
		return user;
	}

	public TreeSet<Anime> getAnimeList(int status)
	{
		switch(status)
		{
			case Anime.WATCHING:
				return watchingAnime;
			case Anime.COMPLETED:
				return completedAnime;
			case Anime.ONHOLD:
				return onHoldAnime;
			case Anime.DROPPED:
				return droppedAnime;
			case Anime.PLANEDTOWATCH:
				return plannedAnime;
			default:
				return null;
		}
	}

	public Anime getAnimeByName(String name)
	{
		for(Anime anime : watchingAnime)
			if(anime.getTitle().equals(name))
				return anime;
		for(Anime anime : completedAnime)
				return anime;
		for(Anime anime : onHoldAnime)
			if(anime.getTitle().equals(name))
				return anime;
		for(Anime anime : droppedAnime)
			if(anime.getTitle().equals(name))
				return anime;
		for(Anime anime : plannedAnime)
			if(anime.getTitle().equals(name))
				return anime;
		return null;
	}

	private void removeAnimeFromSets(Anime anime)
	{
		watchingAnime.remove(anime);
		completedAnime.remove(anime);
		onHoldAnime.remove(anime);
		droppedAnime.remove(anime);
		plannedAnime.remove(anime);
	}

	public void modify(Window parent, Anime anime)
	{
		Anime modified = new ModifyAnimeDialogFrame(parent, anime).showDialog();
		removeAnimeFromSets(anime);
		addAnimeToSets(modified);
	}

	private void addAnimeToSets(Anime anime)
	{
		switch(anime.getStatus())
		{
			case Anime.WATCHING:
				watchingAnime.add(anime);
				break;
			case Anime.COMPLETED:
				completedAnime.add(anime);
			break;
			case Anime.ONHOLD:
				onHoldAnime.add(anime);
			break;
			case Anime.DROPPED:
				droppedAnime.add(anime);
			break;
			case Anime.PLANEDTOWATCH:
				plannedAnime.add(anime);
			break;
		}
	}
}
