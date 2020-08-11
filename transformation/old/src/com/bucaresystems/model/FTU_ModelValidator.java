package net.frontuari.model;



import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.adempiere.base.event.AbstractEventHandler;
import org.adempiere.base.event.IEventManager;
import org.adempiere.base.event.IEventTopics;
import org.adempiere.exceptions.AdempiereException;
import org.compiere.acct.FactLine;
import org.compiere.model.MBPartner;
import org.compiere.model.MDocType;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutConfirm;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInOutLineConfirm;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MPayment;
import org.compiere.model.MRMA;
import org.compiere.model.MRMALine;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.process.DocAction;
import org.compiere.util.CLogger;
import org.compiere.util.Env;
import org.osgi.service.event.Event;

import net.frontuari.base.CustomEvent;
import net.frontuari.base.CustomEventFactory;

/**
 *	Validator or Localization Colombia (Detailed Names)
 *	
 *  @author Carlos Ruiz - globalqss - Quality Systems & Solutions - http://globalqss.com 
 *	@version $Id: LCO_Validator.java,v 1.4 2007/05/13 06:53:26 cruiz Exp $
 */
public class FTU_ModelValidator extends CustomEvent
{
	/**	Logger			*/
	private static CLogger log = CLogger.getCLogger(FTU_ModelValidator.class);

	/**
	 *	Initialize Validation
	 */
	/*@Override
	protected void initialize() {
		registerTableEvent(IEventTopics.DOC_AFTER_COMPLETE, MInOutConfirm.Table_Name);
		registerTableEvent(IEventTopics.PO_BEFORE_CHANGE, MInOutConfirm.Table_Name);
		//registerTableEvent(IEventTopics.PO_BEFORE_NEW, X_MP_Maintain.Table_Name);
		//registerTableEvent(IEventTopics.PO_BEFORE_CHANGE, X_MP_Maintain.Table_Name);
	}	//	initialize*/

    /**
     *	Model Change of a monitored Table.
     *	Called after PO.beforeSave/PO.beforeDelete
     *	when you called addModelChange for the table
     *  @param event
     *	@exception Exception if the recipient wishes the change to be not accept.
     */
	@Override
	protected void doHandleEvent(/*Event event*/) {
		PO po = getPO();
		String type = getEventType();
		log.info(po + " Type: " + type);
		String msg;

		// Check Digit based on TaxID
		if (po.get_TableName().equals(MInOut.Table_Name) )
		{
			MInOut inout = (MInOut)po;
			if(type.equals(IEventTopics.DOC_AFTER_COMPLETE) || type.equals(IEventTopics.PO_BEFORE_CHANGE)) {
				//MInOut inout = new MInOut (po.getCtx(), confirm.getM_InOut_ID(), po.get_TrxName());
				
				
				MDocType dt = MDocType.get(po.getCtx(), inout.getC_DocType_ID());
				boolean automaticreturn = dt.get_ValueAsBoolean("IsAutomaticReturn");
				if(automaticreturn) {
					int C_DocTypeAutoReturn_ID = dt.get_ValueAsInt("C_DocTypeAutoReturn_ID");
					int C_DocTypeRMA_ID = dt.get_ValueAsInt("C_DocTypeRMA_ID");
					int M_RMAType_ID= dt.get_ValueAsInt("M_RMAType_ID");					
					for(MInOutConfirm confirm:inout.getConfirmations(false)) {
						if(C_DocTypeAutoReturn_ID > 0 && C_DocTypeRMA_ID > 0 && M_RMAType_ID > 0)
							automaticReturnInOut(inout,C_DocTypeAutoReturn_ID,C_DocTypeRMA_ID,M_RMAType_ID,confirm);
						else 
							throw new AdempiereException("Los documentos no estan configurados correctamente para hacer la devolucion automatica");
					}
				}
					
			}else if(type.equals(IEventTopics.DOC_BEFORE_COMPLETE) ) {
				MDocType dt = MDocType.get(po.getCtx(), inout.getC_DocType_ID());
				boolean automaticreturn = dt.get_ValueAsBoolean("IsAutomaticReturn");
				if(automaticreturn) {
					MInOutConfirm[] confirms = inout.getConfirmations(false);
					if(confirms.length>0) {
						for(MInOutLine line:inout.getLines(false)) {
							line.setQty(line.getQtyEntered());
							line.setMovementQty(line.getQtyEntered());
							line.saveEx(po.get_TrxName());
						}
					}
				}
			}
		}
			
			
			
		
		
		
		
	}	//	doHandleEvent
	
	private void automaticReturnInOut (MInOut original, int C_DocTypeReturn_ID,int C_DocTypeRMA_ID,int M_RMAType_ID, MInOutConfirm confirm)
	{
		MInOut inoutReturn = null;
		MRMA rma = null;
		MInOutLineConfirm[] confirmLines = confirm.getLines(false); 
		//	Go through confirmations 
		MOrder order = (MOrder)original.getC_Order();
		for (int i = 0; i < confirmLines.length; i++)
		{
			MInOutLineConfirm confirmLine = confirmLines[i];
			BigDecimal differenceQty = confirmLine.getDifferenceQty();
			if (differenceQty.compareTo(Env.ZERO) == 0)
				continue;
			//
			MInOutLine oldLine = confirmLine.getLine();
			if (log.isLoggable(Level.FINE)) log.fine("Qty=" + differenceQty + ", Old=" + oldLine);
			//
			// Create Header
			if (inoutReturn == null)
			{
				//inoutReturn = new MInOut(original.getCtx(), 0, original.get_TrxName());
				inoutReturn = new MInOut(order, C_DocTypeReturn_ID, original.getMovementDate());
				//inoutReturn.setC_DocType_ID(C_DocTypeReturn_ID);
				inoutReturn.setC_Order_ID(-1);
				
				inoutReturn.saveEx(original.get_TrxName());
				
				
				rma = new MRMA(original.getCtx(), 0, original.get_TrxName());
				rma.setName("Devolucion Automatica de la Recepcion:"+original.getDocumentNo());
				rma.setIsSOTrx(original.isSOTrx());
				rma.setAD_Org_ID(original.getAD_Org_ID());
				rma.setM_RMAType_ID(M_RMAType_ID);
				rma.setC_DocType_ID(C_DocTypeRMA_ID);
				rma.setM_InOut_ID(original.getM_InOut_ID());
				rma.setC_BPartner_ID(original.getC_BPartner_ID());
				rma.setSalesRep_ID(original.getSalesRep_ID()>0?original.getSalesRep_ID():order.getSalesRep_ID());
				
				rma.saveEx(original.get_TrxName());
				/*StringBuilder msgd = new StringBuilder("Splitted from ").append(original.getDocumentNo());
				inoutReturn.addDescription(msgd.toString());
				inoutReturn.setIsInDispute(true);
				inoutReturn.saveEx();*/
				StringBuilder msgd = new StringBuilder("Devolucion: ").append(inoutReturn.getDocumentNo());
				msgd.append(", Autorizacion:" +rma.getDocumentNo());
				original.addDescription(msgd.toString());
				original.saveEx();
			}
			//
			MInOutLine returnLine = new MInOutLine (inoutReturn);
			//returnLine.setC_OrderLine_ID(oldLine.getC_OrderLine_ID());
			returnLine.setC_UOM_ID(oldLine.getC_UOM_ID());
			returnLine.setDescription(oldLine.getDescription());
			returnLine.setIsDescription(oldLine.isDescription());
			returnLine.setLine(oldLine.getLine());
			returnLine.setM_AttributeSetInstance_ID(oldLine.getM_AttributeSetInstance_ID());
			returnLine.setM_Locator_ID(oldLine.getM_Locator_ID());
			returnLine.setM_Product_ID(oldLine.getM_Product_ID());
			returnLine.setM_Warehouse_ID(oldLine.getM_Warehouse_ID());
			returnLine.setRef_InOutLine_ID(oldLine.getRef_InOutLine_ID());
			/*StringBuilder msgd = new StringBuilder("Split: from ").append(oldLine.getMovementQty());
			returnLine.addDescription(msgd.toString());*/
			//	Qtys
			returnLine.setQty(differenceQty);		//	Entered/Movement
			returnLine.saveEx(original.get_TrxName());
			

			
			MRMALine rmaLine = new MRMALine(original.getCtx(),0,original.get_TrxName());
			rmaLine.setM_RMA_ID(rma.getM_RMA_ID());
			rmaLine.setAD_Org_ID(oldLine.getAD_Org_ID());
			rmaLine.setDescription(oldLine.getDescription());
			rmaLine.setM_InOutLine_ID(oldLine.getM_InOutLine_ID());
			rmaLine.setLine(oldLine.getLine());
			rmaLine.setM_Product_ID(oldLine.getM_Product_ID());
			rmaLine.setQty(differenceQty);
			rmaLine.saveEx(original.get_TrxName());
			
			//	Old
			/*msgd = new StringBuilder("Splitted: from ").append(oldLine.getMovementQty());
			oldLine.addDescription(msgd.toString());
			oldLine.setQty(oldLine.getQtyEntered());
			oldLine.setMovementQty(MovementQty);
			oldLine.saveEx();*/
			//	Update Confirmation Line
			/*confirmLine.setTargetQty(confirmLine.getTargetQty().subtract(differenceQty));
			confirmLine.setDifferenceQty(Env.ZERO);
			confirmLine.saveEx();*/
		}	//	for all confirmations
		
		// Nothing to split
		if (inoutReturn == null)
		{
			return ;
		}
		
		/*if (!original.processIt(DocAction.ACTION_Complete))
			throw new AdempiereException(original.getProcessMsg());
	
		original.saveEx(original.get_TrxName());*/
		//
		if (!inoutReturn.processIt(DocAction.ACTION_Complete))
			throw new AdempiereException(inoutReturn.getProcessMsg());
	
		inoutReturn.saveEx(original.get_TrxName());
		
		if (!rma.processIt(DocAction.ACTION_Complete))
			throw new AdempiereException(rma.getProcessMsg());
	
		rma.saveEx(original.get_TrxName());
		
		
	}	//	splitInOut



	
}	//	FTU_ModelValidator


