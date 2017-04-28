import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

import javax.swing.JApplet;
import javax.swing.JFrame;



public class AntRace extends JApplet
{
	private JFrame frame;
	private Graphics2D g, g2;
	private BufferedImage bi;
	
	public AntRace(){}
	
	public void begin()
	{
		frame = new JFrame("!ANT RACES!");
		frame.setSize((int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth(),(int)java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocation(0,0);
		
		frame.add(this);
		
		frame.setVisible(true);
		
		bi = new BufferedImage(getSize().width,getSize().height, 5);
		g = bi.createGraphics();
		g2 = (Graphics2D)(this.getGraphics());

		
		
		long CT=System.currentTimeMillis(),LT=System.currentTimeMillis();
		while(true)
		{
			CT = System.currentTimeMillis();
			if (CT-LT > 32)
			{
				LT=CT;
				this.paint();
			}
			
		}
		
		
	}
	
	int startX = 1;
	public void paint()
	{
		int w = 6, h = 6;
		for (int x = 0; x < getSize().width; x+= w)
		{
			for (int y = 0; y < getSize().height; y += h)
			{
				g.setPaint(RCBW());
				g.fillRect(x,y,w,h);
			}
		}
		
		
		g2.drawImage(bi,null,0,0);
		
	}
	
	public static Color RCBW()
	{
		if (Math.random() > 0.5)
			return Color.GREEN;
		else
			return Color.MAGENTA;
	}
	public static Color RC()
	{
		return new Color((float)Math.random(), (float)Math.random(), (float)Math.random());
	}
	
	
	
	public static void main(String[]args)
	{
		AntRace a = new AntRace();
		a.begin();
	}
	
}