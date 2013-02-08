package com.progdan.zipengine;

//
// ZipList.java - A Tiny Software (tm) Project
//
// April 27, 1997
// Mark Nelson
// markn@tiny.com
// http://web2.airmail.net/markn
//
// This command line Java application provides a brief listing
// of the contents of a Zip file. The ZipFile class makes
// this easy by providing an Enumeration object that can be
// used to step through all the entries in the Zip file.  The
// ZipEntry object returned by the enumeration is used to get
// the file name, size, compressed size, and timestamps.
//
// To compile:  javac ZipList.java
//
// To run: java ZipList zip-name
//
// Requires JDK 1.1
//
import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.text.*;

public class ZipList
{
    public static void main( String[] args )
    {
        if ( args.length < 1 ) {
            System.out.println( "Usage: ZipList zipfile" );
            return;
        }
        //
        // Printing the times and dates require a little bit
        // of work beforehand.  The two formatting objects
        // are what do the job inside the main loop.
        //
        DateFormat df= DateFormat.getDateInstance();
        DateFormat tf= DateFormat.getTimeInstance();
        tf.setTimeZone( TimeZone.getDefault() );
        //
        try {
            ZipFile z = new ZipFile( args[ 0 ] );
            System.out.println( "Listing of : " + z.getName() );
            System.out.println( "" );
            System.out.println( "  Size    Raw Size     Date       Time                Name" );
            System.out.println( "--------  ---------  ---------  ---------- --------------------------" );
            Enumeration enum = z.entries();
            while ( enum.hasMoreElements() ) {
                ZipEntry e = (ZipEntry) enum.nextElement();
                Date d = new Date( e.getTime() );
                System.out.print( format( e.getSize(), 9 ) + " " );
                System.out.print( format( e.getCompressedSize(), 9 ) + " " );
                System.out.print( " " + df.format( d ) + " " );
                System.out.print( " " + tf.format( d ) + " " );
                System.out.println( " " + e.getName() );
            }
        }
        catch (IOException ioe ) {
            System.out.println( "exception: " + ioe );
            ioe.printStackTrace();
        }
    }
    //
    // This function is used to print a long integer using a
    // specific width.
    //
    static String format( long l, int width )
    {
        String s = new Long( l ).toString();
        while ( s.length() < width )
            s += " ";
        return s;
    }
}
