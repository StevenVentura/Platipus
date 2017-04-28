import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;




public class ParticleEffect
{
public long lifeLeft;
public long initialLife;

public double dx;
public double dy;
public final double ddx;
public final double ddy;
public double x;
public double y;

public Color color;

public ParticleEffect(double x, double y, double dx, double dy, double ddx, double ddy, long initialLife)
{
	this.x=x;this.y=y;this.dx=dx;this.dy=dy;this.ddx=ddx;this.ddy=ddy;this.initialLife=initialLife;
	
	lifeLeft = initialLife;
	
}


public void tic(long time)
{
	double m = ((double)time)/1000;
	x += dx*m;
	y += dy*m;
	
	dx += ddx*m;
	dy += ddy*m;
	
	lifeLeft -= time;
}

public void draw(Graphics2D g, double rw, double rh, int h, int hShift, int vShift)
{
	g.setPaint(color);
	double drawX = (x-hShift)*rw;
	double drawY = h - (y-vShift)*rh;
	g.draw(new Line2D.Double(drawX,drawY,drawX,drawY));
}
public static Color RC()
{
	return new Color((float)Math.random(), (float)Math.random(),(float)Math.random());
}

public static int negative(double chance)
{
	if (Math.random() < chance)
		return -1;
	else
		return 1;
}

public static int RP()//random polarity
{
	if (Math.random() > 0.50)
		return 1;
	else
		return -1;
}



}