package testDrivers;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import utility.ElementPos;
import utility.Log;

import components.BlockShape;

public class BlockShapeTesting {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//"Test case #1: test [getGameShape(String shapeName)]"
		Log.print("Test case #1: test [public BlockShape()]");
		BlockShape shape = new BlockShape();
		Log.print("BlockShape shape = new BlockShape();");
		Log.print("BlockShape shape has name: "+shape.getShapeName());
		if(shape.getShape().isEmpty()){
			Log.print("BlockShape shape is empty");
		}else{
			Log.print("BlockShape shape has element");
		}
		
		Log.print("");
		
		//"Test case #2: test [public BlockShape(final String shapeName)]"
		Log.print("Test case #2: test [public BlockShape(final String shapeName)]");
		shape = new BlockShape("newShape");
		Log.print("shape = new BlockShape(\"newShape\");");
		Log.print("BlockShape shape has name: "+shape.getShapeName());
		if(shape.getShape().isEmpty()){
			Log.print("BlockShape shape is empty");
		}else{
			Log.print("BlockShape shape has element");
		}
		
		Log.print("");
		
		//"Test case #3: test [public BlockShape(final String shapeName, final Map<ElementPos,Color> shape)]"
		Log.print("Test case #3: test [public BlockShape(final String shapeName, final Map<ElementPos,Color> shape)]");
		Map<ElementPos,Color> elements = new HashMap<ElementPos,Color>();
		elements.put(new ElementPos(), Color.green);
		elements.put(new ElementPos(1,1), Color.blue);
		shape = new BlockShape("newShapeWithElement",elements);
		Log.print("shape = new BlockShape(\"newShapeWithElement\",elements);");
		Log.print("BlockShape shape has name: "+shape.getShapeName());
		if(shape.getShape().isEmpty()){
			Log.print("BlockShape shape is empty");
		}else{
			Log.print("BlockShape shape has element:");
			for(Map.Entry<ElementPos, Color> entry: elements.entrySet()){
				Log.print("At position ("+entry.getKey().row+","+entry.getKey().col+"), there is a color:"+ entry.getValue().toString());
			}
		}

		Log.print("");
		
		//"Test case #4: test [setShapeElement(final Color newColor,int row,int col)]"
		Log.print("Test case #4: test [setShapeElement(final Color newColor,int row,int col)]");
		try {//normal set
			shape.setShapeElement(Color.black, 0, 1);
		} catch (IllegalArgumentException e) {
			Log.print(e.getMessage());
		}		
		Log.print("shape.setShapeElement(Color.black, 0, 1);");
		elements = shape.getShape();
		if(shape.getShape().isEmpty()){
			Log.print("BlockShape shape is empty");
		}else{
			Log.print("BlockShape shape has element:");
			for(Map.Entry<ElementPos, Color> entry: elements.entrySet()){
				Log.print("At position ("+entry.getKey().row+","+entry.getKey().col+"), there is a color:"+ entry.getValue().toString());
			}
		}
		try {//overwrite set
			shape.setShapeElement(Color.gray, 0, 0);
		} catch (IllegalArgumentException e) {
			Log.print(e.getMessage());
		}		
		Log.print("shape.setShapeElement(Color.gray, 0, 0);");
		elements = shape.getShape();
		if(shape.getShape().isEmpty()){
			Log.print("BlockShape shape is empty");
		}else{
			Log.print("BlockShape shape has element:");
			for(Map.Entry<ElementPos, Color> entry: elements.entrySet()){
				Log.print("At position ("+entry.getKey().row+","+entry.getKey().col+"), there is a color:"+ entry.getValue().toString());
			}
		}		
		Log.print("shape.setShapeElement(null, 0, 0);");
		try {//trigger IllegalArgumentException
			shape.setShapeElement(null, 0, 0);
		} catch (IllegalArgumentException e) {
			Log.print(e.getMessage());
		}		
		elements = shape.getShape();
		if(shape.getShape().isEmpty()){
			Log.print("BlockShape shape is empty");
		}else{
			Log.print("BlockShape shape has element:");
			for(Map.Entry<ElementPos, Color> entry: elements.entrySet()){
				Log.print("At position ("+entry.getKey().row+","+entry.getKey().col+"), there is a color:"+ entry.getValue().toString());
			}
		}

	}
}
