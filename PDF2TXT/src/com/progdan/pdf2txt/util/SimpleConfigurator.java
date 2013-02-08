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
 */
package com.progdan.pdf2txt.util;

import java.net.URL;

import com.progdan.logengine.BasicConfigurator;
import com.progdan.logengine.Category;
import com.progdan.logengine.Level;

import com.progdan.logengine.spi.Configurator;
import com.progdan.logengine.spi.LoggerRepository;

/**
 * Log4J configurator.
 *
 * @author Robert Dickinson (bob@brutesquadlabs.com)
 * @version $Revision: 1.2 $
 */
public class SimpleConfigurator implements Configurator
{
    /**
     * Constructor.
     */
    public SimpleConfigurator()
    {
    }

    /**
     * Interpret a resource pointed to by a URL and set up log4J accordingly.
     * The configuration is done relative to the <code>heirarchy</code> parameter.
     *
     * @param url The URL to parse
     * @param repository The heirarchy to operate upon
     */
    public void doConfigure(URL url, LoggerRepository repository)
    {
        BasicConfigurator.configure();
        Category.getRoot().setLevel(Level.DEBUG);
    }
}
