import java.util.ArrayList;

public class Collision
{
	public int type;
	public double cx = -555, cy = -555;//collision x, collision y
	public int ix = 0, iy = 0;//correction instructions
	
	public ArrayList<PolyShape> involvedShapes;
	public Collision()
	{
		involvedShapes = new ArrayList<PolyShape>();
	}
	
	
}