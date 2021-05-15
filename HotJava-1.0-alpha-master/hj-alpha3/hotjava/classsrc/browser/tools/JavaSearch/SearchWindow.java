/*
 * @(#)SearchWindow.java	1.34 95/03/20 Sami Shaio, Patrick Chan
 *
 * Copyright (c) 1994 Sun Microsystems, Inc. All Rights Reserved.
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

package browser.tools.JavaSearch;

import awt.*;
import java.lang.*;
import java.io.*;
import java.util.*;
import browser.hotjava;

/** Creates a search window.  If 'wr' is null, the search window 
    is not associated with any hotjava; the hotjava will
    automatically be created when the user selects a document
    to view. */
public
class SearchWindow extends Frame implements ChoiceHandler {
    public SearchWindow(hotjava wr, WServer pServer) {
	super(pServer, true, wr, 319, 475, Color.lightGray);
	setDefaultFont(pServer.fonts.getFont("Dialog", Font.BOLD, 12));
        Label l;
	RowColLayout r;
        FlowLayout fl;
        BorderLayout bl;

        wrunner = wr;
        server = pServer;
	setTitle("Search Java-related Documentation");

        // construct user interface
	//cw = new Window(this, "North", background, 300, 100);

	cw = new Window(this, "Center", background, 300, 300);
        cw.setLayout(new ColumnLayout(true));
	hitList = new List(cw,this,"", 20, false, false);
	hitList.setHFill(true);
	hitList.setVFill(true);

        cw = new Window(this, "South", background, 300, 300);
        cw.setLayout(new ColumnLayout(false));

	l = new Label("Query:","", cw);
	queryField = new QueryTextField(this, "","queryField",cw, true);
	queryField.setHFill(true);

	l = new Label("Books to Search:",null, cw);

	Row row = new Row(cw, "optmenu", true);
        scopeMenu = new OptionMenu(row, "", null);

	Row buttons = new Row(cw, "buttons", true);
	searchB = new SearchButton(this, buttons);
	quitB = new QuitButton(this, buttons);
	new HelpButton(this, buttons);

        if (hasStatusBar()) {
            showStatusBar(true);
        } else {
	    statusBar = new Label("Ready.", null, cw, null);
	    statusBar.setHFill(true);
        }

	selectedItem = 0;
	readListOfBooks();
	scopeMenu.select(0);
    }

    public void readListOfBooks() {
        boolean ok;

        // get docPath
        docPath = System.getenv("HOTJAVA_HOME");
        if (docPath == null) {
            docPath = "/usr/local/hotjava";
        }
        docPath = docPath + File.separator + "doc";
	docPath.replace('/', File.separatorChar);
        ok = new File(docPath).exists();
        if (! ok) {
            setMessage("Can't locate the directory " + docPath + " for documentation.");
        }

        if (ok) {
	    // read ListOfBooks file
	    listOfBooksPath = docPath + File.separator + "ListOfBooks";
	    FileInputStream fstream = null;
	    try {
		fstream = new FileInputStream(listOfBooksPath);
	    } catch (IOException e) {
		setMessage("Could not open " + listOfBooksPath + ".");
                return;
	    }
            listOfBooks = new Vector();
	    DataInputStream dstream = new DataInputStream(fstream);
	    String line;
	    while ((line = dstream.readLine()) != null) {
		scopeMenu.addItem(line);
                line = dstream.readLine();
                if (line == null) {
                    setMessage("Number of lines in " + listOfBooksPath + " file must be even.");
                    listOfBooks = null;
                    break;
                }
                listOfBooks.addElement(line);
	    }
            if (listOfBooks != null && listOfBooks.size() == 0) {
		setMessage(listOfBooksPath + " is empty.");
                listOfBooks = null;
            }
	    fstream.close();
        }
    }

    public static void main(String args[]) {
	WServer		server;
	SearchWindow	searchW;

	try {
	    server = new WServer();
	} catch(Exception e) {
	    System.out.print("Couldn't open connection to window server\n");
	    e.printStackTrace();
	    return;
	}
	server.start();
	searchW = new SearchWindow(null, server);
	searchW.map();
        searchW.resize();
    }

    public void selected(Component c, int pos) {
	selectedItem = pos;
	go(hotjava.dochome + currentSearchResults.getDocAt(pos).filename);
    }

    public void go(String url) {
        if (wrunner == null) {
	    wrunner = new hotjava(server, new String[0]);
        }
	wrunner.map();
	wrunner.go(url);
    }

    public void doSearch() {
	String dbname;
	String query = queryField.getText();

        if (listOfBooks == null) {
            return;
        }
        if (query.length() == 0) {
        }
        dbname = docPath + File.separator 
            + listOfBooks.elementAt(scopeMenu.selectedIndex) + File.separator;
	setMessage("Searching " + dbname + "...");
 
	// Clear the scrolling list before a search:
	hitList.clear();
/*
        while (hitList.nItems() > 0) {
            hitList.delItem(0);
        }
*/

	// Do the search!
        try {
	    Database db = Database.OpenDatabase(dbname);
	    currentDatabase = db;
	    Searcher searcher = new Searcher(db, this);
	    DocList resultDL = searcher.doSearch(query);
	    currentSearchResults = resultDL;
            if (resultDL == null) {
		setMessage("There are no pages that satisfy the query.");
            } else {
                if (resultDL.size() <= displayLimit) {
		    setMessage("Found " + resultDL.size() + " pages.");
		} else {
		    setMessage("Found " + resultDL.size() + " pages. Displaying first "+displayLimit+" only.");
		    displayLimitMessage(resultDL.size());
                }
		for (int i=0; i<Math.min(displayLimit, resultDL.size()); i++) {
		    Doc doc = resultDL.getDocAt(i);
		    String headline = doc.headline;

		    hitList.addItem(headline);
		}
            } 
        } catch (IOException e) {
            setMessage("Failed to open book " + dbname + ".");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Displays the specified message in the status bar */
    public void setMessage(String msg) {
        if (statusBar != null) {
	    statusBar.setText(msg);
        } else {
            setStatusMessage(msg);
        }
    }

    /** Display the list of stopped words in a dialog box. */
    public void displayStoppedWords(Vector words) {
	int listLen = words.size();
	String wordList = "'"+words.elementAt(0)+"'";
	
	for (int i = 1; i < listLen; i++) {
	    if (i == listLen - 1) {
		if (listLen > 2) {
		    wordList += ", and '"+words.elementAt(i)+"'";
		} else {
		    wordList += " and '"+words.elementAt(i)+"'";
		}
	    } else {
		wordList += ", '"+words.elementAt(i)+"'";
	    }
	}
	    
	new MessageDialog(this, "Query status",
			  "Removed from query: "+wordList,
			  MessageDialog.INFO_TYPE, 1, false,
			  null, null, null, null).show();
    }
    
    /** Put up a dialog that tells the user that there are too many results
     *  to display.  Suggest better search strategies. */
    public void displayLimitMessage(int numResults) {
	new MessageDialog(this, "Search status",
			  "Found "+numResults+" pages. (Display limit is "+
			  displayLimit+".)  Try using more keywords or "+
			  "AND to narrow your search.",
			  MessageDialog.INFO_TYPE, 1, false,
			  null, null, null, null).show();
    }
    
    public void doubleClick(Component c, int pos) {
	selectedItem = pos;
	System.out.println("Double-click on " + hitList.itemAt(pos));

	Doc doc = currentSearchResults.getDocAt(pos);
	System.out.println("\n--> headline of this doc is "+doc.headline);

	String pathname = currentDatabase.docPathPrefix + doc.filename;
	System.out.println("--> The full pathname for this doc is "+
			   pathname);

	String fullURL = null;
	if (currentDatabase.docURLPrefix != null) {
	    fullURL = currentDatabase.docURLPrefix + doc.filename;
	    System.out.println("--> The full URL for this doc is "+
			       fullURL);
	}
	else {
	    System.out.println("--> (This Database does not have a URL prefix...)");
	}
	    

	//
	// Ok!  Do something with this document!
	//
	// Given "pathname", maybe open it in its own window?
	//
	// Or, if we're running as an Applet, here's the time
	//   to do "MosaicWindow.pushURL(fullURL)"!
	//
    }

    hotjava wrunner;
    WServer server;

    // widgets
    Button		quitB;
    Button		searchB;
    Window		cw;
    int			selectedItem;
    List		hitList;
    TextField		queryField;
    Label               statusBar;
    OptionMenu          scopeMenu;

    String docPath;         // HOTJAVA_HOME/doc
    String listOfBooksPath; // HOTJAVA_HOME/doc/ListOfBooks
    Vector listOfBooks;     // list of books.  == null if no books found.
    Database currentDatabase;
    DocList currentSearchResults;

    final int displayLimit = 100; // display no more than this many hits
}

class HelpButton extends Button {
    SearchWindow target;

    HelpButton(SearchWindow searchWindow, Container w) {
	super("Help", null, w);
        target = searchWindow;
    }
    public void selected(Component c, int pos) {
        target.go(hotjava.dochome + "JavaSearchHelp.html");
    }
}

class QuitButton extends Button {
    Frame f;
    QuitButton(Frame pF, Container w) {
	super("Cancel", null, w);
        f = pF;
    }
    public void selected(Component c, int pos) {
	f.unMap();
    }
}

class SearchButton extends Button {
    SearchWindow sw;
    SearchButton(SearchWindow pF, Container w) {
	super("Search", null, w);
        sw = pF;
    }
    public void selected(Component c, int pos) {
	sw.doSearch();
    }
}

class QueryTextField extends TextField {
    SearchWindow target;

    QueryTextField(SearchWindow searchWindow, 
		   String initValue, String pName, 
		   Container p, boolean editable) {
        super(initValue, pName, p, editable);
        target = searchWindow;
    }
    public void selected() {
        target.doSearch();
    }
}



