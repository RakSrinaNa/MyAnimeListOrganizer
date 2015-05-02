package mrcraftcod.myanimelistorganizer.frames.components;

import mrcraftcod.myanimelistorganizer.enums.Status;
import mrcraftcod.myanimelistorganizer.frames.MainFrame;
import mrcraftcod.myanimelistorganizer.objects.Anime;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class SearchPanel extends JPanel
{
	private final JTableUneditableModel model;
	private final JTable table;
	private MainFrame parent;
	private JTextField entry;

	public SearchPanel(MainFrame parent)
	{
		this.parent = parent;
		setName("Recherche");
		setLayout(new GridBagLayout());
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		model = new JTableUneditableModel(null , new String[]{"Titre"});
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
		this.table.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent event)
			{
			}

			@Override
			public void mouseEntered(MouseEvent event)
			{
			}

			@Override
			public void mouseExited(MouseEvent event)
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
					final String name = SearchPanel.this.table.getValueAt(rowindex, 0).toString().trim();
					JPopupMenu popup = new JPopupMenu();
					JMenuItem viewMoreInfo = new JMenuItem("Informations");
					viewMoreInfo.addActionListener(event1 -> {
						try
						{
							final Anime anime = parent.myal.getAnimeInfos(getAnimeID(name));
						}
						catch(Exception exception)
						{
						}
					});
					JMenuItem addAnime = new JMenuItem("Ajouter \340 la liste");
					addAnime.addActionListener(event1 -> {
						try
						{
							parent.myal.addAnime(getAnimeID(name), Status.PLANNEDTOWATCH);
						}
						catch(Exception exception)
						{
						}
					});
					popup.add(viewMoreInfo);
					popup.add(addAnime);
					popup.show(event.getComponent(), event.getX(), event.getY());
				}
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
		entry = new JTextField();
		JButton search = new JButton("Rechercher");
		search.addActionListener(e -> search());
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
		for (int column = 0; column < table.getColumnCount(); column++)
		{
			int width = 50;
			for (int row = 0; row < table.getRowCount(); row++)
			{
				TableCellRenderer renderer = table.getCellRenderer(row, column);
				Component comp = table.prepareRenderer(renderer, row, column);
				width = Math.max(comp.getPreferredSize().width, width);
			}
			columnModel.getColumn(column).setPreferredWidth(width);
		}
	}

	private int getAnimeID(String name)//TODO
	{
		return 1;
	}

	private void search()
	{
		String name = entry.getText();
	}
}
