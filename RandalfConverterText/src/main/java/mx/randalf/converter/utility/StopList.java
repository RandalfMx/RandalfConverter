/**
 * 
 */
package mx.randalf.converter.utility;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Questa classe viene utilizzata per verificare la stopList
 * 
 * @author Randazzo Massimiliano
 * @version 0.1
 * @serialData 04/05/2006
 */
public class StopList
{

	/**
   * Questa variabile viene utilizzata per loggare l'applicazione
   */
	private static Logger log = LogManager.getLogger(StopList.class);

  /**
   * Costruttore principale
   */
  public StopList()
  {
    super();
  }

  /**
   * Questo metodo viene utilizzato per verificare se una parola � presente nella stopList 
   * oppure no
   * 
   * @param parola Parola da verificare 
   * @return risultato del confronto
   */
  public static boolean checkStopList(String parola)
  {
    String ris = "N";
    Properties prop = new Properties();
    InputStream is = null;
    try
    {
    	is = StopList.class.getResource("StopList.properties").openStream();
      prop.load(is);
      ris = prop.getProperty(parola.toLowerCase(),"N");
    }
    catch (FileNotFoundException e)
    {
      log.error(e.getMessage(), e);
    }
    catch (IOException e)
    {
      log.error(e.getMessage(), e);
    }
    finally
    {
    	try
			{
				if (is != null)
					is.close();
			}
			catch (IOException e)
			{
	      log.error(e.getMessage(), e);
			}
    }
    return (!ris.equals("N"));
    
  }

}
