/**
 * 
 */
package mx.randalf.converter.xls2Xml.quartz;

import java.io.File;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import it.sbn.iccu.metaag1.Img;
import it.sbn.iccu.metaag1.Metadigit;
import mx.randalf.converter.xls2Xml.quartz.scanFolder.DatiScheda;
import mx.randalf.converterxls2xml.Input.Image;
import mx.randalf.mag.MagXsd;
import mx.randalf.quartz.QuartzTools;

/**
 * @author massi
 *
 */
public class GenMag implements Job {

	public static String INPUTIMAGE = "inputImage";

	public static String FOLDEROUTPUT = "folderOutput";

	public static String DATISCHEDA = "datiScheda";

	public static String TYPE = "mag";

	private Logger log = Logger.getLogger(GenMag.class);

	/**
	 * 
	 */
	public GenMag() {
	}

	/**
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Image inputImage = null;
		File folderOutput = null;
		Vector<DatiScheda> datiScheda = null;
		File fMag = null;
		File fMagCert = null;
		int numImgOutput = 0;
		Metadigit mag = null;
		MagXsd magXsd = null;
		Img img = null;

		try {
			if (QuartzTools.checkJob(context)){

				inputImage = (Image) context.getJobDetail().getJobDataMap().get(INPUTIMAGE);
				folderOutput = (File) context.getJobDetail().getJobDataMap().get(FOLDEROUTPUT);
				datiScheda = (Vector<DatiScheda>) context.getJobDetail().getJobDataMap().get(DATISCHEDA);

				magXsd = new MagXsd();
				mag = new Metadigit();
				mag.setGen(datiScheda.get(0).genGen());
				mag.setBib(datiScheda.get(0).genBib());

				fMag = datiScheda.get(0).getPathOutput(folderOutput, true);
				fMagCert = new File(fMag.getAbsolutePath()+".cert");
				if (!fMagCert.exists()){
					System.out.println("["+QuartzTools.getName(context)+"] Genero il file: "+fMag.getAbsolutePath());
					for (int x=0; x<datiScheda.size(); x++){
						
						for(int y=new Integer(datiScheda.get(x).getpStart()).intValue(); y<=new Integer(datiScheda.get(x).getpStop()); y++){
							numImgOutput ++;
							img = datiScheda.get(x).genImg(inputImage, y, folderOutput, numImgOutput);
							magXsd.calcImg(img, fMag.getParentFile().getAbsolutePath());
							mag.getImg().add(img);
							datiScheda.get(x).addNumPagina();
						}
					}
					magXsd.write(mag, fMag);
				}
			}
		} catch (JobExecutionException e) {
			throw e;
		} catch (SchedulerException e) {
			log.error(e.getMessage(), e);
			throw new JobExecutionException(e.getMessage(), e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new JobExecutionException(e.getMessage(), e);
		}
	}
}
