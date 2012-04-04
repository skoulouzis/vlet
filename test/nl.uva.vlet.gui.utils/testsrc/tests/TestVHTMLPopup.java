package tests;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JTextPane;

import nl.uva.vlet.gui.vhtml.VHTMLKit;


public class TestVHTMLPopup
{
    private static String content = new StringBuffer()
            .append("<html><body>\n")
            .append("<a href=\"popup:It doest't have to be a valid URL\">\n")
            .append("Right click to see popup menu.</a><br>\n")
            .append("<a href=\"http://www.experts-exchange.com/jsp/qShow.jsp?qid=20143329\">\n")
            .append("You still can handle normal URLs.</a><br>\n")
            .append("And you can have mouse-insensitive text\n")
            .append("</body></html>\n")
            .toString();
        
    public static void main(String[] args) throws IOException
    {
            System.out.println(content);
            System.setErr(new PrintStream(new FileOutputStream("err.log")));
            JTextPane textPane = new JTextPane();
            textPane.setEditorKit(new VHTMLKit());
            textPane.setText(content);
            textPane.addHyperlinkListener(new VHTMLKit.LinkListener());
            textPane.setEditable(false);
            JFrame frame = new JFrame("HTML Popup Menu");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(textPane);
            frame.pack();
            frame.setSize(300,300);
            frame.setVisible(true);
    }
        
    
    
}
