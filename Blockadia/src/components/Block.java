package components;

import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import utility.BlockSettings;

/**
 * This class represents the blocks to be put on the game board
 * The block is a blockshape with extra fields to identify its position,
 * size and physics settings
 * 
 * @author alex.yang
 * */
public class Block extends BlockShape{

	public static final Vec2 DEFAULT_SIZE_ON_SCREEN =new Vec2(80f,80f);
	public static final Vec2 DEFAULT_SIZE_IN_WORLD =new Vec2(80f,80f);
	public static final Vec2 DEFAULT_POS_ON_SCREEN = new Vec2();
	public static final Vec2 DEFAULT_POS_IN_WORLD = new Vec2();

	private final BlockSettings settings;
	private String blockName;
	private Vec2 sizeInWorld;																				//sizeInWorld = sizeOnScreen*cachedCameraScale
	//private Vec2 sizeOnScreen;																			//sizeOnScreen can be set
	private Vec2 posInWorld;																				//posInWorld might need to use ViewportTransform
	//private Vec2 posOnScreen;																				//posOnScreen can be set
	
	public Block(){
		this(BlockShape.DEFAULT_NAME,DEFAULT_SIZE_IN_WORLD,DEFAULT_POS_IN_WORLD);
	}
	
	public Block(final String blockName){
		this(blockName,DEFAULT_SIZE_IN_WORLD,DEFAULT_POS_IN_WORLD);
	}
	
	public Block(final String blockName, final Vec2 sizeInWorld, final Vec2 posInWorld){
		super();
		this.blockName = blockName;
		this.setSizeInWorld(sizeInWorld.clone());
		this.setPosInWorld(posInWorld.clone());
		this.settings = new BlockSettings();				//Automatically populates the default block settings
	}

	@Override
	public void setShapeName(String shapeName){
		super.setShapeName(shapeName);
		this.setBlockName(shapeName);
	}

	public BlockSettings getSettings() {
		return settings;
	}

	public String getBlockName() {
		return blockName;
	}

	public void setBlockName(String blockName) {
		this.blockName = blockName;
	}

	public Vec2 getSizeInWorld() {
		return sizeInWorld;
	}

	public void setSizeInWorld(Vec2 sizeInWorld) {
		this.sizeInWorld = sizeInWorld;
	}

	public Vec2 getPosInWorld() {
		return posInWorld;
	}

	public void setPosInWorld(Vec2 posInWorld) {
		this.posInWorld = posInWorld;
	}
	
	public boolean equals(Object otherBlock){
		if (!(otherBlock instanceof Block))return false;
		Block anotherBlock = (Block)otherBlock;
		if(!blockName.equals(anotherBlock.getBlockName())) return false;
		if(!posInWorld.equals(anotherBlock.getPosInWorld())) return false;
		if(!sizeInWorld.equals(anotherBlock.getSizeInWorld())) return false;
		//TODO: check settings
		if(!super.equals(otherBlock)) return false;
		
		return true;
	}
	
	/**This method returns the smallest outside bounding box of the entire block.
	 * The bounding box is decided by the blockShape*/
	public AABB bigBoundingBox(){
		
	}
	
	/**This method puts this block into the world (It assumes the ground is created)
	 * Before calling this method, you need to check the following things are set:
	 * 1. the block name
	 * 2. the sizeInWorld
	 * 3. the posInWorld
	 * 4. settings*/
	public void createBlockInWorld(World world){
		//TODO
	}
}
