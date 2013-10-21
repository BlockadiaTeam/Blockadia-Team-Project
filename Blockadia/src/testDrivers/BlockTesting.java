package testDrivers;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import utility.ElementPos;
import utility.Log;

import components.Block;
import components.BlockShape;

public class BlockTesting {

  public static void main(String[] args) {

	//"Test case #1: test [convert a blockShape into a block]"
	Log.print("Test case #1: test [convert a blockShape into a block]");
	BlockShape shape = new BlockShape();
	shape.setShapeName("newBlock");
	shape.setResolution(new Vec2(40,40));
	Map<ElementPos,Color> elements = new HashMap<ElementPos,Color>();
	try {//normal set
	  shape.setShapeElement(Color.black, 0, 1);
	} catch (IllegalArgumentException e) {
	  Log.print(e.getMessage());
	}		
	try {//overwrite set
	  shape.setShapeElement(Color.gray, 0, 0);
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
	Log.print("Now convert it into a Block");
	Block block= shape.cloneToBlock();
	Log.print("First test its name: "+ block.getBlockName());
	Log.print("Then test its resolution: "+ block.getResolution().toString());
	Log.print("Finally test its shape: ");
	if(shape.getShape().isEmpty()){
	  Log.print("Block's shape is empty");
	}else{
	  Log.print("Block's shape has element:");
	  for(Map.Entry<ElementPos, Color> entry: elements.entrySet()){
		Log.print("At position ("+entry.getKey().row+","+entry.getKey().col+"), there is a color:"+ entry.getValue().toString());
	  }
	}		

	Log.print("");

	//"Test case #2: test [public boolean equals(Object otherBlock)]"
	Log.print("Test case #2: test [public boolean equals(Object otherBlock)]");

	Log.print("Is block equal to its parent shape? "+block.equals(shape));
	Log.print("Is shaoe equal to its child block with same fields? "+shape.equals(block));

	Block block1 = new Block();
	Block block2 = new Block();
	Log.print("2 blocks by default constructors: "+block1.equals(block2));

	block1 = new Block();
	block1.setBlockName("anotherName");
	block2 = new Block();
	Log.print("2 blocks by different names: "+block1.equals(block2));

	block1 = new Block();
	block1.setPosInWorld(new Vec2(0,1));
	block2 = new Block();
	Log.print("2 blocks by different posInWorld: "+block1.equals(block2));

	block1 = new Block();
	block1.setSizeInWorld(new Vec2(79,80));
	block2 = new Block();
	Log.print("2 blocks by different sizeInWorld: "+block1.equals(block2));

	Log.print("");

	//"Test case #3: test [lowerBoundElement,upperBoundElement]"
	Log.print("Test case #3: test [lowerBoundElement,upperBoundElement]");
	block1 = new Block();
	block2 = new Block();
	Log.print("default constructor:");
	Log.print(block1.boundingBox().toString());
	Log.print(block2.boundingBox().toString());
	Log.print("one element:");
	try {
	  block1.setShapeElement(Color.black, 0, 0);
	} catch (IllegalArgumentException e) {
	  Log.print(e.getMessage());
	}	
	Log.print(block1.boundingBox().toString());
	Log.print(block2.boundingBox().toString());
	Log.print("two elements:");
	try {
	  block1.setShapeElement(Color.black, 0, 1);
	} catch (IllegalArgumentException e) {
	  Log.print(e.getMessage());
	}	
	Log.print(block1.boundingBox().toString());
	Log.print(block2.boundingBox().toString());
	Log.print("remove first element:");
	block1.removeShapeElement(0, 0);
	Log.print(block1.boundingBox().toString());
	Log.print(block2.boundingBox().toString());
	Log.print("remove second element:");
	block1.removeShapeElement(0, 1);
	Log.print(block1.boundingBox().toString());
	Log.print(block2.boundingBox().toString());
	Log.print("add in (1,1) and (2,2)");
	block1.setShapeElement(Color.black, 1, 1);
	block1.setShapeElement(Color.black, 2, 2);
	Log.print(block1.boundingBox().toString());
	Log.print(block2.boundingBox().toString());
	Log.print("test 8x8(size 80x80, pos 40x40)");
	block1.setResolution(new Vec2(8,8));
	block1.setShapeElement(Color.black, 1, 5);
	block1.setShapeElement(Color.black, 2, 7);
	Log.print(block1.boundingBox().toString());
	Log.print(block2.boundingBox().toString());

	Log.print("");

	//"Test case #4: test [getShapeRect()]"
	Log.print("Test case #4: test [getShapeRect()]");
	block1 = new Block();
	block1.setResolution(new Vec2(4,4));
	block1.setShapeElement(Color.black, 0, 0);
	block1.setShapeElement(Color.black, 1, 1);
	block1.setShapeElement(Color.black, 2, 1);
	block1.setShapeElement(Color.black, 3, 3);
	Map<Rectangle2D,Color> shapeRect = block1.getShapeRect();
	for(Map.Entry<Rectangle2D, Color> entry : shapeRect.entrySet()){
	  Log.print(entry.getKey().toString());
	}
	Log.print("Bounding box: "+block1.boundingBox().toString());

	Log.print("");

	//"Test case #5: test [createBlockInWorld()]"
	Log.print("Test case #5: test [createBlockInWorld()]");
	Vec2 gravity = new Vec2(0,-10f);
	World world = new World(gravity); 
	block1.createBlockInWorld(world);
  }
}
