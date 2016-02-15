package fr.myanimelistorganizer.objects;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AnimeInfo implements Comparable
{
	private int ID;
	private String title;
	private String englishTitle;
	private int episodes;
	private String type;
	private String status;
	private Date start;
	private Date end;
	private String synopsis;

	public AnimeInfo()
	{
	}

	public static AnimeInfo bindXML(Node node) throws ParseException
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		AnimeInfo anime = new AnimeInfo();
		Element element = (Element) node;
		anime.setID(Integer.parseInt(element.getElementsByTagName("id").item(0).getTextContent()));
		anime.setTitle(element.getElementsByTagName("title").item(0).getTextContent());
		anime.setEnglishTitle(element.getElementsByTagName("english").item(0).getTextContent());
		anime.setEpisodes(Integer.parseInt(element.getElementsByTagName("episodes").item(0).getTextContent()));
		anime.setType(element.getElementsByTagName("type").item(0).getTextContent());
		anime.setStatus(element.getElementsByTagName("status").item(0).getTextContent());
		anime.setStart(df.parse(element.getElementsByTagName("start_date").item(0).getTextContent()));
		anime.setStart(df.parse(element.getElementsByTagName("end_date").item(0).getTextContent()));
		anime.setSynopsis(element.getElementsByTagName("synopsis").item(0).getTextContent());
		return anime;
	}

	public int getID()
	{
		return ID;
	}

	public void setID(int ID)
	{
		this.ID = ID;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getEnglishTitle()
	{
		return englishTitle;
	}

	public void setEnglishTitle(String englishTitle)
	{
		this.englishTitle = englishTitle;
	}

	public int getEpisodes()
	{
		return episodes;
	}

	public void setEpisodes(int episodes)
	{
		this.episodes = episodes;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public Date getStart()
	{
		return start == null ? new Date(0) : start;
	}

	public void setStart(Date start)
	{
		this.start = start;
	}

	public Date getEnd()
	{
		return end == null ? new Date(0) : end;
	}

	public void setEnd(Date end)
	{
		this.end = end;
	}

	public String getSynopsis()
	{
		return synopsis;
	}

	public void setSynopsis(String synopsis)
	{
		this.synopsis = synopsis;
	}

	@Override
	public int hashCode()
	{
		return getTitle().hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if(!(o instanceof AnimeInfo))
			return false;
		AnimeInfo anime = (AnimeInfo) o;
		return this.getTitle().equals(anime.getTitle());
	}

	@Override
	public String toString()
	{
		return getTitle();
	}

	@Override
	public int compareTo(Object o)
	{
		if(!(o instanceof AnimeInfo))
			return 1;
		AnimeInfo anime = (AnimeInfo) o;
		return this.getTitle().compareTo(anime.getTitle());
	}
}
