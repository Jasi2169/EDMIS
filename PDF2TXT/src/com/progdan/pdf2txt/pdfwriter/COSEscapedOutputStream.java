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
package com.progdan.pdf2txt.pdfwriter;


import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Simple output stream escaping literal strings in the physical pdf file.
 *
 * @author Michael Traut
 * @version $Revision: 1.2 $
 */
public class COSEscapedOutputStream extends FilterOutputStream
{
    /**
     * the escape character in strings.
     */
    public static final byte[] ESCAPE = "\\".getBytes();

    /**
     * CR escape characters.
     */
    public static final byte[] CR_ESCAPE = "\\n".getBytes();
    /**
     * LF escape characters.
     */
    public static final byte[] LF_ESCAPE = "\\r".getBytes();
    /**
     * HT escape characters.
     */
    public static final byte[] HT_ESCAPE = "\\t".getBytes();
    /**
     * BS escape characters.
     */
    public static final byte[] BS_ESCAPE = "\\b".getBytes();
    /**
     * FF escape characters.
     */
    public static final byte[] FF_ESCAPE = "\\f".getBytes();

    /**
     * COSOutputStream constructor comment.
     *
     * @param out The wrapped output stream.
     */
    public COSEscapedOutputStream(OutputStream out)
    {
        super(out);
    }

    /**
     * This will escape a byte if necessary.
     *
     * @param b The byte to write.
     *
     * @throws IOException If the underlying stream throws an exception
     */
    public void write(int b) throws IOException
    {
        switch( b )
        {
            case '(':
            case ')':
            case '\\':
            {
                out.write(ESCAPE);
                out.write(b);
                break;
            }
            case 10: //LF
            {
                out.write( LF_ESCAPE );
                break;
            }
            case 13: // CR
            {
                out.write( CR_ESCAPE );
                break;
            }
            case '\t':
            {
                out.write( HT_ESCAPE );
                break;
            }
            case '\b':
            {
                out.write( BS_ESCAPE );
                break;
            }
            case '\f':
            {
                out.write( FF_ESCAPE );
                break;
            }
            default:
            {
                out.write( b );
            }
        }
    }
}
