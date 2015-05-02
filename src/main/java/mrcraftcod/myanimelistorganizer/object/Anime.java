package mrcraftcod.myanimelistorganizer.object;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Anime implements Comparable
{
	public static final int WATCHING = 1, COMPLETED = 2, ONHOLD = 3, DROPPED = 4, PLANEDTOWATCH = 6, LOW = 0, MEDIUM = 1, HIGH = 2;
	private int ID;
	private String title;
	private int type;
	private int episodes;
	private int serieStatus;
	private Date start;
	private Date end;
	private int watched;
	private Date startWatching;
	private Date endWatching;
	private int score;
	private int status;

	public Anime()
	{}

	public static Anime bindXML(Node node) throws ParseException
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Anime anime = new Anime();
		Element element = (Element) node;
		anime.setID(Integer.parseInt(element.getElementsByTagName("series_animedb_id").item(0).getTextContent()));
		anime.setTitle(element.getElementsByTagName("series_title").item(0).getTextContent());
		anime.setType(Integer.parseInt(element.getElementsByTagName("series_type").item(0).getTextContent()));
		anime.setEpisodes(Integer.parseInt(element.getElementsByTagName("series_episodes").item(0).getTextContent()));
		anime.setSerieStatus(Integer.parseInt(element.getElementsByTagName("series_type").item(0).getTextContent()));
		anime.setStart(df.parse(element.getElementsByTagName("series_start").item(0).getTextContent()));
		anime.setEnd(df.parse(element.getElementsByTagName("series_end").item(0).getTextContent()));
		anime.setWatched(Integer.parseInt(element.getElementsByTagName("my_watched_episodes").item(0).getTextContent()));
		anime.setStartWatching(df.parse(element.getElementsByTagName("my_start_date").item(0).getTextContent()));
		anime.setEndWatching(df.parse(element.getElementsByTagName("my_finish_date").item(0).getTextContent()));
		anime.setScore(Integer.parseInt(element.getElementsByTagName("my_score").item(0).getTextContent()));
		anime.setStatus(Integer.parseInt(element.getElementsByTagName("my_status").item(0).getTextContent()));
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

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public int getEpisodes()
	{
		return episodes;
	}

	public void setEpisodes(int episodes)
	{
		this.episodes = episodes;
	}

	public int getSerieStatus()
	{
		return serieStatus;
	}

	public void setSerieStatus(int serieStatus)
	{
		this.serieStatus = serieStatus;
	}

	public Date getStart()
	{
		return start;
	}

	public void setStart(Date start)
	{
		this.start = start;
	}

	public Date getEnd()
	{
		return end;
	}

	public void setEnd(Date end)
	{
		this.end = end;
	}

	public int getWatched()
	{
		return watched;
	}

	public void setWatched(int watched)
	{
		this.watched = watched;
	}

	public Date getStartWatching()
	{
		return startWatching;
	}

	public void setStartWatching(Date startWatching)
	{
		this.startWatching = startWatching;
	}

	public Date getEndWatching()
	{
		return endWatching;
	}

	public void setEndWatching(Date endWatching)
	{
		this.endWatching = endWatching;
	}

	public int getScore()
	{
		return score;
	}

	public void setScore(int score)
	{
		this.score = score;
	}

	public int getStatus()
	{
		return status;
	}

	public void setStatus(int status)
	{
		this.status = status;
	}

	@Override
	public String toString()
	{
		return getTitle();
	}

	@Override
	public int hashCode()
	{
		return getTitle().hashCode();
	}

	@Override
	public int compareTo(Object o)
	{
		if(!(o instanceof Anime))
			return 1;
		Anime anime = (Anime)o;
		return this.getTitle().compareTo(anime.getTitle());
	}

	@Override
	public boolean equals(Object o)
	{
		if(!(o instanceof Anime))
			return false;
		Anime anime = (Anime)o;
		return this.getTitle().equals(anime.getTitle());
	}
}
