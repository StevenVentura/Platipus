import java.awt.Color;
import java.awt.Graphics2D;

public class PointZone extends PolyShape
{
	public int pointValue = -1;
	public int respawnX;
	public int respawnY;
	
	public PointZone()
	{
		this.color = Color.red.darker().darker();
		this.solid = false;
	}
	public void draw(Graphics2D g, double rw, double rh, int h, int hShift, int vShift)
	{
		super.draw(g,rw,rh,h,hShift,vShift);
	}
	
	public PointZone(int[] xs, int[] ys, int pointValue)
	{
		super(xs,ys);
		this.solid = false;
		
		this.pointValue = pointValue;  
	}
	
	public String toFile()
	{
		return super.toFile() + pointValue + "\r\n" + respawnX + " " + respawnY + "\r\n";
	}
	
	public String toSocketString()
	{
		String n = "+";
		return super.toSocketString() + pointValue + n + respawnX + " " + respawnY + n;
	}
	
	
	
}