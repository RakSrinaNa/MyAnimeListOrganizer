package fr.myanimelistorganizer.utils;

import com.mashape.unirest.http.exceptions.UnirestException;
import fr.myanimelistorganizer.enums.Status;
import fr.myanimelistorganizer.frames.ModifyAnimeDialogFrame;
import fr.myanimelistorganizer.objects.Anime;
import fr.myanimelistorganizer.objects.AnimeInfo;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.utils.URIBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import javax.swing.*;
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
		catch(SAXParseException e)
		{
			Desktop.getDesktop().browse(getAnimeXMLURL().toURI());
			JOptionPane.showMessageDialog(null, "Une v\351rification est n\351cessaire.\nCliquez sur OK quand celle-ci est termin\351e.", "V\351rification", JOptionPane.WARNING_MESSAGE);
			getAnimes();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Erreur durant la r\351cup\351ration de la liste des animes!\n" + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
		}
	}

	private URL getAnimeXMLURL() throws MalformedURLException, URISyntaxException
	{
		HashMap<String, String> params = new HashMap<>();
		params.put("u", getUser());
		params.put("status", "all");
		params.put("type", "anime");
		URIBuilder uriBuilder = new URIBuilder(new URL("http://myanimelist.net/malappinfo.php").toURI());
		for(String key : params.keySet())
			uriBuilder.addParameter(key, params.get(key));
		return new URL(uriBuilder.toString());
	}

	private synchronized void getAnimes() throws IOException, URISyntaxException, ParserConfigurationException, SAXException
	{
		HttpURLConnection con = getHttpConnexion(getAnimeXMLURL());
		con.connect();
		String xml = convertForJDOM(IOUtils.toString(con.getInputStream()));
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
					case WATCHING:
						watchingAnime.add(anime);
						break;
					case COMPLETED:
						completedAnime.add(anime);
						break;
					case ONHOLD:
						onHoldAnime.add(anime);
						break;
					case DROPPED:
						droppedAnime.add(anime);
						break;
					case PLANNEDTOWATCH:
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

	private HttpURLConnection getHttpConnexion(URL url) throws IOException
	{
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestProperty(URLHandler.USER_AGENT_KEY, URLHandler.USER_AGENT);
		con.setRequestProperty(URLHandler.CHARSET_TYPE_KEY, URLHandler.CHARSET_TYPE);
		con.setRequestProperty(URLHandler.CONTENT_TYPE_KEY, URLHandler.CONTENT_TYPE);
		con.setRequestProperty(URLHandler.LANGUAGE_TYPE_KEY, URLHandler.LANGUAGE_TYPE);
		return con;
	}

	private boolean connect() throws IOException, URISyntaxException, UnirestException
	{
		HashMap<String, String> header = new HashMap<>();
		header.put("Authorization", "Basic " + auth);
		return URLHandler.getStatus(new URL("http://myanimelist.net/api/account/verify_credentials.xml"), header) == 200;
	}

	public synchronized boolean addAnime(AnimeInfo anime, Status status) throws IOException, URISyntaxException, UnirestException
	{
		HashMap<String, String> header = new HashMap<>();
		header.put("Authorization", "Basic " + auth);
		if(URLHandler.postCode(new URL("http://myanimelist.net/api/animelist/add/" + anime.getID() + ".xml"), header, genXMLAnime(0, status.getID(), 0, null, null, 0)) == 201)
		{
			addAnimeToSets(new Anime(anime, status));
			return true;
		}
		return false;
	}

	public TreeSet<AnimeInfo> search(String name) throws IOException, ParserConfigurationException, SAXException
	{
		TreeSet<AnimeInfo> result = new TreeSet<>();
		HttpURLConnection con = getHttpConnexion(new URL("http://myanimelist.net/api/anime/search.xml?q=" + name.replaceAll(" ", "+")));
		con.setRequestProperty("Authorization", "Basic " + auth);
		con.connect();
		String xml = convertForJDOM(IOUtils.toString(con.getInputStream()));
		con.disconnect();
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setNamespaceAware(true);
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
		doc.getDocumentElement().normalize();
		NodeList animeList = doc.getElementsByTagName("entry");
		for(int i = 0; i < animeList.getLength(); i++)
		{
			Node animeNode = animeList.item(i);
			if(animeNode.getNodeType() != Node.ELEMENT_NODE)
				continue;
			try
			{
				result.add(AnimeInfo.bindXML(animeNode));
			}
			catch(ParseException e)
			{
				e.printStackTrace();
			}
		}
		return result;
	}

	public synchronized boolean deleteAnime(Anime anime) throws IOException, URISyntaxException, UnirestException
	{
		HashMap<String, String> header = new HashMap<>();
		header.put("Authorization", "Basic " + auth);
		if(URLHandler.postCode(new URL("http://myanimelist.net/api/animelist/delete/" + anime.getID() + ".xml"), header) == 200)
		{
			removeAnimeFromSets(anime);
			return true;
		}
		return false;
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

	public synchronized boolean updateAnime(int ID, int episode, Status status, int score, Date start, Date end, int priority) throws MalformedURLException, URISyntaxException, UnirestException
	{
		HashMap<String, String> header = new HashMap<>();
		header.put("Authorization", "Basic " + auth);
		return URLHandler.postCode(new URL("http://myanimelist.net/api/animelist/update/" + ID + ".xml"), header, genXMLAnime(episode, status.getID(), score, start, end, priority)) == 200;
	}

	public String getUser()
	{
		return user;
	}

	public TreeSet<Anime> getAnimeList(Status status)
	{
		switch(status)
		{
			case WATCHING:
				return watchingAnime;
			case COMPLETED:
				return completedAnime;
			case ONHOLD:
				return onHoldAnime;
			case DROPPED:
				return droppedAnime;
			case PLANNEDTOWATCH:
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
			if(anime.getTitle().equals(name))
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

	public void modify(Window parent, Anime anime) throws MalformedURLException, UnirestException, URISyntaxException
	{
		new ModifyAnimeDialogFrame(parent, anime).showDialog();
		removeAnimeFromSets(anime);
		addAnimeToSets(anime);
		updateAnime(anime);
	}

	private void addAnimeToSets(Anime anime)
	{
		switch(anime.getStatus())
		{
			case WATCHING:
				watchingAnime.add(anime);
				break;
			case COMPLETED:
				completedAnime.add(anime);
				break;
			case ONHOLD:
				onHoldAnime.add(anime);
				break;
			case DROPPED:
				droppedAnime.add(anime);
				break;
			case PLANNEDTOWATCH:
				plannedAnime.add(anime);
				break;
		}
	}

	public void updateAnime(Anime anime) throws URISyntaxException, UnirestException, MalformedURLException
	{
		updateAnime(anime.getID(), anime.getWatched(), anime.getStatus(), anime.getScore(), anime.getStartWatching(), anime.getEndWatching(), anime.getPriority());
	}

	private String convertForJDOM(String string)
	{
		return string.replaceAll("&mdash;", "-").replaceAll("&ocirc;", "o").replaceAll("&ucirc;", "u");
	}

	public Anime getAnimeByID(int ID)
	{
		for(Anime anime : watchingAnime)
			if(anime.getID() == ID)
				return anime;
		for(Anime anime : completedAnime)
			if(anime.getID() == ID)
				return anime;
		for(Anime anime : onHoldAnime)
			if(anime.getID() == ID)
				return anime;
		for(Anime anime : droppedAnime)
			if(anime.getID() == ID)
				return anime;
		for(Anime anime : plannedAnime)
			if(anime.getID() == ID)
				return anime;
		return null;
	}

	public boolean containsID(int ID)
	{
		return getAnimeByID(ID) != null;
	}
}
