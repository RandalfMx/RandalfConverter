/**
 * 
 */
package mx.randalf.converter.xls2Xml.quartz;

import java.io.File;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import mx.randalf.converter.xls2Xml.exception.RandalfConvertXls2XmlException;
import mx.randalf.converter.xls2Xml.quartz.scanFolder.FileFilterCsv;
import mx.randalf.converter.xls2Xml.quartz.scanFolder.FileFilterMaster;
import mx.randalf.converter.xls2Xml.quartz.scanFolder.FileFilterTsv;
import mx.randalf.converter.xls2Xml.quartz.scanFolder.FileFilterXls;
import mx.randalf.converterxls2xml.Input;
import mx.randalf.converterxls2xml.Xls2Xml;
import mx.randalf.quartz.QuartzTools;
import mx.randalf.quartz.job.JobExecute;

//import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author massi
 *
 */
public class ScanFileSystem extends JobExecute {

	private Logger log = LogManager.getLogger(ScanFileSystem.class);

	public static String XLS2XML = "xls2Xml";

	public static String NUMBERINPUT = "numberInput";

	private Input input = null;

	private FileFilterMaster filter = null;

	/**
	 * 
	 */
	public ScanFileSystem() {
	}


	@Override
	protected String jobExecute(JobExecutionContext context) throws JobExecutionException {
		Xls2Xml x2x = null;
		Integer numberInput = 0;
		String msg = null;
		try {
				x2x = (Xls2Xml) context.getJobDetail().getJobDataMap().get(XLS2XML);
				numberInput = (Integer) context.getJobDetail().getJobDataMap().get(NUMBERINPUT);
				
				input = x2x.getInput().get(numberInput.intValue());
				if (input.getFormatoFile().getTipo().equals("xls")){
					filter = new FileFilterXls(x2x, numberInput);
				} else if (input.getFormatoFile().getTipo().equals("csv")){
					filter = new FileFilterCsv(x2x, numberInput);
				} else if (input.getFormatoFile().getTipo().equals("tsv")){
					filter = new FileFilterTsv(x2x, numberInput);
				}
				System.out.println("["+QuartzTools.getName(context)+"] Inizio analisi cartella: "+input.getPath());
				scanFolder(new File(input.getPath()), context);
				System.out.println("["+QuartzTools.getName(context)+"] Fine analisi cartella: "+input.getPath());
				filter.closedAllThread();
				System.out.println("["+QuartzTools.getName(context)+"] Fine analisi cartella: "+input.getPath()+" TUTTI I JOBS TERMINATI");
				msg = "Fine analisi cartella: "+input.getPath()+" TUTTI I JOBS TERMINATI";
		} catch (JobExecutionException e) {
			throw e;
		} catch (SchedulerException e) {
			log.error(e.getMessage(), e);
			throw new JobExecutionException(e.getMessage(), e);
		} catch (RandalfConvertXls2XmlException e) {
			log.error(e.getMessage(), e);
			throw new JobExecutionException(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new JobExecutionException(e.getMessage(), e);
		}
		return msg;
	}

	private void scanFolder(File folder, JobExecutionContext context) throws RandalfConvertXls2XmlException{
		File[] fl = null;
		
		fl = folder.listFiles(filter);

		for (int x=0; x<fl.length; x++){
			if (fl[x].isDirectory()){
				scanFolder(fl[x], context);
			} else {
				try{
					System.out.println("["+QuartzTools.getName(context)+"] "+x+"/"+fl.length);
					elabFile(fl[x], context);
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	private void elabFile(File file, JobExecutionContext context) throws RandalfConvertXls2XmlException{
		try {
			System.out.println("["+QuartzTools.getName(context)+"] File da Analizzare: "+file.getAbsolutePath());
			filter.readFile(file);
			System.out.println("["+QuartzTools.getName(context)+"] Inizio Analizzi: "+file.getAbsolutePath());
			filter.start(context);
			System.out.println("["+QuartzTools.getName(context)+"] Fine Analizzi: "+file.getAbsolutePath());
		} catch (RandalfConvertXls2XmlException e) {
			throw e;
		}
	}
}
