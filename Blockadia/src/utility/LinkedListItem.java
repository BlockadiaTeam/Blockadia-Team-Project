package utility;

public class LinkedListItem<E> {

  private E element;
  private LinkedListItem<E> next;
  
  public LinkedListItem(){
	setElement(null);
	setNext(null);
  }
  
  public LinkedListItem(E element){
	this.setElement(element);
	setNext(null);
  }
  
  public LinkedListItem(E element, LinkedListItem<E> next){
	this.element = element;
	this.next = next;
  }

  public E getElement() {
	return element;
  }

  public void setElement(E element) {
	this.element = element;
  }

  public LinkedListItem<E> getNext() {
	return next;
  }

  public void setNext(LinkedListItem<E> next) {
	this.next = next;
  }
}
