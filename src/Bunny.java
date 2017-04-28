import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.TreeMap;

public class Bunny extends Mob implements Collidable
{
	int size = 14*2;
	public static final int DEFAULT_SIZE = 14*2;
	public double dx, dy, lx, ly, llx, lly;
	public boolean collidable = true;
	public boolean onGround = true;
	

	public int getImageIndex()
	{
		return ImageHelper.IMAGE_BUNNY;
	}
	
	public boolean onGround()
	{
		return onGround;
	}
	public void setOnGround(boolean yes)
	{
		onGround = yes;
	}
	public void setFacingLeft(boolean left)//for client only
	{
		goLeft = left;
	}
	public boolean facingLeft()
	{
		return goLeft;
	}
	public void onPlayerCollideClient(Player p)
	{
		p.dy = 15;
		if (goLeft)
			p.dx = -20;
		else
			p.dx = 20;
		p.setOnGround(false);
	}
	public Bunny()
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
	public void definePolyShape(double x, double y)
	{
		
		this.x = x; this.y = y;
		
		int xx = (int)x, yy = (int)y;
		
		int[] xs = {xx-size,xx,xx+size};
		int[] ys = {yy,yy+size*2,yy};
		
		p = new PolyShape(xs, ys);
		p.color = new Color(62, 180, 137);//mint green!
	}
	
	public Bunny(double x, double y)
	{
		super(x,y);
		definePolyShape(this.x, this.y);
	}
	
	public Bunny(String in)
	{
		String[] s = in.split(" ");
		spawnX = pint(s[0]);
		spawnY = pint(s[1]);
		x = spawnX; y = spawnY;
		definePolyShape(x,y);
		p.color = new Color(pint(s[2]),pint(s[3]),pint(s[4]));
		
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
	
	public void draw(Graphics2D g, double rw, double rh, int h, int hShift, int vShift)
	{
		g.setColor(p.color);
		
		
		p.defineDrawableShape(rw, rh, h, hShift, vShift);
		
		g.fill(p.drawableShape);
	}
	
	boolean goLeft = false;
	
	public double getDY()
	{
		return dy;
	}
	public double getDX()
	{
		return dx;
	}
	public void move(TreeMap<Integer, Player> players)//move for Collidable objects can only request left and right movement; no Y-movement
	{
		
		
		
		
		x += dx;
		
		
		if (onGround())
		{
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
			goLeft = (p.x < this.x);
		}
		
		
		if (goLeft)
			dx=-15;
		else
			dx=15;
		
		
		
			
			this.dy = 24;//bunny hop!
			onGround = false;
		}
		
		if (goLeft)
			dx=-15;
		else
			dx=15;
		
		
		
	}
	
	public boolean collidable()
	{
		return true;
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
	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public void setX(double x) {
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
	
	
	
}