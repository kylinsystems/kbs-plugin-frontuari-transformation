package net.frontuari.process;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;

import net.frontuari.base.CustomProcess;
import net.frontuari.model.FTUMProduction;
import net.frontuari.model.FTUMProductionLine;
import net.frontuari.model.X_FTU_R_ProductionPreview;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.model.I_M_ProductionPlan;
import org.compiere.model.MAttributeSetInstance;
import org.compiere.model.MClient;
import org.compiere.model.MLocator;
import org.compiere.model.MProduct;
import org.compiere.model.MProductCategory;
import org.compiere.model.MProduction;
import org.compiere.model.MProductionPlan;
import org.compiere.model.MStorageOnHand;
import org.compiere.model.MSysConfig;
import org.compiere.model.Query;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.util.AdempiereUserError;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;



public class ProductionPreview extends CustomProcess {
	//implements ProcessCall 
	
	
		private int p_AD_Org_ID = 0;
		
		private int p_M_Warehouse_ID = 0;
		
		private int p_M_Locator_ID = 0;
		
		private String p_TrxType = null; 
	
		private int p_M_Product_ID = 0;
		
		private int p_PP_Product_BOM_ID = 0;
		
		private BigDecimal p_ProductionQty = null;
		
		private boolean p_CreateProduction ;
		
		private Timestamp p_DateDoc ;

		private String p_DocAction = null;
		
		private FTUMProduction m_production = null;
		
		@Override
		protected void prepare() {
			
			ProcessInfoParameter[] para = getParameter();
			for (int i = 0; i < para.length; i++)
			{
				String name = para[i].getParameterName();
				if ("AD_Org_ID".equals(name))
					p_AD_Org_ID =  para[i].getParameterAsInt();
				else if ("M_Warehouse_ID".equals(name))
					p_M_Warehouse_ID  = para[i].getParameterAsInt();
				else if ("M_Locator_ID".equals(name))
					p_M_Locator_ID  = para[i].getParameterAsInt();	
				else if ("TrxType".equals(name))
					p_TrxType  = para[i].getParameterAsString();	
				else if ("M_Product_ID".equals(name))
					p_M_Product_ID  = para[i].getParameterAsInt();	
				else if ("PP_Product_BOM_ID".equals(name))
					p_PP_Product_BOM_ID  = para[i].getParameterAsInt();				
				else if ("ProductionQty".equals(name))
					p_ProductionQty  = para[i].getParameterAsBigDecimal();
				else if ("CreateProduction".equals(name))
					p_CreateProduction  = para[i].getParameterAsBoolean();
				else if ("DateDoc".equals(name))
					p_DateDoc  = para[i].getParameterAsTimestamp();
				else if ("DocAction".equals(name))
					p_DocAction  = para[i].getParameterAsString();	
				else
					log.log(Level.SEVERE, "Unknown Parameter: " + name);		
			}
						
		}

		@Override
		public String doIt() throws Exception {
			
			if (p_CreateProduction)
				createProduction();
			
			String sql = "SELECT * FROM ftu_rv_productionpreview";
			
			String whereClause = " WHERE M_Product_ID="+p_M_Product_ID;
			
			if(p_PP_Product_BOM_ID>0)
				whereClause += " AND PP_Product_BOM_ID="+p_PP_Product_BOM_ID;
			if(p_TrxType.length()>0)
				whereClause += " AND TrxType="+p_TrxType;
			if(p_M_Locator_ID>0)
				whereClause += " AND M_Locator_ID="+p_M_Locator_ID;
			if(p_M_Warehouse_ID>0)
				whereClause += " AND M_Warehouse_ID="+p_M_Warehouse_ID;
			if(p_AD_Org_ID>0)
				whereClause += " AND AD_Org_ID="+p_AD_Org_ID;
			
			sql += whereClause;
			
			PreparedStatement pstm = null;
			ResultSet rs = null;
			try {
				
				BigDecimal totalAmt = BigDecimal.ZERO;
				BigDecimal priceActual = BigDecimal.ZERO; 
				
				
				pstm = DB.prepareStatement(sql, get_TrxName());
				rs = pstm.executeQuery();
				
				while(rs.next()) {
					int M_Product_ID = rs.getInt("m_product_id");
					String product_value = rs.getString("product_value");
					String sku = rs.getString("sku");
					String product_name = rs.getString("product_name");
					String ProductionMethod = rs.getString("productionmethod");
					boolean IsBOM = rs.getBoolean("isbom");
					String PP_Product_BOM_Value = rs.getString("PP_Product_BOM_Value");
					String PP_Product_BOM_Name = rs.getString("PP_Product_BOM_Name");
					String DocumentNo = rs.getString("documentno");
					int PP_Product_BOM_ID = rs.getInt("PP_Product_BOM_ID");
					int M_ProductBOM_ID = rs.getInt("M_ProductBom_ID");
					BigDecimal QtyUsed = rs.getBigDecimal("QtyUsed");
					BigDecimal MovementQty = rs.getBigDecimal("MovementQty");
					BigDecimal Scrap = rs.getBigDecimal("scrap");
					BigDecimal PriceActual = rs.getBigDecimal("priceactual");
					BigDecimal QtyOnHand = rs.getBigDecimal("qtyonhand");
					BigDecimal QtyOrdered = rs.getBigDecimal("qtyordered");
					BigDecimal QtyReserved = rs.getBigDecimal("qtyreserved");
					int M_Locator_ID = rs.getInt("m_locator_id");
					String locator_value = rs.getString("locator_value");
					int M_Warehouse_ID = rs.getInt("m_warehouse_id");
					String warehouse_name = rs.getString("warehouse_name");
					int AD_Org_ID = rs.getInt("ad_org_id");
					String org_name = rs.getString("org_name");
					int productuom_id = rs.getInt("productuom_id");
					int bom_uom_id = rs.getInt("bom_uom_id");
					String bom_uom_name = rs.getString("bom_uom_name");
					String bom_uomsymbol = rs.getString("bom_uomsymbol");
					BigDecimal DivideRate = rs.getBigDecimal("dividerate");
					int bom_uombase_id = rs.getInt("bom_uombase_id");
					String product_bom_name = rs.getString("Product_BOM_Name");
					String product_bom_sku = rs.getString("Product_BOM_sku");
					String Product_BOM_Value = rs.getString("Product_BOM_Value");
					
					X_FTU_R_ProductionPreview pp = new X_FTU_R_ProductionPreview (getCtx(),0,get_TrxName());
					pp.setM_Product_ID(M_Product_ID);
					pp.setproduct_value(product_value);
					pp.setSKU(sku);
					pp.setproduct_name(product_name);
					pp.setproduct_value(product_value);
					pp.setProductionMethod(ProductionMethod);	
					pp.setIsBOM(IsBOM);
					pp.setpp_product_bom_value(PP_Product_BOM_Value);
					pp.setpp_product_bom_name(PP_Product_BOM_Name);
					pp.setDocumentNo(DocumentNo);
					pp.setPP_Product_BOM_ID(PP_Product_BOM_ID);
					pp.setM_ProductBOM_ID(M_ProductBOM_ID);
					
					QtyUsed = QtyUsed.multiply(p_ProductionQty).setScale(4, RoundingMode.HALF_UP);
					pp.setQtyUsed(QtyUsed);
					MovementQty = MovementQty.multiply(p_ProductionQty).setScale(4, RoundingMode.HALF_UP);
					pp.setMovementQty(MovementQty);
					pp.setScrap(Scrap);
					pp.setScrappedQty(QtyUsed.subtract(MovementQty));
					pp.setPriceActual(PriceActual);
					pp.setLineTotalAmt(PriceActual.multiply(p_ProductionQty).setScale(4, RoundingMode.HALF_UP));
					totalAmt = totalAmt.add(PriceActual.multiply(p_ProductionQty).setScale(4, RoundingMode.HALF_UP));
					
					if(p_TrxType.equalsIgnoreCase("T")) {
						priceActual = PriceActual;
					}
					
					pp.setQtyOnHand(QtyOnHand);
					pp.setQtyOrdered(QtyOrdered);
					pp.setQtyReserved(QtyReserved);
					pp.setM_Locator_ID(M_Locator_ID);
					pp.setlocator_value(locator_value);
					pp.setM_Warehouse_ID(M_Warehouse_ID);
					pp.setwarehouse_name(warehouse_name);
					pp.setAD_Org_ID(AD_Org_ID);
					pp.setorg_name(org_name);
					pp.setproductuom_id(productuom_id);
					pp.setbom_uom_id(bom_uom_id);
					pp.setbom_uom_name(bom_uom_name);
					pp.setbom_uomsymbol(bom_uomsymbol);
					pp.setDivideRate(DivideRate);
					pp.setbom_uombase_id(bom_uombase_id);
					pp.setproduct_bom_name(product_bom_name);
					pp.setproduct_bom_sku(product_bom_sku);
					pp.setProduct_BOM_Value(Product_BOM_Value);
					pp.setAD_PInstance_ID(getAD_PInstance_ID());	
					
					if (p_CreateProduction)
						pp.set_ValueOfColumn("M_Production_ID", m_production.getM_Production_ID());
					
					pp.saveEx(get_TrxName());
				}
				

				if(p_TrxType.equalsIgnoreCase("P")) {
					priceActual = totalAmt.divide(p_ProductionQty,4, RoundingMode.HALF_UP);
				}
				sql = "UPDATE ftu_rv_productionpreview SET TotalAmt="+totalAmt+",TotalPrice="+priceActual+" WHERE AD_PInstance_ID="+getAD_PInstance_ID();
				DB.executeUpdateEx(sql, get_TrxName());
				
				
			}catch(Exception e) {
				
				throw new AdempiereException(e);	
				
			}finally {
				
				rs.close();
				rs=null;
				pstm=null;
				
			}
			if (p_CreateProduction)
				return m_production.getDocumentNo();
			return "";
		}
		
		public void createProduction() {
			m_production = new FTUMProduction(getCtx(),0,get_TrxName());
			m_production.setAD_Org_ID(p_AD_Org_ID);
			m_production.setM_Locator_ID(p_M_Locator_ID);
			m_production.setM_Product_ID(p_M_Product_ID);
			m_production.setMovementDate(p_DateDoc);
			m_production.setDatePromised(p_DateDoc);
			m_production.setProductionQty(p_ProductionQty);
			m_production.saveEx(get_TrxName());
			if(p_TrxType.equalsIgnoreCase("P"))
				m_production.createProductionLines(false, p_PP_Product_BOM_ID);
			else if(p_TrxType.equalsIgnoreCase("T"))
				m_production.createTransformationLines(false);
			
			if(!m_production.processIt(p_DocAction))
				throw new AdempiereException(m_production.getProcessMsg());
			else 
				m_production.saveEx(get_TrxName());
			
		}
	
}
