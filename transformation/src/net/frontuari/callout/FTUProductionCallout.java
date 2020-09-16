package net.frontuari.callout;

import org.compiere.model.MProduct;

import net.frontuari.base.CustomCallout;

public class FTUProductionCallout extends CustomCallout{

	@Override
	protected String start() {
		String colummName = getColumnName();
		if(colummName.equalsIgnoreCase("M_Product_ID")) {
			int M_Product_ID = (int)getValue();
			if(M_Product_ID>0) {
				MProduct prod = new MProduct(getCtx(),M_Product_ID,null);
				setValue("C_UOM_ID", prod.getC_UOM_ID());
			}
		}
		
		
		return null;
	}

}
