import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Stack;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class PlatipusServer
{
	public static int pint(String is)
	{
		return Integer.parseInt(is);
	}
	
public ArrayList<clientdata> clients = new ArrayList<clientdata>();
public ServerSocket serversocket;

public ServerGUI gui;


public TreeMap<Integer, Player> players;

public TreeMap<Integer, PlatipusMap> levels;

public MapDecider md;


public String startingLevelName;
public boolean editorTestRun = false;
public PlatipusServer()
{
   startingLevelName = "lobby.map";
   editorTestRun = false;
}
public PlatipusServer(String level)
{
	editorTestRun = true;
	startingLevelName = level;
}

private void addLevel(PlatipusMap pm)
{
	pm.populateGrid();
	int index = levels.size();
	for (Integer i : pm.mobs.keySet())
	{
		Mob m = pm.mobs.get(i);
		m.levelIndex = index;
	}
	
	
	levels.put(index, pm);
	
	
	
	
}

public void begin()
{
try{

players = new TreeMap<Integer, Player>();
levels = new TreeMap<Integer, PlatipusMap>();
md = new MapDecider();

addLevel(new PlatipusMap(startingLevelName));//everyone starts in the lobby
populateDoors();

gui = new ServerGUI(this);
gui.println("Starting level on: " + startingLevelName);
gui.println("Waitin for connections...");
if (editorTestRun)
{
	gui.println("**Editor Test Run**");
}

serversocket = new ServerSocket(8123);
new Thread(this.accept()).start();//accepts connections

long CT = System.currentTimeMillis(), LT = System.currentTimeMillis();
long LTC = System.currentTimeMillis();
long WTC = System.currentTimeMillis();
while(runTheServer)
{
	//Thread.sleep(1);
	CT = System.currentTimeMillis();
	if (CT - LT > 0)
	{
		LT = CT;
		
		this.handleClientCommands();
		
	}
	if (CT - LTC > 50)
	{
		LTC = CT;
		
		this.handleServerCollisions();
	}
	if (CT - WTC > 100)
	{
		WTC = CT;
		
		this.moveNPCs();
		this.sendNPCs();
		this.disposeNPCs();
	}
}
gui.frame.dispose();



}catch(Exception e){e.printStackTrace();};

try{
serversocket.close();
}catch(Exception e){e.printStackTrace();};
}

public void disposeNPCs()
{
	for (Integer i : levels.keySet())
	{
		ArrayList<Integer> removeUs = new ArrayList<Integer>();
		for (Integer c : levels.get(i).mobs.keySet())
		{
			Mob m = levels.get(i).mobs.get(c);
			if (m.flaggedForRemoval)
			{
				removeUs.add(c);
			}
		}
		for (Integer x : removeUs)
		{
			broadcast("removemob " + i + " " + x);
			levels.get(i).mobs.remove(x);
			
		}
	}
}


public void applyPhysics(Collidable c)
{
	c.setY(c.getY() + c.getDY());
	c.setDY(c.getDY() + Mob.ddy);
	if (c.getDY() < -47.5/2)
	{
		c.setDY(-47.5/2);
	}
}


public void moveNPCs()
{
	
	for (Integer level : levels.keySet())
	{
		PlatipusMap pm = levels.get(level);
		
		for (Integer i : pm.mobs.keySet())
		{
			Mob m = pm.mobs.get(i);
			if (m.collidable())
			{
				
				Collidable mm = (Collidable)m;
				mm.setLLX(mm.getLX()); mm.setLLY(mm.getLY());
				mm.setLX(mm.getX()); mm.setLY(mm.getY());
				doServerCollision(m, pm);
			}
			m.move(players);
			
			if (m.collidable())
			{
				Collidable mm = (Collidable)m;
				this.applyPhysics(mm);
			
				doServerCollision(m, pm);
			}
			
			m.playerIntersected = -1;//will still be -1 if there is no player collision. can only intersect with 1 player at a time.
			
			//check if mob collides with any player in the level
			for (Integer player : players.keySet())
			{
				Player play = players.get(player);
				if (play.levelIndex == level)
				{
					
					if (Mob.getCollideRectangle(m, m.x, m.y).intersects(new Rectangle((int)play.x-16,(int)play.y-16,32,32)))
					{
						m.playerIntersected = play.ID;
						m.flaggedForRemoval = true;
						break;
					}
					
					
				}
				
				
			}
			
			
			
			
		}
		
		
	}
	
}

public void doServerCollision(Mob m, PlatipusMap pm)
{
		Collidable mob = (Collidable)m;
		int hits = 0;
		
		CollisionHelper ch = new CollisionHelper(mob);
		
		
		if (pm == null)
			return;
		
		boolean pointZoneCollided = false;
		PointZone pz = null; 
		
		ArrayList<PolyShape> collidedGridShapes = new ArrayList<PolyShape>();
		
		{
		int minRow = pm.grid.getRow(Math.min(mob.getY(),mob.getLY()));
		int maxRow = pm.grid.getRow(Math.max(mob.getY(),mob.getLY()));
		int minCol = pm.grid.getCol(Math.min(mob.getX(), mob.getLX()));
		int maxCol = pm.grid.getCol(Math.max(mob.getX(), mob.getLX()));
		
		Line2D.Double playerPath = new Line2D.Double(mob.getLX(), mob.getLY(), mob.getX(), mob.getY());
		
		for (int r = minRow; r <= maxRow; r++)
		{
			if (r < 0 || r > pm.grid.numRows-1)
				continue;
			for (int c = minCol; c <= maxCol; c++)
			{
				if (c < 0 || c > pm.grid.numCols - 1)
					continue;
				
				Rectangle2D.Double rect = new RectGridCell(pm.grid, r, c).bounds;//new Rectangle2D.Double(pm.grid.cellWidth*c, pm.grid.cellHeight*r, pm.grid.cellWidth, pm.grid.cellHeight);
				
				if (playerPath.intersects(rect))
				{
					for (PolyShape p : pm.grid.field[r][c])
						collidedGridShapes.add(p);
				}
				
			}
		}
		}
		
		
		
		boolean collidedWithANonFloor = false;//isnt on ground -- for carrying 
		
		for (PolyShape s : collidedGridShapes)     
		{
			
			if (s.solid == false)
				continue;
			
			boolean shapeAlreadyAdded = false;
			Line2D.Double playerPath = new Line2D.Double(mob.getLX(),mob.getLY(),mob.getX(),mob.getY());
			for (CollisionLine c : s.collisionLines)
			{
				Line2D.Double wall = c.wallLine;
				if (playerPath.intersectsLine(wall))
				{
					Collision collision = new Collision();
					if (s instanceof Platform)
					{
						
							mob.setOnGround(true);
							collision.cy = ((Platform)s).top;
							collision.iy = 1;
						
						collision.cx = mob.getX();
						mob.setDY(0);
						collision.ix = 0;
						collision.type = CollisionLine.TYPE_PLATFORM;
						ch.collisions.add(collision);
						break;
					}
					if (!shapeAlreadyAdded)
					collision.involvedShapes.add(s);
					shapeAlreadyAdded = true;
					
					
					hits++;
					
					
					
					if (!(s instanceof Platform))
					 switch (c.type)
					{
					
					
					case (CollisionLine.TYPE_HORIZONTAL):
						{
						collision.type = CollisionLine.TYPE_HORIZONTAL;
						boolean falling = mob.getLY() > mob.getY();
						
						double ix = mob.getX(), iy = wall.y1;
						
						//mob.getX() = ix; mob.getY() = iy;
						collision.cx = ix; collision.cy = iy;
						if (falling)
						{
							//mob.getY()++;
							collision.iy = 1;
							mob.setOnGround(true);
						}
						else
						{
							collision.iy = -1;//mob.getY()--;
							mob.setDY(0);//hit a ceiling
							collidedWithANonFloor = true;
						}
						
						}
						ch.collisions.add(collision);
						break;
						
					case (CollisionLine.TYPE_VERTICAL):
						{boolean movingLeft = mob.getLX() > mob.getX();
						
						double ix = wall.x1, iy = mob.getY();
						
						//mob.getX() = ix; mob.getY() = iy;
						collision.cx = ix; collision.cy = iy;
						collidedWithANonFloor = true;
						if (movingLeft)
							collision.ix = 1;//mob.getX()++;
						else
							collision.ix = -1;//mob.getX()--;
						}
						ch.collisions.add(collision);
						break;
						
					case (CollisionLine.TYPE_POSITIVE):
					{
						
					collision.involvedShapes.remove(s);
					collision.type = CollisionLine.TYPE_POSITIVE;
						
					boolean movingLeft = mob.getLX() > mob.getX();
					boolean falling = mob.getLY() > mob.getY();
					
					boolean justFalling = mob.getLX() == mob.getX();
					boolean above = falling || (!movingLeft && !justFalling);
					
					
					if (above)//if (justFalling)//solves the undefined issue
					{
						collision.cy = (wall.y2-wall.y1)/(wall.x2-wall.x1) * (mob.getX() - wall.x1) + wall.y1; 
						collision.cx = mob.getX();
						mob.setOnGround(true);
					}
					else
					{
						collidedWithANonFloor = true;
						collision.cx = mob.getLX();//collision.cx = (mob.getY()*wall.x2 - wall.y1*wall.x2 - mob.getY()*wall.x1 + wall.y2*wall.x1)/(wall.y2-wall.y1);
						collision.cy = mob.getLY();
					}
					
					if (above)//always called
					{
						collision.iy = 1;//mob.getY()++;
					}
					else
					{
						collision.iy = -1;//mob.getY()--;
					}
					
					}
					ch.collisions.add(collision);
					break;
					
					case (CollisionLine.TYPE_NEGATIVE)://mostly copy pasted from type_positive
					{
						
						collision.involvedShapes.remove(s);
						
					collision.type = CollisionLine.TYPE_NEGATIVE;
					
					boolean movingLeft = mob.getLX() > mob.getX();
					boolean falling = mob.getLY() > mob.getY();
					
					
					
					boolean justFalling = mob.getLX() == mob.getX();
					
					boolean above = falling || (movingLeft && !justFalling);
					
					
					if (above)//if (justFalling)//solves the undefined issue
					{
						collision.cy = (wall.y2-wall.y1)/(wall.x2-wall.x1) * (mob.getX() - wall.x1) + wall.y1; 
						collision.cx = mob.getX();
						mob.setOnGround(true);
					}
					else
					{
						collidedWithANonFloor = true;
						/*collision.cx = (mob.getY()*wall.x2 - wall.y1*wall.x2 - mob.getY()*wall.x1 + wall.y2*wall.x1)/(wall.y2-wall.y1);//changed to fix acute angle jump clip
						collision.cy = mob.getY();*/
						collision.cx = mob.getLX();
						collision.cy = mob.getLY();
						
					}
					
					if (above)//always called
					{
						collision.iy = 1;//mob.getY()++;
					}
					else
					{
						collision.iy = -1;//mob.getY()--;
					}
					
					}
					ch.collisions.add(collision);
					break;
					
					
					case (-555):
						System.out.println("what the?");
						break;
					case (-1):
						System.out.println("Invalid line somehow");
						break;
						
					}
				}
				
			}//end for collision
				
		}
		
		
		double xChanged = mob.getX();
		double yChanged = mob.getY();
		ch.perform();//magic
		if (mob.getX() != xChanged)
			mob.setDX(0);
			
		
	
	
}

public void sendNPCs()//updates
{
	/*
	 * which level the NPC is on
	 * which NPC it is in the NPC array
	 * 
	 */
	/*
	 * 
	 * construct a different NPC string for each level
	 * 	and only send the string to players on that level.
	 */
	
	final String n = "+";
	
	
	
	for (Integer level : levels.keySet())
	{
		String out = "npc ";
		PlatipusMap pm = levels.get(level);
		if (pm.mobs.size() == 0)
			continue;
		
		for (Integer i : pm.mobs.keySet())
		{
			Mob m = pm.mobs.get(i);
			out += m.toUpdateString();
			out += n;
		}
		
		for (Integer player : players.keySet())
			{
			Player p = players.get(player);
			if (clients.get(p.clientArrayIndex).player.levelIndex == level)
				clients.get(p.clientArrayIndex).write(out);
			}
		
		
	}
	
}

public void handleServerCollisions()
{
	
	for (Integer i1 : players.keySet())
	{
		Player p1 = players.get(i1);
		double r = 100;
		double ø = p1.swordSwing;
		if (!p1.facingLeft)
			ø = 180 - ø;
		int[] xs = {(int)(p1.x+r*Math.cos(Math.toRadians(ø+15))),(int)(p1.x+r*Math.cos(Math.toRadians(ø-15))),(int)p1.x};
		int[] ys = {(int)(p1.y+r*Math.sin(Math.toRadians(ø+15))),(int)(p1.y+r*Math.sin(Math.toRadians(ø-15))),(int)p1.y};
		PolyShape poly = new PolyShape(xs,ys);
		
		for (Integer i2 : players.keySet())
		{
			if (i1 == i2)//a player cannot interact with himself.
				continue;
			
			Player p2 = players.get(i2);
			
			
			//sword-player collision
			if (p1.swordSwing != -1)//if the sword is currently being swung
			{
				if (poly.collisionShape.intersects(new Rectangle((int)p2.x-16,(int)p2.y-16,32,32)))
				{
					broadcast("smash " + p2.ID + " " + p1.ID);
				}
				
				
			}
			
			
			//if they intersect at all. players can walk through each other. but the higher one sticks to the lower one's head :D
			if ((p1.stance == Player.STANCE_DEFAULT && p2.stance == Player.STANCE_DEFAULT) && new Rectangle((int)p1.x-16,(int)p1.y-16,32,32).intersects(new Rectangle((int)p2.x-16,(int)p2.y-16,32,32)))//stance-based collisions
			{
				//find which player is higher. if they are equal height don't do anything.
				
				double p1y = (p1.y + p1.ly) / 2;
				double p2y = (p2.y + p2.ly) / 2;
				
				boolean p1higher = (p1y > p2y);
				
				if (p1higher)
				{
					clients.get(p1.clientArrayIndex).write("stance " + Player.STANCE_ON_OTHERS_HEAD + " " + p2.ID);//on top of p2
				}
				else if (p1y != p2y)
				{
					clients.get(p2.clientArrayIndex).write("stance " + Player.STANCE_ON_OTHERS_HEAD + " " + p1.ID);//on top of p1
				}
				
				
				
			}
			
		}
		
		//sword-mob collision
		if (p1.swordSwing != -1)//if the sword is currently being swung
		{
			PlatipusMap pm = levels.get(p1.levelIndex);
			
			int mobIndex = -1;
			for (Integer i : pm.mobs.keySet())
			{
				Mob m = pm.mobs.get(i);
				if (m.flaggedForRemoval) continue;
				mobIndex++;
				m.definePolyShape(m.x,m.y);
				if (poly.collisionShape.intersects(m.p.collisionShape.getBounds()))
				{
					m.x = m.spawnX; m.y = m.spawnY;
					broadcast("mobsmash " + p1.ID + " " + mobIndex);
				}
				
			}
			
			
			
			
		}
		
		
		
		
	}
	
	
	
}

public void populateDoors()
{
	//doors.clear();
	for (Integer i : levels.keySet())
	{
		PlatipusMap pm = levels.get(i);
		int doorIndex = -1;
		for (PolyShape s : pm.shapes)
		{
			
			if (s instanceof Door)
			{	
				doorIndex++;
				((Door)s).levelFromIndex = i;
				((Door)s).doorFromIndex = doorIndex;
				//doors.add((Door)s);
					
			
			}
		}
	}
}

private void addLevel(Door d, int levelFrom, int doorFrom)//choose a level based on which way this door faces, and add it to the array. also populate the door info.  //if a player walks through a door that doesn't have a destination set yet; has stats "-1 -1 -1 -1"
{
	//find a door of the opposite direction somewhere in the arraylist of levels and doors
	int index = d.levelToIndex;
	if (index == -1)//it will always be -1 i think
	{
		index = levels.size(); //cus im adding a new level
	}
	
	int r = d.direction;
	int e = -1;
	if (r == Door.DOWN)
		e = Door.PAIR_DOWN;
	if (r == Door.UP)
		e = Door.PAIR_UP;
	if (r == Door.RIGHT)
		e = Door.PAIR_RIGHT;
	if (r == Door.LEFT)
		e = Door.PAIR_LEFT;
	//e is the direction that i want the new door to be in.
	
	md.decide(d.levelFromIndex, e);
	
	
	PlatipusMap newMap = new PlatipusMap(md.getChoiceName());
	
	
	if (!levels.containsKey(index))
		addLevel(newMap);
	
	d.levelToIndex = index;//d is the door that sent him here.
	d.doorToIndex = md.getChoiceDoorIndex();
	
	int doorIndex = -1;
	for (PolyShape s : newMap.shapes)//setting the attributes on the new level's entry door -- the destination door
	{
		if (s instanceof Door)
		{
			doorIndex++;
			
			if (doorIndex == d.doorToIndex)
			{
				Door d2 = (Door)s;
				
				d2.levelToIndex = levelFrom;
				d2.doorToIndex = doorFrom;
				
			}
			
		}
		
	}
	
	
	
}


public void handleClientCommands()
{
	
	for (int i = 0; i < clients.size(); i++)
	{
		
		Stack<String> commands = clients.get(i).commands;
		while(!commands.empty())
		{
			
			String s = commands.pop();
			if (s.startsWith("new"))
			{
				String[] z = s.split(" ");
				int ID = pint(z[2]);
				if (!players.containsKey(ID))
				{
					Player p = new Player(s);
					players.put(p.ID,p);
					return;
				}
				else
				{
					double lx = players.get(clients.get(i).player.ID).x, ly = players.get(clients.get(i).player.ID).y;
					players.get(ID).updatePlayer(s);
					players.get(clients.get(i).player.ID).lx = lx; players.get(clients.get(i).player.ID).ly = ly;
				}
				
				broadcast(players.get(ID).toString());//bounces the new player back to the rest of the clients.
			}
			else if (s.startsWith("map"))//"map " + user().destinationInfo
			{
				String[] split = s.split(" ");
				
				
				populateDoors();//label doors
				
				int relativeDoorFromIndex = pint(split[1]);//user walked into door # ... 
				
				Door door = null;
				
				for (PolyShape p : levels.get(clients.get(i).player.levelIndex).shapes)
				{
					if (p instanceof Door)
					{
						Door d2 = (Door)p;
						if (d2.doorFromIndex == relativeDoorFromIndex)
						{
							door = d2;//find which door the user walked in to.
						}
						
					}
				}
				
				
				int levelTo = door.levelToIndex, levelFrom = door.levelFromIndex, doorTo = door.doorToIndex, doorFrom = door.doorFromIndex;
				String levelString = "map ";
				
				
				
				if (levels.containsKey(levelTo))//if the door already leads somewhere
				{
					clients.get(i).player.levelIndex = levelTo;
					levelString += ""+door.doorToIndex + " " + door.levelToIndex + " *" + levels.get(levelTo).toSocketString();
				}
				else //if the door doesn't have a destination yet, generate a new level.
				{
					addLevel(door, levelFrom, doorFrom);//populates the doorTo. the latter 2 parameters are used for the new door
					levelTo = door.levelToIndex; doorTo = door.doorToIndex;
					
					clients.get(i).player.levelIndex = levelTo;
					players.get(clients.get(i).player.ID).levelIndex = levelTo;
					
					levelString += ""+door.doorToIndex + " " + door.levelToIndex + " *" + levels.get(levelTo).toSocketString();
					
					populateDoors();
				}
				
				//now levelTo has the correct level
				
				players.get(clients.get(i).player.ID).levelIndex = levelTo;
				clients.get(i).write(levelString);
				
				if (players.get(clients.get(i).player.ID).stance == Player.STANCE_CARRYING_OTHER)
				{
					Player p = players.get(players.get(clients.get(i).player.ID).otherPlayerID);
					
					p.levelIndex = door.levelToIndex;
					clients.get(p.clientArrayIndex).player.levelIndex = door.levelToIndex;
					clients.get(p.clientArrayIndex).write(levelString);
					//clients.get(players.get(players.get(clients.get(i).player.ID).otherPlayerID).clientArrayIndex).write(levelString);
				}
			}
			else if (s.startsWith("breakfree")) 
			{
				String[] split = s.split(" ");
				clients.get(players.get(pint(split[1])).clientArrayIndex).write("stance " + Player.STANCE_DEFAULT + " " + -1);
			}
			else if (s.startsWith("pickup"))
			{
				Player p = players.get(clients.get(i).player.ID);
				
				if (p.stance == Player.STANCE_ON_OTHERS_HEAD)
				{
				clients.get(i).write("stance " + Player.STANCE_CARRYING_OTHER + " " + p.otherPlayerID);
				clients.get(players.get(p.otherPlayerID).clientArrayIndex).write("stance " + Player.STANCE_BEING_CARRIED + " " + p.ID);
				clients.get(players.get(p.otherPlayerID).clientArrayIndex).write("map -1 " + p.levelIndex + " *" + levels.get(p.levelIndex).toSocketString());
				clients.get(players.get(p.otherPlayerID).clientArrayIndex).player.levelIndex = p.levelIndex;
				}
			}
			else if (s.startsWith("throw"))
			{
				Player p = players.get(clients.get(i).player.ID);
				
				if (p.stance == Player.STANCE_CARRYING_OTHER)
				{
				clients.get(i).write("stance " + Player.STANCE_DEFAULT + " " + p.otherPlayerID);	
				
				clients.get(players.get(p.otherPlayerID).clientArrayIndex).write(s);
					
				}
				
			}
			else if (s.startsWith("particles"))
			{
				broadcast(s);
			}
			else if (s.startsWith("chat"))
			{
				String txt = s.substring(5);
				ArrayList<String> out = new ArrayList<String>();
				while (txt.length() >= 44)//word wrap
				{
					out.add(txt.substring(0, 44));
					if (txt.length() > 44)
					txt = txt.substring(44);
				}
				out.add(txt);
					
				for (String str : out)
				{
					gui.println(clients.get(i).player.ID + ": " + str);
					broadcast("chat " + str);
				}
			}
			else if (s.startsWith("stop"))
			{
				String[] split = s.split(" ");
				if (split[1].equals("server"))
						runTheServer = false;
				return;
				
			}
			
			
		}
		
		
		
		
		
		
	}
	
	
}

public void Say(String s)//server Say
{
	broadcast("chat -1*" + s);
	gui.println("[server]:" + s);
}

public void Tell(int player, String s)
{
	broadcast("chat " + player + "*" + s);
}


public static void main(String[]args)
{
try{
	PlatipusServer p;
	if (args.length != 0)
	{
		p = new PlatipusServer(args[0]);
	}
	else
p = new PlatipusServer();
p.begin();
}catch(Exception e){e.printStackTrace();};
}


private int numConnections = 0;

public Runnable accept()
{
return new Runnable() { 
public void run() {
try{
int numConnections = 0;
while(runTheServer)
{
	Socket ssa = null;
clientdata temp = new clientdata(ssa = serversocket.accept());//has a Socket parameter

numConnections++;//not necessarily unique connections; this value could still increase if the same user leaves and joins again


int already = temp.alreadyExistsInGame();

if (already == -1)//a new player connecting
{
	temp.startConnection();
	clients.add(temp);
	int newPlayerID = players.size()+1;
	temp.player.ID = newPlayerID;
	temp.player.levelIndex = 0;//player spawns in the lobby
	temp.player.clientArrayIndex = clients.size()-1;
	players.put(temp.player.ID,temp.player);
	
	temp.out.println(temp.player.toString().replaceFirst("player","user"));

	temp.out.println("map -1 0 *" + levels.get(0).toSocketString());//send him to the lobby
	Tell(newPlayerID, "Welcome to Platipus!");
	for (Integer i : players.keySet())
		broadcast(players.get(i).toString());
}
else//player just left and rejoined
{
	gui.println(temp.player.IP + " reconnected!");
	Player p = clients.get(already).player;
	System.out.println("pID="+p.ID);
	clients.set(already, new clientdata(ssa));
	clients.get(already).startConnection();
	clients.get(already).player = p;
	
	clients.get(already).out.println(p.toString().replaceFirst("player","user"));
	
	clients.get(already).out.println("map -1 0 *" + levels.get(0).toSocketString());//send him to the lobby
	Tell(p.ID, "Welcome back!");
	gui.println(""+clients.size());
}



}
}catch(Exception e){e.printStackTrace();}; } };
}

private void broadcast(String command)
{
	
for (int i = 0; i < clients.size(); i++)//for (clientdata c : clients)
{
	clientdata c = clients.get(i);
	c.out.println(command);
}
}



class clientdata//the Player object is created on both the Client and the Server, but the clientdata object is only created on the server.
{
private Socket s;
private PrintWriter out;
public BufferedReader in;
public Stack<String> commands;//holds all of the data sent from the user to the server!

public Player player;

public clientdata(Socket s)
{
try{
this.s=s;
player = new Player(this.s.getRemoteSocketAddress().toString(), startingLevelName);//"levela.txt");

this.s=s;
commands = new Stack<String>();



}catch(Exception e){e.printStackTrace();}
}

public void startConnection()
{
	try{
	gui.println(player.IP + " has connected.");
	out = new PrintWriter(s.getOutputStream(),true); in = new BufferedReader(new InputStreamReader(s.getInputStream()));
	new Thread(receivedWriter()).start();
	}catch(Exception e){e.printStackTrace();};
}
public void closeConnection()
{
	out.close();
	try {in.close();} catch (IOException e) {e.printStackTrace();}
	
	
	
	
}


public int alreadyExistsInGame()
{
	int already = -1;
	for (clientdata c : clients)
	{
		Player p = c.player;
		String IP = p.IP.substring(0,p.IP.indexOf(":"));
		
		if (player.IP.substring(0, player.IP.indexOf(":")).equals(IP))
			return p.levelIndex;
	}
	return already;
	
}

public void write(String write)
{
this.out.println(write);
}

private Runnable receivedWriter()// 
{
return new Runnable() {
public void run() {
try{
String received;
while(true)
{
received = in.readLine();
commands.add(received);
}
}catch(Exception e){
	
	
	closeConnection();
	gui.println(""+player.IP+" lost connection.");
	


};


}};
}

}

public void updateUser(int ID)
{
	clients.get(players.get(ID).clientArrayIndex).out.println(players.get(ID).toString().replaceFirst("player","user"));
}

public volatile boolean runTheServer = true;

class ServerGUI
{
private PlatipusServer bs;
private JFrame frame;
private JTextArea console;
private JScrollPane jsp;
private final JTextField jtf;

public ServerGUI(PlatipusServer bs)
{
this.bs=bs;
frame = new JFrame("PlatipusServer by Steven");

frame.setResizable(false);
frame.setLayout(new FlowLayout());
frame.setSize(800,600);

if (editorTestRun == false)
frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


console = new JTextArea();
console.setEditable(false);


jsp = new JScrollPane(console);
jsp.setPreferredSize(new Dimension(800-14, 500-21));
frame.add(jsp);

jtf = new JTextField();
jtf.setBackground(new Color(0.025f,0.025f,0.025f).darker());
jtf.setForeground(Color.WHITE);
jtf.setFont(new Font("DejaVu Sans", Font.BOLD, 26));
jtf.setPreferredSize(new Dimension(800-14, 100-21));
jtf.setText("");

jtf.addActionListener(
		new ActionListener()
		{
			public void actionPerformed(ActionEvent e)//on the Enter key-press
			{
				String text = e.getActionCommand().toLowerCase();
				
				String[] txt = text.split(" ");
				
				if (txt[0].equals("role"))
				{
					try{
						
						players.get(pint(txt[1])).role = pint(txt[2]);
						updateUser(pint(txt[1]));
						
						println(txt[1] + "'s role changed to " + txt[2]);
						
					}catch(Exception x){println("syntax: role [player] [value]");};
				}
				else if (txt[0].equals("level"))
				{
					try{
						players.get(pint(txt[1])).levelIndex = pint(txt[2]);
						clients.get(players.get(pint(txt[1])).clientArrayIndex).out.println("map " + txt[3] + " " + txt[2] + " " + "*" + levels.get(pint(txt[2])).toSocketString());//send him to the lobby
						
					}catch(Exception x){println("syntax: level [player] [levelID] [door]");
					
					for (Integer i : levels.keySet())
					{
						PlatipusMap p  = levels.get(i);
						println("level " + i + " " + p.toServerGUIString());
					}
					
					
					};
				}
				else if (txt[0].equals("chat") || txt[0].equals("say"))
				{
					String s = e.getActionCommand();
					Say(s.substring(s.indexOf(" ")+1));
				}
				else
				try{
					println(players.size() + " players:");
				for (Integer i : players.keySet())
				{
					Player p = players.get(i);
					
					println(p.toServerGUIString());
					
				}
				
				}catch(Exception ee){ee.printStackTrace();};
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				jtf.setText("");
			}
			
			
		}
				);
frame.add(jtf);

WindowListener windowlisten = new WindowAdapter() {
	public void windowClosing(WindowEvent e) {
		
		runTheServer = false;
		for (clientdata c : clients)
		{
			c.out.println("stop client");
		}
	}
	
};
if (editorTestRun)
frame.addWindowListener(windowlisten);


console.setBackground(new Color(0.1686274509803922f,0.0588235294117647f, 0.003921568627451f));
console.setForeground(new Color(0.7372549019607843f,0.7333333333333333f, 0.5019607843137255f));
console.setFont(new Font("Times New Roman", Font.BOLD, 40));
frame.setVisible(true);
jtf.requestFocus();


}

public void print(String s)
{
console.append(s);
}
public void println(String s)
{
this.print(s);
this.print("\r\n");
console.scrollRectToVisible(new Rectangle(0,console.getBounds().height,1,1));
}


}


}