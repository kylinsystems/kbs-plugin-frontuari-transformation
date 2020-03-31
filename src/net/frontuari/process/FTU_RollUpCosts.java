package net.frontuari.process;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MCost;
import org.compiere.model.MProduct;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.DB;
import org.compiere.util.Env;

public class FTU_RollUpCosts extends SvrProcess {


	int category = 0;
	int product_id = 0;
	int client_id = 0; 
	int costelement_id = 0;
	int p_ad_org_id=0;
	private HashSet<Integer> processed;
	private int M_Production_ID;
	private MAcctSchema schema;
	
	public FTU_RollUpCosts(){
		
	}
	
	public FTU_RollUpCosts(int M_Production_ID, MAcctSchema schema) {
		this.M_Production_ID = M_Production_ID;
		this.schema = schema;
	}

	protected void prepare() 
	{
	
		int chosen_id = 0;
				
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			String name = para[i].getParameterName();
		//	log.fine("prepare - " + para[i]);
			if (para[i].getParameter() == null)
				;
			else if (name.equals("M_Product_Category_ID"))
				category = para[i].getParameterAsInt();
			else if (name.equals("M_Product_ID"))
				chosen_id = para[i].getParameterAsInt();
			else if (name.equals("M_CostElement_ID"))
				costelement_id = para[i].getParameterAsInt();
			else if (name.equals("AD_Org_ID"))
				p_ad_org_id = para[i].getParameterAsInt();
			else
				log.log(Level.SEVERE, "Unknown Parameter: " + name);		
		}
		
	
		product_id = getRecord_ID();
		if (product_id == 0)
		{
			product_id = chosen_id;
		}
	}
	
	protected String doIt() throws Exception
	{
		client_id = Env.getAD_Client_ID(getCtx());
		createArray();
		String result = rollUp();
		return result;
	}
	
	protected void createArray() throws Exception
	{
		
		processed = new HashSet<Integer>();
		
	}

	protected String rollUp() throws Exception {
		
		
		if (product_id != 0) //only for the product
		{
			String Modality  = DB.getSQLValueStringEx(MSG_InvalidArguments, "Select Modality from M_product where M_Product_ID = "+product_id);
			if ("T".equals(Modality))
				rollUpCostsTransformation(product_id);
			else if ("P".equals(Modality))
				rollUpCostsProduction(product_id);
		}
		else if (category != 0) //roll up for all categories
		{
			String sql = "SELECT M_Product_ID FROM M_Product WHERE M_Product_Category_ID = ? AND AD_Client_ID = ? " +
			    " AND M_Product_ID IN (SELECT M_Product_ID FROM M_Product_BOM)";
			int[] prodids = DB.getIDsEx(get_TrxName(), sql, category, client_id);
			for (int prodid : prodids) {
				String Modality  = DB.getSQLValueStringEx(MSG_InvalidArguments, "Select Modality from M_product where M_Product_ID = "+prodid);
				if ("T".equals(Modality))
					rollUpCostsTransformation(prodid);
				else if ("P".equals(Modality))
					rollUpCostsProduction(prodid);
				
			}
		}
		else {//do it for all products 
		
			
			if (M_Production_ID!=0){ //soropeza
						
					String sql = "Select M_Product_ID from M_Production where M_Production_ID = "+M_Production_ID;
					int productBOM_ID = DB.getSQLValueEx(get_TrxName(),sql);
					
					String Modality  = DB.getSQLValueStringEx(MSG_InvalidArguments, "Select Modality from M_product where M_Product_ID = "+productBOM_ID);
					if ("T".equals(Modality)){
							rollUpCostsTransformation_production(0,0);
					}else if ("P".equals(Modality))
							rollUpCostsProduction_production(productBOM_ID);
						
				
			}else{
				String sql = "SELECT M_Product_ID FROM M_Product WHERE AD_Client_ID = ? " +
				   " AND M_Product_ID IN (SELECT M_Product_ID FROM M_Product_BOM)";
				int[] prodids = DB.getIDsEx(get_TrxName(), sql, client_id);
				for (int prodid : prodids) {
					String Modality  = DB.getSQLValueStringEx(MSG_InvalidArguments, "Select Modality from M_product where M_Product_ID = "+prodid);
					if ("T".equals(Modality))
						rollUpCostsTransformation(prodid);
					else if ("P".equals(Modality))
						rollUpCostsProduction(prodid);
				}
			}
	    }
		
		return "Roll Up Complete";
	}
    
	private void rollUpCostsProduction_production(int M_ProductBOM_ID) throws Exception {
		
		String whereOrg = "";
		if (schema.getCostingLevel().equals("O"))
			whereOrg = " and c.AD_Org_ID = b.AD_Org_ID ";
		
		String update = "UPDATE M_Cost set updated = now(), "
				+ " CurrentCostPrice = COALESCE((select Sum (b.Qty * c.currentcostprice)"
											+ " FROM M_ProductionLine b"
											+ " INNER JOIN M_Cost c ON (b.M_Product_ID = c.M_Product_ID "+whereOrg+")"
											+ " WHERE b.M_Production_ID = "+M_Production_ID+" AND M_CostElement_ID = "+costelement_id
											+ " AND (b.AD_Org_ID="+p_ad_org_id+" OR b.AD_Org_ID = 0)  and b.isEndProduct = 'N' and b.Qty !=0),0),"
				+ " FutureCostPrice = COALESCE((select Sum (b.Qty * c.futurecostprice)"
											+ " FROM M_ProductionLine b"
											+ " INNER JOIN M_Cost c ON (b.M_Product_ID = c.M_Product_ID "+whereOrg+") "
											+ " WHERE b.M_Production_ID = "+M_Production_ID+" AND M_CostElement_ID = "+costelement_id
											+ " AND (b.AD_Org_ID="+p_ad_org_id+" OR b.AD_Org_ID = 0) and b.isEndProduct = 'N' and b.Qty !=0),0) "
				+ "WHERE M_Product_ID = "+M_ProductBOM_ID+" AND AD_Client_ID = "+client_id+" AND M_CostElement_ID = "+costelement_id+" AND (AD_Org_ID="+p_ad_org_id+" OR AD_Org_ID = 0)";
    
		DB.executeUpdateEx(update.toString(), get_TrxName());
	
		processed.add(M_ProductBOM_ID);
		
	}

	private void rollUpCostsTransformation_production(int M_ProductionLine_ID, int M_Product_ID) {
		// TODO Auto-generated method stub
		
	}


	//by Sergio Oropeza/Erick Villamizar
	protected void rollUpCostsTransformation(int p_id) throws Exception {
		
		MCost mCostParent =  new Query(Env.getCtx(), MCost.Table_Name, "M_Product_ID = ? AND M_CostElement_ID = ? AND AD_Org_ID=?",get_TrxName())
		.setParameters(p_id, costelement_id,p_ad_org_id)
		.setOnlyActiveRecords(true).first(); 
		
		if (mCostParent==null)
			return; 
		
		BigDecimal currentCostParent =mCostParent.getCurrentCostPrice();
		BigDecimal SumBOMqty = new BigDecimal(DB.getSQLValueStringEx(get_TrxName(),"Select COALESCE(SUM(BOMqty),0) FROM M_Product_BOM where M_Product_ID = "+p_id));
		if(SumBOMqty.compareTo(BigDecimal.ONE)!=0)
			throw new AdempiereException("BOM for Transformation should be equals to 1. SumBOMQty="+SumBOMqty);
		
		String sqlProductBOM = "SELECT M_Product_BOM_ID FROM M_Product_BOM WHERE M_Product_ID =  ?" ;
		int[] prodids = DB.getIDsEx(get_TrxName(), sqlProductBOM, p_id);
		//int precision = MAcctSchema.get(Env.getCtx(), mCostParent.getC_AcctSchema_ID()).getCostingPrecision();
		for (int M_Product_BOM_ID : prodids) {
			
			//BigDecimal BOMqty = new BigDecimal(DB.getSQLValueStringEx(get_TrxName(),"Select COALESCE(BOMqty,0) FROM M_Product_BOM where M_Product_BOM_ID = "+M_Product_BOM_ID));		
			//BigDecimal PercCurrentCost = (BOMqty.divide(SumBOMqty, precision, RoundingMode.HALF_UP).multiply(PercDiff)).add(BOMqty);//PercDiff*BOMQry/SumBOMQty + BOMQty
			
			//BigDecimal CurrentCost = currentCostParent.multiply(PercCurrentCost);
			String sqlisBOM = DB.getSQLValueStringEx(get_TrxName(),"Select isBOM FROM M_Product p JOIN M_Product_BOM pb ON p.M_Product_ID = pb.M_ProductBOM_ID where pb.M_Product_BOM_ID = "+M_Product_BOM_ID);  
			Boolean isBOM = sqlisBOM.equals("Y")?true:false;
			int M_Product_ID = DB.getSQLValueEx(get_TrxName(),"Select M_ProductBOM_ID FROM M_Product_BOM where M_Product_BOM_ID = "+M_Product_BOM_ID);
			
			
			MCost cost  = new Query(Env.getCtx(), MCost.Table_Name, "M_Product_ID = ? AND M_CostElement_ID = ? AND AD_Org_ID=?",get_TrxName())
			.setParameters(M_Product_ID, costelement_id,p_ad_org_id)
			.setOnlyActiveRecords(true).first();
			
			
			if (cost==null){
				MProduct product = new MProduct(Env.getCtx(), M_Product_ID, get_TrxName());
				cost =new MCost(product, 0, (MAcctSchema)mCostParent.getC_AcctSchema(), p_ad_org_id, costelement_id);
			} 
			cost.setCurrentCostPrice(currentCostParent);
			cost.saveEx();
			
			if (isBOM) 
				rollUpCostsTransformation(M_Product_ID);
		}
		


		
	}
	
	protected void rollUpCostsProduction(int p_id) throws Exception 
	{
		StringBuilder sql = new StringBuilder("SELECT M_ProductBOM_ID FROM M_Product_BOM WHERE M_Product_ID = ? ") 
		    .append(" AND AD_Client_ID = ").append(client_id);
		int[] prodbomids = DB.getIDsEx(get_TrxName(), sql.toString(), p_id);
		
		for (int prodbomid : prodbomids) {
			if ( !processed.contains(p_id)) {
				rollUpCostsProduction(prodbomid);
			}
		}

		//once the subproducts costs are accurate, calculate the costs for this product
		StringBuilder update = new StringBuilder("UPDATE M_Cost set CurrentCostPrice = COALESCE((select Sum (b.BOMQty * c.currentcostprice)") 
           .append(" FROM M_Product_BOM b INNER JOIN M_Cost c ON (b.M_PRODUCTBOM_ID = c.M_Product_ID) ") 
           .append(" WHERE b.M_Product_ID = ").append(p_id).append(" AND M_CostElement_ID = ").append(costelement_id)
           .append(" AND (b.AD_Org_ID=").append(p_ad_org_id).append(" OR b.AD_Org_ID = 0)").append("),0),") 
           .append(" FutureCostPrice = COALESCE((select Sum (b.BOMQty * c.futurecostprice) FROM M_Product_BOM b ") 
           .append(" INNER JOIN M_Cost c ON (b.M_PRODUCTBOM_ID = c.M_Product_ID) ") 
           .append(" WHERE b.M_Product_ID = ").append(p_id).append(" AND M_CostElement_ID = ").append(costelement_id)
           .append(" AND (b.AD_Org_ID=").append(p_ad_org_id).append(" OR b.AD_Org_ID = 0)").append("),0)")
           .append(" WHERE M_Product_ID = ").append(p_id).append(" AND AD_Client_ID = ").append(client_id)
           .append(" AND M_CostElement_ID = ").append(costelement_id)
           .append(" AND (AD_Org_ID=").append(p_ad_org_id).append(" OR AD_Org_ID = 0)")
           .append(" AND M_PRODUCT_ID IN (SELECT M_PRODUCT_ID FROM M_PRODUCT_BOM)");
        
		DB.executeUpdateEx(update.toString(), get_TrxName());

		processed.add(p_id);
		
	}

}

