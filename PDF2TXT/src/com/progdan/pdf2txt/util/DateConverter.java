/**
 * Copyright (c) 2004, ProgDan® Software
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of pdf2txt; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://progdan.no-ip.org:25000
 *
 */
package com.progdan.pdf2txt.util;

import java.text.SimpleDateFormat;

import java.io.IOException;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * This class is used to convert dates to strings and back using the PDF
 * date standards.  Date are described in PDFReference1.4 section 3.8.2
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class DateConverter
{
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat( "yyyyMMddHHmmss" );

    /**
     * This will convert the calendar to a string.
     *
     * @param date The date to convert to a string.
     *
     * @return The date as a String to be used in a PDF document.
     */
    public String toString( Calendar date )
    {
        return "D:" + DATE_FORMAT.format( date.getTime() );
    }

    /**
     * This will convert a string to a calendar.
     *
     * @param date The string representation of the calendar.
     *
     * @return The calendar that this string represents.
     *
     * @throws IOException If the date string is not in the correct format.
     */
    public Calendar toCalendar( String date ) throws IOException
    {
        //these are the default values
        int year = 0;
        int month = 1;
        int day = 1;
        int hour = 0;
        int minute = 0;
        int second = 0;
        //first string off the prefix if it exists
        try
        {
            if( date.startsWith( "D:" ) )
            {
                date = date.substring( 2, date.length() );
            }
            if( date.length() < 4 )
            {
                throw new IOException( "Error: Invalid date format '" + date + "'" );
            }
            year = Integer.parseInt( date.substring( 0, 4 ) );
            if( date.length() > 6 )
            {
                month = Integer.parseInt( date.substring( 4, 6 ) );
            }
            if( date.length() > 8 )
            {
                day = Integer.parseInt( date.substring( 6, 8 ) );
            }
            if( date.length() > 10 )
            {
                hour = Integer.parseInt( date.substring( 8, 10 ) );
            }
            if( date.length() > 12 )
            {
                minute = Integer.parseInt( date.substring( 10, 12 ) );
            }
            if( date.length() > 14 )
            {
                second = Integer.parseInt( date.substring( 12, 14 ) );
            }
            return new GregorianCalendar( year, month-1, day, hour, minute, second );
        }
        catch( NumberFormatException e )
        {
            return null;
        }

    }
}
