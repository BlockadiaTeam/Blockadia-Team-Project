package testDrivers;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.jbox2d.common.Vec2;

import utility.ElementPos;
import utility.Log;

import components.Block;
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

		Log.print("");

		//"Test case #5: test [public boolean equals(Object otherShape)]"
		Log.print("Test case #5: test [public boolean equals(Object otherShape)]");
		BlockShape shape1 = new BlockShape();
		BlockShape shape2 = new BlockShape();
		Log.print("2 shapes generated by default constructor: "+ shape1.equals(shape2));

		shape2.setShapeName("anotherName");
		Log.print("2 shapes with different names: "+ shape1.equals(shape2));

		shape1 = new BlockShape();
		shape2 = new BlockShape();
		shape2.setResolution(new Vec2(4,4));
		Log.print("2 shapes with different resolutions: "+ shape1.equals(shape2));
		
		shape1 = new BlockShape();
		shape2 = new BlockShape();
		Map<ElementPos,Color> shape_1 = new HashMap<ElementPos,Color>();
		Map<ElementPos,Color> shape_2 = new HashMap<ElementPos,Color>();
		shape_1.put(new ElementPos(), Color.black);
		shape_2.put(new ElementPos(0,1), Color.black);
		shape1.setShape(shape_1);
		shape2.setShape(shape_2);
		Log.print("2 shapes with different mapping keys but same map size: "+ shape1.equals(shape2));
		
		shape1 = new BlockShape();
		shape2 = new BlockShape();
		shape_1 = new HashMap<ElementPos,Color>();
		shape_2 = new HashMap<ElementPos,Color>();
		shape_1.put(new ElementPos(), Color.black);
		shape_1.put(new ElementPos(0,1), Color.black);
		shape_2.put(new ElementPos(), Color.black);
		shape1.setShape(shape_1);
		shape2.setShape(shape_2);
		Log.print("2 shapes with different map size: "+ shape1.equals(shape2));
		
		shape1 = new BlockShape();
		shape2 = new BlockShape();
		shape_1 = new HashMap<ElementPos,Color>();
		shape_2 = new HashMap<ElementPos,Color>();
		shape_1.put(new ElementPos(), Color.black);
		shape_1.put(new ElementPos(0,1), Color.black);
		shape_2.put(new ElementPos(), Color.black);
		shape_2.put(new ElementPos(0,1), Color.black);
		shape1.setShape(shape_1);
		shape2.setShape(shape_2);
		Log.print("2 shapes with same map size, same mapping keys, same values: "+ shape1.equals(shape2));
		
		shape1 = new BlockShape();
		shape2 = new BlockShape();
		shape_1 = new HashMap<ElementPos,Color>();
		shape_2 = new HashMap<ElementPos,Color>();
		shape_1.put(new ElementPos(), Color.black);
		shape_1.put(new ElementPos(0,1), Color.black);
		shape_2.put(new ElementPos(), Color.white);
		shape_2.put(new ElementPos(0,1), Color.black);
		shape1.setShape(shape_1);
		shape2.setShape(shape_2);
		Log.print("2 shapes with same map size, same mapping keys, different values: "+ shape1.equals(shape2));
		
		shape1 = new BlockShape();
		Block shape3 = new Block();
		Log.print("2 shapes with different type(One block one blockShape): "+ shape1.equals(shape3));
		
		Log.print("");

		//"Test case #6: test [clone()]"
		Log.print("Test case #6: test [clone()]");
		shape1 = new BlockShape();
		shape2 = shape1.clone();
		shape_1 = new HashMap<ElementPos,Color>();
		shape1.setShapeName("anotherName");
		Log.print("shape1 name:"+shape1.getShapeName());
		shape1.setResolution(new Vec2(4,4));
		Log.print("shape1 reso:"+shape1.getResolution().toString());
		shape_1.put(new ElementPos(), Color.black);
		shape_1.put(new ElementPos(0,1), Color.black);
		shape1.setShape(shape_1);
		if(shape1.getShape().isEmpty()){
			Log.print("shape1 map is empty");
		}else{
			Log.print("shape1 map is not empty");
		}
		
		shape2.setShapeName("anotherName2");
		Log.print("shape2 name:"+shape2.getShapeName());
		Log.print("shape2 reso:"+shape2.getResolution().toString());
		if(shape2.getShape().isEmpty()){
			Log.print("shape2 map is empty");
		}else{
			Log.print("shape2 map is not empty");
		}
		
		
	}
}
