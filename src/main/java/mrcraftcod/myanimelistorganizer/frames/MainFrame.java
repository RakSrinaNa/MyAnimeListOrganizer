package mrcraftcod.myanimelistorganizer.frames;

import com.mashape.unirest.http.exceptions.UnirestException;
import mrcraftcod.myanimelistorganizer.Main;
import mrcraftcod.myanimelistorganizer.enums.Status;
import mrcraftcod.myanimelistorganizer.frames.components.PanelAnime;
import mrcraftcod.myanimelistorganizer.objects.Anime;
import mrcraftcod.myanimelistorganizer.utils.MyAnimeListHandler;
import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class MainFrame extends JFrame
{
	public final MyAnimeListHandler myal;
	private final PanelAnime watching;
	private final PanelAnime completed;
	private final PanelAnime onHold;
	private final PanelAnime dropped;
	private final PanelAnime planned;

	public MainFrame(MyAnimeListHandler myAnimeListHandler)
	{
		super("MyAnimeList Organizer");
		if(myAnimeListHandler == null)
			System.exit(2);
		myal = myAnimeListHandler;
		this.setIconImages(Main.icons);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(new GridBagLayout());
		watching = new PanelAnime(this, Status.WATCHING);
		completed = new PanelAnime(this, Status.COMPLETED);
		onHold = new PanelAnime(this, Status.ONHOLD);
		dropped = new PanelAnime(this, Status.DROPPED);
		planned = new PanelAnime(this, Status.PLANNEDTOWATCH);
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab(watching.getName(), null, watching);
		tabbedPane.addTab(completed.getName(), null, completed);
		tabbedPane.addTab(onHold.getName(), null, onHold);
		tabbedPane.addTab(dropped.getName(), null, dropped);
		tabbedPane.addTab(planned.getName(), null, planned);
		int line = 0;
		GridBagConstraints gcb = new GridBagConstraints();
		getContentPane().setLayout(new GridBagLayout());
		gcb.anchor = GridBagConstraints.PAGE_START;
		gcb.fill = GridBagConstraints.BOTH;
		gcb.weighty = 100;
		gcb.weightx = 100;
		gcb.gridheight = 1;
		gcb.gridwidth = 1;
		gcb.gridx = 0;
		gcb.gridy = line++;
		getContentPane().add(tabbedPane, gcb);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - this.getPreferredSize().width / 2, dim.height / 2 - this.getPreferredSize().height / 2);
		pack();
		setVisible(true);
	}

	public void modify(Anime anime) throws URISyntaxException, UnirestException, MalformedURLException
	{
		myal.modify(this, anime);
		updateAll();
	}

	public void updateAll()
	{
		watching.reload();
		completed.reload();
		onHold.reload();
		dropped.reload();
		planned.reload();
	}
}
