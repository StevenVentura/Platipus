import java.awt.geom.Rectangle2D;


public class RectGridCell//used for adding the polyshapes to the grid, collision of polyshape lines and grid.
{
	int row, column;
	
	Rectangle2D.Double bounds;
	
	public RectGridCell(Grid g, int row, int col)
	{
		this.row = row;
		this.column = col;
		
		
		double minx = col*g.cellWidth + g.negativeMinimumWidth;
		
		double miny = row*g.cellHeight + g.negativeMinimumHeight;
		
		bounds = new Rectangle2D.Double(minx, miny, g.cellWidth, g.cellHeight);
		
		
		
		
		
	}
	
	
	
	
	
	
}