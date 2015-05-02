package mrcraftcod.myanimelistorganizer.frames.components;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class DocumentLimitNumbers extends PlainDocument
{
	private static final long serialVersionUID = 1L;
	private final int limit;

	public DocumentLimitNumbers(int limit)
	{
		super();
		this.limit = limit;
	}

	@Override
	public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException
	{
		if(str.matches("\\d+") && (limit == 0 || Integer.parseInt(getText(0, getLength()) + str) <= limit))
			try
			{
				Integer.parseInt(str);
				super.insertString(offset, str, attr);
			}
			catch(Exception ignored)
			{
			}
	}
}