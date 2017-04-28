import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.TreeMap;



public class Bloop extends Mob
{ 
	public int getImageIndex()
	{
		return ImageHelper.IMAGE_BLOOP;
	}
	public boolean collidable()
	{
		return false;
	}
	public boolean collidable = false;
	public Bloop()
	{
		super();
		definePolyShape(this.x, this.y);
	}
	public int getWidth()
	{
		return size;
	}
	public int getHeight()
	{
		return size;
	}
	int size = 17*2;
	public static final int DEFAULT_SIZE = 17*2;
	public void definePolyShape(double x, double y)
	{
		this.x = x; this.y = y;
		
		int xx = (int)this.x, yy = (int)this.y;//just an int cast
		int[] xs = {xx+size,xx,xx-size,xx};
		int[] ys = {yy, yy+size, yy, yy-size};
		p = new PolyShape(xs, ys);
		p.color = new Color(18,53,36);//Phthalo green	
	}
	
	public void onPlayerCollideClient(Player p)
	{
		//draw particle effects on collision. (the server handles the reset of the mob)
		boolean flyLeft = (this.x < p.x);
		
		p.addDamage(1);
		
			if (flyLeft)
				p.dx = 12;
			else
				p.dx = -12;
			p.dy = 12;
			p.setOnGround(false);
		
		
		
		
	}
	
	public void setFacingLeft(boolean left)
	{
		facingLeft = left;
	}
	public boolean facingLeft()
	{
		return facingLeft;
	}
	
	public boolean facingLeft;
	public void move(TreeMap<Integer, Player> players)
	{
		
		
		int target = -1;//find closest player to target
		double closestDistance = Double.MAX_VALUE;
		for (Integer player : players.keySet())
		{
			Player p = players.get(player);
			if (p.role != Player.ROLE_SERVICE && p.levelIndex == this.levelIndex)//they don't go after service-men.
			{
				double distance = Math.sqrt(Math.pow(p.x-this.x,2) + Math.pow(p.y-this.y,2));
				if (distance < closestDistance)
				{
					closestDistance = distance;
					target = p.ID;
					facingLeft = (p.x < this.x);
				}
			}
		}
		
		if (target == -1)//if no target found
		{
			return;
		}
		
			
		Player p = players.get(target);
		double speed = 8.88D;//fly at player
		
		double angle = Math.atan((p.y-this.y-0.01)/(p.x-this.x+0.01));
		
		double dx, dy;
		dx = Math.cos(angle)*speed;
		dy = Math.sin(angle)*speed;
		if (p.x < this.x)
		{
			dx *= -1;
			dy *= -1;
		}
		
		
		this.x += dx;
		this.y += dy;
	}
	public Bloop(double x, double y)
	{
		super(x,y);
		definePolyShape(this.x, this.y);
	}
	public void draw(Graphics2D g, double rw, double rh, int h, int hShift, int vShift)
	{
		g.setColor(p.color);
		
		p.defineDrawableShape(rw, rh, h, hShift, vShift);
		
		g.fill(p.drawableShape);
	}
	
	
	public String toFile()
	{
		String out = super.toFile();

		out += (int)x + " " + (int)y + " ";
		out += p.color.getRed() + " " + p.color.getGreen() + " " + p.color.getBlue(); 
		System.out.println("mob.java:::: out is " + out);
		
		out = out.toLowerCase();
		out += "\r\n";
		
		return out;
	}
	
	public Bloop(String in)
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
		out += p.color.getRed() + " " + p.color.getGreen() + " " + p.color.getBlue();
		out += n;
		
		return out;
		
	}
	
	
	
	
}