package fr.myanimelistorganizer.frames.components;

import fr.myanimelistorganizer.enums.Status;
import fr.myanimelistorganizer.frames.AnimeInfoDialog;
import fr.myanimelistorganizer.frames.MainFrame;
import fr.myanimelistorganizer.objects.AnimeInfo;
import org.xml.sax.SAXException;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;

public class SearchPanel extends JPanel
{
	private final JTableUneditableModel model;
	private final JTable table;
	private final MainFrame parent;
	private final JTextField entry;

	public SearchPanel(MainFrame parent)
	{
		this.parent = parent;
		setName("Recherche");
		setLayout(new GridBagLayout());
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		model = new JTableUneditableModel(new AnimeInfo[][]{}, new String[]{"Titre"});
		table = new JTable(model)
		{
			private static final long serialVersionUID = 4244155500155330717L;

			@Override
			public Class<?> getColumnClass(int column)
			{
				return AnimeInfo.class;
			}
		};
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
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
				int row = SearchPanel.this.table.rowAtPoint(event.getPoint());
				if(row >= 0 && row < SearchPanel.this.table.getRowCount())
					SearchPanel.this.table.setRowSelectionInterval(row, row);
				else
					SearchPanel.this.table.clearSelection();
				int rowindex = SearchPanel.this.table.getSelectedRow();
				if(event.isPopupTrigger() && event.getComponent() instanceof JTable)
				{
					final AnimeInfo anime = getAnimeInfo(SearchPanel.this.table.getValueAt(rowindex, 0).toString().trim());
					if(anime == null)
						return;
					JPopupMenu popup = new JPopupMenu();
					JMenuItem viewMoreInfo = new JMenuItem("Informations");
					viewMoreInfo.addActionListener(event1 -> {
						try
						{
							new AnimeInfoDialog(SearchPanel.this.parent, anime);
						}
						catch(Exception exception)
						{
							exception.printStackTrace();
						}
					});
					JMenuItem addAnime = new JMenuItem("Ajouter \340 la liste");
					addAnime.addActionListener(event1 -> {
						try
						{
							parent.myal.addAnime(anime, (Status)JOptionPane.showInputDialog(parent, "Quel status voulez-vous lui attribuer?", "Status", JOptionPane.QUESTION_MESSAGE, null, Status.values(), Status.PLANNEDTOWATCH));
							parent.updateAll();
						}
						catch(Exception exception)
						{
							exception.printStackTrace();
						}
					});
					popup.add(viewMoreInfo);
					if(!parent.myal.containsID(anime.getID()))
						popup.add(addAnime);
					popup.addSeparator();
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
		this.table.setDefaultRenderer(AnimeInfo.class, new AnimeListRenderer(centerRenderer));
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
		entry = new JTextField();
		entry.addKeyListener(new KeyListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{

			}

			@Override
			public void keyPressed(KeyEvent e)
			{
				if(e.getExtendedKeyCode() == KeyEvent.VK_ENTER)
					try
					{
						search();
					}
					catch(ParserConfigurationException | SAXException | IOException e1)
					{
						e1.printStackTrace();
					}
			}

			@Override
			public void keyReleased(KeyEvent e)
			{

			}
		});
		entry.requestFocus();
		JButton search = new JButton("Rechercher");
		search.addActionListener(e -> {
			try
			{
				search();
			}
			catch(ParserConfigurationException | SAXException | IOException e1)
			{
				e1.printStackTrace();
			}
		});
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
		gcb.weighty = 1;
		gcb.gridy = line++;
		add(entry, gcb);
		gcb.gridy = line++;
		add(search, gcb);
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

	private AnimeInfo getAnimeInfo(String name)
	{
		for(int i = 0; i < model.getRowCount(); i++)
		{
			AnimeInfo anime = (AnimeInfo) model.getValueAt(i, 0);
			if(anime.getTitle().equals(name))
				return anime;
		}
		return null;
	}

	private void search() throws ParserConfigurationException, SAXException, IOException
	{
		model.setRowCount(0);
		for(AnimeInfo anime : parent.myal.search(entry.getText()))
			model.addRow(new AnimeInfo[]{anime});
		model.fireTableDataChanged();
	}
}
