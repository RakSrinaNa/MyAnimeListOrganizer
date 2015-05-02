package mrcraftcod.myanimelistorganizer.frame.component;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class AnimeListRenderer implements TableCellRenderer
{
	private final TableCellRenderer wrappedRenderer;

	public AnimeListRenderer(TableCellRenderer wrappedRenderer)
	{
		this.wrappedRenderer = wrappedRenderer;
	}

	public Color getTableBackgroundColour(int row)
	{
		return row % 2 == 0 ? Color.LIGHT_GRAY : Color.GRAY;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		Component component = this.wrappedRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		component.setBackground(getTableBackgroundColour(row));
		return component;
	}
}