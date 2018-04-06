package src.model;

import java.util.HashSet;

import src.model.board.Case;
import src.model.board.LineWall;
import src.model.MazeDimension;
import src.utils.FloatVector;

/**
 * Player est la classe qui représente le joueur dans le labyrinthe.
 */
public class Player
{
	//Est on en mode ghost ?
	private boolean ghostMode = false;

	private float hitBoxCircle;
	private float hitBoxZ;
	private float posX;
	private float posY;
	private float posZ;
	private float horizontalAngle;
	private float verticalAngle;
	private boolean hasWin    = false;
	private float speed       = 1;
	private boolean isRunning = false;
	private long time;

	//Constante de déplacement (Angle)
	public final float rot = 2f;       // En degré

	//Constantes d'accélération et de vitesse max
	private static final float ACCELERATIONXY = 0.001f / 16000000.0f;
	private static final float VMAX           = 0.04f;
	private static final float PESANTEUR      = -0.015f / 320000000.0f;
	private static final float VELOCITYGHOST  = 0.02f;

	//Vitesse selon les axes/plan
	private float velocityXY;
	private float velocityZ;

	//Angle du dernier déplacement
	private int lastDepAngle;

	//Gestionnaires de Collisions
	private transient CollisionsXYManager xyCol = new CollisionsXYManager(this);
	private transient CollisionsZManager zCol   = new CollisionsZManager(this);

	//Ensemble de déplacements à faire
	public final HashSet <Directions> dirs = new HashSet <Directions>();

	public Player(float hitBoxCircle, float hitBoxZ, float posX, float posY, float posZ, float horizontalAngle, float verticalAngle)
	{
		this.hitBoxCircle    = hitBoxCircle;
		this.hitBoxZ         = hitBoxZ;
		this.posX            = posX;
		this.posY            = posY;
		this.posZ            = posZ;
		this.horizontalAngle = horizontalAngle;
		this.verticalAngle   = verticalAngle;
	}

	public boolean getGhostMode()
	{
		return (ghostMode);
	}

	public void setGhostMode(boolean b)
	{
		this.ghostMode = b;
	}

	public float getHitBoxCircle()
	{
		return (this.hitBoxCircle);
	}

	public float getHitBoxZ()
	{
		return (this.hitBoxZ);
	}

	public float getPosX()
	{
		return (this.posX);
	}

	public float getPosY()
	{
		return (this.posY);
	}

	public float getPosZ()
	{
		return (this.posZ);
	}

	public float getHorizontalAngle()
	{
		return (this.horizontalAngle);
	}

	public float getVerticalAngle()
	{
		return (this.verticalAngle);
	}

	public boolean getHasWin()
	{
		return (this.hasWin);
	}

	public float getSpeed()
	{
		return (this.speed);
	}

	public long getTime()
	{
		return (this.time);
	}

	public void updateTime(long l)
	{
		time += l;
	}

	public void setSpeed(float f)
	{
		speed = f;
	}

	public void setPosX(float x)
	{
		posX = x;
	}

	public void setPosY(float y)
	{
		posY = y;
	}

	public void addPosZ(float z)
	{
		posZ += z;
	}

	public void setWin(boolean b)
	{
		hasWin = b;
	}

	public void addHorizontalAngle(float x)
	{
		this.horizontalAngle += x;
	}

	public void addVerticalAngle(float y)
	{
		this.verticalAngle += y;
	}

	public boolean playerInCase(Case c)
	{
		float diff = 0.5f;

		return (Math.abs(posX - (c.getX() + diff)) < diff && Math.abs(posY - (c.getY() + diff)) < diff);
	}

	public void invertRun()
	{
		isRunning = !isRunning;
	}

	/**
	 * Transport the player to the specified case
	 * @param c The case
	 */
	public boolean goTo(Case c)
	{
		if (c != null)
		{
			posX = c.getX() + 0.5f;
			posY = c.getY() + 0.5f;
			return (true);
		}
		else
		{
			return (false);
		}
	}

	/**
	 * Update the player position with the collisions with walls
	 * @param m MainMaze
	 * @param time Time
	 */
	//public void update(LineWall [] lw, MazeDimension md, long time)
	public void update(MainMaze m, long time)
	{
		int angle       = 0;
		int nbDirPushed = 0;

		if (time <= 100000000)     //if not init
		{
			this.time += time;
		}

		for (Directions d: dirs)
		{
			switch (d)
			{
			case north: angle += 0; nbDirPushed++; break;

			case east: angle += 90; nbDirPushed++; break;

			case south: angle += 180; nbDirPushed++; break;

			case west: angle += -90; nbDirPushed++; break;

			case left: horizontalAngle -= rot;
				lastDepAngle           += rot;
				break;

			case right: horizontalAngle += rot;
				lastDepAngle            -= rot;
				break;

			case up: verticalAngle += rot; break;

			case down: verticalAngle -= rot; break;

			case goUp: if (ghostMode)
				{
					posZ += 0.05f;
				}
				break;

			case goDown: if (ghostMode)
				{
					posZ -= 0.05f;
				}
				break;

			case jump:  if (this.zCol.isOnFloor())
				{
					this.velocityZ = 1.5f * Math.max(0.015f, this.velocityXY * 7f / 20.0f) * (float)Math.sin(90 - this.velocityXY / VMAX * 45);
				}
				break;
			}
		}

		int            currentLevel = m.getCurrentLevel();
		ContentMaze [] cms          = { m.getContentMazeCurrentLevel(), m.getContentMaze(currentLevel + 1) };

		this.zCol.updateFloor(cms, currentLevel);
		this.xyCol.updateWalls(cms[0].getLineWalls());

		this.reallyMove();

		if (!this.zCol.isOnFloor() && !ghostMode)
		{
			this.velocityZ += (PESANTEUR * time);
		}
		else if (nbDirPushed != 0)
		{
			this.velocityXY   = Math.min(VMAX, this.velocityXY + ACCELERATIONXY * time);
			this.lastDepAngle = (this.dirs.contains(Directions.south) && this.dirs.contains(Directions.west)) ? -135 : (angle / nbDirPushed);
		}
		else
		{
			this.velocityXY = Math.max(0, this.velocityXY - ACCELERATIONXY * 2 * time);
		}
	}

	/**
	 * Really move the player
	 */
	private void reallyMove()
	{
		final double r1 = Math.toRadians(this.horizontalAngle + this.lastDepAngle);

		float speed = (this.isRunning) ? this.speed * 2 : this.speed;

		final float dx = (float)(Math.sin(r1) * this.velocityXY * this.speed);
		final float dy = (float)(Math.cos(r1) * this.velocityXY * this.speed);

		FloatVector d = new FloatVector(dx, dy);

		if (ghostMode)
		{
			this.applyMove(d, 0);
		}
		else
		{
			this.xyCol.updateMove(d);
			this.velocityXY = this.xyCol.getNorm();

			this.zCol.updateMove(this.velocityZ);
			this.velocityZ = this.zCol.getNorm();

			this.applyMove(this.xyCol.getMove(), this.zCol.getMove());
		}
	}

	/**
	 * Déplace le joueur à la position (x + v.getX(), y + v.getY())
	 * @param v Vecteur de déplacement
	 */
	public void applyMove(FloatVector v, float dz)
	{
		this.posX += v.getX();
		this.posY += v.getY();
		this.posZ += dz;
	}

	public void setVelocityZ(float f)
	{
		this.velocityZ = f;
	}

	public String toString()
	{
		return ("posX = " + posX + " posY = " + posY + " other things");
	}
}
