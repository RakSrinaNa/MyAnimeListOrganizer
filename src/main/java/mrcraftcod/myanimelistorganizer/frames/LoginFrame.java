package mrcraftcod.myanimelistorganizer.frames;

import mrcraftcod.myanimelistorganizer.Main;
import mrcraftcod.myanimelistorganizer.utils.MyAnimeListHandler;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class LoginFrame extends JDialog
{
	private final JTextField user;
	private final JPasswordField password;
	private MyAnimeListHandler result = null;

	public LoginFrame()
	{
		super((JFrame) null);
		setTitle("Authentification");
		this.setModal(true);
		this.setIconImages(Main.icons);
		this.setResizable(false);
		this.setPreferredSize(new Dimension(500, 120));
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.getContentPane().setLayout(new GridBagLayout());
		JLabel userLabel = new JLabel("Nom d'utilisateur:");
		JLabel passwordLabel = new JLabel("Mot de passe:");
		user = new JTextField();
		user.addKeyListener(new KeyListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{

			}

			@Override
			public void keyPressed(KeyEvent e)
			{
				if(e.getExtendedKeyCode() == KeyEvent.VK_ENTER)
					connect();
			}

			@Override
			public void keyReleased(KeyEvent e)
			{

			}
		});
		password = new JPasswordField();
		password.addKeyListener(new KeyListener()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{

			}

			@Override
			public void keyPressed(KeyEvent e)
			{
				if(e.getExtendedKeyCode() == KeyEvent.VK_ENTER)
					connect();
			}

			@Override
			public void keyReleased(KeyEvent e)
			{

			}
		});
		JButton login = new JButton("Connexion");
		login.addActionListener(e -> connect());
		int line = 0;
		GridBagConstraints gcb = new GridBagConstraints();
		getContentPane().setLayout(new GridBagLayout());
		gcb.anchor = GridBagConstraints.PAGE_START;
		gcb.fill = GridBagConstraints.BOTH;
		gcb.insets = new Insets(2, 2, 2, 2);
		gcb.weighty = 100;
		gcb.weightx = 1;
		gcb.gridheight = 1;
		gcb.gridwidth = 1;
		gcb.gridx = 0;
		gcb.gridy = line++;
		getContentPane().add(userLabel, gcb);
		gcb.gridx = 1;
		gcb.weightx = 100;
		getContentPane().add(user, gcb);
		gcb.weightx = 1;
		gcb.gridx = 0;
		gcb.gridy = line++;
		getContentPane().add(passwordLabel, gcb);
		gcb.gridx = 1;
		gcb.weightx = 100;
		getContentPane().add(password, gcb);
		gcb.gridx = 0;
		gcb.weighty = 1;
		gcb.gridwidth = 2;
		gcb.gridy = line++;
		getContentPane().add(login, gcb);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - this.getPreferredSize().width / 2, dim.height / 2 - this.getPreferredSize().height / 2);
		pack();
	}

	private void connect()
	{
		try
		{
			result = new MyAnimeListHandler(user.getText(), convertToString(password.getPassword()));
			setVisible(false);
			dispose();
		}
		catch(Exception e1)
		{
			JOptionPane.showMessageDialog(LoginFrame.this, "Identifiants incorrectes!", "Erreur", JOptionPane.ERROR_MESSAGE);
		}
	}

	private String convertToString(char[] tab)
	{
		String result = "";
		for(char c : tab)
			result += c;
		return result;
	}

	public MyAnimeListHandler login()
	{
		this.setVisible(true);
		return result;
	}
}
