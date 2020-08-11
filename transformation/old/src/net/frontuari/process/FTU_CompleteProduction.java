package net.frontuari.process;

import net.frontuari.base.FTUProcess;
import net.frontuari.model.FTUMProduction;

import org.compiere.util.Msg;


public class FTU_CompleteProduction extends FTUProcess {

	@Override
	protected void prepare() {
		
	}

	@Override
	protected String doIt() throws Exception {
		if(getRecord_ID() <= 0)
			return "@Error@: " + Msg.getMsg(getCtx(), "BSCA_ErrorRecord_ID");
		
		FTUMProduction production = new FTUMProduction(getCtx(), getRecord_ID(), get_TrxName());
		try {
			if(production.processIt(production.getDocAction())) {
				production.saveEx(get_TrxName());
				return "Produccion: " + production.getDocumentNo() + " Procesada Satisfactoriamente! - " + production.getDocStatus();
			} else {
				//production.saveEx(get_TrxName());
				return "@Error@: No se pudo Procesar Produccion: " + production.getDocumentNo() + " - " + production.m_processMsg + " - " + production.getDocStatus();
			}
		} catch (Exception ex) {
			//production.saveEx(get_TrxName());
			return "@Error@: No se pudo Procesar Produccion: " + production.getDocumentNo() + " - " + production.m_processMsg + " - " + ex.getMessage() + " - " + production.getDocStatus();
		}
	}

}
