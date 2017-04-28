import java.io.File;
import java.util.ArrayList;



public class MapDecider
{
	// 5/25/14 added consideration of map difficulty
	
	ArrayList<PlatipusMap> maps;
	public MapDecider()
	{
		
		/*
		 * 
		 * List all of the level files to get their door info
		 */
		try{
			File fff = new File(".");
			File[] all = fff.listFiles();
			
			ArrayList<File> mapFiles = new ArrayList<File>();
			for (File s : all)
				if (s.getName().endsWith(".map"))
					mapFiles.add(s);
			
			maps = new ArrayList<PlatipusMap>();
			for (File f : mapFiles)
				maps.add(new PlatipusMap(f.getName()));
			
			
			
			
			}catch(Exception e){e.printStackTrace();};
		
			
			
			
			
	}
	
	public static int getDifficulty (int depth)
	{
		if (depth <= 5)
			return 1;
		if (depth > 5 && depth <= 15)
			return 2;
		if (depth > 15)
			return 3;
		
		return -1;
		
	}
	String choiceFileName;
	int choiceDoorIndex;
	public void decide(int depth, int desiredDoorDirection)
	{
		if (depth%5 == 0)
		{
			choiceFileName = "saferoom.map";
			choiceDoorIndex = 0;
			return;
		}
		int desiredDifficulty = getDifficulty(depth);
		ArrayList<Integer> validLevelIndices = new ArrayList<Integer>();
		
		for (int i = 0; i < maps.size(); i++)
		{
			if (maps.get(i).difficulty == desiredDifficulty && !maps.get(i).name.equals("saferoom.map"))
			for (int c = 0; c < maps.get(i).doors.size(); c++)
			{
				Door d = maps.get(i).doors.get(c);
				if (d.direction == desiredDoorDirection)
				{
					validLevelIndices.add(i);
					break;//so it doesn't add the same level twice.
				}
			}
		}
		
		
		int chosenLevelIndex = (int)(Math.random() * validLevelIndices.size());
		choiceFileName = maps.get(validLevelIndices.get(chosenLevelIndex)).name;
		
		//now that i know which level i want to use, which door do i want to use?
		
		PlatipusMap pm = new PlatipusMap(true, maps.get(validLevelIndices.get(chosenLevelIndex)).toSocketString());
		
		ArrayList<Integer> validDoorIndices = new ArrayList<Integer>();
		
		
		int di = -1;//door index
		for (int i = 0; i < pm.shapes.size(); i++)
		{
			PolyShape s = pm.shapes.get(i);
			if (s instanceof Door)
			{
				di++;
				Door d = (Door)s;
				
				if (d.direction == desiredDoorDirection)
					validDoorIndices.add(di);
				
			}
		}
		
		int ci = (int)(Math.random()*validDoorIndices.size());
		
		choiceDoorIndex = validDoorIndices.get(ci);
		
		
		
		/*
		 * the choice door index is relative to a single level, rather than absolute of all of the level arrays.
		 * i think i will make it relative in all code, because it just seems to make more sense.
		 */
	}
	
	
	public String getChoiceName()//must decide(i) first -- returns the map name so i can reload it in my main file.
	{//called by the class holding the MapDecider
		return choiceFileName;
	}
	
	public int getChoiceDoorIndex()//must decide(i) first -- which door am i entering in to?
	{//called by the class holding the MapDecider
		return choiceDoorIndex;
	}
	
	
	
	
	
	
	
	
	
	
	
}