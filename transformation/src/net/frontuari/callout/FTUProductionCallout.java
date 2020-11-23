package net.frontuari.callout;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.compiere.model.MProduct;
import org.compiere.util.DB;

import net.frontuari.base.CustomCallout;

public class FTUProductionCallout extends CustomCallout{

	@Override
	protected String start() {
		String colummName = getColumnName();
		if(colummName.equalsIgnoreCase("M_Product_ID") && getValue() != null) {
			int M_Product_ID = (int)getValue();
			if(M_Product_ID>0) {
				MProduct prod = new MProduct(getCtx(),M_Product_ID,null);
				setValue("C_UOM_ID", prod.getC_UOM_ID());
			}
		}
		if(colummName.equalsIgnoreCase("Discount")) {
			int M_Product_ID = getTab().getValue("M_Product_ID")!=null?(int)getTab().getValue("M_Product_ID"):0;
			int M_Production_ID = getTab().getValue("M_Production_ID")!=null?(int)getTab().getValue("M_Production_ID"):0;
			BigDecimal discount = getValue()!=null?(BigDecimal)getValue():BigDecimal.ZERO;
			
			if(discount.signum()!=0) 
				discount = (new BigDecimal(100).subtract(discount)).divide(new BigDecimal(100),5,RoundingMode.HALF_UP);
			else
				discount = BigDecimal.ONE ;
				//Update End Product Cost
			if (M_Product_ID>0) {
				String sqlU = "UPDATE M_ProductionLine pl SET PriceCost = (pf.PriceCost/pf.productionqty)*"+discount+" FROM"
						+ " (SELECT SUM(ppl.PriceCost*(ppl.movementqty*-1)) AS PriceCost, pp.productionqty AS productionqty,"
							+ " CASE WHEN Discount>0 THEN ((100-Discount)/100) ELSE 1 END AS Discount FROM M_ProductionLine ppl "
						+ " JOIN M_Production pp ON ppl.M_Production_ID=pp.M_Production_ID"
						+ " WHERE ppl.M_Product_ID <> "+ M_Product_ID +" AND ppl.M_Production_ID = "+ M_Production_ID +" "
								+ "GROUP BY pp.M_Production_ID,pp.productionqty,pp.Discount) pf"
						+ " WHERE pl.M_Product_ID = "+ M_Product_ID +" AND pl.M_Production_ID = " + M_Production_ID;
				int cont = 0;
					cont = DB.executeUpdate(sqlU,null);
					System.out.println(cont);
			}			
		}	
		
		return null;
	}

}
