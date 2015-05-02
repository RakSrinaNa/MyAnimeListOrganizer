package mrcraftcod.myanimelistorganizer.frame;

import mrcraftcod.myanimelistorganizer.object.Anime;
import javax.swing.*;
import java.awt.*;

public class ModifyAnimeDialogFrame extends JDialog
{
	private Anime result;

	public ModifyAnimeDialogFrame(Window parent, Anime anime)
	{
		super(parent);
		result = anime;
		setTitle("Modification de l'anime : " + anime.getTitle());
		pack();
	}

	public Anime showDialog()
	{
		setVisible(true);
		return result;
	}
}
