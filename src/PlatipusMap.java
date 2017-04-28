import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

public class PlatipusMap
{
/*
 * difficulty::
1: beginning, no weapon -- shouldn't have to kill any
2: beginning, maybe a single-swing bat -- probably have to kill one to get through
3: maybe has something better now -- probably have to kill multiple to get through


1:
start_tiny.map
mario.map
hall.map


2:
ledgekill.map
bm.map
umap.map


3:
climb.map


 * 
 */

public String name;//map name

//ArrayList<Player> mapPlayers;

ArrayList<PolyShape> shapes;

ArrayList<Door> doors;

TreeMap<Integer, Mob> mobs;

public int depth;//for depth in the castle! 
public int difficulty;

public int minHeight = 0, maxHeight = 600;
public int minWidth = 0, maxWidth = 800;

public PlatipusMap()//for the map editor and the client
{
	shapes = new ArrayList<PolyShape>();
	doors = new ArrayList<Door>();
	mobs = new TreeMap<Integer, Mob>();
	difficulty = 1;
	
	populateGrid();
}

public Grid grid;

public void populateGrid()
{
	int maxx = this.maxWidth;
	int maxy = this.maxHeight;
	int minx = this.minWidth;
	int miny = this.minHeight;
	
	int ngw=0, ngy=0;
	
	if (minx < 0)
		ngw = minx;
	if (miny < 0)
		ngy = miny;
	grid = new Grid(maxx-minx, maxy-miny, 20, 20,ngw,ngy);
	ArrayList<RectGridCell> rgcs = new ArrayList<RectGridCell>();
	for (int r = 0; r < grid.numRows; r++)
	{
		for (int c = 0; c < grid.numCols; c++)
		{
			rgcs.add(new RectGridCell(grid,r,c));
		}
	}
	
	for (PolyShape p : this.shapes)
	{
		//getrow and getcol of each line and add to grids
		for (RectGridCell rgc : rgcs)
		for (CollisionLine c : p.collisionLines)
		{
			if (rgc.bounds.intersectsLine(c.wallLine))
			{
				grid.addToCell(p, rgc.row, rgc.column);
			}
			
		}
		
		
	}
	
	
	
}

public void save()//saves to a file with filename name 
{
	name = name.replace(".txt",".map");
	if (!name.contains("."))
		name = name + ".map";
	
	String lines = "";
	String n = "\r\n";
	lines += difficulty + n;
	lines += name + n;
	lines += "" + minHeight + " " + maxHeight + " " + minWidth + " " + maxWidth + n;
	
	lines += shapes.size() + n;
	
	for (PolyShape s : shapes)
		lines += s.toFile();
	
	lines += mobs.size() + n;
	for (Integer i : mobs.keySet())
			{
		Mob m = mobs.get(i);
		lines += m.toFile();
			}
	
	PlatipusClient.createFile(name,lines);
	
	System.out.println("file " + name + " was saved");
}

public String toSocketString()//doesn't include difficulty
{
	String out = "";
	
	String n = "+";
	
	out += name + n;
	out += "" + minHeight + " " + maxHeight + " " + minWidth + " " + maxWidth + n;
	
	out += shapes.size() + n;
	
	for (PolyShape s : shapes)
		out += s.toSocketString();
	
	out += mobs.size() + n;
	
	for (Integer i : mobs.keySet())
			{
		Mob m = mobs.get(i);
		out += m.toSocketString();
			}
	
	return out;
}

public static Integer pint(String x)
{
	return Integer.parseInt(x);
}

public static int[] spacedStringToIntArray(String s)
{
	String[] x = s.split(" ");
	int[] out = new int[x.length];
	
	for (int i = 0; i  < x.length; i++)
	{
		out[i] = pint(x[i]);
	}
	
	
	return out;
}

public PlatipusMap(boolean irrelevant, String line)//the constructor sent from the server to the client. the  boolean is just there just to overload.
{//doesnt include difficulty
	
shapes = new ArrayList<PolyShape>();
doors = new ArrayList<Door>();
mobs = new TreeMap<Integer, Mob>();
	Scanner scan = new Scanner(line).useDelimiter("[+]");
	
	this.name= scan.next().substring(1);
	
	int[] bounds = spacedStringToIntArray(scan.next());
	minHeight = bounds[0]; maxHeight = bounds[1]; minWidth = bounds[2]; maxWidth = bounds[3];

	int shapesLength = pint(scan.next());
	for (int i = 0; i < shapesLength; i++)
	{
		String[] cc = scan.next().split(" ");
		Color c = new Color(pint(cc[0]),pint(cc[1]),pint(cc[2]));
		boolean solid = Boolean.parseBoolean(scan.next());
		
		String className = scan.next();
		
		int[] xs = spacedStringToIntArray(scan.next());
		int[] ys = spacedStringToIntArray(scan.next());
		
		if (className.equalsIgnoreCase("Door"))
		{
			int direction = pint(scan.next());
			Door d = new Door(xs,ys,direction);
			d.color = c;
			d.solid = false;
			shapes.add(d);
			doors.add(d);
		}
		else if (className.equalsIgnoreCase("PolyShape"))
		{
			PolyShape s = new PolyShape(xs, ys);
			s.color = c;
			s.solid = solid;
			shapes.add(s);
		}
		else if (className.equalsIgnoreCase("Platform"))//same as polyshape basically
		{
			Platform p = new Platform(xs,ys);
			p.color = c;
			p.solid = solid;
			shapes.add(p);
		}
		else if (className.equalsIgnoreCase("ServiceLedge"))
		{
			ServiceLedge s = new ServiceLedge(xs,ys);
			s.color = c;
			s.solid = solid;
			shapes.add(s);
		}
		else if (className.equalsIgnoreCase("PointZone"))
		{
			int pointValue = pint(scan.next());
			String[] split = scan.next().split(" ");
			PointZone p = new PointZone(xs,ys,pointValue);
			p.respawnX = pint(split[0]);
			p.respawnY = pint(split[1]);
			p.color = c;
			p.solid = false;
			shapes.add(p);
		}
	}
	
	int numMobs = pint(scan.next());
	for (int i = 0; i < numMobs; i++)
	{
		String className = scan.next();
		if (className.equalsIgnoreCase("bloop"))
		{
			addMob(new Bloop(scan.next()));
		}
		if (className.equalsIgnoreCase("bunny"))
		{
			addMob(new Bunny(scan.next()));
		}
		if (className.equalsIgnoreCase("zomboo"))
			addMob(new Zomboo(scan.next()));
	}

	scan.close();

}

public PlatipusMap(String mapName)//reads from file with given map name -- only meant for the server
{
	this.name=mapName;
	
shapes = new ArrayList<PolyShape>();
doors = new ArrayList<Door>();
mobs = new TreeMap<Integer, Mob>();

try{
Scanner scan = new Scanner(new File(mapName));

difficulty = pint(scan.nextLine());
scan.nextLine();//skip this.name
int[] bounds = spacedStringToIntArray(scan.nextLine());
minHeight = bounds[0]; maxHeight = bounds[1]; minWidth = bounds[2]; maxWidth = bounds[3];

int shapesLength = pint(scan.nextLine());
for (int i = 0; i < shapesLength; i++)
{
	String[] cc = scan.nextLine().split(" ");
	Color c = new Color(pint(cc[0]),pint(cc[1]),pint(cc[2]));
	boolean solid = Boolean.parseBoolean(scan.nextLine());
	
	String className = scan.nextLine();
	
	int[] xs = spacedStringToIntArray(scan.nextLine());
	int[] ys = spacedStringToIntArray(scan.nextLine());
	
	if (className.equalsIgnoreCase("Door"))
	{
		int direction = pint(scan.nextLine());
		Door d = new Door(xs,ys,direction);
		d.color = c;
		d.solid = false;
		shapes.add(d);
		doors.add(d);
	}
	else if (className.equalsIgnoreCase("PolyShape"))
	{
		PolyShape s = new PolyShape(xs, ys);
		s.color = c;
		s.solid = solid;
		shapes.add(s);
	}
	else if (className.equalsIgnoreCase("Platform"))//same as polyshape
	{
		Platform p = new Platform(xs, ys);
		p.color = c;
		p.solid = solid;
		shapes.add(p);
	}
	else if (className.equalsIgnoreCase("ServiceLedge"))//same as platform
	{
		ServiceLedge s = new ServiceLedge(xs,ys);
		s.color = c;
		s.solid = solid;
		shapes.add(s);
	}
	else if (className.equalsIgnoreCase("PointZone"))
	{
		int pointValue = pint(scan.nextLine());
		String[] split = scan.nextLine().split(" ");
		PointZone p = new PointZone(xs,ys,pointValue);
		p.respawnX = pint(split[0]);
		p.respawnY = pint(split[1]);
		p.color = c;
		p.solid = false;
		shapes.add(p);
	}
}






int numMobs = pint(scan.nextLine());
for (int i = 0; i < numMobs; i++)
{
	String className = scan.nextLine();
	if (className.equalsIgnoreCase("bloop"))
		addMob(new Bloop(scan.nextLine()));
	if (className.equalsIgnoreCase("bunny"))
		addMob(new Bunny(scan.nextLine()));
	if (className.equalsIgnoreCase("zomboo"))
		addMob(new Zomboo(scan.nextLine()));
}


scan.close();
}catch(FileNotFoundException e){System.out.println("error: file " + mapName + " was not found");};

}
public void addMob(Mob m)
{
	m.ID = latestMobIndex;
	mobs.put(latestMobIndex, m);
	latestMobIndex++;
}

public void putMob(Integer i , Mob m)
{
	mobs.put(i, m);
}

public int latestMobIndex = 0;

public void defineDrawableShapes(double rw, double rh, int h, int hShift, int vShift)//defineAll
{
	for (PolyShape s : shapes)
		s.defineDrawableShape(rw, rh, h, hShift, vShift);
}

public String toServerGUIString()//for server console purposes
{
	String out = "" + name + "\r\n";
	out += "available doors:";
	for (int i = 0; i < doors.size(); i++)
	{
		out += i + " ";
	}
	
	return out;
	
}






}