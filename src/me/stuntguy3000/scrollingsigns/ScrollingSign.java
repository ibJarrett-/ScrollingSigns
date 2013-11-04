package me.stuntguy3000.scrollingsigns;

public class ScrollingSign {
	
	private int x;
	private int y;
	private int z;
	private String world;
	
	private Line l1;
	private Line l2;
	private Line l3;
	private Line l4;
	
	public ScrollingSign(int x, int y, int z, String world, Line l1, Line l2, Line l3, Line l4) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
		
		this.l1 = l1;
		this.l2 = l2;
		this.l3 = l3;
		this.l4 = l4;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	public String getWorld() {
		return world;
	}
	
	public Line getLine1() {
		return l1;
	}
	
	public Line getLine2() {
		return l2;
	}
	
	public Line getLine3() {
		return l3;
	}
	
	public Line getLine4() {
		return l4;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setZ(int z) {
		this.z = z;
	}
	
	public void setWorld(String world) {
		this.world = world;
	}
	
	public void setLine1(Line l1) {
		this.l1 = l1;
	}
	
	public void setLine2(Line l2) {
		this.l2 = l2;
	}
	
	public void setLine3(Line l3) {
		this.l3 = l3;
	}
	
	public void setLine4(Line l4) {
		this.l4 = l4;
	}
}
