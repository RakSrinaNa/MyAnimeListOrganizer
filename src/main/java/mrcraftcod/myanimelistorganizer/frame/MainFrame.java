package mrcraftcod.myanimelistorganizer.frame;

import mrcraftcod.myanimelistorganizer.Main;
import mrcraftcod.myanimelistorganizer.frame.component.PanelAnime;
import mrcraftcod.myanimelistorganizer.object.Anime;
import mrcraftcod.myanimelistorganizer.utils.MyAnimeListHandler;
import javax.swing.*;
import java.awt.*;

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
		watching = new PanelAnime(this, Anime.WATCHING);
		completed = new PanelAnime(this, Anime.COMPLETED);
		onHold = new PanelAnime(this, Anime.ONHOLD);
		dropped = new PanelAnime(this, Anime.DROPPED);
		planned = new PanelAnime(this, Anime.PLANEDTOWATCH);
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Watching", null, watching);
		tabbedPane.addTab("Completed", null, completed);
		tabbedPane.addTab("On hold", null, onHold);
		tabbedPane.addTab("Dropped", null, dropped);
		tabbedPane.addTab("Plan to watch", null, planned);
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

	public void modify(Anime anime)
	{
		myal.modify(this, anime);
		updateAll();
	}

	private void updateAll()
	{
		watching.reload();
		completed.reload();
		onHold.reload();
		dropped.reload();
		planned.reload();
	}
}
