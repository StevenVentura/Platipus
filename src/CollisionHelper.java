import java.awt.Point;
import java.util.ArrayList;

public class CollisionHelper
{
	/*
	 * log all of the things i collide with and then do stuff with them.
	 * 
	 * 
	 */
	
	public ArrayList<Collision> collisions;
	
	
	private Collidable user;
	public CollisionHelper(Collidable user)
	{
	this.user=user;	
	collisions = new ArrayList<Collision>();
	}
	
	public void perform()//called once all of the collisions have been recorded in this CollisionHelper
	{
		
		ArrayList<Collision> diagonals = new ArrayList<Collision>();
		ArrayList<Collision> horizontals = new ArrayList<Collision>();
		ArrayList<Collision> verticals = new ArrayList<Collision>();
		
		ArrayList<PolyShape> involvedShapes = new ArrayList<PolyShape>();
		
		for (Collision c : collisions)
		{
			for (PolyShape p : c.involvedShapes)
				involvedShapes.add(p);
			
			switch(c.type)
			{
			case(CollisionLine.TYPE_NEGATIVE):	
			diagonals.add(c);break;
			case(CollisionLine.TYPE_POSITIVE):
			diagonals.add(c);break;
			
			case(CollisionLine.TYPE_HORIZONTAL):
			horizontals.add(c);break;
			
			case(CollisionLine.TYPE_VERTICAL):
			verticals.add(c);break;
			}
		}
		
		boolean done = false;//instead of saying return
		
		if (diagonals.size() > 0)//this part works
		{
			//find the closest diagonal, only intersect with the closest diagonal
			int closestIndex = -1;
			double closestDistance = Double.MAX_VALUE;
			
			int index = -1;
			for (Collision d : diagonals)
			{
				index++;
				
				
				double distance = Math.sqrt(Math.pow(user.getX() - d.cx,2) + Math.pow(user.getY() - d.cy,2));
				
				if (distance < closestDistance)
				{
					closestIndex = index;
					closestDistance = distance;
				}
				
			}
			
			Collision d = diagonals.get(closestIndex);
			
			user.setX(d.cx);
			user.setY(d.cy);
			
			user.setX(user.getX() + d.ix);
			user.setY(user.getY() + d.iy);
			
			done = true;
			
			
			
		}
		
		
		if (!done && horizontals.size() > 0 && verticals.size() > 0)//somethin new to try
		{
			user.setX(verticals.get(0).cx);
			
			user.setY(horizontals.get(0).cy);
			
			
			user.setX(user.getX() + verticals.get(0).ix);
			
			user.setY(user.getY() + horizontals.get(0).iy);
			done = true;
		}
		
		
		if (!done && horizontals.size() > 0)//just a horizontal line
		{
			
			int closestIndex = -1;
			double closestDistance = Double.MAX_VALUE;
			
			int index = -1;
			for (Collision d : horizontals)
			{
				index++;
				
				
				double distance = Math.sqrt(Math.pow(user.getLX() - d.cx,2) + Math.pow(user.getLY() - d.cy,2));
				
				if (distance < closestDistance)
				{
					closestIndex = index;
					closestDistance = distance;
				}
				
			}
			
			Collision d = horizontals.get(closestIndex);
			
			user.setX(d.cx);
			user.setY(d.cy);
			
			user.setX(user.getX() + d.ix);
			user.setY(user.getY() + d.iy);
			done = true;
			
		}
		if (!done && verticals.size() > 0)//just a horizontal line
		{
			Collision c = verticals.get(0);
			
			user.setX(c.cx);
			user.setY(c.cy);
			user.setX(user.getX() + c.ix);
			user.setY(user.getY() + c.iy);
			
			done = true;
		}
		
		if (!done && collisions.size() > 0)
		{
			if (collisions.get(0).type == CollisionLine.TYPE_PLATFORM)
			{
				Collision c = collisions.get(0);
				user.setX(c.cx);
				user.setY(c.cy);
				user.setX(user.getX() + c.ix);
				user.setY(user.getY() + c.iy);
				done = true;
			}
		}
		
		
		for (PolyShape p : involvedShapes)
		{
			if (p.collisionShape.contains(new Point((int)user.getX(),(int)user.getY())))
			{
				user.setDX(0);
				user.setDY(0);
				user.setX(user.getLLX());
				user.setY(user.getLLY());
			}
			
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
	}
	
}