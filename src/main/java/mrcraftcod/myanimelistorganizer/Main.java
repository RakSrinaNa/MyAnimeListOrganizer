package mrcraftcod.myanimelistorganizer;

/**
 * Created by MrCraftCod on 01/05/2015.
 */
public class Main
{
	public static final String VERSION = "1.0";

	public static void main(String[] args) throws Exception
	{
		new MainFrame(new LoginFrame().login());
	}
}
