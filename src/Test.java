import java.util.Scanner;


public class Test
{
	
	public static void main(String[]args)
	{
		/*try{
			File f = new File(".");
			
			File[] all = f.listFiles();
			
			for (File s : all)
				System.out.println(s.getName());
			
			
			ArrayList<File> maps = new ArrayList<File>();
			
			for (File s : all)
				if (s.getName().endsWith(".map"))
					maps.add(s);
			
			System.out.println("---- Valid Maps ----");
			
			for (File s : maps)
				System.out.println(s.getName());
			
			
			}catch(Exception e){e.printStackTrace();};*/
		
		try{
			Scanner scan = new Scanner("lol+hi+my+name is+steven")
				.useDelimiter("[+]");
			
			while(scan.hasNext())
				System.out.println(scan.next());
			
			
			scan.close();
		}catch(Exception e){e.printStackTrace();};
	}
}