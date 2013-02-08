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
package com.progdan.pdf2txt.pdfviewer;

import java.awt.Graphics;

import java.io.IOException;

import javax.swing.JPanel;

import com.progdan.pdf2txt.pdmodel.PDPage;

import com.progdan.pdf2txt.pdmodel.common.PDRectangle;

/**
 * This is a simple JPanel that can be used to display a PDF page.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.1 $
 */
public class PDFPagePanel extends JPanel
{

    private PDPage page;
    private PageDrawer drawer = new PageDrawer();

    /**
     * Constructor.
     */
    public PDFPagePanel()
    {
    }

    /**
     * This will set the page that should be displayed in this panel.
     *
     * @param pdfPage The page to draw.
     */
    public void setPage( PDPage pdfPage )
    {
        page = pdfPage;
        PDRectangle pageSize = page.findMediaBox();
        setSize( pageSize.createDimension() );
        setBackground( java.awt.Color.white );
    }

    /**
     * @see JPanel#paint( Graphics )
     */
    public void paint(Graphics g )
    {
        try
        {
            g.setColor( getBackground() );
            g.fillRect( 0, 0, getWidth(), getHeight() );
            drawer.drawPage( g, page );
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }
    }
}
