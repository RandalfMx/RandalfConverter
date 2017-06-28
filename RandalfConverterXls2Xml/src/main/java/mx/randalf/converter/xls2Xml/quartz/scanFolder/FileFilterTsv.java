/**
 * 
 */
package mx.randalf.converter.xls2Xml.quartz.scanFolder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.UUID;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.SchedulerException;

import mx.randalf.converter.xls2Xml.exception.RandalfConvertXls2XmlException;
import mx.randalf.converter.xls2Xml.quartz.GenMag;
import mx.randalf.converterxls2xml.Input.Image;
import mx.randalf.converterxls2xml.Xls2Xml;
import mx.randalf.quartz.QuartzTools;
import mx.randalf.quartz.job.JobExecute;

/**
 * @author massi
 *
 */
public class FileFilterTsv extends FileFilterMaster {
	
//	private Vector<JobKey> listJobs = null;

	private Hashtable<String, Vector<DatiScheda>> datiBib = null;

	private Logger log = Logger.getLogger(FileFilterTsv.class);

	/**
	 * @param suffix
	 * @throws SchedulerException 
	 */
	public FileFilterTsv(Xls2Xml x2x, Integer numberInput) throws SchedulerException {
		super(".tsv", x2x, numberInput);
	}

	@Override
	public void readFile(File f) throws RandalfConvertXls2XmlException{
		BufferedReader br = null;
		FileReader fr = null;
		String line = null;
		String[] st = null;
		String key = null;
		Object value = null;
		Vector<DatiScheda> datiBibs = null; 
		String pStart = null;
		String pStop = null;
		DatiScheda datiScheda = null;
		boolean newDatiScheda = false;

		try {
			fr = new FileReader(f);
			br = new BufferedReader(fr);

			for (int x=0; x<x2x.getInput().get(numberInput.intValue()).getFormatoFile().getPrimaRiga().intValue(); x++){
				br.readLine();
			}

			datiBib = new Hashtable<String, Vector<DatiScheda>>();
			while ((line = br.readLine())!=null){
				st = line.split("\t");

				pStart = null;
				pStop = null;
				
				if (x2x.getOutput().getPagina()!= null){
					if (!DatiScheda.isEmpty(st, x2x.getOutput().getPagina())){
						pStart = DatiScheda.readCol(st, x2x.getOutput().getPagina());
						pStop = DatiScheda.readCol(st, x2x.getOutput().getPagina());
					}
				} else {
					if (!DatiScheda.isEmpty(st, x2x.getOutput().getPagine().getStart()) &&
							!DatiScheda.readCol(st, x2x.getOutput().getPagine().getStart()).trim().equals("0")){
						pStart = DatiScheda.readCol(st, x2x.getOutput().getPagine().getStart());
						if (!DatiScheda.isEmpty(st, x2x.getOutput().getPagine().getStop()) &&
								!DatiScheda.readCol(st, x2x.getOutput().getPagine().getStop()).trim().equals("0")) {
							pStop = DatiScheda.readCol(st, x2x.getOutput().getPagine().getStop());
						} else {
							pStop = DatiScheda.readCol(st, x2x.getOutput().getPagine().getStart());
						}
					}
				}
				if (pStart != null &&
						pStop != null){
					key = "";
					for (int x=0; x<x2x.getOutput().getBib().getKey().getDati().size(); x++){
						value =DatiScheda.readDati(x2x.getOutput().getBib().getKey().getDati().get(x), st);
						if (value != null){
							key += (key.equals("")?"":"_")+((String)value).trim();
						}
					}
					
					if (!key.trim().equals("")){
						if (datiBib.get(key)==null){
							datiBibs = new Vector<DatiScheda>();
						} else {
							datiBibs = datiBib.get(key);
						}
						datiScheda = new DatiScheda(st, x2x.getOutput(), pStart, pStop, 
								x2x.getParameter().getPathImageMagick());
						newDatiScheda = true;
						try{
							for (int x=0; x<datiBibs.size(); x++){
								if (datiBibs.get(x).getPagInizio()>datiScheda.getPagInizio()){
									System.out.println("insertElementAt: "+x+" - "+datiBibs.get(x).getPagInizio()+" > "+datiScheda.getPagInizio());
									datiBibs.insertElementAt(datiScheda, x);
									newDatiScheda = false;
									break;
								}
							}
						}catch(NumberFormatException e){
							e.printStackTrace();
						}catch(Exception e){
							e.printStackTrace();
						}
						if (newDatiScheda){
							datiBibs.add(datiScheda);
						}
						datiBib.put(key, datiBibs);
					}
				}
			}
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new RandalfConvertXls2XmlException(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new RandalfConvertXls2XmlException(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RandalfConvertXls2XmlException(e.getMessage(), e);
		} finally {
			try {
				if (br != null){
					br.close();
				}
				if (fr != null){
					fr.close();
				}
			} catch (IOException e) {
				log.error(e.getMessage(), e);
				throw new RandalfConvertXls2XmlException(e.getMessage(), e);
			} finally {
				System.out.println("Record Trovati: "+datiBib.size());
			}
		}
	}

	@Override
	public void start(JobExecutionContext context) throws RandalfConvertXls2XmlException {
		Enumeration<String> keys = null;
		String key = null;
		
		try {
			setScheduler(context.getScheduler());
			keys = datiBib.keys();
			while(keys.hasMoreElements()){
				key = keys.nextElement();

				add(QuartzTools.getName(context), 
						start(
								context, 
								x2x.getInput().get(numberInput).getImage(), 
								new File(x2x.getOutput().getPath()),
								datiBib.get(key), key));
//				listJobs=QuartzTools.checkJobs(context, listJobs, x2x.getParameter().getNThread().intValue(), x2x.getParameter().getTSleep().intValue());
//				listJobs.add(
//						start(
//								context, 
//								x2x.getInput().get(numberInput).getImage(), 
//								new File(x2x.getOutput().getPath()),
//								datiBib.get(key), key));
			}
		} catch (SchedulerException e) {
			log.error(e.getMessage(), e);
			throw new RandalfConvertXls2XmlException(e.getMessage(),e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RandalfConvertXls2XmlException(e.getMessage(),e);
		} finally {
			try {
				closedAllThread();
			} catch (SchedulerException e) {
				log.error(e.getMessage(), e);
				throw new RandalfConvertXls2XmlException(e.getMessage(),e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private JobKey start (JobExecutionContext context, 
			Image inputImage, 
			File folderOutput, 
			Vector<DatiScheda> datiScheda, 
			String key) throws SchedulerException{
		JobKey jobKey = null;
		Hashtable<String, Object> params = null;
		Class<?> myClass = null;

		try {
			log.info("["+QuartzTools.getName(context)+"] Schedulo il Job: File."+key);
			params = new Hashtable<String, Object>();
			params.put(GenMag.INPUTIMAGE, inputImage);
			params.put(GenMag.FOLDEROUTPUT, folderOutput);
			params.put(GenMag.DATISCHEDA, datiScheda);

			if (x2x.getOutput().getTypeFileOutput().equals(GenMag.TYPE)){
				myClass = GenMag.class;
			}
			if (myClass != null){
				jobKey = QuartzTools.startJob(context.getScheduler(), (Class<? extends JobExecute>) myClass, 
						context.getJobDetail().getKey().getGroup()+"."+ 
						context.getJobDetail().getKey().getName(), 
						key,
						"File",
						key+" - "+UUID.randomUUID().toString(),
						params);
			} else {
				log.error("Non è possibile eseguire la verifica per il tipo File ["+x2x.getOutput().getTypeFileOutput()+"]");
			}
		} catch (SchedulerException e) {
			log.error("["+QuartzTools.getName(context)+"] "+e.getMessage(), e);
			throw e;
		}
		return jobKey;
	}

	
}
