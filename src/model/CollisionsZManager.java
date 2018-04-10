package src.model;

import src.model.MazeDimension;
import src.model.Player;

import java.lang.Math;

public class CollisionsZManager
{
	private Player p;
	private float zMove;
	private ContentMaze [] cms;
	private int highLevel;
	private boolean isFlyMode;


	public CollisionsZManager(Player p, boolean isFlyMode)
	{
		this.p         = p;
		this.isFlyMode = isFlyMode;
	}

	public void updateFloor(ContentMaze [] cms, int lvl)
	{
		this.cms       = cms;
		this.highLevel = lvl;
	}

	public float getMove()
	{
		return (this.zMove);
	}

	public float getNorm()
	{
		return (this.zMove);
	}

	public void updateMove(float dz)
	{
		/*	Il faut mettre a la place le test des murs au dessus
		 * if (dz > 0 && cms[1] != null && this.p.getPosZ() + this.p.getHitBoxZ() + dz > highLevel + 1 && (this.isFloor(cms[1]))
		 * {
		 *  this.zMove = (highLevel + 1) - this.p.getHitBoxZ() - this.p.getPosZ();
		 * }
		 *  Faire le test des murs en dessous si il n'y a pas de sol
		 * else*/if (dz < 0 && this.p.getPosZ() - this.p.hitBoxBottom >= highLevel && this.isFloor(cms[0]))
		{
			this.zMove = (this.p.getPosZ() - this.p.hitBoxBottom + dz < highLevel) ? highLevel - (this.p.getPosZ() - this.p.hitBoxBottom) : dz;
		}
		else
		{
			this.zMove = dz;
		}
	}

	public boolean isFloor(ContentMaze cm)
	{
		return (cm.isFloor(this.p.getPosX(), this.p.getPosY()));
	}

	public boolean isOnFloor()
	{
		return (this.isFloor(cms[0]) && Math.abs(this.p.getPosZ() - this.p.hitBoxBottom - highLevel) < 10e-4);
	}
}
