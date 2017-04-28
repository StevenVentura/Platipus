import java.awt.geom.Line2D;
import java.util.ArrayList;

public class CollisionLine
{
	public int x1, x2, y1, y2;
	public int type = -1;

	public static final int TYPE_HORIZONTAL = 0;
	public static final int TYPE_VERTICAL = 1;
	public static final int TYPE_POSITIVE = 2;
	public static final int TYPE_NEGATIVE = 3;
	public static final int TYPE_PLATFORM = 4;
	
	public Line2D.Double wallLine;
	
	
	public CollisionLine(int x1, int y1, int x2, int y2)
	{
		this.x1=x1;this.y1=y1;this.x2=x2;this.y2=y2;
		wallLine = new Line2D.Double(x1,y1,x2,y2);
		type = this.classify();
	}
	private int classify()
	{
		if (x1 == x2)
			return TYPE_VERTICAL;
		if (y1 == y2)
			return TYPE_HORIZONTAL;
		
		
		/*
		 * find rightmost x
		 */
		boolean x2rightmost = (x2 > x1);
		
		if (x2rightmost)
			if (y2 > y1)
				return TYPE_POSITIVE;
			else
				return TYPE_NEGATIVE;
		
		if (!x2rightmost)
			if (y1 > y2)
				return TYPE_POSITIVE;
			else
				return TYPE_NEGATIVE;
			
			
			
			
		return -555;//shouldn't be possible
		
		
		
	}
	
	public static ArrayList<CollisionLine> getCollisionLines(PolyShape p)
	{
		ArrayList<CollisionLine> out = new ArrayList<CollisionLine>();
		
		int[] xs = p.xs;
		int[] ys = p.ys;
		int numLines = xs.length+1;
		
		for (int i = 0; i < xs.length-1; i++)
		{
			
			CollisionLine c = new CollisionLine(xs[i],ys[i],xs[i+1],ys[i+1]);
			out.add(c);
		}
		int last = xs.length-1;
		CollisionLine c = new CollisionLine(xs[last],ys[last],xs[0],ys[0]);
		out.add(c);
		
		return out;
	}
	
	
	
	
	
}