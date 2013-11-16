package testDrivers;

import rules.Spacecraft.TextLine;
import utility.DynamicTextLine;
import utility.LinkedListItem;
import utility.Log;
import exceptions.ElementNotExistException;

public class DynamicTextLineTest {


  /**
   * @param args
   */
  public static void main(String[] args) {

	//"Test case #1: test [addAtBack(TextLine newText)]"
	Log.print("Test case #1: test [addAtBack(TextLine newText)]");
	DynamicTextLine test = new DynamicTextLine();
	test.addAtBack(new TextLine("TestLine1"));
	test.addAtBack(new TextLine("TestLine1"));
	test.addAtBack(new TextLine("TestLine1"));
	test.addAtBack(new TextLine("TestLine1"));
	test.addAtBack(new TextLine("TestLine1"));
	LinkedListItem<TextLine> current;

	for(current = test.getHead(); current != null; current = current.getNext()){
	  Log.print(current.getElement().toString());
	}
	Log.print("");

	//"Test case #2: test [remove(TextLine textline)]"
	Log.print("Test case #2: test [remove(TextLine textline)]");
	try {
	  TextLine testline = new TextLine("TestLine1");
	  test.remove(testline);
	} catch (ElementNotExistException e) {
	  e.printStackTrace();
	}

	for(current = test.getHead(); current != null; current = current.getNext()){
	  Log.print(current.getElement().toString());
//	  Log.print("Head: "+ test.getHead().getElement().toString());
//	  Log.print("Tail: "+ test.getTail().getElement().toString());
	}
	Log.print("");
	
	Log.print("Add again");
	test.addAtBack(new TextLine("TestLine1"));
	for(current = test.getHead(); current != null; current = current.getNext()){
	  Log.print(current.getElement().toString());
	}
  }


}
