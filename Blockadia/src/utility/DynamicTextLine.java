package utility;

import rules.Spacecraft.TextLine;
import exceptions.ElementNotExistException;

/**This is basically just a customized Doubly-head linked-list*/
@SuppressWarnings("serial")
public class DynamicTextLine {

  /**Max lines of text to display*/
  public static final int MAX_LINE = 9;

  private LinkedListItem<TextLine> head;
  private LinkedListItem<TextLine> tail;

  private int size;
  
  public DynamicTextLine(){
	head = null;
	tail = null;
	setSize(0);
  }

  public LinkedListItem<TextLine> getHead(){
	return head;
  }

  public LinkedListItem<TextLine> getTail(){
	return tail;
  }
  
  public int getSize() {
	return size;
  }

  public void setSize(int size) {
	this.size = size;
  }

  public void addAtBack(TextLine newText){
	LinkedListItem<TextLine> item = new LinkedListItem<TextLine>();
	item.setElement(newText);
	
	if (head == null) {
	  item.getElement().setTextline(20);
	  
	  head = item;
	  tail = item;
	}
	else{
	  int oldLastLine = tail.getElement().getTextline();
	  int newLastLine = oldLastLine + 15;
	  item.getElement().setTextline(newLastLine);
	  
	  tail.setNext(item);
	  tail = item;
	}
	size++;
  }
  
  public void remove(TextLine textline) throws ElementNotExistException{
	LinkedListItem<TextLine> current = head;
	LinkedListItem<TextLine> next;
	
	if(head == null){
	  throw new ElementNotExistException("No textline available");
	}
	
	if(head.getNext() == null){
	  if(head.getElement().equals(textline)){
		head = tail = null;
		size--;
		return;
	  }
	  else{
		throw new ElementNotExistException("No textline available");
	  }
	}
	else{
	  if(head.getElement().equals(textline)){
		head = head.getNext();
		size--;
		for(LinkedListItem<TextLine> curr = head; curr != null ; curr = curr.getNext()){
		  curr.getElement().setTextline(curr.getElement().getTextline()-15);
		}
		return;
	  }
	}
	
	for(next = head.getNext(); next != null; next = next.getNext()){
	  TextLine nextText = next.getElement();
	  if(nextText.equals(textline)){
		current.setNext(next.getNext());
		if(next.getNext() == null){
		  tail = current;
		}
		size--;
		for(LinkedListItem<TextLine> curr = current.getNext(); curr != null ; curr = curr.getNext()){
		  curr.getElement().setTextline(curr.getElement().getTextline()-15);
		}
		return;
	  }
	  else{
		current = current.getNext();
	  }
	}
  }

}
