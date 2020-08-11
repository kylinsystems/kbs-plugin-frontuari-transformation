package net.frontuari.process;

import java.math.BigDecimal;

import org.compiere.model.MProduction;
import org.compiere.model.MProductionLine;
import org.compiere.process.ProcessCall;
import org.compiere.process.SvrProcess;
import org.compiere.util.Msg;

public class FTU_ValidateProduction extends SvrProcess implements ProcessCall {

	private int p_M_Production_ID = 0;
	private MProduction production = null;
	String p_trx=null;
	
	@Override
	protected void prepare() {

		if(getProcessInfo() != null)
			p_M_Production_ID = getRecord_ID();
		if(p_M_Production_ID != 0 && production == null)
			production = new MProduction(getCtx(), p_M_Production_ID, get_TrxName());
	}

	public void setTrx(String trxName){
		p_trx=trxName;
	}
	
	public String get_TrxName()
	{
		if (p_trx != null)
			return p_trx;
		return super.get_TrxName();
	}	//	get_TrxName
	
	
	@Override
	public String doIt() throws Exception {
		
		if(production == null)
			return null;
		
		boolean haveEndProduct = false;
		boolean haveNoEndProduct = false;
		for(MProductionLine productionLine : production.getLines()) {
			BigDecimal Qty = (BigDecimal) productionLine.get_Value("Qty");
			if(Qty == null)
				Qty = BigDecimal.ZERO;
			if(productionLine.isEndProduct() && productionLine.getM_Product_ID() == production.getM_Product_ID())
				haveEndProduct = true;
			else if(!productionLine.isEndProduct() && Qty.compareTo(BigDecimal.ZERO) != 0)
				haveNoEndProduct = true;
		}
		if(haveEndProduct && haveNoEndProduct) {
			production.setIsCreated(MProduction.ISCREATED_Yes);
			production.saveEx();
			return Msg.getMsg(getCtx(), "BSCA_ProductionIsValid");
		} else {
			production.setIsCreated(MProduction.ISCREATED_No);
			production.saveEx();
			return Msg.getMsg(getCtx(), "BSCA_ProductionIsNotValid");
		}
	}
	
	public void setM_Production_ID(int M_Production_ID) {
		p_M_Production_ID = M_Production_ID;
	}
	
	public int getM_Production_ID() {
		return p_M_Production_ID;
	}
	
	public void setMProduction(MProduction p_Production) {
		production = p_Production;
		p_M_Production_ID=production.get_ID();
	}
	
	public MProduction getMProduction() {
		return production;
	}

}