import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.TreeMap;



public abstract class Mob
{
	
	public double x, y;
	public double spawnX, spawnY;
	public double respawnTimeLeft = 3;
	public double respawnMaxTime = 3;
	public PolyShape p;
	public int levelIndex;//same as it's PlatipusMap container.
	private boolean collidable = false;
	
	boolean flaggedForRemoval = false;//server only
	
	public int ID = -1;
	
	//public static abstract String getImageFileName // image name is just .getClass() + ".png";
	
	
	public abstract boolean collidable();
	
	public static final double ddy = -2.90;//ddy is used for gravity.
	
	public Mob()
	{

	}
	public static Rectangle getCollideRectangle(Mob m, double x, double y)
	{
		return new Rectangle((int)(x-m.getWidth()/2), (int)(y), m.getWidth(), m.getHeight());
	}
	public static Rectangle getClickRectangle(Mob m, double x, double y)
	{
		return new Rectangle((int)(x-m.getWidth()/2), (int)(y + m.getHeight()), m.getWidth(), m.getHeight());
	}
	
	public abstract void setFacingLeft(boolean left);
	public abstract boolean facingLeft();
	
	public abstract int getWidth();
	public abstract int getHeight();
	
	public abstract int getImageIndex();
	
	public abstract void onPlayerCollideClient(Player p);
	
	public abstract void move(TreeMap<Integer, Player> players);
	
	public abstract void definePolyShape(double x, double y);
	
	public Mob(double x, double y)
	{
		this.x = x; this.y = y;
		definePolyShape(x,y);
	}
	
	
	public static final String[] names = {"bloop", "bunny"};
	
	public static int getValue(String name)
	{
		for (int i = 0; i < names.length; i++)
		{
			if (names[i].equals(name))
				return i;
		}
		
		return -1;
	}
	public static String getName(int value)
	{
		return names[value];
	}
	
	public String toFile()
	{
		String out = "";
		out += this.getClass().getName() + "\r\n";
		return out;
	}
	
	public Mob(String in)
	{
		//do nothing
	}
	public static int pint(String s)
	{
		return Integer.parseInt(s);
	}
	public String toSocketString()//is overwritten by subclasses
	{
		return this.getClass().getName();
	}
	
	
	
	public String toUpdateString()
	{
		String out = ID + " " + (int)x + " " + (int)y + " " + playerIntersected + " " + Compactor.booleanToDigit(facingLeft());
		return out;
	}
	
	public int playerIntersected = -1;
	
	
	public abstract void draw(Graphics2D g, double rw, double rh, int h, int hShift, int vShift);
	
	
	
	
	
}