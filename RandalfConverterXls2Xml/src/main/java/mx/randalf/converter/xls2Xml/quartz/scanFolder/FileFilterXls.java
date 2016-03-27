/**
 * 
 */
package mx.randalf.converter.xls2Xml.quartz.scanFolder;

import java.io.File;

import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;

import mx.randalf.converter.xls2Xml.exception.RandalfConvertXls2XmlException;
import mx.randalf.converterxls2xml.Xls2Xml;

/**
 * @author massi
 *
 */
public class FileFilterXls extends FileFilterMaster {

	/**
	 * @param suffix
	 * @throws SchedulerException 
	 */
	public FileFilterXls(Xls2Xml x2x, Integer numberInput) throws SchedulerException {
		super(".xls", x2x, numberInput);
	}

	@Override
	public void readFile(File f) throws RandalfConvertXls2XmlException{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start(JobExecutionContext context) throws RandalfConvertXls2XmlException {
		// TODO Auto-generated method stub
		
	}

}
