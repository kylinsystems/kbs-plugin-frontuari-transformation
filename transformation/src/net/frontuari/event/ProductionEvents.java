package net.frontuari.event;

import org.adempiere.base.event.IEventTopics;
import org.compiere.model.MInOutLine;
import org.compiere.model.PO;
import org.compiere.util.CLogger;

import net.frontuari.base.CustomEvent;
import net.frontuari.model.FTUMProduction;

public class ProductionEvents extends  CustomEvent{

	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(ProductionEvents.class);

	@Override
	protected void doHandleEvent() {
		PO po = getPO();
		String type = getEventType();
		log.info(po + " Type: " + type);
		String msg;

			/** **/
			if (po.get_TableName().equals(FTUMProduction.Table_Name)) {
				FTUMProduction production = (FTUMProduction) po;
				if(type.equals(IEventTopics.PO_AFTER_CHANGE)) {
					
				}
				
			}
		}
}
