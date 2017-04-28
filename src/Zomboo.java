import java.awt.Color;
import java.awt.Graphics2D;
import java.util.TreeMap;

public class Zomboo extends Mob implements Collidable
{
	public double dx, dy, lx, ly, llx, lly;
	public boolean onGround = true;
	public boolean facingLeft = false;
	
	
	
	public static final int DEFAULT_WIDTH = 32;
	public static final int DEFAULT_HEIGHT = 65;

	
	
	public Zomboo()
	{
		
	}
public Zomboo(double x, double y)
{
	this.x =x ; this.y= y;
	definePolyShape(x, y);
}
public Zomboo(String in)
{
	String[] s = in.split(" ");
	spawnX = pint(s[0]);
	spawnY = pint(s[1]);
	x = spawnX; y = spawnY;
	definePolyShape(x,y);
	p.color = new Color(pint(s[2]),pint(s[3]),pint(s[4]));
	
}
public String toSocketString()
{
	String n = "+";
	String out = super.toSocketString() + n;
	
	out += (int)x + " " + (int)y + " ";
	out += p.color.getRed() + " " + p.color.getGreen() + " " + p.color.getBlue() + " ";
	out += n;
	
	return out;
	
}
public String toFile()
{
	String out = super.toFile();

	out += (int)x + " " + (int)y + " ";
	out += p.color.getRed() + " " + p.color.getGreen() + " " + p.color.getBlue(); 
	
	out = out.toLowerCase();
	out += "\r\n";
	
	return out;
}


	
	public void move(TreeMap<Integer, Player> players) 
	{

		if (facingLeft)
		{
			x -= 5;
		}
		else
			x += 5;
		
		int target = -1;//find closest player to target
		double closestDistance = Double.MAX_VALUE;
		for (Integer player : players.keySet())
		{
			Player p = players.get(player);
			if (p.levelIndex == this.levelIndex)
			{
				double distance = Math.sqrt(Math.pow(p.x-this.x,2) + Math.pow(p.y-this.y,2));
				if (distance < closestDistance)
				{
					closestDistance = distance;
					target = p.ID;
				}
			}
		}
		
		
		
		if (target != -1)//if a valid target was found
		{
			Player p = players.get(target);
			facingLeft = (p.x < this.x);
		}
		
		
		
		
		
	}
	public void onPlayerCollideClient(Player p)
	{
		p.addDamage(1);
		p.color = Color.GREEN;
		
	}
	
	@Override
	public double getX() 
	{
		return x;
	}

	@Override
	public double getY() 
	{
		return y;
	}

	@Override
	public void setX(double x)
	{
		this.x=x;
	}

	@Override
	public void setY(double y) {
		this.y=y;
		
	}

	@Override
	public void setDX(double dx) {
		this.dx=dx;
		
	}

	@Override
	public void setDY(double dy) {
		this.dy = dy;
		
	}

	public double getLLY()
	{
		return lly;
	}
	public double getLLX()
	{
		return llx;
	}
	public double getLX()
	{
		return lx;
	}
	public double getLY()
	{
		return ly;
	}
	@Override
	public void setLLX(double llx) {
		this.llx = llx;
		
	}
	@Override
	public void setLLY(double lly) {
		this.lly = lly;
		
	}
	@Override
	public void setLX(double lx) {
		this.lx = lx;
		
	}
	@Override
	public void setLY(double ly) {
		this.ly = ly;
		
	}


	
	public void setOnGround(boolean yes) 
	{
		
		this.onGround = yes;
	}

	
	public boolean collidable()
	{
		
		return true;
	}

	
	public void setFacingLeft(boolean left)
	{
		
		this.facingLeft = left;
	}

	
	public boolean facingLeft()
	{
		return facingLeft;
	}

	
	public int getWidth()
	{
		
		return DEFAULT_WIDTH;
	}

	
	public int getHeight()
	{
		
		return DEFAULT_HEIGHT;
	}

	
	public int getImageIndex() 
	{
		return ImageHelper.IMAGE_ZOMBOO;
	}

	
	

	
	public void definePolyShape(double x, double y)
	{
this.x = x; this.y = y;
		
		int xx = (int)this.x, yy = (int)this.y;//just an int cast
		int[] xs = {xx+1,xx,xx-1,xx};
		int[] ys = {yy, yy+1, yy, yy-1};
		p = new PolyShape(xs, ys);
		p.color = new Color(18,53,36);//Phthalo green	
		
	}

	
	public void draw(Graphics2D g, double rw, double rh, int h, int hShift, int vShift)
	{
		
		
	}

	@Override
	public double getDY() {
		return dy;
	}

	@Override
	public double getDX() {
		return dx;
	}

	@Override
	public boolean onGround() {
return onGround;
	}
	
}