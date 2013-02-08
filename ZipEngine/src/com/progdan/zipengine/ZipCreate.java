package com.progdan.zipengine;

//
// ZipCreate.java - A Tiny Software (tm) Project
//
// April 27, 1997
// Mark Nelson
// markn@tiny.com
// http://web2.airmail.net/markn
//
// This command line application is used create a Zip
// archive.  Its first argument is the zip file name,
// followed by a list of wild card file specifications.
// The wild cards are expanded into a list, duplicates
// are removed, and the zip file is created.
//
// Most of the work is done by an helper class called
// Zipper.  Class ZipCreate is the shell that calls
// the various worker routines in Zipper.
//
// Note that the wild card expansion is accomplished
// using the regular expression package pat.zip from
// Steven R. Brandt.  This is copyrighted shareware
// available at http://www.win.net/~stevesoft/pat.  To
// use it with this package, simply add the full path
// for patbin113.zip in in your CLASSPATH environment
// variable.  Under Windows 95, you might use a command
// line something like this:
//
//  set CLASSPATH=.;C:\JDK\LIB\CLASSES.ZIP;.\patbin113.zip
//
// To compile:  javac ZipCreate.java
//
// To run: java ZipCreate zip-name wild-spec [wild-spec ...]
//
// Requires JDK 1.1
//
import java.util.*;
import java.util.zip.*;
import java.io.*;

//
// ZipCreate delegates most of the work to class Zipper.  The
// zip file name is pulled in from the first command line arg.
// The remaining command line arguments are considered to be
// wild card args, and are passed to the Zipper method add(),
// which expands them and adds them to the list of files to be
// processed.
//
// Once all the command line args have been processed, the
// removeDuplicates() method is called to do just that, and
// then the zip file is created.
//
public class ZipCreate {
    public static void main( String[] args ) throws IOException
    {
        if ( args.length < 2 ) {
            System.out.println( "Usage: ZipCreate zipfile wild-name [wild-name ...]" );
            return;
        }
        String zip_name = args[ 0 ];
        if ( zip_name.indexOf( '.' ) == -1 )
            zip_name = zip_name + ".zip";
        try {
            Zipper zipper = new Zipper();
            for ( int i = 1 ; i < args.length ; i++ )
                zipper.addFiles( args[ i ] );
            zipper.removeDuplicates();
            zipper.create( zip_name );
        }
        catch (Exception e ) {
            System.out.println( "Exception: " + e );
            e.printStackTrace();
            File f = new File( zip_name );
            f.delete();
        }
    }
};

//
// Class Zipper does all the work needed to create a Zip
// archive.  After creating an empty Zipper object,
// the addFiles() method is repeatedly called to add files
// to the internal list, stored in Vector m_fileNames.
// Once that's complete, removeDuplicates() is called to
// sort the input list and remove any duplicate file names.
// Finally, create() is called to create the zip file and
// add the files to it.
//
class Zipper {
    //
    // m_fileNames is a vector that holds all the file names
    // that are going to be inserted into the zip file.
    //
    Vector m_fileNames;
    //
    // m_isWin95 is used to work around a problem in file name
    // processing. Win95 file names are case insensitive but
    // case preserving.  So under Win95, when attempting to remove
    // duplicates file names, I have to ignore case.  But I still
    // need to preserve the case for storage in the archive.
    // My test to see if this is Win95 is very simple, and prone
    // to error, but it is accomplished without resorting to
    // native methods.
    //
    static boolean m_isWin95;
    {
        File temp = new File( "C:\\MSDOS.SYS" );
        m_isWin95 = temp.exists();
    }
    //
    // The constructor creates a new vector to hold the filenames.
    //
    Zipper() {
        m_fileNames = new Vector();
    }
    //
    // The addFiles() method accepts a string that is a wildcard
    // filespec.  It does some minimal parsing of the wildcard,
    // the expands it and adds the resulting list of files to the
    // Vector holding all names.  Expanding the wildcard is done
    // using the list() method of File.  list() can be invoked with
    // a FileNameFilter object, which I supply using a regular
    // expression from the pat package.  Note that things get a
    // little tricky, because I have to determine what directory
    // the wild card should be expanded in.  If it doesn't look
    // like a directory was part of the wild card spec, I use "."
    // as the current directory.
    //
    // I noted one problem when expanding Win95 file names.  You
    // would expect that the getCanonicalPath() argument would always
    // return a unique file name for any file.  However, it turns out
    // that you can get a return from this function that looks like
    // C:\temp.dat or c:\temp.dat, with the drive name being either
    // upper or lower case.  This leads to a lot of complications
    // when searching for duplicate file names.
    //
    void addFiles( String filespec ) throws IOException
    {
        File f = new File( filespec );
        String parent = f.getParent();
        if ( parent == null )
            parent = ".";

        File dir = new File( parent );
        String wildname = f.getName();
        String[] files = dir.list( buildRegex( wildname ) );
        //
        // I have a list of file names, now I have to add
        // them to my list.  First things first, though,
        // I have to check to see if any of the files I
        // got back in the list are actually directories.
        // If they are, I toss them out.
        //
        for ( int i = 0 ; i < files.length ; i++ ) {
            String name;
            File tempf;
            if ( parent.equals( "." ) )
                tempf = new File( files[ i ] );
            else
                tempf = new File( parent + f.separator + files[ i ] );
            if ( tempf.isDirectory() == false ) {
                if ( parent.equals( "." ) )
                    m_fileNames.addElement( tempf.getName() );
                else
                    m_fileNames.addElement( tempf.getCanonicalPath() );
            }
        }
    }
    //
    // It's possible to have lots of duplicates of individual
    // filenames when using overlapping duplicates.  We want to
    // eliminate this.  I do this by first sorting the filenames
    // in the list, so that any duplicates are adjacent to one
    // another.  I then make a second pass through the list,
    // throwing out any adjacent duplicates.  Under Win95,
    // names that differ only in case will invariably point to
    // the same file, so all sorting and comparison are done on
    // a case insensitive basis.
    //
    void removeDuplicates()
    {
        for ( int i = 0 ; i < ( m_fileNames.size() - 1 ) ; i++ )
            for ( int j = i + 1 ; j < m_fileNames.size() ; j++ ) {
                String s1 = (String) m_fileNames.elementAt( i );
                String s2 = (String) m_fileNames.elementAt( j );
                if ( m_isWin95 ) {
                    s1 = s1.toUpperCase();
                    s2 = s2.toUpperCase();
                }
                if ( s1.compareTo( s2 ) > 0 ) {
                    Object temp = m_fileNames.elementAt( i );
                    m_fileNames.setElementAt( m_fileNames.elementAt( j ), i );
                    m_fileNames.setElementAt( temp, j );
                }
            }
        for ( int i = 0 ; i < ( m_fileNames.size() - 1 ) ;  ) {
            String s1 = new String( (String) m_fileNames.elementAt( i ) );
            String s2 = new String( (String) m_fileNames.elementAt( i + 1 ) );
            if ( m_isWin95 ) {
                s1 = s1.toUpperCase();
                s2 = s2.toUpperCase();
            }
            if ( s1.equals( s2 ) )
                m_fileNames.removeElementAt( i + 1 );
            else
                i++;
        }
    }
    //
    // create() is the final method called when creating a new
    // Zip archive.  It actually creates the file, then works
    // its way through the list, calling protected method add()
    // to do the work of actually adding the file to the zip.  The
    // creation of a Zip file is actually done with the use of
    // the ZipFile class.  Simply writing new entries to
    // a ZipOutputStream object does all of the necessary work.
    //
    // This routine is complicated by the fact that I have to
    // check every file to make sure I'm not adding a copy of
    // the zip file to itself.  That is often disastrous!
    //
    void create( String zip_file_name ) throws IOException {
        File zip_file = new File( zip_file_name );
        String full_name = zip_file.getCanonicalPath();
        ZipOutputStream z = new ZipOutputStream( new FileOutputStream( zip_file ) );
        for ( int i = 0 ; i < m_fileNames.size() ; i++ ) {
            File test = new File( (String) m_fileNames.elementAt( i ) );
            String test_name = test.getCanonicalPath();
            boolean skip;
            if ( m_isWin95 )
                skip = test_name.equalsIgnoreCase( full_name );
            else
                skip = test_name.equals( full_name );
            if ( skip )
                System.out.println( "Skipping " + zip_file_name );
            else
                add( z, (String) m_fileNames.elementAt( i ) );
        }
        z.close();
    }
    //
    // Steven Brandt's regular expression package uses
    // a syntax slightly different from the format used
    // for file expansion under MS-DOS, Win95 or *NIX.
    // This routine converts a standard command line
    // wild card expression to the format expected by
    // the pat package.  Once the wild card is in that
    // format, it can be used as an argument to the
    // list() method of File, which makes it very handy.
    //
    protected Regex buildRegex( String wildname ) {
        String regex = new String();
        for ( int i = 0 ; i < wildname.length() ; i++ ) {
            char c = wildname.charAt( i );
            switch ( c ) {
                case '*' : regex += "(.*)"; break;
                case '?' : regex += "(.?)"; break;
                default  : regex += c;
            }
        }
        return new Regex( regex );
    }
    //
    // This protected method is used to add a single file to a
    // Zip archive, via a ZipOutputStream  object.  It first has
    // to do a couple of things that java.util.zip ought to do
    // but doesn't.  First, it strips any leading drive or path
    // components from the file, just as Zip or PKZIP will do.
    // Second, it changes any backslash characters to forward
    // slashes.  It then creates a new ZipEntry() object, and
    // calls the putNextEntry() method of ZipOutputStream, which
    // writes out most of the file info to the Zip file.
    // After that, the entire input file is copied to the
    // ZipOutputStream, which compresses it transparently whenever
    // we call the write() method.  While this is going on
    // pacifier strings are written to the screen.  Finally, after
    // the entire file has been written, the closeEntry() method
    // is called for ZipOutputStream.  This will take care of
    // writing the file size, compressed size, and CRC to the
    // Zip file, as well as adding data to the central directory.
    //
    protected void add( ZipOutputStream z, String file_name ) throws IOException {
        String entry_name = new String( file_name );
        if ( m_isWin95 ) {
            if ( entry_name.charAt( 1 ) == ':' )
                entry_name = entry_name.substring( 2 );
        }
        if ( entry_name.charAt( 0 ) == File.separatorChar )
            entry_name = entry_name.substring( 1 );
        ZipEntry entry = new ZipEntry( entry_name.replace( '\\', '/' ) );
        z.putNextEntry( entry );
        FileInputStream f = new FileInputStream( file_name );
        System.out.print( entry_name + " " );
        byte[] buf = new byte[ 10240 ];
        for ( int i = 0 ; ; i++ ) {
            int len = f.read( buf );
            if ( len < 0 )
                break;
            z.write( buf, 0, len );
            switch ( i & 3 ) {
                case 0  : System.out.print( "|\b" ); break;
                case 1  : System.out.print( "/\b" ); break;
                case 2  : System.out.print( "-\b" ); break;
                default : System.out.print( "\\\b" ); break;
            }
        }
        z.closeEntry();
        System.out.println( " " );
    }
};
