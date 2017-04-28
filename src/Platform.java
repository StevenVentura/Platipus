import java.awt.Color;
import java.util.ArrayList;

public class Platform extends PolyShape
{
	public double top, bottom;
	public static final Color INDICATION_COLOR = Color.PINK, SERVICE_INDICATION_COLOR = new Color(0,15,137);//Phthalo Blue
	
	private double getTop()
	{
		ArrayList<CollisionLine> cls = CollisionLine.getCollisionLines(this);
		
		int top = Integer.MIN_VALUE;
		for (CollisionLine c : cls)
		{
			if (c.type == CollisionLine.TYPE_HORIZONTAL)
			{
				if (c.y1 > top)
					top = c.y1;
			}
		}
		
		return top;
	}
	private double getBottom()
	{
		
		ArrayList<CollisionLine> cls = CollisionLine.getCollisionLines(this);
		
		int bottom = Integer.MAX_VALUE;
		for (CollisionLine c : cls)
		{
			if (c.type == CollisionLine.TYPE_HORIZONTAL)
			{
				if (c.y1 < bottom)
					bottom = c.y1;
			}
		}
		
		return bottom;
	}
	
	public Platform(int[] xs, int[] ys)
	{
		super(xs,ys);
		this.top = getTop();
		this.bottom = getBottom();
	}
	
}