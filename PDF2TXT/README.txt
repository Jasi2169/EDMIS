Contact
------------------------------------
If you need help or would like to submit a bug or feature request then please
ask on the sourceforge forums or post a bug there.  Don't forget to attach 
any PDF documents that you are having problems with.  Some PDFs are too
large for sourceforge, please email me the PDF and reference in the bug
description.

ben@csh.rit.edu
http://www.csh.rit.edu/~ben

The project page can be found at:

http://www.pdfbox.org

The project is managed at SourceForge:

http://sourceforge.net/projects/pdfbox


Extracting text from a PDF document.
------------------------------------

1. Make sure that the PDFBox jar is in your classpath

2. Run the following command

    java org.pdfbox.ExtractText <PDF-file> <output-text-file>


Note: Ensure that all jar files in the external directory are in your classpath.



Integrating with a search engine.
------------------------------------
Currently there is only direct support for the lucene search engine.
You can look at how that is implemented to write your own or you can e-mail
me to request support for one.

  Lucene
  ------
  In your IndexHTML or IndexFiles that you use for your search engine you can
  simply call the getDocument method in
  org.pdfbox.searchengine.lucene.LucenePDFDocument when you encounter a PDF
  document that needs to be indexed.  Refer to the javadocs for the API to
  this class.

Building the project
-------------------------------------
1. Install ant if not already installed.
2. Copy the PDFBOX_HOME\external\junit.jar to ANT_HOME\lib
3. Run the 'ant compile' task.

Using the ANT task
--------------------------------------

1. Define the task using something similiar to the following

    <path id="build.classpath">
        <pathelement path="${pdfbox.jar}" />
    </path>
    <taskdef name="pdf2text" classname="org.pdfbox.ant.PDFToTextTask" classpathref="build.classpath" />

2. Use the task with filesets that determine which PDF's to convert

    <pdf2text>
        <fileset dir="test">
            <include name="**/*.pdf" />
        </fileset>
    </pdf2text>