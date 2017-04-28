public interface Collidable
{
	public abstract double getX();
	public abstract double getY();
	public abstract void setX(double x);
	public abstract void setY(double y);
	
	public abstract void setDX(double dx);
	public abstract void setDY(double dy);
	public abstract double getDY();
	public abstract double getDX();
	
	public abstract void setLLX(double llx);
	public abstract void setLLY(double lly);
	public abstract void setLX(double lx);
	public abstract void setLY(double ly);
	
	public abstract double getLLX();
	public abstract double getLLY();
	public abstract double getLX();
	public abstract double getLY();
	
	public abstract boolean onGround();
	
	public abstract void setOnGround(boolean yes);
	
	
	
	
	
	
	
	
}