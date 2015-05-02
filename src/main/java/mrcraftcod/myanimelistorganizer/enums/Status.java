package mrcraftcod.myanimelistorganizer.enums;

public enum Status
{
	WATCHING(1, "En cours"), COMPLETED(2, "Compl\351t\351"), ONHOLD(3, "En attente"), DROPPED(4, "Abandonn\351"), PLANNEDTOWATCH(6, "Pr\351vu");

	private final int ID;
	private final String name;

	Status(int ID, String name)
	{
		this.ID = ID;
		this.name = name;
	}

	public int getID()
	{
		return ID;
	}

	public String getName()
	{
		return name;
	}

	@Override
	public String toString()
	{
		return getName();
	}

	public static Status getByID(int ID)
	{
		for(Status status : Status.values())
			if(status.getID() == ID)
				return status;
		return PLANNEDTOWATCH;
	}
}
