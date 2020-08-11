package net.frontuari.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.logging.Level;

import net.frontuari.base.FTUProcess;
import net.frontuari.model.FTUMProduction;
import net.frontuari.model.FTUMProductionLine;

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



public class ProductionCreate extends FTUProcess {
	//implements ProcessCall 
	
		private int p_M_Production_ID=0;
		private FTUMProduction m_production = null;
		private boolean mustBeStocked = false;  //not used
		private boolean p_Recreate = false;
		private BigDecimal p_NewQty = null;
		private int AD_Org_ID = 0;
		private int AD_Client_ID = 0;
		private String p_trx=null;
		private int lineno;
		private int count;
		
		@Override
		protected void prepare() {
			
			ProcessInfoParameter[] para = getParameter();
			for (int i = 0; i < para.length; i++)
			{
				String name = para[i].getParameterName();
				if ("Recreate".equals(name))
					p_Recreate = "Y".equals(para[i].getParameter());
				else if ("ProductionQty".equals(name))
					p_NewQty  = (BigDecimal) para[i].getParameter();
				else
					log.log(Level.SEVERE, "Unknown Parameter: " + name);		
			}
			
			p_M_Production_ID = getRecord_ID();
			m_production = new FTUMProduction(getCtx(), p_M_Production_ID, get_TrxName());
			
		}

		@Override
		public String doIt() throws Exception {
			
			if ( m_production.get_ID() == 0 )
				return "@Error@: " + Msg.getMsg(getCtx(), "BSCA_NotLoadProductionHeader");

			if ( m_production.isProcessed() )
				return Msg.getMsg(getCtx(), "BSCA_AlreadyProcessed");
			
			if((m_production.getProductionQty().compareTo(BigDecimal.ZERO))<=0){
				throw new AdempiereException("La candidad a Transformar debe ser mayor a cero");
			}
			
			if(getProcessInfo() == null)
				AD_Client_ID = m_production.getAD_Client_ID();
			else
				AD_Client_ID = getAD_Client_ID();
			
			AD_Org_ID = m_production.getAD_Org_ID();
			return createLines();
		}
		
		protected String createLines() throws Exception {
			
			int created = 0;
			if (!m_production.isUseProductionPlan()) {
				validateEndProduct(m_production.getM_Product_ID());
				
				if (!p_Recreate && "Y".equalsIgnoreCase(m_production.getIsCreated()))
					throw new AdempiereUserError(Msg.getMsg(getCtx(), "BSCA_ProductionAlreadyCreated"));
				
				if (p_NewQty != null )
					m_production.setProductionQty(p_NewQty);
				
				m_production.deleteLines(get_TrxName());
				created = createLines(mustBeStocked);
			} else {
				Query planQuery = new Query(getCtx(), I_M_ProductionPlan.Table_Name, "M_ProductionPlan.M_Production_ID=?", get_TrxName());
				List<MProductionPlan> plans = planQuery.setParameters(m_production.getM_Production_ID()).list();
				for(MProductionPlan plan : plans) {
					validateEndProduct(plan.getM_Product_ID());
					
					if (!p_Recreate && "Y".equalsIgnoreCase(m_production.getIsCreated()))
						throw new AdempiereUserError(Msg.getMsg(getCtx(), "BSCA_ProductionAlreadyCreated"));
					
					plan.deleteLines(get_TrxName());
					int n = plan.createLines(mustBeStocked);
					if ( n == 0 ) 
					{return "@Error@ " + Msg.getMsg(getCtx(), "BSCA_FailedToCreateProduction"); }
					created = created + n;
				}
			}
			if ( created == 0 ) 
			{return "@Error@ " + Msg.getMsg(getCtx(), "BSCA_FailedToCreateProduction"); }
			
			
			m_production.setIsCreated(MProduction.ISCREATED_Yes);
			m_production.save(get_TrxName());
			
			for(FTUMProductionLine line : m_production.getLines()) {
				if(line.isEndProduct())
					line.set_ValueOfColumn("Qty", m_production.getProductionQty().abs());
				else
					line.set_ValueOfColumn("Qty", line.getQtyUsed().abs());
				line.save(get_TrxName());
			}
			StringBuilder msgreturn = new StringBuilder().append(created).append(Msg.getMsg(getCtx(), "BSCA_ProductionLinesCreateds"));
			if(getProcessInfo() != null)
				addLog(m_production.get_ID(), m_production.getMovementDate(), null, m_production.getM_Product().getName() +": "+m_production.getDocumentNo() + ": OK", MProduction.Table_ID, m_production.getM_Product_ID());
			return msgreturn.toString();
		}
		
		private void validateEndProduct(int M_Product_ID) throws Exception {
			isBom(M_Product_ID);
			
			if (!costsOK(M_Product_ID)) {
				String msg = Msg.getMsg(getCtx(), "BSCA_DiffCosts");
				if (MSysConfig.getBooleanValue(MSysConfig.MFG_ValidateCostsDifferenceOnCreate, false, AD_Client_ID)) {
					throw new AdempiereUserError(Msg.getMsg(getCtx(), "BSCA_DiffCosts"));
				} else {
					log.warning(msg);
				}
			}
		}
		
		protected void isBom(int M_Product_ID) throws Exception {
			String bom = DB.getSQLValueString(get_TrxName(), "SELECT isbom FROM M_Product WHERE M_Product_ID = ?", M_Product_ID);
			if ("N".compareTo(bom) == 0)
			{
				throw new AdempiereUserError (Msg.getMsg(getCtx(), "BSCA_ProductNotBOM"));
			}
			int materials = DB.getSQLValue(get_TrxName(), "SELECT count(M_Product_BOM_ID) FROM M_Product_BOM WHERE M_Product_ID = ? AND (AD_Org_ID = 0 OR AD_Org_ID = ?) ", M_Product_ID, AD_Org_ID);
			if (materials == 0)
			{
				throw new AdempiereUserError (Msg.getMsg(getCtx(), "BSCA_ProductNotHaveBOMs"));
			}
		}
		
		private boolean costsOK(int M_Product_ID) throws AdempiereUserError {
			// Warning will not work if non-standard costing is used
			String sql = "SELECT ABS(((cc.currentcostprice-(SELECT SUM(c.currentcostprice*bom.bomqty)"
	            + " FROM m_cost c"
	            + " INNER JOIN m_product_bom bom ON (c.m_product_id=bom.m_productbom_id)"
		            + " INNER JOIN m_costelement ce ON (c.m_costelement_id = ce.m_costelement_id AND ce.costingmethod = 'S')"
	            + " WHERE bom.m_product_id = pp.m_product_id)"
	            + " )/cc.currentcostprice))"
	            + " FROM m_product pp"
	            + " INNER JOIN m_cost cc on (cc.m_product_id=pp.m_product_id)"
	            + " INNER JOIN m_costelement ce ON (cc.m_costelement_id=ce.m_costelement_id)"
	            + " WHERE cc.currentcostprice > 0 AND pp.M_Product_ID = ?"
	            + " AND ce.costingmethod='S'";
			
			BigDecimal costPercentageDiff = DB.getSQLValueBD(get_TrxName(), sql, M_Product_ID);
			
			if (costPercentageDiff == null)
			{
				costPercentageDiff = Env.ZERO;
				String msg = "Could not retrieve costs";
				if (MSysConfig.getBooleanValue(MSysConfig.MFG_ValidateCostsOnCreate, false, AD_Client_ID)) {
					throw new AdempiereUserError(msg);
				} else {
					log.warning(msg);
				}
			}
			
			if ( (costPercentageDiff.compareTo(new BigDecimal("0.005")))< 0 )
				return true;
			
			return false;
		}
		
		public void setMProduction(int M_Production_ID) {
			p_M_Production_ID = M_Production_ID;
			m_production = new FTUMProduction(getCtx(), p_M_Production_ID, get_TrxName());
		}
		
		public int getM_Production_ID() {
			return p_M_Production_ID;
		}
		
		public void setRecreate(boolean recreate) {
			p_Recreate = recreate;
		}
		
		public boolean getRecreate() {
			return p_Recreate;
		}
		
		public void setNewQty(BigDecimal newQty) {
			p_NewQty = newQty;
		}
		
		public BigDecimal getNewQty() {
			return p_NewQty;
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
		
		public int createLines(boolean mustBeStocked) throws Exception {
			
			lineno = 100;

			count = 0;

			// product to be produced
			MProduct finishedProduct = new MProduct(getCtx(), m_production.getM_Product_ID(), get_TrxName());
			
			if(m_production.get_ValueAsBoolean("IsTransformation")){
				

				BigDecimal MovementQty = m_production.getProductionQty();
				
				MStorageOnHand[] storages = null;
				
				MLocator finishedLocator = MLocator.get(getCtx(), m_production.getM_Locator_ID());
								
				int M_Warehouse_ID = finishedLocator.getM_Warehouse_ID();
				
				int asi = 0;
				
				MClient client = MClient.get(getCtx());
				MProductCategory pc = MProductCategory.get(getCtx(),
						finishedProduct.getM_Product_Category_ID());
				String MMPolicy = pc.getMMPolicy();
				if (MMPolicy == null || MMPolicy.length() == 0) { 
					MMPolicy = client.getMMPolicy();
				}

				storages = MStorageOnHand.getWarehouse(getCtx(), M_Warehouse_ID, finishedProduct.get_ID(), 0, null,
						MProductCategory.MMPOLICY_FiFo.equals(MMPolicy), true, 0, get_TrxName());


				FTUMProductionLine Line = null;
				int prevLoc = -1;
				int previousAttribSet = -1;
				// Create lines from storage until qty is reached
				for (int sl = 0; sl < storages.length; sl++) {
					BigDecimal lineQty = storages[sl].getQtyOnHand();
					if (lineQty.signum() != 0) {
						if (lineQty.compareTo(MovementQty) > 0)
							lineQty = MovementQty;

						int loc = storages[sl].getM_Locator_ID();
						int slASI = storages[sl].getM_AttributeSetInstance_ID();
						int locAttribSet = new MAttributeSetInstance(getCtx(), asi,
								get_TrxName()).getM_AttributeSet_ID();

						// roll up costing attributes if in the same locator
						if (locAttribSet == 0 && previousAttribSet == 0
								&& prevLoc == loc) {
							Line.setQtyUsed(Line.getQtyUsed()
									.add(lineQty));
							Line.setPlannedQty(Line.getQtyUsed());
							Line.setMovementQty(Line.getQtyUsed().negate());
							Line.saveEx(get_TrxName());

						}
						// otherwise create new line
						else {
							Line = new FTUMProductionLine(m_production);
							Line.setLine(lineno);
							Line.setM_Product_ID(finishedProduct.get_ID());
							Line.setM_Locator_ID(loc);
							Line.setQtyUsed(lineQty);
							Line.setPlannedQty(lineQty);
							Line.setMovementQty(lineQty.negate());
							Line.setIsEndProduct(false);
							if (slASI != 0 && locAttribSet != 0)  // ie non costing attribute
								Line.setM_AttributeSetInstance_ID(slASI);
							Line.saveEx(get_TrxName());

						}
						prevLoc = loc;
						previousAttribSet = locAttribSet;
						// enough ?
						MovementQty = MovementQty.subtract(lineQty);
						if (MovementQty.signum() == 0)
							break;
				/*
				MProductionLine line = new MProductionLine( m_production );
				line.setLine( lineno );
				line.setM_Product_ID( finishedProduct.get_ID() );
				line.setM_Locator_ID( m_production.getM_Locator_ID());
				//line.setMovementQty(m_production.getProductionQty());
				line.setQtyUsed(m_production.getProductionQty());
				line.setPlannedQty(m_production.getProductionQty());
				
				line.saveEx();
				*/	}
				}
				
			}else{
				FTUMProductionLine line = new FTUMProductionLine( m_production );
				line.setLine( lineno );
				line.setM_Product_ID( finishedProduct.get_ID() );
				line.setM_Locator_ID( m_production.getM_Locator_ID() );
				line.setMovementQty(m_production.getProductionQty());
				line.setPlannedQty(m_production.getProductionQty());
				
				line.saveEx();
			}

			
			count++;
			
			createLines(mustBeStocked, finishedProduct, m_production.getProductionQty());
			
			return count;
		}

		@SuppressWarnings("resource")
		public int createLines(boolean mustBeStocked, MProduct finishedProduct, BigDecimal requiredQty) throws Exception  {
			
			lineno = lineno + 10;
			
			int defaultLocator = 0;
			
			MLocator finishedLocator = MLocator.get(getCtx(), m_production.getM_Locator_ID());
			
			int M_Warehouse_ID = finishedLocator.getM_Warehouse_ID();
			
			int asi = 0;

			// products used in production
			String sql = " SELECT M_ProductBom_ID, BOMQty, PerformanceFactor FROM M_Product_BOM "
					+ " WHERE M_Product_ID=" + finishedProduct.getM_Product_ID() + " AND isactive='Y' AND (AD_Org_ID = 0 OR AD_Org_ID = " + m_production.getAD_Org_ID() + ") "
					+ " ORDER BY Line";

			PreparedStatement pstmt = null;
			ResultSet rs = null;

			try {
				pstmt = DB.prepareStatement(sql, get_TrxName());
				rs = pstmt.executeQuery();
				while (rs.next()) {
					
					
					int BOMProduct_ID = rs.getInt(1);
					BigDecimal BOMQty = rs.getBigDecimal(2);
					BigDecimal BOMMovementQty = BOMQty.multiply(requiredQty);
					
					MProduct bomproduct = new MProduct(Env.getCtx(), BOMProduct_ID, get_TrxName());
					
					if (bomproduct.isBOM() && bomproduct.isPhantom() && (!m_production.get_ValueAsBoolean("IsTransformation"))) {
						createLines(mustBeStocked, bomproduct, BOMMovementQty);
					}
					else {
						defaultLocator = bomproduct.getM_Locator_ID();
						if ( defaultLocator == 0 )
							defaultLocator = m_production.getM_Locator_ID();
						
						
						if(m_production.get_ValueAsBoolean("IsTransformation")){
							BigDecimal factor = rs.getBigDecimal("PerformanceFactor");
							BigDecimal qtyUsed = new BigDecimal(0);
							
							if(factor.compareTo(qtyUsed)>0){
								qtyUsed= factor.multiply(BOMMovementQty);
							}else{
								throw new AdempiereException("El Producto hijo no tiene factor de conversión");
							}
							
							if (!bomproduct.isStocked() || BOMMovementQty.signum() == 0) {		
							FTUMProductionLine line = new FTUMProductionLine( m_production );
							line.setLine( lineno );
							line.setM_Product_ID(BOMProduct_ID);
							line.setM_Locator_ID(defaultLocator);
							line.setMovementQty(BOMMovementQty);
							line.setPlannedQty(BOMMovementQty);
							line.setQtyUsed(BOMMovementQty);
							line.setIsEndProduct(true);
							line.saveEx();
							lineno = lineno + 10;
							count++;
							
							} else {

								// BOM stock info
								MProduct usedProduct = MProduct.get(getCtx(), BOMProduct_ID);
								defaultLocator = usedProduct.getM_Locator_ID();
								if ( defaultLocator == 0 )
									defaultLocator = m_production.getM_Locator_ID();
								if (usedProduct == null || usedProduct.get_ID() == 0)
									return 0;
								
								FTUMProductionLine line = new FTUMProductionLine( m_production );
								line.setLine( lineno );
								line.setM_Product_ID(BOMProduct_ID);
								line.setM_Locator_ID(defaultLocator);
								line.setMovementQty(qtyUsed);
								line.setPlannedQty(BOMMovementQty);
								line.setQtyUsed(BOMMovementQty);
								line.setIsEndProduct(true);
								//line.setDescription("P:"+BOMProduct_ID+" MQ:"+BOMMovementQty+" ,");
								
								line.saveEx();
								lineno = lineno + 10;
								count++;
								
							}
								
						}else {
							
							
							if (!bomproduct.isStocked()) {					
								FTUMProductionLine BOMLine = new FTUMProductionLine(m_production);
								BOMLine.setLine( lineno );
								BOMLine.setM_Product_ID(BOMProduct_ID);
								BOMLine.setM_Locator_ID(defaultLocator);  
								BOMLine.setQtyUsed(BOMMovementQty);
								BOMLine.setPlannedQty(BOMMovementQty);
								BOMLine.saveEx(get_TrxName());

								lineno = lineno + 10;
								count++;					
							} else if (BOMMovementQty.signum() == 0) {
								FTUMProductionLine BOMLine = new FTUMProductionLine(m_production);
								BOMLine.setLine( lineno );
								BOMLine.setM_Product_ID(BOMProduct_ID);
								BOMLine.setM_Locator_ID(defaultLocator);  
								BOMLine.setQtyUsed(BOMMovementQty);
								BOMLine.setPlannedQty(BOMMovementQty);
								BOMLine.saveEx(get_TrxName());

								lineno = lineno + 10;
								count++;
							} else {
								FTUMProductionLine BOMLine = new FTUMProductionLine(m_production);
								BOMLine.setLine( lineno );
								BOMLine.setM_Product_ID(BOMProduct_ID);
								BOMLine.setM_Locator_ID(defaultLocator);  
								BOMLine.setQtyUsed(BOMMovementQty);
								BOMLine.setPlannedQty(BOMMovementQty);
								BOMLine.saveEx(get_TrxName());

								lineno = lineno + 10;
								count++;							
								/**
								// BOM stock info
								MStorageOnHand[] storages = null;
								MProduct usedProduct = MProduct.get(getCtx(), BOMProduct_ID);
								defaultLocator = usedProduct.getM_Locator_ID();
								if ( defaultLocator == 0 )
									defaultLocator = m_production.getM_Locator_ID();
								if (usedProduct == null || usedProduct.get_ID() == 0)
									return 0;

								MClient client = MClient.get(getCtx());
								MProductCategory pc = MProductCategory.get(getCtx(),
										usedProduct.getM_Product_Category_ID());
								String MMPolicy = pc.getMMPolicy();
								if (MMPolicy == null || MMPolicy.length() == 0) { 
									MMPolicy = client.getMMPolicy();
								}

								storages = MStorageOnHand.getWarehouse(getCtx(), M_Warehouse_ID, BOMProduct_ID, 0, null,
										MProductCategory.MMPOLICY_FiFo.equals(MMPolicy), true, 0, get_TrxName());

								FTUMProductionLine BOMLine = null;
								int prevLoc = -1;
								int previousAttribSet = -1;
								// Create lines from storage until qty is reached
								for (int sl = 0; sl < storages.length; sl++) {
									BigDecimal lineQty = storages[sl].getQtyOnHand();
									if (lineQty.signum() != 0) {
										if (lineQty.compareTo(BOMMovementQty) > 0)
											lineQty = BOMMovementQty;

										int loc = storages[sl].getM_Locator_ID();
										int slASI = storages[sl].getM_AttributeSetInstance_ID();
										int locAttribSet = new MAttributeSetInstance(getCtx(), asi,
												get_TrxName()).getM_AttributeSet_ID();

										// roll up costing attributes if in the same locator
										if (locAttribSet == 0 && previousAttribSet == 0
												&& prevLoc == loc) {
											BOMLine.setQtyUsed(BOMLine.getQtyUsed()
													.add(lineQty));
											BOMLine.setPlannedQty(BOMLine.getQtyUsed());
											BOMLine.saveEx(get_TrxName());

										}
										// otherwise create new line
										else {
											BOMLine = new FTUMProductionLine(m_production);
											BOMLine.setLine(lineno);
											BOMLine.setM_Product_ID(BOMProduct_ID);
											BOMLine.setM_Locator_ID(loc);
											BOMLine.setQtyUsed(lineQty);
											BOMLine.setPlannedQty(lineQty);
											if (slASI != 0 && locAttribSet != 0)  // ie non costing attribute
												BOMLine.setM_AttributeSetInstance_ID(slASI);
											BOMLine.saveEx(get_TrxName());

											lineno = lineno + 10;
											count++;
										}
										prevLoc = loc;
										previousAttribSet = locAttribSet;
										// enough ?
										BOMMovementQty = BOMMovementQty.subtract(lineQty);
										if (BOMMovementQty.signum() == 0)
											break;
									}
								} // for available storages

								// fallback
								if (BOMMovementQty.signum() != 0 ) {
									if (!mustBeStocked) {
										// roll up costing attributes if in the same locator
										if ( previousAttribSet == 0
												&& prevLoc == defaultLocator) {
											BOMLine.setQtyUsed(BOMLine.getQtyUsed()
													.add(BOMMovementQty));
											BOMLine.setPlannedQty(BOMLine.getQtyUsed());
											BOMLine.saveEx(get_TrxName());

										}
										// otherwise create new line
										else {
											BOMLine = new FTUMProductionLine(m_production);
											BOMLine.setLine(lineno);
											BOMLine.setM_Product_ID(BOMProduct_ID);
											BOMLine.setM_Locator_ID(defaultLocator);  
											BOMLine.setQtyUsed(BOMMovementQty);
											BOMLine.setPlannedQty(BOMMovementQty);
											BOMLine.saveEx(get_TrxName());

											lineno = lineno + 10;
											count++;
										}

									} else {
										throw new AdempiereUserError("Not enough stock of " + BOMProduct_ID);
									}
								}
								
								
								
								
								**/
							}
						}
						
						
						
					}
				} // for all bom products
			} catch (Exception e) {
				throw new AdempiereException("Failed to create production lines", e);
			}
			finally {
				DB.close(rs, pstmt);
			}

			return count;
		}
		
	
}
