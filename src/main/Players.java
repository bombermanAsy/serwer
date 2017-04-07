package main;

import java.awt.Point;

public class Players {
	private Point pos;
	
	public Players(int x, int y) {
		pos = new Point(x,y);
	}

	public Point getPos() {
		return pos;
	}

	public void setPos(Point pos) {
		this.pos = pos;
	}

	
	
}
