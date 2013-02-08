package com.progdan.zipengine;

//
// ZipExtract.java - A Tiny Software (tm) Project
//
// April 27, 1997
// Mark Nelson
// markn@tiny.com
// http://web2.airmail.net/markn
//
// This graphical application is used extract files from
// a Zip File. As is often the case in GUI programs, the
// majority of the code is concerned with the User
// Interface, and it tends to obscure the code concerned
// with actual data processing.
//
// This program complicates things further by spurning the
// normal layout manager.  Instead, I fix the size of the
// framing window and use my own functions to position the
// controls on the window.  If the window is too small or
// too large on your system, you should be able to adjust
// the size of the window by modifying the call to init().
//
// There are two methods in class ZipExtract that actually
// work with the Zip file. Method readZipFile() reads the
// entries in the zip file and adds them to a list box.
// Method extractFiles() is used to extract the selected
// files from the Zip file.
//
// Note that the UI uses the JDK 1.1 event model.  If you
// are accustomed to the 1.0 AWT event model, you should be
// able to catch up on this quickly by looking up the docs
// on the ActionListener, WindowListener, and KeyListener
// interfaces.
//
// To compile:  javac ZipExtract.java
//
// To run: java ZipExtract
//
// Requires JDK 1.1
//
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.zip.*;

class ZipExtract extends Frame implements ActionListener,
                                          WindowListener,
                                          KeyListener {
    MyLayout m_layout;
    TextField m_textField;
    java.awt.List m_list;
    Label m_message;
    //
    public static void main( String[] args ) {
        ZipExtract f = new ZipExtract();
    }
    //
    // The constructor creates the frame window, and
    // adds three buttons used to read the Zip file,
    // extract from it, and exit the program.  It also
    // adds a text box for entering the Zip file name,
    // and a list box for selecting entries to be
    // extracted.
    //
    ZipExtract() {
        addWindowListener( this );
        setTitle( "Zip Extract - paths will be stripped!" );
        setBackground( Color.lightGray );
        m_layout = new MyLayout( this );
        m_layout.init( 350, 175 );
        add( new Label( "Zip file: ", Label.RIGHT ), 0, 0, 33, 17 );
        m_textField = new TextField();
        add( m_textField, 33, 0, 67, 17 );
        add( new Button( "Read Zip File" ), 0, 33, 33, 17 );
        add( new Button( "Extract Files" ), 0, 50, 33, 17 );
        add( new Button( "Exit" ), 0, 67, 33, 17 );
        m_list = new java.awt.List( 1, true );
        add( m_list, 33, 17, 67, 67 );
        m_message = new Label();
        add( m_message, 0, 83, 100, 17 );
    }
    //
    // This method is a utility routine I call to add
    // controls to the framing window.  It uses my layout
    // class to position and size the control, and adds
    // the control to the appropriate listener list.
    //
    void add( Component c, int x, int y, int width, int height ) {
        add( c );
        m_layout.setLocation( c, x, y );
        m_layout.setSize( c, width, height );
        if ( c instanceof Button )
            ((Button) c).addActionListener( this );
        if ( c instanceof TextField )
            ((TextField) c).addKeyListener( this );
    }
    //
    // When the user pushes the Read button, this
    // routine is called.  It creates a new Zip object,
    // then uses the entries() method to get an
    // Enumerator object.  That object provides
    // a list of ZipEntry objects, which are used to
    // get the names of each file in the Zip file. The
    // names are inserted in the list box.
    //
    void readZipFile() {
        m_message.setText( "" );
        m_list.removeAll();
        String text = m_textField.getText();
        try {
            ZipFile z = new ZipFile( text );
            Enumeration enum = z.entries();
            while ( enum.hasMoreElements() ) {
                ZipEntry e = (ZipEntry) enum.nextElement();
                    m_list.add( e.getName() );
            }
        }
        catch ( IOException ioe ) {
            m_list.add( "Error reading Zip file!" );
        }
    }
    //
    // To extract files, I walk through the selected indices in the
    // list box.  I get the string from the list box, and use that
    // as an argument to the ZipFile.getEntry() method.  This returns
    // a ZipEntry object.  That object then provides me with a
    // ZipInputStream object that can read the deflated file data
    // in the Zip file.  I then pass this object to the extractOneFile()
    // method, which takes care of reading the bytes, storing them
    // in the output, and updating the user interface.
    //
    public void extractFiles() {
        try {
            String text = m_textField.getText();
            ZipFile z = new ZipFile( text );
            m_message.setText( "Extracting selected files from " + text + "..." );
            int[] indices = m_list.getSelectedIndexes();
            for ( int i = 0 ; i < indices.length ; i++ ) {
                m_message.setText( "Extracting: " + m_list.getItem( indices[ i ] ) );
                ZipEntry entry = z.getEntry( m_list.getItem( indices[ i ] ) );
                InputStream input = z.getInputStream( entry );
                String temp = new String( m_list.getItem( indices[ i ] ) );
                int k = temp.lastIndexOf( "/" );
                if ( k >= -1 )
                    temp = temp.substring( k + 1 );
                k = temp.lastIndexOf( "\\" );
                if ( k >= -1 )
                    temp = temp.substring( k + 1 );
                System.out.println( "Extracting to " + temp );
                extractOneFile( m_list.getItem( indices[ i ] ), temp, input );
                temp = null;
            }
        }
        catch (IOException ioe ) {
           m_message.setText( "" + ioe );
        }
    }
    //
    // This method does all the work to extract a single
    // file from the ZipFile.  The java.util.zip package
    // doesn't have a handy extract() function, it simply
    // provides a stream and lets you do the work.  This
    // makes more work for you, but it also makes it easy
    // to implement your own feedback method.  This routine
    // simply prints a line of text every 100K bytes.
    //
    public void extractOneFile( String full_name,
                                String short_name,
                                InputStream input ) throws IOException {
        FileOutputStream output = new FileOutputStream( short_name );
        byte[] buf = new byte[ 100000 ];
        int old_pacifier = -1;
        int j;
        for ( j = 0 ; ;  ) {
            int length = input.read( buf );
            if ( length <= 0 )
                break;
            j += length;
            output.write( buf, 0, length );
            int new_pacifier = ( j / 100000 ) * 100000;
            if ( new_pacifier != old_pacifier ) {
                 m_message.setText( "Extracting: " +
                                    full_name +
                                    ": " +
                                    new_pacifier );
                 old_pacifier = new_pacifier;
            }
        }
        output.close();
        m_message.setText( "Extracting: " +
                           full_name +
                           ": complete, " +
                           j + " bytes" );
    }
    //
    // The remaining methods in this class are the
    // event handlers for this class.
    //
    public void actionPerformed( ActionEvent e ) {
        if ( e.getActionCommand().equals( "Exit" ) )
            System.exit(0);
        else if ( e.getActionCommand().equals( "Read Zip File" ) )
            readZipFile();
        else if ( e.getActionCommand().equals( "Extract Files" ) )
            extractFiles();
    }
    public void windowActivated(WindowEvent e) {
    }
    public void windowClosed(WindowEvent e) {
    }
    public void windowClosing(WindowEvent e) {
        System.exit(0);
    }
    public void windowDeactivated(WindowEvent e) {
    }
    public void windowDeiconified(WindowEvent e) {
    }
    public void windowIconified(WindowEvent e) {
    }
    public void windowOpened(WindowEvent e) {
    }
    public void keyTyped(KeyEvent e) {
    }
    public void keyPressed(KeyEvent e) {
        if ( e.getKeyCode() == 10 ) { //Enter in the text box
            readZipFile();
        }
    }
    public void keyReleased(KeyEvent e) {
    }
};

//
// In place of a layout manager, I use a fixed frame
// window in this program.  The init() function sets
// the size of the window.  New components are placed
// on the window using two functions: setLocation() and
// setSize().  Both of these functions use x and y
// values between 0 and 100 that represent percentages
// of the framing window.  This means you can change
// the size in pixels of the window without modifying
// the calls that position controls.
//
class MyLayout {
    int m_width;
    int m_height;
    Frame m_frame;
    static final int m_hGap = 7;
    static final int m_vGap = 5;
    Insets m_insets;

    MyLayout( Frame f ) {
        m_frame = f;
    }

    void init( int width, int height ) {
        m_width = width;
        m_height = height;
        m_frame.setSize( m_width, m_height );
        m_frame.setLayout( null );
        m_frame.setResizable( false );
        m_frame.show();
        m_insets = m_frame.getInsets();
        m_frame.setSize( m_width + m_insets.left + m_insets.right + m_hGap,
                         m_height + m_insets.top + m_insets.bottom + m_vGap );
    }
    void setLocation( Component c, int x, int y ) {
        c.setLocation( m_hGap + m_insets.left + ( x * m_width / 100 ),
                       m_vGap + m_insets.top + ( y * m_height / 100 ) );
    };
    void setSize( Component c, int width, int height ) {
        c.setSize( ( ( width * m_width ) / 100 ) - m_hGap,
                   ( ( height * m_height ) / 100 ) - m_vGap );
    };
};


