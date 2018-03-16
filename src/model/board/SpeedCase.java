package src.model.board;

public class SpeedCase extends Case
{
	private float speedModif;
	private boolean activated = false;

	public SpeedCase(int x, int y, float val)
	{
		super(x, y);
		this.speedModif = val;
	}

	public float getSpeedModif()
	{
		return (this.speedModif);
	}

	public SpeedCase clone()
	{
		return (new SpeedCase(this.x, this.y, this.speedModif));
	}

	public boolean isActivated()
	{
		return (this.activated);
	}

	public void activate()
	{
		activated = true;
	}

	public String toString()
	{
		return ("SpeedCase\n" + super.toString() + "Speed modification : " + this.speedModif);
	}

	public TypeCase getTypeCase()
	{
		return (TypeCase.SPEED);
	}
}