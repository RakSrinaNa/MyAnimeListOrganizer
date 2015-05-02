package mrcraftcod.myanimelistorganizer;

import mrcraftcod.myanimelistorganizer.frames.LoginFrame;
import mrcraftcod.myanimelistorganizer.frames.MainFrame;
import javax.imageio.ImageIO;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;

public class Main
{
	public static final String VERSION = "1.0";
	public static ArrayList<Image> icons;

	public static void main(String[] args) throws Exception
	{
		icons = new ArrayList<>();
		icons.add(ImageIO.read(getResource("icons/icon16.png")));
		icons.add(ImageIO.read(getResource("icons/icon32.png")));
		icons.add(ImageIO.read(getResource("icons/icon64.png")));
		new MainFrame(new LoginFrame().login());
	}

	public static URL getResource(String path)
	{
		return Main.class.getResource("/" + path);
	}
}
