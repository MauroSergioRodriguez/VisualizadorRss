package mauro.rodriguez.visualizadorrss.datos;

import android.util.Log;
import android.util.Xml;
import mauro.rodriguez.visualizadorrss.entidades.Noticia;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by Mauro Rodriguez on 01/06/2015.
 */
public class ParserNoticias {
    XmlPullParser parser = Xml.newPullParser();

    public ParserNoticias(StringReader sr){
        try{
            parser.setInput(sr);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Noticia> parseListaNoticias()
    {
        ArrayList<Noticia> noticias = null;
        int event;
        Noticia noticia = null;

        try
        {
            event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT)
            {
                String tag = "";
                switch (event)
                {
                    case XmlPullParser.START_DOCUMENT:
                        noticias = new ArrayList<Noticia>();
                        break;

                    case XmlPullParser.START_TAG:
                        tag = parser.getName();
                        if (tag.equals("item")) {
                            noticia = new Noticia();
                        }
                        if(noticia != null){
                            if(tag.equals("title")){
                                String n = new String(parser.nextText().getBytes("ISO-8859-1"), "UTF-8");
                                noticia.setTitulo(n);
                            }else if(tag.equals("link")){
                                String n = new String(parser.nextText().getBytes("ISO-8859-1"), "UTF-8");
                                noticia.setLink(n);
                            }else if(tag.equals("description")){
                                String n = new String(parser.nextText().getBytes("ISO-8859-1"), "UTF-8");
                                noticia.setDesc(n);
                            }else if(tag.equals("pubDate")){
                                String n = new String(parser.nextText().getBytes("ISO-8859-1"), "UTF-8");
                                noticia.setFecha(n);
                            }else if(tag.equals("thumbnail")){
                                noticia.setLinkImg(parser.getAttributeValue(null,"url"));
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        tag = parser.getName();
                        if (tag.equals("item") && noticia != null)
                        {
                            noticias.add(noticia);
                            noticia=null;
                        }
                        break;
                }
                event = parser.next();
            }
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
        return noticias;
    }
}