package model;

public abstract class OverlayItem {
	
	double X;
	double Y;
	double Z;

	public OverlayItem(final Double x, final Double y, final Double z) {
		if(x!=null)
			this.X = x;
		if(y!=null)
			this.Y = y;
		if(z!=null)
			this.Z = z;
	}

	public double getX() {
		return X;
	}

	public void setX(final double x) {
		X = x;
	}

	public double getY() {
		return Y;
	}

	public void setY(final double y) {
		Y = y;
	}
	
	public double getZ() {
		return Z;
	}

	public void setZ(final double z) {
		Z = z;
	}

}
