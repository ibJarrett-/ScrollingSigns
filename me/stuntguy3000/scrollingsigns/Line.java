package me.stuntguy3000.scrollingsigns;


public class Line {

	private String fullText;
	private String prefix = "";
	private int currentPos = 0;

	public Line(String fullText) {
		this.fullText = fullText;
	}

	public String getFullText() {
		return this.fullText;
	}

	public void setFullText(String fullText) {
		this.fullText = fullText;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String next() {
		if (getFullText() == null)
			return null;

		if (getFullText().length() < 16)
			return getFullText();

		if (currentPos > getFullText().length() )
			currentPos = 0;

		String text = getFullText();

		text = text.substring(currentPos);

		this.currentPos = currentPos + 1;
		
		if (text.startsWith("&")) {
			setPrefix(text.substring(0, 2));
			
			this.currentPos = currentPos + 2;
			
			if (currentPos > getFullText().length() )
				currentPos = 0;

			text = getFullText();
			text = text.substring(currentPos);
		}
		
		return prefix + text;
	}
}
