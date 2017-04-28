import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PolyShape
{
	public int[] xs, ys;
	public Color color;
	public Shape drawableShape = null;
	public Shape collisionShape;
	
	public ArrayList<CollisionLine> collisionLines;
	
	public boolean solid = true;
	
	public void draw(Graphics2D g, double rw, double rh, int h, int hShift, int vShift)
	{
		
		if (this instanceof Platform)
		g.setPaint(color.darker());
		else
			g.setPaint(color);
		g.fill(drawableShape);
		
	}
	public static final Color DEFAULT_COLOR = new Color(226,88,34);//flame red
	public PolyShape()
	{
		color = DEFAULT_COLOR;
	}
	
	public PolyShape(int[] xs, int[] ys)
	{
		this.xs = xs; this.ys = ys;
		collisionShape = new Polygon(xs,ys,xs.length);
		collisionLines = CollisionLine.getCollisionLines(this);
	}
	
	public void defineDrawableShape(double rw, double rh, int h, int hShift, int vShift)//must be called initially, and on window resize.
	{
		int[] nYs = new int[ys.length];
		
		for (int i = 0; i < ys.length; i++)
		{
			nYs[i] = h - (int)((ys[i]-vShift)*rh);
		}
		
		int[] nXs = new int[xs.length];
		
		for (int i = 0; i < xs.length; i++)
		{
			nXs[i] = (int)((xs[i]-hShift)*rw);	
		}
		
		drawableShape = new Polygon(nXs,nYs, xs.length);
	}
	
	public void addPoint(java.awt.Point p)//used for the editor
	{
		if (this.xs == null || this.ys == null)
		{
			xs = new int[1]; ys = new int[1];
			xs[0] = p.x; ys[0] = p.y;
		}
		else
		{
		if (p.x == this.xs[xs.length-1] && p.y == this.ys[ys.length-1])//tried to add the same point
			return;
		int[] xs = new int[this.xs.length+1];//the new coordinate sets
		int[] ys = new int[this.ys.length+1];
		for (int i = 0; i < this.xs.length; i++)//populating
		{
			xs[i] = this.xs[i];
			ys[i] = this.ys[i];
		}
		
		xs[xs.length-1] = p.x;
		ys[ys.length-1] = p.y;
		
		this.xs = xs;
		this.ys = ys;
		}
	}
	
	public boolean shapeEmpty()//used for the editor
	{
		return (this.xs == null || this.ys == null || this.xs.length < 3 || this.ys.length < 3);
	}
	public void popPoint()//used for the editor
	{
		if (this.xs == null || this.ys == null || this.xs.length == 0 || this.ys.length == 0)
		{
			return;//do nothing
		}
		if (this.xs.length == 1)
		{
			this.xs = null;//flag it for re-make
			this.ys = null;
			return;
		}
		
		//else
		int[] xs = new int[this.xs.length-1];//the new coordinate sets
		int[] ys = new int[this.ys.length-1];
		for (int i = 0; i < this.xs.length-1; i++)//populating
		{
			xs[i] = this.xs[i];
			ys[i] = this.ys[i];
		}
		
		this.xs = xs;
		this.ys = ys;
		
	}
	
	
	
	
	public String toFile()
	{
		String out = "";
		String n = "\r\n";
		out += color.getRed() + " " + color.getGreen() + " " + color.getBlue() + n;
		
		out += "" + solid + n;
		
		out += this.getClass().getName() + n;
		
		out += intArrayToSpacedString(xs) + n;
		out += intArrayToSpacedString(ys) + n;
		
		return out;
	}
	public String toSocketString()
	{
		String out = "";
		String n = "+";
		
		out += color.getRed() + " " + color.getGreen() + " " + color.getBlue() + n;
		
		out += "" + solid + n;
		
		out += this.getClass().getName() + n;
		
		out += intArrayToSpacedString(xs) + n;
		out += intArrayToSpacedString(ys) + n;
		
		return out;
	}
	public static String intArrayToSpacedString(int[] in)
	{
		String out = "";
		
		for (int i = 0; i < in.length; i++)
		{
			out += in[i] + " ";
		}
		
		out = out.trim();//get rid of the last space
		
		
		
		
		return out;
	}
	
	
	
	
}