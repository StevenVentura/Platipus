import java.util.ArrayList;

public class Grid
{
public double width, height;
public int numRows, numCols;//, numHCells, numVCells;
public ArrayList<PolyShape>[][] field;

public double cellWidth, cellHeight;

public double negativeMinimumWidth = 0;
public double negativeMinimumHeight = 0;
//width height, numrows, numcolumns
public Grid(double w, double h, int r, int c,int negativeMinimumWidth, int negativeMinimumHeight)
{
width = w; height = h; numRows = r; numCols = c;
this.negativeMinimumWidth = negativeMinimumWidth;
this.negativeMinimumHeight = negativeMinimumHeight;

field = (ArrayList<PolyShape>[][])new ArrayList[numRows][numCols];

for (int R = 0; R < numRows; R++)
{
for (int C = 0; C < numCols; C++)
{
field[R][C] = new ArrayList<PolyShape>();
}
}

cellWidth = width / numCols;
cellHeight = height / numRows;

}

/*public void clearAll()
{
for (int r = 0; r < numRows; r++)
{
for (int c = 0; c < numCols; c++)
{
field[r][c].clear();
}
}

}*/


public void addToCell(PolyShape addMe, int row, int column)
{
if (row < numRows && column < numCols && row > -1 && column > -1)
if (!field[row][column].contains(addMe))
field[row][column].add(addMe);
}
public int getRow(double y)
{
	y -= negativeMinimumHeight;
return (int)(y / cellHeight);
}
public int getCol(double x)
{
	x -= negativeMinimumWidth;
return (int)(x / cellWidth);
}


public String toString()
{
	String out = "";
	out += "width="+width+"height="+height+"numRows="+numRows+"numCols="+numCols+"cellWidth="+cellWidth+"cellHeight="+cellHeight+"negminW="+negativeMinimumWidth+"negminH="+negativeMinimumHeight
			 + "";
	return out;
}


}