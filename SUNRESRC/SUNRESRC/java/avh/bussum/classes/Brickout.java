import awt.*;
import browser.*;
import browser.audio.*;

class Brickout extends Applet implements Runnable {
	static final int puckWidth = 40;
	static final int puckHeight = 10;

	static final int ballWidth = 10;
	static final int ballHeight = 10;

	static final int brickCols = 7;
	static final int brickRows = 5;

	int startRow;

	int itsPuckOffset;
	boolean itsRunning;

	int itsBallX;
	int itsBallY;

	int lastX, lastY, lastPuck;
	boolean blockChanged = false;

	int itsBallXAcc;
	int itsBallYAcc;

	int ballCount;

	Thread kicker;

	boolean blockExists[];

	private final boolean CheckCollisions() {
		int tenthWidth = (width / brickCols);
		int twoPucks = puckHeight; //  * 2;
		
		for (int x = 0;  x < brickCols;  x++) {
			for (int y = brickRows - 1;  y >= 0; y--) {
				if (blockExists[y * brickRows + x]) {
					int x1 = x * tenthWidth;
					int y1 = y * twoPucks + startRow;
					int x2 = x1 + tenthWidth;
					int y2 = y1 + puckHeight;

					if (itsBallX + ballWidth >= x1 && itsBallX <= x2)
						if (itsBallY + ballHeight >= y1 && itsBallY <= y2) {
							blockExists[y * brickRows + x] = false;
							blockChanged = true;
//							play("audio/ding.au");
							return true;
						}
				}
			}
		}
		return false;
	}
	
	private final Color getOneColor(int c) {
		switch (c) {
		  case 0:
			return Color.red;
		  case 1:
			return Color.pink;
		  case 2:
			return Color.orange;
		  case 3:
			return Color.yellow;
		  case 4:
			return Color.green;
		  case 5:
			return Color.magenta;
		  case 6:
			return Color.blue;
		  case 7:
			return Color.cyan;
		}
		return Color.black;
	}

	private final void DrawBricks(Graphics g) {
		int tenthWidth = (width / brickCols);
		int twoPucks = puckHeight;	// * 2;

		boolean drewOne = false;

		for (int x = 0;  x < brickCols;  x++) {
			for (int y = 0;  y < brickRows; y++) {
				if (blockExists[y * brickRows + x]) {
					drewOne = true;
					g.setForeground(getOneColor((y * brickRows + x) % 8));
//					g.setForeground(getOneColor((x + 1) * (y + 1) % 8));
					g.fillRect(x * tenthWidth, y * twoPucks + startRow,
							   tenthWidth, puckHeight);
				} else
					g.clearRect(x * tenthWidth, y * twoPucks + startRow,
							   tenthWidth, puckHeight);
			}
		}

		if (! drewOne) {
//			play("audio/joy.au");
			int saveStartRow = startRow;
			ReInitialize(true);
			saveStartRow += puckHeight;
			startRow = saveStartRow;
		}
		blockChanged = false;

		g.setForeground(Color.black);
	}

	private final void DrawPuck(Graphics g) {
		g.fillRect(itsPuckOffset, height - puckHeight,
					 puckWidth, puckHeight);
		lastPuck = itsPuckOffset;
	}

	private final void UpdatePuck(int newH) {
		if (newH + puckWidth > width)
			newH = width - puckWidth;
//		Graphics g = getParent().graphics.createChild(x, y, 1, 1);
//		ErasePuck(g);
		itsPuckOffset = newH;
		repaint();
//		DrawPuck(g);
	}
	
	public void DrawBall(Graphics g) {
		g.fillRect(itsBallX, itsBallY, ballWidth, ballHeight);
		lastX = itsBallX;
		lastY = itsBallY;
	}

	public void LostBall() {
		ballCount--;
		if (ballCount <= 0) {
//			play("audio/slap.au");
			kicker = null;
			repaint();
		} else {
			itsBallX = width / 2;
			itsBallY = startRow + (brickRows + 1) * puckHeight;
		}
	}

	public void MoveBall() {
		itsBallX += itsBallXAcc;
		itsBallY += itsBallYAcc;

		boolean gotCollision = CheckCollisions();

		if (itsBallX < 0) {
			itsBallX = 0;
			itsBallXAcc = -itsBallXAcc;
		} else if (itsBallX + ballWidth > width) {
			itsBallX = width - ballWidth;
			itsBallXAcc = -itsBallXAcc;
		}

		if (gotCollision) {
			itsBallYAcc = -itsBallYAcc;
			if (gotCollision) {
				itsBallXAcc = (int) (Math.random() * 5.0);
				if (itsBallXAcc > 5)
					itsBallXAcc = 5;
				else if (itsBallXAcc < -5)
					itsBallXAcc = -5;
			}
		} else {
			if (itsBallY < 0 || gotCollision) {
				itsBallY = 0;
				itsBallYAcc = -itsBallYAcc;
			} else if (itsBallY + ballHeight + puckHeight > height) {
				if (itsBallX + ballWidth < itsPuckOffset 
					|| itsBallX > itsPuckOffset + puckWidth) {
					LostBall();
				} else {
					itsBallY = height - puckHeight - ballHeight;
					itsBallYAcc = -itsBallYAcc;
					// spin
						itsBallXAcc = ((itsBallX - itsPuckOffset)
									   - (puckWidth / 2)) / 2;
					if (itsBallXAcc > 5)
						itsBallXAcc = 5;
					else if (itsBallXAcc < -5)
						itsBallXAcc = -5;
				}
			}
		}
	}

	public void run() {
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		while (kicker != null) {
			MoveBall();
			repaint();
//			item.requestUpdate();
			Thread.sleep(10);
		}
	}
	private final void ReInitialize(boolean keepBalls) {
		if (! keepBalls)
			ballCount = 3;

		itsPuckOffset = 0;
		itsBallX = width / 2;
		itsBallY = height / 2;

		itsBallXAcc = 3;
		itsBallYAcc = 3;

		blockExists = new boolean[brickRows * brickCols];
		for (int i = 0;  i < brickRows * brickCols;  i++)
			blockExists[i] = true;

		startRow = 20;
	}

    /**
     * Applet methods
     */
    public void init() {
		resize(400, 400);

		ReInitialize(false);
    }
    public void start() {
    }
    public void stop() {
    }
    public void destroy() {
    }

    /**
     * Paint a rectangle with some wierd lines...
     */
    public void paint(Graphics g) {
		g.drawRect(0, 0, width - 1, height - 1);
		if (ballCount > 0) {
			DrawBricks(g);
			DrawBall(g);
			DrawPuck(g);
		} else {
			// draw something that says, "You lose, click to continue"
		}
    }

    /**
     * Paint a rectangle with some wierd lines...
     */
    public void update(Graphics g) {
		g.drawRect(0, 0, width - 1, height - 1);
		if (ballCount > 0) {
		    if (blockChanged)
			DrawBricks(g);
			g.clearRect(lastPuck, height - puckHeight,
					 puckWidth, puckHeight);
			g.clearRect(lastX, lastY, ballWidth, ballHeight);

			DrawBall(g);
			DrawPuck(g);
		} else {
			// draw something that says, "You lose, click to continue"
		}
    }

    /**
     * Mouse methods
     */
    public void mouseDown(int x, int y) {
		ReInitialize(false);
    }
    public void mouseDrag(int x, int y) {
    }
    public void mouseUp(int x, int y) {
    }
    public void mouseMove(int x, int y) {
		UpdatePuck(x);
    }
    public void mouseEnter() {
		if (kicker == null) {
			kicker = new Thread(this);
			kicker.start();
		}
    }
    public void mouseExit() {
		kicker = null;
    }

    /**
     * Focus methods
     */
    public void gotFocus() {
    }
    public void lostFocus() {
    }
    public void keyDown(int key) {
    }
}
