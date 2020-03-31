package net.frontuari.callout;

import java.math.BigDecimal;
import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MLocator;
import org.compiere.model.MOrgInfo;
import org.compiere.model.MProduct;
import org.compiere.model.MProduction;
import org.compiere.model.MProductionLine;
import org.compiere.model.ProductCost;
import org.compiere.model.Query;
import org.compiere.util.DB;
import org.compiere.util.Env;

public class FTU_SetProductionValues implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab,
			GridField mField, Object value, Object oldValue) {
		if(value == null)
			return null;
		
		if(MProduction.Table_Name.equalsIgnoreCase(mTab.getTableName())) {
			if(MProduction.COLUMNNAME_AD_Org_ID.equalsIgnoreCase(mField.getColumnName())) {
				int AD_Org_ID = (int)mTab.getValue(MProduction.COLUMNNAME_AD_Org_ID);
				if(AD_Org_ID>0){
					MOrgInfo orgInfo = new Query(ctx, MOrgInfo.Table_Name, "AD_Org_ID=?", null)
					.setOnlyActiveRecords(true).setParameters(AD_Org_ID).first();
				int M_Warehouse_ID = orgInfo.getM_Warehouse_ID();
				if(M_Warehouse_ID != 0) {
					MLocator locator = new Query(ctx, MLocator.Table_Name, "M_Warehouse_ID=?", null)
						.setOnlyActiveRecords(true).setOrderBy("IsDefault DESC").setParameters(M_Warehouse_ID).first();
					if(locator != null) {
						int M_Locator_ID = locator.get_ID();
						mTab.setValue(MProduction.COLUMNNAME_M_Locator_ID, M_Locator_ID);
					} else 
						mTab.setValue(MProduction.COLUMNNAME_M_Locator_ID, null);
				} else
					mTab.setValue(MProduction.COLUMNNAME_M_Locator_ID, null);
		
				}
			}
			if(MProduction.COLUMNNAME_M_Product_ID.equalsIgnoreCase(mField.getColumnName())) {
				int M_Product_ID = (int) mTab.getValue(MProduction.COLUMNNAME_M_Product_ID);
				if(M_Product_ID>0){
					MProduct product = new MProduct(ctx, M_Product_ID, null);

					String Modality = product.get_ValueAsString("Modality");
					mTab.setValue("Modality", Modality);
					boolean isTransformation= (boolean)mTab.getValue("IsTransformation");
					if(isTransformation){
					int orgId = (int)mTab.getValue("AD_Org_ID");
					int asId = Env.getContextAsInt(ctx, "$"+MAcctSchema.COLUMNNAME_C_AcctSchema_ID);
					MAcctSchema as = new MAcctSchema(ctx, asId, null);
					ProductCost pc = new ProductCost (ctx, 
							M_Product_ID, 0, null);
					pc.setQty(BigDecimal.ONE);
					String costingMethod = null;
					BigDecimal costs = pc.getProductCosts(as, orgId, costingMethod, 
							0, false);
					if(costs.compareTo(BigDecimal.ZERO)>0){
						mTab.setValue("PriceActual", costs);
					}
					
					String sql = "SELECT MAX(il.C_Invoice_ID) FROM C_InvoiceLine il "
							+ " INNER JOIN C_Invoice i ON il.C_Invoice_ID=i.C_Invoice_ID"
							+ " WHERE il.M_Product_ID = "+M_Product_ID+" AND i.DocStatus = 'CO'"
							+ " AND i.AD_Org_ID="+orgId+"AND il.IsActive = 'Y' AND i.IsSOTrx='N' ";
					
					int invoiceId = DB.getSQLValue(null, sql);
					
					if(invoiceId>0){
						mTab.setValue("C_Invoice_ID", invoiceId);

						sql = "SELECT priceentered FROM C_InvoiceLine il WHERE C_Invoice_ID="+invoiceId
						+ " AND M_Product_ID="+M_Product_ID;
						BigDecimal price = DB.getSQLValueBD(null, sql);
						if(price.compareTo(BigDecimal.ZERO)>0){
							mTab.setValue("AmountInvoiced", price);
						}
					}
					
					int userId = product.get_ValueAsInt("User1_ID");
					
					if(userId>0){
						mTab.setValue("User1_ID",userId);
					}
					
					int userY = product.get_ValueAsInt("User1Y_ID");
					
					if(userY>0){
						
						String sqlY = "SELECT MAX(C_Activity_ID) FROM FTU_ProductCostCenter WHERE User1Y_ID="+userY+" AND (AD_Org_ID ="+orgId+" OR AD_Org_ID=0)";
						
						int activityId = DB.getSQLValue(null, sqlY);
						
						if(activityId > 0){
							
							mTab.setValue("C_Activity_ID", activityId);
						}
						
					}
					
					int locatorId = (int) mTab.getValue(MProduction.COLUMNNAME_M_Locator_ID);
					
					if(locatorId>0){
						sql = "SELECT COALESCE(SUM(QtyAvailable),0) FROM FTU_RV_Storage_Available_Product  WHERE M_Product_ID = "+M_Product_ID+" AND AD_Org_ID = "+orgId+" AND M_Locator_ID="+locatorId;						
					}else{
						sql = "SELECT COALESCE(SUM(QtyAvailable),0) FROM FTU_RV_Storage_Available_Product  WHERE M_Product_ID = "+M_Product_ID+" AND AD_Org_ID = "+orgId;
						
					}
					
					BigDecimal qtyAvaliable = DB.getSQLValueBD(null, sql);
					mTab.setValue("QtyAvailable", qtyAvaliable);
				  }					
				}
				
				
			}
			if("Qty".equalsIgnoreCase(mField.getColumnName()) || "PriceActual".equalsIgnoreCase(mField.getColumnName()) || "Modality".equalsIgnoreCase(mField.getColumnName())
					|| MProduction.COLUMNNAME_M_Product_ID.equalsIgnoreCase(mField.getColumnName())) {
				if(mTab.getValue("Modality") == null || mTab.getValue("Qty") == null || mTab.getValue(MProduction.COLUMNNAME_M_Product_ID) == null)
					return null;
				
				String Modality = mTab.getValue("Modality").toString();
				BigDecimal Qty = (BigDecimal) mTab.getValue("Qty");
				
				if("P".equalsIgnoreCase(Modality)) {
					mTab.setValue(MProduction.COLUMNNAME_ProductionQty, Qty);
					BigDecimal priceActual = BigDecimal.ZERO;
					if(mTab.getValue("PriceActual") != null)
						priceActual = (BigDecimal) mTab.getValue("PriceActual");
					BigDecimal priceProduction = priceActual.multiply(Qty);
					mTab.setValue("TotalLines", priceProduction);
				}
				else if("T".equalsIgnoreCase(Modality))
					mTab.setValue(MProduction.COLUMNNAME_ProductionQty, Qty.negate());
			}
			if(MProduction.COLUMNNAME_ProductionQty.equalsIgnoreCase(mField.getColumnName())){
				
				if(mTab.getValue(MProduction.COLUMNNAME_ProductionQty) == null)
					return null;
				boolean isTransformation= (boolean)mTab.getValue("isTransformation");//((String)mTab.getValue("isTransformation")).equals("Y");
				if(isTransformation){
				BigDecimal productionQty = (BigDecimal) value;

				BigDecimal storageQty = BigDecimal.ZERO;
				
				if(productionQty.compareTo(BigDecimal.ZERO)<=0){
					mTab.setValue(MProduction.COLUMNNAME_ProductionQty, 0);
					return "La cantidad a transformar debe ser mayor a cero";
				}
				
				int productId = (int)mTab.getValue("M_Product_ID");
				
				int orgId =  (int)mTab.getValue("AD_Org_ID");
				
				
				int locatorId = (int) mTab.getValue(MProduction.COLUMNNAME_M_Locator_ID);
				
				String sql ="";
				if(locatorId>0){
					sql = "SELECT COALESCE(SUM(QtyAvailable),0) FROM FTU_RV_Storage_Available_Product  WHERE M_Product_ID = "+productId+" AND AD_Org_ID = "+orgId+" AND M_Locator_ID="+locatorId;						
				}else{
					sql = "SELECT COALESCE(SUM(QtyAvailable),0) FROM FTU_RV_Storage_Available_Product  WHERE M_Product_ID = "+productId+" AND AD_Org_ID = "+orgId;
					
				}
				storageQty = DB.getSQLValueBD(null, sql);
				
				
				if(productionQty.compareTo(storageQty)<=0){
					mTab.setValue("IsCreated", "N");
					return null;
					
				}else{
					mTab.setValue(MProduction.COLUMNNAME_ProductionQty, 0);
					return "La cantidad a transformar es mayor a la cantidad disponible en la ubicacion seleccionada, la cantidad disponible es:"+storageQty; 
				}
				}
		
				
				//Query query = new 
			}
		}
		if(MProductionLine.Table_Name.equalsIgnoreCase(mTab.getTableName())) {
			if("Qty".equalsIgnoreCase(mField.getColumnName())) {
				if(mTab.getValue(MProductionLine.COLUMNNAME_M_Production_ID) == null)
					return null;
				int M_Production_ID = (int) mTab.getValue(MProductionLine.COLUMNNAME_M_Production_ID);
				MProduction production = new MProduction(ctx, M_Production_ID, null);
				String Modality = production.get_ValueAsString("Modality");
				BigDecimal Qty = (BigDecimal) mTab.getValue("Qty");
				if(Modality == null || mTab.getValue("Qty") == null)
					return null;
				if("P".equalsIgnoreCase(Modality))
					mTab.setValue(MProductionLine.COLUMNNAME_QtyUsed, Qty);
				else if("T".equalsIgnoreCase(Modality))
					mTab.setValue(MProductionLine.COLUMNNAME_QtyUsed, Qty.negate());
			}
		}
		return null;
	}

}
