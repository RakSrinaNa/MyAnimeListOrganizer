package mrcraftcod.myanimelistorganizer.frames;

import mrcraftcod.myanimelistorganizer.enums.Status;
import mrcraftcod.myanimelistorganizer.frames.components.DocumentLimitNumbers;
import mrcraftcod.myanimelistorganizer.objects.Anime;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ModifyAnimeDialogFrame extends JDialog
{
	private final Anime result;
	private final JTextField watched;
	private final JTextField score;
	private final JComboBox<Status> status;

	public ModifyAnimeDialogFrame(Window parent, Anime anime)
	{
		super(parent);
		result = anime;
		setTitle("Modification de l'anime : " + anime.getTitle());
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setLayout(new GridBagLayout());
		setModal(true);
		addWindowListener(new WindowListener()
		{
			@Override
			public void windowOpened(WindowEvent e)
			{
			}

			@Override
			public void windowClosing(WindowEvent e)
			{
				valid();
			}

			@Override
			public void windowClosed(WindowEvent e)
			{
			}

			@Override
			public void windowIconified(WindowEvent e)
			{
			}

			@Override
			public void windowDeiconified(WindowEvent e)
			{
			}

			@Override
			public void windowActivated(WindowEvent e)
			{
			}

			@Override
			public void windowDeactivated(WindowEvent e)
			{
			}
		});
		JLabel watchedLabel = new JLabel("Episodes vus (/" + anime.getEpisodes() + "): ");
		watchedLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		watched = new JTextField();
		watched.setDocument(new DocumentLimitNumbers(anime.getEpisodes()));
		watched.setText("" + anime.getWatched());
		JLabel scoreLabel = new JLabel("Note (/10): ");
		scoreLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		score = new JTextField();
		score.setDocument(new DocumentLimitNumbers(10));
		score.setText("" + anime.getScore());
		JLabel statusLabel = new JLabel("Status: ");
		statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		status = new JComboBox<>(Status.values());
		status.setSelectedItem(anime.getStatus());
		int line = 0;
		GridBagConstraints gcb = new GridBagConstraints();
		getContentPane().setLayout(new GridBagLayout());
		gcb.anchor = GridBagConstraints.PAGE_START;
		gcb.fill = GridBagConstraints.BOTH;
		gcb.insets = new Insets(3, 5, 3, 5);
		gcb.weighty = 100;
		gcb.weightx = 1;
		gcb.gridheight = 1;
		gcb.gridwidth = 1;
		gcb.gridx = 0;
		gcb.gridy = line++;
		getContentPane().add(watchedLabel, gcb);
		gcb.weightx = 100;
		gcb.gridx = 1;
		getContentPane().add(watched, gcb);
		gcb.gridy = line++;
		gcb.gridx = 0;
		gcb.weightx = 1;
		getContentPane().add(scoreLabel, gcb);
		gcb.weightx = 100;
		gcb.gridx = 1;
		getContentPane().add(score, gcb);
		gcb.gridy = line++;
		gcb.gridx = 0;
		gcb.weightx = 1;
		getContentPane().add(statusLabel, gcb);
		gcb.weightx = 100;
		gcb.gridx = 1;
		getContentPane().add(status, gcb);
		setLocationRelativeTo(parent);
		pack();
	}

	private void valid()
	{
		result.setWatched(watched.getText().equals("") ? 0 : Integer.parseInt(watched.getText()));
		result.setScore(score.getText().equals("") ? 0 : Integer.parseInt(score.getText()));
		result.setStatus(((Status) status.getSelectedItem()));
		setVisible(false);
	}

	public Anime showDialog()
	{
		setVisible(true);
		return result;
	}
}
