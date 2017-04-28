import java.awt.Color;
import java.awt.Point;

public class Door extends PolyShape
{
	/*
	 * all 4 of these are determined at runtime
	 */
	int levelToIndex = -1;
	int levelFromIndex = -1;
	int doorToIndex = -1;
	int doorFromIndex = -1;
	
	int direction;//stored in the text file, determined by the PlatipusEdit.
	
	public static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;//which way the door sends you
	public static final int PAIR_UP = 1, PAIR_DOWN = 0, PAIR_LEFT = 3, PAIR_RIGHT = 2;//the receiving side
	
	public Door(){this.color=Color.CYAN;this.solid = false;}
	
	public Door(int[] xs, int[] ys, int direction)
	{
		super(xs,ys);
		this.solid = false;
		this.direction = direction;
	}
	
	public void setDirection(String dir)//for PlatipusEditor.java
	{
		dir = dir.toLowerCase();
		if (dir.equals("up"))
			direction = UP;
		if (dir.equals("down"))
			direction = DOWN;
		if (dir.equals("left"))
			direction = LEFT;
		if (dir.equals("right"))
			direction = RIGHT;
	}
	
	public Point spawnLocation()//where to spawn the player when he enters from this door
	{
		double x = 0, y = 0;
		
		for (int i = 0; i < xs.length; i++)
			x += xs[i];
		x /= xs.length;
		
		for (int i = 0; i < ys.length; i++)
			y += ys[i];
		y /= ys.length;
		
		int d = 40;
		
		if (direction == UP)
			y -= d;
		if (direction == DOWN)
			y += d;
		if (direction == LEFT)
			x += d;
		if (direction == RIGHT)
			x -= d;
		
		return new Point((int)x,(int)y);
	}
	
	public String destination;
	
	public String getDestination()//for use only on the server.
	{
		this.destination = levelToIndex + " " + levelFromIndex + " " + doorToIndex + " " + doorFromIndex;
		return this.destination;
	}
	
	public void setDestination(int levelToIndex, int levelFromIndex, int doorToIndex, int doorFromIndex)//for use only on the server.
	{
		this.levelToIndex = levelToIndex; this.levelFromIndex = levelFromIndex; this.doorToIndex = doorToIndex; this.doorFromIndex = doorFromIndex;
	}
	
	public String toSocketString()
	{
		String n = "+";
		return super.toSocketString() + direction + n;
	}
	
	public String toFile()
	{
		return super.toFile() + direction + "\r\n";
	}
	
	
	
	
	
	
}