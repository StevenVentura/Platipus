import java.io.File;
import java.util.Scanner;

public class PlayerStats
{
	//each ability can be used once per room?
	
	//all of these can be bought with experience or gold or something.
	
	//abilities -- passive and active -- cost mana
	public boolean doubleJump;
	public boolean throwingDagger;
	public boolean dash;
	
	//items
	public boolean sword;
	
	//attributes
	public double manaRegenRate;
	public double healthRegenRate;
	public int maxHealth;
	public int maxMana;
	public float leechPercent;
	
	//energy shield -- to be stored in Player
	
	//level stuff
	public int combatLevel;
	public double experience;
	
	public static final double[] experienceForLevel = {5, 10, 20, 40, 100, 200, 400, 500, 1200};
	
	public PlayerStats()
	{
		
	}
	public PlayerStats(String name)
	{
		try{
			fileName = name;
	Scanner scan = new Scanner(new File(name + ".stats"));
	//read from the file
	
	
	scan.close();
		}catch(Exception e){e.printStackTrace();};
	}
	String fileName = "nobody";
	
	
	public void saveToFile()
	{
		String out = "";
		
		
		
		PlatipusClient.createFile(fileName+".stats", out);
	}
	
}