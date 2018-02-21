package src.model.gen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

import src.model.board.Case;
import src.model.board.EndCase;
import src.model.board.LineWall;
import src.model.board.LineWallUtils;
import src.model.board.StartCase;
import src.model.ContentMaze;
import src.model.gen.Algo;
import src.model.gen.ContentMazeFactory;
import src.model.gen.RectMaze;
import src.model.gen.RectMazeShift;
import src.utils.DisplayMazeConsole;

public class AlgoBackTracker extends Algo
{
	private static final boolean DEBUG_MODE = false;

	public AlgoBackTracker(int y, int x)
	{
		super();
		this.cmfactory = new ContentMazeFactory();

		RectMaze rm = buildRect(y, x);

		RectMazeShift rms = new RectMazeShift(rm, 0, 0);

		this.cmfactory.addContentMazeShift(rms);

		this.cmfactory.initiateSpecialCases();
		this.cmfactory.normalize();
		this.cm = new ContentMaze(this.cmfactory.getFinalSpecialCases(), this.cmfactory.getFinalLineWall());
	}

	public RectMaze buildRect(int size_y, int size_x)
	{
		RectMaze rm;

		LineWall[] listWalls;

		listWalls = this.buildWalls(size_x, size_y);
		rm        = new RectMaze(new ContentMaze(new Case[0], listWalls), size_x, size_y);
		return (rm);
	}

	private LineWall[] buildWalls(int size_x, int size_y)
	{
		HashMap <Integer, ArrayList <LineWall> > list_X = new HashMap <>();
		HashMap <Integer, ArrayList <LineWall> > list_Y = new HashMap <>();
		ArrayList <Point> unvisited_cases        = new ArrayList <>();
		ArrayList <Point> neighborhoods          = null;
		Point             current_position_agent = null;
		Point             ptActu = null;
		Point             res    = null;
		Point             next   = null;
		Random            ran    = new Random();

		// Initialize Walls : Fill it
		for (int i = 0; i <= size_y; i++)
		{
			ArrayList <LineWall> tmp = new ArrayList <>();
			tmp.add(LineWall.buildDirectedLineWall(true, 0, size_x, i));
			list_X.put(i, tmp);
		}

		for (int j = 0; j <= size_x; j++)
		{
			ArrayList <LineWall> tmp = new ArrayList <>();
			tmp.add(LineWall.buildDirectedLineWall(false, 0, size_y, j));
			list_Y.put(j, tmp);
		}

		for (int i = 0; i < size_y; i++)
		{
			for (int j = 0; j < size_x; j++)
			{
				unvisited_cases.add(new Point(i, j));
			}
		}

		current_position_agent = Point.getArrayList(unvisited_cases, ran.nextInt(size_y), ran.nextInt(size_x));
		unvisited_cases.remove(current_position_agent);

		// Recursive Backtracking Algorithm

		while (unvisited_cases.isEmpty() == false)
		{
			neighborhoods = new ArrayList <>();

			if ((ptActu = Point.getArrayListLeft(unvisited_cases, current_position_agent)) != null)
			{
				neighborhoods.add(ptActu);
			}
			if ((ptActu = Point.getArrayListRight(unvisited_cases, current_position_agent)) != null)
			{
				neighborhoods.add(ptActu);
			}
			if ((ptActu = Point.getArrayListUp(unvisited_cases, current_position_agent)) != null)
			{
				neighborhoods.add(ptActu);
			}
			if ((ptActu = Point.getArrayListDown(unvisited_cases, current_position_agent)) != null)
			{
				neighborhoods.add(ptActu);
			}

			if (neighborhoods.isEmpty())
			{
				current_position_agent = Point.getRandomWhenNoNeighboors(unvisited_cases, size_y, size_x, ran);
				continue;
			}

			next = neighborhoods.get(ran.nextInt(neighborhoods.size()));
			unvisited_cases.remove(next);
			Point.removeWalls(list_Y, list_X, current_position_agent, next);
			current_position_agent = next;

			if (DEBUG_MODE)
			{
				DisplayMazeConsole.displayMaze(new ContentMaze(null, buildFinal(list_X, list_Y)), false);
				try
				{
					Thread.sleep(50);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					System.exit(1);
				}
			}
		}

		return (buildFinal(list_Y, list_X));
	}

	private LineWall[] buildFinal(HashMap <Integer, ArrayList <LineWall> > list1, HashMap <Integer, ArrayList <LineWall> > list2)
	{
		ArrayList <LineWall> res = new ArrayList <>();

		Collection <ArrayList <LineWall> > content_y = list1.values();
		Object[] content_y2 = content_y.toArray();
		for (Object obj : content_y2)
		{
			res.addAll((ArrayList <LineWall> )obj);
		}

		Collection <ArrayList <LineWall> > content_x = list2.values();
		Object[] content_x2 = content_x.toArray();
		for (Object obj : content_x2)
		{
			res.addAll((ArrayList <LineWall> )obj);
		}
		return (res.toArray(new LineWall[0]));
	}

	private static class Point
	{
		public int y;
		public int x;

		public Point(int y, int x)
		{
			this.y = y;
			this.x = x;
		}

		public String toString()
		{
			return ("(" + x + ", " + y + ")");
		}

		public boolean onSameRow(Point pt)
		{
			return (this.y == pt.y);
		}

		public static Point getArrayList(ArrayList <Point> arr, int y, int x)
		{
			for (Point pt : arr)
			{
				if (pt.y == y && pt.x == x)
				{
					return (pt);
				}
			}
			return (null);
		}

		public static Point getArrayListLeft(ArrayList <Point> arr, Point pt)
		{
			return (Point.getArrayList(arr, pt.y, pt.x - 1));
		}

		public static Point getArrayListRight(ArrayList <Point> arr, Point pt)
		{
			return (Point.getArrayList(arr, pt.y, pt.x + 1));
		}

		public static Point getArrayListUp(ArrayList <Point> arr, Point pt)
		{
			return (Point.getArrayList(arr, pt.y - 1, pt.x));
		}

		public static Point getArrayListDown(ArrayList <Point> arr, Point pt)
		{
			return (Point.getArrayList(arr, pt.y + 1, pt.x));
		}

		public static LineWall getBetweenPoints(Point pt1, Point pt2)
		{
			boolean horizontal;

			horizontal = pt1.onSameRow(pt2);
			if (horizontal)
			{
				if (pt1.x > pt2.x)
				{
					return (getBetweenPoints(pt2, pt1));
				}
				return (new LineWall(pt2.x, pt2.y, pt2.x, pt2.y + 1));
			}
			if (pt1.y > pt2.y)
			{
				return (getBetweenPoints(pt2, pt1));
			}
			return (new LineWall(pt2.x, pt2.y, pt2.x + 1, pt2.y));
		}

		public static void removeWalls(HashMap <Integer, ArrayList <LineWall> > list_walls_Y, HashMap <Integer, ArrayList <LineWall> > list_walls_X, Point pt1, Point pt2)
		{
			HashMap <Integer, ArrayList <LineWall> > list_walls;
			ArrayList <LineWall> list;
			LineWall             toRemove;
			boolean isHorizontal;

			isHorizontal = pt1.onSameRow(pt2);
			if (isHorizontal == false)
			{
				list_walls = list_walls_X;
			}
			else
			{
				list_walls = list_walls_Y;
			}
			toRemove = Point.getBetweenPoints(pt1, pt2);
			list     = list_walls.get(toRemove.isHorizontal() ? toRemove.getY1() : toRemove.getX1());
			if (list == null)
			{
				return;
			}
			for (int i = 0; i < list.size(); i++)
			{
				LineWall             lw       = list.get(i);
				ArrayList <LineWall> excepted = LineWallUtils.except(lw, toRemove);
				if ((excepted.size() == 1 && excepted.get(0).equals(lw)) == false)
				{
					list.remove(lw);
					list.addAll(excepted);
					return;
				}
			}
		}

		public static Point getRandomWhenNoNeighboors(ArrayList <Point> unvisited_cases, int size_y, int size_x, Random ran)
		{
			Point res;
			Point tmp;

			tmp = unvisited_cases.get(ran.nextInt(unvisited_cases.size()));
			if (tmp.y > 0 && getArrayListUp(unvisited_cases, tmp) == null)
			{
				return (new Point(tmp.y - 1, tmp.x));
			}
			else if (tmp.x > 0 && getArrayListLeft(unvisited_cases, tmp) == null)
			{
				return (new Point(tmp.y, tmp.x - 1));
			}
			else if (tmp.y < size_y - 1 && getArrayListDown(unvisited_cases, tmp) == null)
			{
				return (new Point(tmp.y + 1, tmp.x));
			}
			else if (tmp.x < size_x - 1 && getArrayListRight(unvisited_cases, tmp) == null)
			{
				return (new Point(tmp.y, tmp.x + 1));
			}
			return (getRandomWhenNoNeighboors(unvisited_cases, size_y, size_x, ran));
		}
	}
}