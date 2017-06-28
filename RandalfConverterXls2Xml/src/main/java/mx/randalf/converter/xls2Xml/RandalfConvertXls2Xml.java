/**
 * 
 */
package mx.randalf.converter.xls2Xml;

import java.io.File;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.quartz.JobKey;
import org.quartz.SchedulerException;

import mx.randalf.converter.xls2Xml.exception.RandalfConvertXls2XmlException;
import mx.randalf.converter.xls2Xml.quartz.ScanFileSystem;
import mx.randalf.converter.xls2Xml.xsd.Xls2XmlXsd;
import mx.randalf.converterxls2xml.Xls2Xml;
import mx.randalf.quartz.QuartzMaster;
import mx.randalf.quartz.QuartzTools;
import mx.randalf.xsd.exception.XsdException;

/**
 * @author massi
 *
 */
public class RandalfConvertXls2Xml extends QuartzMaster {

	private Logger log = Logger.getLogger(RandalfConvertXls2Xml.class);

	/**
	 * @throws RandalfConvertXls2XmlException 
	 * @throws SchedulerException 
	 * 
	 */
	public RandalfConvertXls2Xml() throws SchedulerException {
		super(true);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RandalfConvertXls2Xml rcx2x = null; 
		
		try {
			if (args.length==1){
				rcx2x = new RandalfConvertXls2Xml();
				rcx2x.esegui(new File(args[0]));
			} else {
				System.out.println("Indicare il nome del file  di configurazione per l'elaborazione del materiale");
			}
		} catch (RandalfConvertXls2XmlException e) {
			e.printStackTrace();
		} catch (SchedulerException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rcx2x !=null){
					System.out.println("RandalfConvertXls2Xml Verifico la Chiusura");
					rcx2x.closed(true);
					System.out.println("RandalfConvertXls2Xml Verifico la Chiusura CONFERMATA");
				}
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		}
	}

	public void esegui(File fileConfig) throws RandalfConvertXls2XmlException{
		Xls2XmlXsd x2xx = null;
		Xls2Xml x2x = null;
		
		try {
			x2xx = new Xls2XmlXsd();
			x2x = x2xx.read(fileConfig);
//			setnThread(x2x.getParameter().getNThread().intValue());
//			settSleep(x2x.getParameter().getTSleep().intValue());
			if (x2x.getParameter().getTSleepClosed() != null){
				settSleepClosed(x2x.getParameter().getTSleepClosed().intValue());
			}
			
			for (int x=0; x<x2x.getInput().size(); x++){
				add("ScanFileSystem", start(x2x, x));
			}
		} catch (XsdException e) {
			log.error(e.getMessage(), e);
			throw new RandalfConvertXls2XmlException(e.getMessage(), e);
		} catch (SchedulerException e) {
			log.error(e.getMessage(), e);
			throw new RandalfConvertXls2XmlException(e.getMessage(), e);
		}
	}


	/**
	 * Metodo utilizzato per l'esecuzione della scheduzione dell'attività
	 * relativo all'istituzione
	 * 
	 * @param mdIstituzione
	 * @return
	 * @throws SchedulerException
	 */
	private JobKey start(Xls2Xml x2x, Integer numberInput) throws SchedulerException {
		JobKey jobKey = null;
		Hashtable<String, Object> params = null;

		try {

			log.info("Schedulo il Job: numberInput." + numberInput);
			params = new Hashtable<String, Object>();
			params.put(ScanFileSystem.XLS2XML, x2x);
			params.put(ScanFileSystem.NUMBERINPUT, numberInput);
//			jobKey = QuartzTools.startJob(scheduler, JFolder.class, "Folder",
//					folder.getName(), "Folder", folder.getName(), params);
			jobKey = QuartzTools.startJob(scheduler, ScanFileSystem.class, "ScanFolder",
					x2x.getInput().get(numberInput).getPath(), "numberInput", numberInput+"", params);
		} catch (SchedulerException e) {
			throw e;
		}
		return jobKey;
	}

}
