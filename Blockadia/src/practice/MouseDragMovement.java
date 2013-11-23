package practice;

/**
 * File: MouseDragMovement.java
 *
 * This file is free to use and modify as it is for educational use.
 *
 * Version:
 * 1.1 Initial Version
 *
 */
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This is an adaptation of the EightCharacterMovement adding movement toward
 * the mouses current position if the player is dragging the mouse. The
 * modifications here were simply changing the KeyAdapter to a
 * MouseMotionAdapter. And changing the update call.<br/>
 * This snippet can be used to make mouse based movement games.
 * <br/>
 * Notes: Performance seemed decent enough if things are too slow try enabling
 * the sun.java2d.opengl or d3d properties if you change the block graphics to 
 * use an image with transparent pixels use the sun.java2d.transaccel property 
 * to improve performance.
 * <br/>
 * Challenge: There were issues when the block gets to close to the mouse in some 
 * circumstances I attempted to fix it by checking to see if the mouse was over
 * the block, but this didn't work 100% of the time. See if you can figure out
 * a way to get it to never move back and forth around the cursor not matter 
 * what situation.
 * 
 * @author Nick
 */
public class MouseDragMovement extends Canvas {

	// java 2d control flags
	static {
		// System.setProperty("sun.java2d.transaccel", "True");
		// System.setProperty("sun.java2d.opengl", "true");
		// System.setProperty("sun.java2d.d3d", "True");
		// System.setProperty("sun.java2d.ddforcevram", "True");
	}

	// app title.
	private static final String	TITLE		= "GPSnippets: Mouse Drag Movement Snippet";

	// the strategy used for double buffering, or any number of buffered frames.
	private BufferStrategy		strategy;

	// our time keeper
	private final Timer			timer;

	// the main render and update task.
	private TimerTask			renderTask;

	// basic avatar
	private Rectangle2D.Double	basicBlock;

	// just a gradient background for our little demo.
	private Paint				backgroundGradient;

	// our little basic blocks speed, which will be multipled by the number
	// of seconds passed, each render frame.
	private final double		speed		= 150.0d /* px/sec */;

	// the mouse position
	private double				mouseX;
	private double				mouseY;

	// the indicator used to determine if we should move the character
	protected boolean			dragging	= false;

	/**
	 * This configures the canvas component for rendering. Creates any objects
	 * we need and sets up some default listeners for component events.
	 */
	public MouseDragMovement() {
		// we will be doing our own rendering, using the strategy.
		this.setIgnoreRepaint(true);

		timer = new Timer(); // used for the render thread

		// add quick and dirty mouse adapter
		addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				// save the mouse position to a couple variables
				mouseX = e.getX();
				mouseY = e.getY();

				// we need an indicator that the player is trying to move the
				// character.
				dragging = true;
				super.mouseDragged(e);
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				dragging = false;
				super.mouseMoved(e);
			}

		});
	}

	/**
	 * Our drawing function which utilizes the BufferStrategy that enables us to
	 * do offscreen rendering without having to wait for swing to repaint, the
	 * component. It also eliminates flickering and splicing issues.
	 */
	public void render() {
		// the back buffer graphics object
		Graphics2D bkG = (Graphics2D) strategy.getDrawGraphics();

		// clear the backbuffer, this could be substituted for a background
		// image or a tiled background.
		bkG.setPaint(backgroundGradient);
		bkG.fillRect(0, 0, getWidth(), getHeight());

		// TODO: Draw your game world, or scene or anything else here.

		// Rectangle2D is a shape subclass, and the graphics object can render
		// it, makes things a little easier.
		bkG.setColor(Color.green.darker());
		bkG.fill(basicBlock);

		// properly dispose of the backbuffer graphics object. Release resources
		// and cleanup.
		bkG.dispose();

		// flip the backbuffer to the canvas component.
		strategy.show();

		// synchronize drawing with the display refresh rate.
		Toolkit.getDefaultToolkit().sync();
	}

	/**
	 * It is necessary to wait until after the component has been displayed in
	 * order to create and retreive the buffer strategy. This is the part that
	 * took me the longest to figure out. But it makes sense since the component
	 * requires native resources in order to perform hardware accleration and
	 * those resources are handled by the component itself, and are only
	 * available once the component is created and displayed.
	 */
	public void setup() {
		basicBlock = new Rectangle2D.Double(0, 0, 16, 16);

		// center the block in the canvas.
		basicBlock.x = this.getWidth() / 2 - basicBlock.width / 2;
		basicBlock.y = this.getHeight() / 2 - basicBlock.height / 2;

		// create the background gradient paint object.
		backgroundGradient = new GradientPaint(0, 0, Color.gray, getWidth(),
				getHeight(), Color.lightGray.brighter());

		// create a strategy that uses two buffers, or is double buffered.
		this.createBufferStrategy(2);

		// get a reference to the strategy object, for use in our render method
		// this isn't necessary but it eliminates a call during rendering.
		strategy = this.getBufferStrategy();

		start();
	}

	/**
	 * Initialize the render and update tasks, to call the render method, do
	 * timing and fps counting, handling input and cancelling existing tasks.
	 */
	public void start() {
		// if the render task is already running stop it, this may cause an
		// exception to be thrown if the task is already canceled.
		if (renderTask != null) {
			renderTask.cancel();
		}

		// our main task for handling the rendering and for updating and
		// handling input and movement events. The timer class isn't the most
		// reliable for game updating and rendering but it will suffice for the
		// purpose of this snippet.
		renderTask = new TimerTask() {
			long	lasttime	= System.currentTimeMillis();

			@Override
			public void run() {

				// get the current system time
				long time = System.currentTimeMillis();

				// calculate the time passed in milliseconds
				double dt = (time - lasttime) * 0.001;

				// save the current time
				lasttime = time;

				// for now just move the basic block
				update(dt);

				render();
			}
		};

		// These will cap our frame rate but give us unexpected results if our
		// rendering or updates take longer than the 'period' time. It
		// is likely that we could have overlapping calls.
		timer.schedule(renderTask, 0, 16);
	}

	/**
	 * Here is the meat of the snippet. This method gets calls at timed
	 * intervals where dt is the actual number of seconds that has passed since
	 * the last call. Our character the basicBlock is then moved. The amount to
	 * move the character depends on how many seconds have passed and the speed
	 * in (pixels per second) that the character is set to move. <br/>
	 * Try experimenting with different speeds to see the effect it has on the
	 * block
	 * 
	 * @param dt
	 */
	protected void update(double dt) {

		// only move if the user is dragging the mouse and we aren't already
		// at the mouse position
		if (dragging && !isMouseOverBlock()) {
			// the amount to move the character in x and y directions
			double dx = 0.0d;
			double dy = 0.0d;

			// relative mouse position from the character
			double relativeMouseX = mouseX - basicBlock.x;
			double relativeMouseY = mouseY - basicBlock.y;

			// determine the angle the mouse is from the current position
			double angle = Math.atan2(relativeMouseY, relativeMouseX);

			// now determine based on the angle what direction to move in
			dx = Math.cos(angle);
			dy = Math.sin(angle);

			/*
			 * move the block by multiplying the amount of time in seconds that
			 * has passed since the last update by the speed constant and by the
			 * change in x or y directions which will either will result in += 0
			 * or dt * speed negative or positive.
			 */
			basicBlock.x += dt * speed * dx;
			basicBlock.y += dt * speed * dy;			
		}

		// TODO: Add code here to start doing game logic
	}

	private boolean isMouseOverBlock() {
		return basicBlock.contains(new Point2D.Double(mouseX, mouseY));
	}

	/**
	 * Stops the rendering cycle so that the application can close gracefully.
	 */
	protected void stop() {
		timer.cancel();
	}

	/**
	 * Creates a Frame and adds a new canvas to the Frame, displays it and
	 * initializes the rendering method.
	 */
	protected static void createAndDisplay() {
		// Never mix swing and awt, since we use a canvas to utilize the
		// buffered strategy we will put the canvas in a Frame instead of a
		// JFrame.
		final Frame frame = new Frame(TITLE);
		frame.setLayout(new BorderLayout());
		final MouseDragMovement canvas = new MouseDragMovement();
		frame.add(canvas);

		// convenience exiting from the demo using the ESCAPE key.
		canvas.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					canvas.stop(); // first stop the drawing and updating
					frame.setVisible(false); // hide the window quickly
					frame.dispose(); // release all system resources
					System.exit(0); // finally exit.
				}
			}
		});

		// need this to trap when the user attempts to close the window using
		// the close icon for the window, or the close option from the window
		// menu or alt+f4 or by other means.
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				canvas.stop(); // first stop the drawing and updating
				frame.setVisible(false); // hide the window quickly
				frame.dispose(); // release all system resources
				System.exit(0); // finally exit.
			}
		});
		frame.setSize(800, 600); // should use configurable properties here
		frame.setLocationRelativeTo(null); // centers window on screen
		frame.setVisible(true); // creates and displays the actual window

		// this is our scene setup to initialize all necessary configurable
		// objects and properties. Using a setup method helps control the way
		// things look from a single location, it can be extended to include
		// how things act as well.
		canvas.setup();
	}

	/**
	 * Calls the swing event thread to create and display a new application
	 * frame. This is done so that the setVisible method is not part of the main
	 * application thread but is done within the swing event thread.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// create and display the window.
		createAndDisplay();
	}
}
