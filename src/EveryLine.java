import java.awt.Color;
import java.awt.Graphics2D;

public class EveryLine//to help me with the editor!
{
	public static final double nullindicator = -555.5;
	public double x1=nullindicator, x2=nullindicator, y1=nullindicator, y2=nullindicator;
	public double every=nullindicator;
	
	
	public boolean defined;
	public PlatipusEditor pe;
	public EveryLine(PlatipusEditor pe)
	{
		this.pe=pe;
		
	}
	
	public void draw(Graphics2D g, double rw, double rh, int h, int hShift, int vShift)
	{
		if (x1 == nullindicator)//shape isnt fully defined yet
			return;
		else if (x2 == nullindicator)//shape is still not fully defined
		{
			//cross
			g.setPaint(Color.WHITE);//g.setPaint(Color.RED.brighter().brighter());
			pe.drawThickLine(x1-10000,y1,x1+10000,y1);//h-line
			pe.drawThickLine(x1,y1-10000,x1,y1+10000);//v-line
			return;
		}
		//shape is mostly defined
		g.setPaint(Color.GREEN.brighter());
		pe.drawThickLine(x1,y1,x2,y2);
		
		if (every == nullindicator)
			return;
		
		boolean vertical = (x2 == x1);
		
		g.setPaint(Color.GREEN.darker());
		
		if (vertical)
		{
			if (y2 > y1)
			for (double y = y1; y <= y2; y+=every)
			{
				pe.fillScaledRect((int)(x1-5),(int)(y+5),10,10);
			}
			else
				for (double y = y1; y >= y2; y+=every)
				{
					pe.fillScaledRect((int)(x1-5),(int)(y+5),10,10);
				}
		}
		else//horizontal
		{
			if (x2 > x1)
			for (double x = x1; x <= x2; x += every)
			{
				pe.fillScaledRect((int)x-5,(int)(y1+5),10,10);
				
			}
			else
				for (double x = x1; x >= x2; x += every)
				{
					pe.fillScaledRect((int)x-5,(int)(y1+5),10,10);
				}
		}
	}
	
	public void removePoint()
	{
		if (x1 == nullindicator)
		{
			return;
		}
		else if (x2 == nullindicator)
		{
			x1 = nullindicator; y1 = nullindicator;
			pe.gui.println("define point 1 again");
			return;
		}
		else
		{
			pe.gui.println("define point 2 again");
			x2 = nullindicator; y2 = nullindicator;
			return;
		}
		
	}
	
	public void addPoint(double x, double y)
	{
		if (x1 == nullindicator)
		{
			x1=x;y1=y;
			pe.gui.println("define point 2");
			
		}
		else
			if (x2 == nullindicator)
			{
				x2=x;y2=y;
				pe.gui.println("define every");
			}
			else
			{//define every
				boolean vertical = (x2 == x1);
				
				if (vertical)
				{
					every = y-y1;
				}
				else
				{
					every = x-x1;
				}
				
				defined = true;
			}
	}
	
	
	
	
}