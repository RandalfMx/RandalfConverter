/**
 * 
 */
package mx.randalf.converter.xls2Xml.quartz.scanFolder;

import java.io.File;
import java.io.FileFilter;

import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;

import mx.randalf.converter.xls2Xml.exception.RandalfConvertXls2XmlException;
import mx.randalf.converterxls2xml.Xls2Xml;
import mx.randalf.quartz.QuartzMaster;

/**
 * @author massi
 *
 */
public abstract class FileFilterMaster extends QuartzMaster implements FileFilter {

	private String suffix = null;

	protected Xls2Xml x2x = null;
	protected Integer numberInput = null;

	/**
	 * @throws SchedulerException 
	 * 
	 */
	public FileFilterMaster(String suffix, Xls2Xml x2x, Integer numberInput) throws SchedulerException {
		super(false);
		this.suffix = suffix;
		this.x2x = x2x;
		this.numberInput = numberInput;

//		setnThread(x2x.getParameter().getNThread().intValue());
//		settSleep(x2x.getParameter().getTSleep().intValue());
		if (x2x.getParameter().getTSleepClosed() != null){
			settSleepClosed(x2x.getParameter().getTSleepClosed().intValue());
		}
	}

	/**
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File f) {
		boolean result = false;
		
		if (!f.getName().startsWith(".")){
			if (f.isDirectory()){
				result = true;
			} else if (f.getName().endsWith(suffix)){
				result = true;
			}
		}
		return result;
	}

	public abstract void readFile(File f) throws RandalfConvertXls2XmlException;

	public abstract void start(JobExecutionContext context) throws RandalfConvertXls2XmlException;

	public void closedAllThread() throws SchedulerException{
		closed(false);
	}
}
