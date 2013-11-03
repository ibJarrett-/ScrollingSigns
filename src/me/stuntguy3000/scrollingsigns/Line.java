package me.stuntguy3000.scrollingsigns;

public class Line {
	
	private String fullText;
	private String signText;
	private int currentPos = 0;
	
	public Line(String fullText) {
		this.fullText = fullText;
	}
	
	public String getFullText() {
		return this.fullText;
	}
	
	public String getSignText() {
		return this.signText;
	}
	
	public void setFullText(String fullText) {
		this.fullText = fullText;
	}
	
	public void setSignText(String signText) {
		this.signText = signText;
	}
	
	public String next() {
		if (getFullText() == null)
			return null;
		
		if (getFullText().length() < 16)
			return getFullText();
		
		if (currentPos > getFullText().length())
			currentPos = 0;
		
		String text = getFullText();
		
		text = text.substring(currentPos);
		
		
		this.currentPos = currentPos + 1;
		
		return text;
	}
}
