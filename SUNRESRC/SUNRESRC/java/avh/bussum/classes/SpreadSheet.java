/*
 * @(#)SpreadSheet.java	1.17 95/03/09 Sami Shaio
 *
 * Copyright (c) 1995 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
import browser.Applet;
import awt.Graphics;
import awt.Color;
import awt.Font;
import java.io.*;
import java.lang.*;
import net.www.html.URL;

class SpreadSheet extends Applet {
    String		title;
    Font		titleFont;
    Color		cellColor;
    Color		inputColor;
    int			cellWidth = 130;
    int			cellHeight = 30;
    int			titleHeight = 30;
    int			rowLabelWidth = 30;
    Font		inputFont;
    boolean		isStopped = false;
    boolean		fullUpdate = true;
    int			rows;
    int			columns;
    int			currentKey = -1;
    int			selectedRow = -1;
    int			selectedColumn = -1;
    SpreadSheetInput	inputArea;
    Cell		cells[][];
    Cell		current = null;

    public synchronized void init() {
	String rs;
	
	cellColor = Color.white;
	inputColor = getColor(100, 100, 225);
	inputFont = getFont("Courier", Font.PLAIN, 16);
	titleFont = getFont("Courier", Font.BOLD, 22);
	title = getAttribute("title");
	if (title == null) {
	    title = "Spreadsheet";
	}
	rs = getAttribute("rows");
	if (rs == null) {
	    rows = 9;
	} else {
	    rows = Integer.parseInt(rs);
	}
	rs = getAttribute("columns");
	if (rs == null) {
	    columns = 5;
	} else {
	    columns = Integer.parseInt(rs);
	}
	cells = new Cell[rows][columns];
	char l[] = new char[1];
	for (int i=0; i < rows; i++) {
	    for (int j=0; j < columns; j++) {

		cells[i][j] = new Cell(this,
				       bgColor,
				       Color.black,
				       cellColor,
				       cellWidth - 2,
				       cellHeight - 2);
		l[0] = (char)((int)'A' + j);
		rs = getAttribute("" + new String(l) + (i+1));
		if (rs != null) {
		    cells[i][j].setUnparsedValue(rs);
		}
	    }
	}

	inputArea = new SpreadSheetInput(null, this, width - 2, cellHeight - 1,
					 inputColor, Color.white); 
	resize(columns * cellWidth + rowLabelWidth,
	       ((rows + 1) * cellHeight) + cellHeight + titleHeight);
    }

    public void setCurrentValue(float val) {
	if (selectedRow == -1 || selectedColumn == -1) {
	    return;
	}
	cells[selectedRow][selectedColumn].setValue(val);
	repaint();
    }

    protected void stop() {
	isStopped = true;
    }

    protected void start() {
	isStopped = false;
    }

    protected void destroy() {
	for (int i=0; i < rows; i++) {
	    for (int j=0; j < columns; j++) {
		if (cells[i][j].type == Cell.URL) {
		    try {
			cells[i][j].updaterThread.stop();
		    } catch (IllegalStateException e) {
		    }
		}
	    }
	}
    }

    public void setCurrentValue(int type, String val) {
	if (selectedRow == -1 || selectedColumn == -1) {
	    return;
	}
	cells[selectedRow][selectedColumn].setValue(type, val);
	repaint();
    }

    public void update(Graphics g) {
	if (! fullUpdate) {
	    int cx, cy;

	    g.setFont(titleFont);
	    for (int i=0; i < rows; i++) {
		for (int j=0; j < columns; j++) {
		    if (cells[i][j].needRedisplay) {
			cx = (j * cellWidth) + 2 + rowLabelWidth;
			cy = ((i+1) * cellHeight) + 2 + titleHeight;
			cells[i][j].paint(g, cx, cy);
		    }
		}
	    }
	} else {
	    paint(g);
	    fullUpdate = false;
	}
    }

    public void recalculate() {
	int	i,j;

	//System.out.println("SpreadSheet.recalculate");
	for (i=0; i < rows; i++) {
	    for (j=0; j < columns; j++) {
		if (cells[i][j] != null && cells[i][j].type == Cell.FORMULA) {
		    cells[i][j].setRawValue(evaluateFormula(cells[i][j].parseRoot));
		    cells[i][j].needRedisplay = true;
		}
	    }
	}
	repaint();
    }

    public float evaluateFormula(Node n) {
	float	val = 0.0;

	//System.out.println("evaluateFormula:");
	//n.print(3);
	if (n == null) {
	    //System.out.println("Null node");
	    return val;
	}
	switch (n.type) {
	  case Node.OP:
	    val = evaluateFormula(n.left);
	    switch (n.op) {
	      case '+':
		val += evaluateFormula(n.right);
		break;
	      case '*':
		val *= evaluateFormula(n.right);
		break;
	      case '-':
		val -= evaluateFormula(n.right);
		break;
	      case '/':
		val /= evaluateFormula(n.right);
		break;
	    }
	    break;
	  case Node.VALUE:
	    //System.out.println("=>" + n.value);
	    return n.value;
	  case Node.CELL:
	    if (n == null) {
		//System.out.println("NULL at 192");
	    } else {
		if (cells[n.row][n.column] == null) {
		    //System.out.println("NULL at 193");
		} else {
		    //System.out.println("=>" + cells[n.row][n.column].value);
		    return cells[n.row][n.column].value;
		}
	    }
	}

	//System.out.println("=>" + val);
	return val;
    }	

    public synchronized void paint(Graphics g) {
	int i, j;
	int cx, cy;
	char l[] = new char[1];


	i = titleFont.stringWidth(title);
	g.setFont(titleFont);
	g.drawString((title == null) ? "Spreadsheet" : title,
		     (width - i) / 2, 22);
	g.setForeground(inputColor);
	g.fillRect(0, cellHeight, width, cellHeight);
	g.setFont(titleFont);
	for (i=0; i < rows+1; i++) {
	    cy = (i+2) * cellHeight;
	    g.paint3DRect(0, cy, width - 1, 2, false, true);
	    if (i < rows) {
		g.setForeground(Color.red);
		g.drawString("" + (i+1), 2, cy + 22);
	    }
	}
	g.setForeground(Color.red);
	for (i=0; i < columns; i++) {
	    cx = i * cellWidth;
	    g.paint3DRect(cx + rowLabelWidth,
			  2 * cellHeight, 1, height, false, true);
	    if (i < columns) {
		g.setForeground(Color.red);
		l[0] = (char)((int)'A' + i);
		g.drawString(new String(l),
			     cx + rowLabelWidth + (cellWidth / 2),
			     height - 6);
	    }
	}

	
	for (i=0; i < rows; i++) {
	    for (j=0; j < columns; j++) {
		cx = (j * cellWidth) + 2 + rowLabelWidth;
		cy = ((i+1) * cellHeight) + 2 + titleHeight;
		if (cells[i][j] != null) {
		    cells[i][j].paint(g, cx, cy);
		}
	    }
	}

	g.paint3DRect(0, titleHeight,
		      width - 1,
		      height - titleHeight,
		      false, false);
	inputArea.paint(g, 1, titleHeight + 1);
    }
    public void mouseDown(int x, int y) {
	Cell cell;
	if (y < (titleHeight + cellHeight)) {
	    selectedRow = -1;
	    if (y <= titleHeight && current != null) {
		current.deselect();
		current = null;
	    }
	    return;
	}
	if (x < rowLabelWidth) {
	    selectedRow = -1;
	    if (current != null) {
		current.deselect();
		current = null;
	    }
	    return;
	}
	selectedRow = ((y - cellHeight - titleHeight) / cellHeight);
	selectedColumn = (x - rowLabelWidth) / cellWidth;
	if (selectedRow > rows ||
	    selectedColumn >= columns) {
	    selectedRow = -1;
	    if (current != null) {
		current.deselect();
		current = null;
	    }
	} else {
	    if (selectedRow >= rows) {
		selectedRow = -1;
		if (current != null) {
		    current.deselect();
		    current = null;
		}
		return;
	    }
	    cell = cells[selectedRow][selectedColumn];
	    inputArea.setText(new String(cell.getPrintString()));
	    if (current != null) {
		current.deselect();
	    }
	    current = cell;
	    current.select();
	    getFocus();
	    fullUpdate = true;
	    repaint();
	}
    }
    public void keyDown(int key) {
	fullUpdate=true;
	inputArea.keyDown(key);
    }
}
class CellUpdater extends Thread {
    Cell 	target;
    InputStream dataStream = null;
    StreamTokenizer tokenStream;

    public CellUpdater(Cell c) {
	super("cell updater");
	target = c;
    }

    public void run() {
	dataStream = new URL(target.app.documentURL,
			     target.getValueString()).openStream();
	tokenStream = new StreamTokenizer(dataStream);
	tokenStream.eolIsSignificant = false;

	while (true) {
	    switch (tokenStream.nextToken()) {
	      case tokenStream.TT_EOF:
		dataStream.close();
		return;
	      default:
		break;
	      case tokenStream.TT_NUMBER:
		target.setTransientValue((float)tokenStream.nval);
		if (! target.app.isStopped && ! target.paused) {
		    target.app.repaint();
		}
		break;
	    }
	    Thread.sleep(2000);
	}
    }
}

class Cell {
    public static final int VALUE = 0;
    public static final int LABEL = 1;
    public static final int URL   = 2;
    public static final int FORMULA = 3;
    
    Node	parseRoot;
    boolean	needRedisplay;
    boolean selected = false;
    boolean transientValue = false;
    public int	type = Cell.VALUE;
    String	valueString = "";
    String	printString = "v";
    float	value;
    Color	bgColor;
    Color	fgColor;
    Color	highlightColor;
    int		width;
    int		height;
    SpreadSheet app;
    CellUpdater	updaterThread;
    boolean	paused = false;

    public Cell(SpreadSheet app,
		Color bgColor,
		Color fgColor,
		Color highlightColor,
		int width,
		int height) {
	this.app = app;
	this.bgColor = bgColor;
	this.fgColor = fgColor;
	this.highlightColor = highlightColor;
	this.width = width;
	this.height = height;
	needRedisplay = true;
    }
		
    public void setRawValue(float f) {
	valueString = Float.toString(f);
	value = f;
    }
    public void setValue(float f) {
	setRawValue(f);
	printString = "v" + valueString;
	type = Cell.VALUE;
	paused = false;
	app.recalculate();
	needRedisplay = true;
    }

    public void setTransientValue(float f) {
	transientValue = true;
	value = f;
	needRedisplay = true;
	app.recalculate();
    }

    public void setUnparsedValue(String s) {
	switch (s.charAt(0)) {
	  case 'v':
	    setValue(Cell.VALUE, s.substring(1));
	    break;
	  case 'f':
	    setValue(Cell.FORMULA, s.substring(1));
	    break;
	  case 'l':
	    setValue(Cell.LABEL, s.substring(1));
	    break;
	  case 'u':
	    setValue(Cell.URL, s.substring(1));
	    break;
	}
    }

    /**
     * Parse a spreadsheet formula. The syntax is defined as:
     *
     * formula -> value
     * formula -> value op value
     * value -> '(' formula ')'
     * value -> cell
     * value -> <number>
     * op -> '+' | '*' | '/' | '-'
     * cell -> <letter><number>
     */
    public String parseFormula(String formula, Node node) {
	String subformula;
	String restFormula;
	float value;
	int length = formula.length();
	Node left;
	Node right;
	char op;

	if (formula == null) {
	    return null;
	}
	subformula = parseValue(formula, node);
	//System.out.println("subformula = " + subformula);
	if (subformula == null || subformula.length() == 0) {
	    //System.out.println("Parse succeeded");
	    return null;
	}
	if (subformula == formula) {
	    //System.out.println("Parse failed");
	    return formula;
	}

	// parse an operator and then another value
	switch (op = subformula.charAt(0)) {
	  case 0:
	    //System.out.println("Parse succeeded");
	    return null;
	  case ')':
	    //System.out.println("Returning subformula=" + subformula);
	    return subformula;
	  case '+':
	  case '*':
	  case '-':
	  case '/':
	    restFormula = subformula.substring(1);
	    subformula = parseValue(restFormula, right=new Node());
	    //System.out.println("subformula(2) = " + subformula);
	    if (subformula != restFormula) {
		//System.out.println("Parse succeeded");
		left = new Node(node);
		node.left = left;
		node.right = right;
		node.op = op;
		node.type = Node.OP;
		//node.print(3);
		return subformula;
	    } else {
		//System.out.println("Parse failed");
		return formula;
	    }
	  default:
	    //System.out.println("Parse failed (bad operator): " + subformula);
	    return formula;
	}
    }

    public String parseValue(String formula, Node node) {
	char	c = formula.charAt(0);
	String	subformula;
	String	restFormula;
	float	value;
	int	row;
	int	column;

	//System.out.println("parseValue: " + formula);
	restFormula = formula;
	if (c == '(') {
	    //System.out.println("parseValue(" + formula + ")");
	    restFormula = formula.substring(1);
	    subformula = parseFormula(restFormula, node);
	    //System.out.println("rest=(" + subformula + ")");
	    if (subformula == null ||
		subformula.length() == restFormula.length()) {
		//System.out.println("Failed");
		return formula;
	    } else if (! (subformula.charAt(0) == ')')) {
	        //System.out.println("Failed (missing parentheses)");
		return formula;
	    }
	    restFormula = subformula;
	} else if (c >= '0' && c <= '9') {
	    int i;

	    //System.out.println("formula=" + formula);
	    try {
		value = Float.valueOf(formula).floatValue();
	    } catch (NumberFormatException e) {
		//System.out.println("Failed (number format error)");
		return formula;
	    }
	    for (i=0; i < formula.length(); i++) {
		c = formula.charAt(i);
		if ((c < '0' || c > '9') && c != '.') {
		    break;
		}
	    }
	    node.type = Node.VALUE;
	    node.value = value;
	    //node.print(3);
	    restFormula = formula.substring(i);
	    //System.out.println("value= " + value + " i=" + i +
		//		       " rest = " + restFormula);
	    return restFormula;
	} else if (c >= 'A' && c <= 'Z') {
	    int i;

	    column = c - 'A';
	    restFormula = formula.substring(1);
	    row = Float.valueOf(restFormula).intValue();
	    //System.out.println("row = " + row + " column = " + column);
	    for (i=0; i < restFormula.length(); i++) {
		c = restFormula.charAt(i);
		if (c < '0' || c > '9') {
		    break;
		}
	    }
	    node.row = row - 1;
	    node.column = column;
	    node.type = Node.CELL;
	    //node.print(3);
	    if (i == restFormula.length()) {
		restFormula = null;
	    } else {
		restFormula = restFormula.substring(i);
		if (restFormula.charAt(0) == 0) {
		    return null;
		}
	    }	    
	}

	return restFormula;
    }


    public void setValue(int type, String s) {
	paused = false;
	if (this.type == Cell.URL) {
	    updaterThread.stop();
	    updaterThread = null;
	}

	valueString = new String(s);
	this.type = type;
	needRedisplay = true;
	switch (type) {
	  case Cell.VALUE:
	    setValue(Float.valueOf(s).floatValue());
	    break;
	  case Cell.LABEL:
	    printString = "l" + valueString;
	    break;
	  case Cell.URL:
	    printString = "u" + valueString;
	    updaterThread = new CellUpdater(this);
	    updaterThread.start();
	    break;
	  case Cell.FORMULA:
	    parseFormula(valueString, parseRoot = new Node());
	    printString = "f" + valueString;
	    break;
	}
	app.recalculate();
    }

    public String getValueString() {
	return valueString;
    }

    public String getPrintString() {
	return printString;
    }

    public void select() {
	selected = true;
	paused = true;
    }
    public void deselect() {
	selected = false;
	paused = false;
	needRedisplay = true;
	app.repaint();
    }
    public void paint(Graphics g, int x, int y) {
	if (selected) {
	    g.setForeground(highlightColor);
	} else {
	    g.setForeground(bgColor);
	}
	g.fillRect(x, y, width - 1, height);
	if (valueString != null) {
	    switch (type) {
	      case Cell.VALUE:
	      case Cell.LABEL:
		g.setForeground(fgColor);
		break;
	      case Cell.FORMULA:
		g.setForeground(Color.red);
		break;
	      case Cell.URL:
		g.setForeground(Color.blue);
		break;
	    }
	    if (transientValue){
		g.drawString("" + value, x, y + (height / 2) + 5);
	    } else {
		if (valueString.length() > 14) {
		    g.drawString(valueString.substring(0, 14),
				 x, y + (height / 2) + 5);
		} else {
		    g.drawString(valueString, x, y + (height / 2) + 5);
		}
	    }
	}
	needRedisplay = false;
    }
}

class Node {
    public static final int OP = 0;
    public static final	int VALUE = 1;
    public static final int CELL = 2;

    int		type;
    Node 	left;
    Node 	right;
    int  	row;
    int  	column;
    float	value;
    char	op;

    public Node() {
	left = null;
	right = null;
	value = 0;
	row = -1;
	column = -1;
	op = 0;
	type = Node.VALUE;
    }
    public Node(Node n) {
	left = n.left;
	right = n.right;
	value = n.value;
	row = n.row;
	column = n.column;
	op = n.op;
	type = n.type;
    }
    public void indent(int ind) {
	for (int i = 0; i < ind; i++) {
	    System.out.print(" ");
	}
    }
    public void print(int indentLevel) {
	char l[] = new char[1];
	indent(indentLevel);
	System.out.println("NODE type=" + type);
	indent(indentLevel);
	switch (type) {
	  case Node.VALUE:
	    System.out.println(" value=" + value);
	    break;
	  case Node.CELL:
	    l[0] = (char)((int)'A' + column);
	    System.out.println(" cell=" + new String(l) + (row+1));
	    break;
	  case Node.OP:
	    System.out.println(" op=" + op);
	    left.print(indentLevel + 3);
	    right.print(indentLevel + 3);
	    break;
	}
    }
}

class InputField {
    int		maxchars = 50;
    int		cursorPos = 0;
    Applet	app;
    String	sval;
    char	buffer[];
    int		nChars;
    int		width;
    int		height;
    Color	bgColor;
    Color	fgColor;

    public InputField(String initValue, Applet app, int width, int height,
		      Color bgColor, Color fgColor) {
	this.width = width;
	this.height = height;
	this.bgColor = bgColor;
	this.fgColor = fgColor;
	this.app = app;
	buffer = new char[maxchars];
	nChars = 0;
	if (initValue != null) {
	    initValue.getChars(0, initValue.length(), this.buffer, 0);
	    nChars = initValue.length();
	}
	sval = initValue;
    }

    public void setText(String val) {
	int i;

	for (i=0; i < maxchars; i++) {
	    buffer[i] = 0;
	}
	sval = new String(val);
	if (val == null) {
	    sval = "";
	    nChars = 0;
	    buffer[0] = 0;
	} else {
	    sval.getChars(0, sval.length(), buffer, 0);
	    nChars = val.length();
	    sval = new String(buffer);
	}
    }

    public String getValue() {
	return sval;
    }

    public void paint(Graphics g, int x, int y) {
	g.setForeground(bgColor);
	g.fillRect(x, y, width, height);
	if (sval != null) {
	    g.setForeground(fgColor);
	    g.drawString(sval, x, y + (height / 2) + 3);
	}
    }
    public void mouseUp(int x, int y) {
	// set the edit position
    }
    public void keyDown(int key) {
	if (nChars < maxchars) {
	    switch (key) {
	      case 8: // delete
		--nChars;
		if (nChars < 0) {
		    nChars = 0;
		}
		buffer[nChars] = 0;
		sval = new String(new String(buffer));
		break;
	      case 10: // return
		selected();
		break;
	      default:
		buffer[nChars++] = (char)key;
		sval = new String(new String(buffer));
		break;
	    }
	}
	app.repaint();
    }
    public void selected() {
    }
}

class SpreadSheetInput extends InputField {
    public SpreadSheetInput(String initValue,
			    SpreadSheet app,
			    int width,
			    int height,
			    Color bgColor,
			    Color fgColor) {
	super(initValue, app, width, height, bgColor, fgColor);
    }

    public void selected() {
	float f;

	switch (sval.charAt(0)) {
	  case 'v':
	    try {
		f = Float.valueOf(sval.substring(1)).floatValue();
		((SpreadSheet)app).setCurrentValue(f);
	    } catch (NumberFormatException e) {
		System.out.println("Not a float...");
	    }
	    break;
	  case 'l':
	    ((SpreadSheet)app).setCurrentValue(Cell.LABEL, sval.substring(1));
	    break;
	  case 'u':
	    ((SpreadSheet)app).setCurrentValue(Cell.URL, sval.substring(1));
	    break;
	  case 'f':
	    ((SpreadSheet)app).setCurrentValue(Cell.FORMULA, sval.substring(1));
	    break;
	}
    }
}
