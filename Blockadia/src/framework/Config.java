package framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import components.Block;
import components.BlockShape;

import exceptions.ElementExistsException;
import exceptions.ElementNotExistException;
import fromTestBed.ContactPoint;

/**
 * This class represent a custom-created game configuration.
 * It also has all the custom-created BlockShape's that belongs to it,
 * all the world settings and the all the blocks in the game board.
 * 
 * @author alex.yang
 * */
public class Config {
	
	public final static String INITIAL_BLOCK_NAME = "--Select a Shape--";
	public static final int MAX_CONTACT_POINTS = 4048; 
	
  // keep these static so we don't have to recreate them every time
  public final static ContactPoint[] points = new ContactPoint[MAX_CONTACT_POINTS];
  static {
    for (int i = 0; i < MAX_CONTACT_POINTS; i++) {
      points[i] = new ContactPoint();
    }
  }

	private Map<String, BlockShape> shapesMap;
	private List<BlockShape> shapesList;
	private List<Block> blocks;

	private World world;
	private Body groundBody;
	
	private final Vec2 mouseWorld = new Vec2();					//world position of the mouse
	private int pointCount;
	private int stepCount;
	
	private GameModel model;
	
	private final LinkedList<QueueItem> inputQueue;
	
	private String title = null;
	private int textLine; //TODO: Do we need this?
	private final LinkedList<String> textList = new LinkedList<String>();//TODO: Do we need this?
	
	private float cachedCameraScale;
	private final Vec2 cachedCameraPos = new Vec2();
	private boolean hasCachedCamera = false;
	
  private boolean dialogOnSaveLoadErrors = true;//TODO: Do we need this?

  private boolean savePending, loadPending, resetPending = false;//TODO: Do we need this?

	public Config(){
		shapesMap = new HashMap<String, BlockShape>();
		shapesList = new ArrayList<BlockShape>();
		blocks = new ArrayList<Block>();
		
		inputQueue = new LinkedList<QueueItem>();
		
		try {
			addGameShape(new BlockShape(INITIAL_BLOCK_NAME));
		} catch (ElementExistsException e) {
			e.printStackTrace();
		}

	}
	
  /**
   * Gets the world position of the mouse
   * 
   * @return mouseWorld
   */
  public Vec2 getWorldMouse() {
    return mouseWorld;
  }

  public void setCachedCameraScale(float cachedCameraScale) {
    this.cachedCameraScale = cachedCameraScale;
  }
  
  public float getCachedCameraScale() {
    return cachedCameraScale;
  }
  
  public void setCachedCameraPos(Vec2 argPos) {
    cachedCameraPos.set(argPos);
  }

  public Vec2 getCachedCameraPos() {
    return cachedCameraPos;
  }

  public void setHasCachedCamera(boolean hasCachedCamera) {
    this.hasCachedCamera = hasCachedCamera;
  }
  
  public boolean isHasCachedCamera() {
    return hasCachedCamera;
  }
	
	public Map<String, BlockShape> getGameShapesMap(){
		return this.shapesMap;
	}
	
	public List<BlockShape> getGameShapesList(){
		return this.shapesList;
	}
	
	public BlockShape getGameShape(String shapeName) throws ElementNotExistException {
		if(!shapesMap.containsKey(shapeName)){
			throw new ElementNotExistException("The shape named: "+ shapeName+ " does not exist");
		}
		return this.shapesMap.get(shapeName);
	}
	
	public void addGameShape(BlockShape shape) throws ElementExistsException{
		if(shapesMap.containsKey(shape.getShapeName())){
			throw new ElementExistsException("The shape with the same name: "+ shape.getShapeName()+" already exist!");
		}
		shapesMap.put(shape.getShapeName(), shape);
		shapesList.add(shape);
	}
	
	public void deleteGameShape(String shapeName) throws ElementNotExistException{
		if(!shapesMap.containsKey(shapeName)){
			throw new ElementNotExistException("The shape named: "+ shapeName+ " does not exist");
		}
		shapesMap.remove(shapeName);
		for(BlockShape shapeToDelete : shapesList){
			if(shapeToDelete.getShapeName().equals(shapeName)){
				shapesList.remove(shapeToDelete);
				return;
			}
		}
	}
	
	enum QueueItemType {
	  MouseDown, MouseMove, MouseUp, ShiftMouseDown, KeyPressed, KeyReleased
	}
	
	class QueueItem {//TODO: REVISIT to figure out what p and c are. WTF
		public QueueItemType type;
		public Vec2 p;								//a vector point
		public char c;								//a char character
		public int code;
		
		public QueueItem(QueueItemType itemType, Vec2 point){
			type = itemType;
			p = point;
		}
		
		public QueueItem(QueueItemType itemType, char theChar, int theCode){
			type = itemType;
			c = theChar;
			code = theCode;
		}
	}
}
