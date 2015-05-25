package mrcraftcod.myanimelistorganizer.frames.components;

import com.mashape.unirest.http.exceptions.UnirestException;
import mrcraftcod.myanimelistorganizer.enums.Status;
import mrcraftcod.myanimelistorganizer.frames.MainFrame;
import mrcraftcod.myanimelistorganizer.objects.Anime;
import mrcraftcod.myanimelistorganizer.utils.URLHandler;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

public class AnimePanel extends JPanel
{
	public final JTableUneditableModel model;
	private final JTable table;
	private final MainFrame parent;
	private final Status status;

	public AnimePanel(MainFrame parent, Status status)
	{
		this.parent = parent;
		this.status = status;
		setName(status.getName());
		setLayout(new GridBagLayout());
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		model = new JTableUneditableModel(getAnimeData(parent.myal.getAnimeList(status)), new String[]{"Titre", "Progression", "Note"});
		table = new JTable(model)
		{
			private static final long serialVersionUID = 4244155500155330717L;

			@Override
			public Class<?> getColumnClass(int column)
			{
				return String.class;
			}
		};
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		this.table.addKeyListener(new KeyListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{
			}

			@Override
			public void keyPressed(KeyEvent e)
			{
				int rowindex = AnimePanel.this.table.getSelectedRow();
				if(rowindex < 0)
					return;
				if(e.getExtendedKeyCode() != KeyEvent.VK_DELETE)
					return;
				String name = AnimePanel.this.table.getValueAt(rowindex, 0).toString().trim();
				Anime anime = parent.myal.getAnimeByName(name);
				try
				{
					if(anime != null)
						parent.myal.deleteAnime(anime);
				}
				catch(IOException | UnirestException | URISyntaxException e1)
				{
					e1.printStackTrace();
				}
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
			}
		});
		this.table.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
			}

			@Override
			public void mousePressed(MouseEvent event)
			{
			}

			@Override
			public void mouseReleased(MouseEvent event)
			{
				int row = AnimePanel.this.table.rowAtPoint(event.getPoint());
				if(row >= 0 && row < AnimePanel.this.table.getRowCount())
					AnimePanel.this.table.setRowSelectionInterval(row, row);
				else
					AnimePanel.this.table.clearSelection();
				int rowindex = AnimePanel.this.table.getSelectedRow();
				if(event.isPopupTrigger() && event.getComponent() instanceof JTable)
				{
					final Anime anime = parent.myal.getAnimeByName(AnimePanel.this.table.getValueAt(rowindex, 0).toString().trim());
					JPopupMenu popup = new JPopupMenu();
					JMenuItem viewMoreAnime = new JMenuItem("+1 Episode vu");
					viewMoreAnime.addActionListener(event1 -> {
						try
						{
							if(anime.addWatched(1))
								if(JOptionPane.showConfirmDialog(parent, "Tout les \351pisodes ont \351t\351 vus.\n\nPasser l'anime en " + Status.COMPLETED.getName().toLowerCase() + "?", "Anime termin\351", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
								{
									int score = 0;
									try
									{
										score = Integer.valueOf(JOptionPane.showInputDialog(parent, "Entrez la note \340 attribuer (0 si pas de note):", "Note", JOptionPane.QUESTION_MESSAGE));
									}
									catch(Exception e1){}
									anime.setScore(score);
									anime.setStatus(Status.COMPLETED);
								}
							parent.myal.updateAnime(anime);
							parent.updateAll();
						}
						catch(Exception exception)
						{
						}
					});
					JMenuItem modifyAnime = new JMenuItem("Modifier");
					modifyAnime.addActionListener(event1 -> {
						try
						{
							parent.myal.modify(parent, anime);
							parent.updateAll();
						}
						catch(Exception exception)
						{
						}
					});
					JMenuItem deleteAnime = new JMenuItem("Supprimer");
					deleteAnime.addActionListener(event1 -> {
						try
						{
							parent.myal.deleteAnime(anime);
							parent.updateAll();
						}
						catch(Exception exception)
						{
						}
					});
					JMenuItem nekochan = new JMenuItem("Tenter d'ouvrir sur nekochan");
					nekochan.addActionListener(e ->{
						try
						{
							URL url = new URL("http://www.neko-san.fr/anime/" + anime.getTitle().replaceAll(" ", "-"));
							if(URLHandler.getAsString(url).contains("http://img.neko-san.fr/img/404.png"))
								JOptionPane.showMessageDialog(parent, "Anime non trouv\351", "Erreur", JOptionPane.ERROR_MESSAGE);
							else
								Desktop.getDesktop().browse(url.toURI());
						}
						catch(IOException | URISyntaxException | UnirestException e1)
						{
							e1.printStackTrace();
						}
					});
					JMenuItem google = new JMenuItem("Rechercher sur google");
					google.addActionListener(e ->{
						try
						{
							HashMap<String, String> params = new HashMap<>();
							params.put("hl", "fr");
							params.put("num", "20");
							params.put("q", anime.getTitle());
							Desktop.getDesktop().browse(URLHandler.getBuiltURI(new URL("http://www.google.fr/search"), params));
						}
						catch(Exception e1)
						{
							e1.printStackTrace();
						}
					});
					if(status == Status.WATCHING)
					{
						if(anime.getWatched() < anime.getEpisodes())
							popup.add(viewMoreAnime);
						popup.add(google);
						popup.add(nekochan);
						popup.addSeparator();
					}
					popup.add(modifyAnime);
					popup.add(deleteAnime);
					popup.show(event.getComponent(), event.getX(), event.getY());
				}
			}

			@Override
			public void mouseEntered(MouseEvent event)
			{
			}

			@Override
			public void mouseExited(MouseEvent event)
			{
			}
		});
		this.table.setDefaultRenderer(String.class, new AnimeListRenderer(centerRenderer));
		this.table.getTableHeader().setReorderingAllowed(false);
		this.table.getTableHeader().setResizingAllowed(true);
		this.table.setRowHeight(25);
		this.table.setShowGrid(true);
		this.table.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		this.table.setGridColor(Color.BLACK);
		JScrollPane scrollPaneStudents = new JScrollPane(this.table);
		scrollPaneStudents.setAutoscrolls(false);
		scrollPaneStudents.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
		table.setRowSorter(sorter);
		ArrayList<RowSorter.SortKey> sortKeys = new ArrayList<>();
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		sorter.setSortKeys(sortKeys);
		sorter.sort();
		int line = 0;
		GridBagConstraints gcb = new GridBagConstraints();
		gcb.anchor = GridBagConstraints.PAGE_START;
		gcb.fill = GridBagConstraints.BOTH;
		gcb.weighty = 100;
		gcb.weightx = 100;
		gcb.gridheight = 1;
		gcb.gridwidth = 1;
		gcb.gridx = 0;
		gcb.gridy = line++;
		add(scrollPaneStudents, gcb);
		resizeColumnWidth(table);
	}

	public void resizeColumnWidth(JTable table)
	{
		final TableColumnModel columnModel = table.getColumnModel();
		for(int column = 0; column < table.getColumnCount(); column++)
		{
			int width = 50;
			for(int row = 0; row < table.getRowCount(); row++)
			{
				TableCellRenderer renderer = table.getCellRenderer(row, column);
				Component comp = table.prepareRenderer(renderer, row, column);
				width = Math.max(comp.getPreferredSize().width, width);
			}
			columnModel.getColumn(column).setPreferredWidth(width);
		}
	}

	private String[][] getAnimeData(TreeSet<Anime> animeList)
	{
		String[][] data = new String[animeList.size()][3];
		Iterator<Anime> iterator = animeList.iterator();
		int i = 0;
		while(iterator.hasNext())
		{
			Anime anime = iterator.next();
			int j = 0;
			data[i][j++] = anime.getTitle();
			data[i][j++] = anime.getWatched() + "/" + anime.getEpisodes();
			data[i][j++] = anime.getScore() == 0 ? "" : "" + anime.getScore();
			i++;
		}
		return data;
	}

	public void reload()
	{
		model.setRowCount(0);
		for(String[] row : getAnimeData(parent.myal.getAnimeList(status)))
			model.addRow(row);
		model.fireTableDataChanged();
	}
}
